package com.microsoft.azure.framework.viewmanager;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.concurrent.ManageableThread;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.framework.domain.aggregate.AggregateException;
import com.microsoft.azure.framework.domain.event.Event;
import com.microsoft.azure.framework.domain.event.impl.EventEntry;
import com.microsoft.azure.framework.eventstore.InputEventStream;
import com.microsoft.azure.framework.servicebus.configuration.AzureServiceBusConfiguration;
import com.microsoft.azure.framework.servicebus.configuration.Namespace;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMessageOptions;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMode;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveSubscriptionMessageResult;
import com.microsoft.windowsazure.services.servicebus.models.SubscriptionInfo;

public abstract class AbstractViewManager {
	private AzureServiceBusConfiguration azureServiceBusConfiguration;
	private EntityManagerFactory entityManagerFactory;
	private ExecutorService executorService;
	private InputEventStream.BuilderFactory inputEventStreamBuilderFactory;
	private Boolean isTerminationRequested = Boolean.FALSE;
	private Logger logger;
	private ManagedThreadFactory managedThreadFactory;
	private ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<EntityManager>();

	private void apply(final UUID aggregateID, final List<Event> events) throws Exception {
		for (final Event event : events) {
			final Method method = this.getClass().getMethod("apply", UUID.class, event.getClass());
			method.invoke(this, aggregateID, event);
		}
	}

	protected abstract String getAggregateClassName();

	protected final EntityManager getEntityManager() {
		if (threadLocalEntityManager.get() == null) {
			threadLocalEntityManager.set(entityManagerFactory.createEntityManager());
		}
		return threadLocalEntityManager.get();
	}

	protected final Logger getLogger() {
		if (logger == null) {
			logger = LoggerFactory.getLogger(this.getClass());
		}
		return logger;
	}

	private ServiceBusContract getSubscriptionService(final String serviceName, final String secretName) {
		final Configuration config = ServiceBusConfiguration.configureWithSASAuthentication(serviceName,
				"RootManageSharedAccessKey", secretName, ".servicebus.windows.net");
		final ServiceBusContract service = ServiceBusService.create(config);
		final SubscriptionInfo subscriptionInfo = new SubscriptionInfo(getViewName().toLowerCase());
		try {
			service.getSubscription(getAggregateClassName(), getViewName().toLowerCase());
		} catch (final Exception e) {
			try {
				service.createSubscription(getAggregateClassName(), subscriptionInfo);
			} catch (final Exception se) {
				throw new AggregateException(se.getMessage(), se);
			}
		}
		return service;
	}

	protected abstract String getViewName();

	private ViewVersion getViewVersion(final EntityManager entityManager, final UUID aggregateId) {
		final Query query = entityManager.createNamedQuery("ViewManager.getVersion");
		query.setParameter("aggregateName", getAggregateClassName());
		query.setParameter("viewName", getViewName());
		query.setParameter("aggregateID", aggregateId);
		try {
			return (ViewVersion) query.getSingleResult();
		} catch (final NoResultException e) {
			final ViewVersion viewVersion = new ViewVersion(getAggregateClassName(), getViewName(), aggregateId);
			entityManager.persist(viewVersion);
			entityManager.flush();
			entityManager.detach(viewVersion);
			return (ViewVersion) query.getSingleResult();
		}
	}

	@PostConstruct
	public final void PostConstruct() {
		getLogger().info("Starting View Manager");
		executorService = Executors.newFixedThreadPool(azureServiceBusConfiguration.getNamespaces().size()
				* azureServiceBusConfiguration.getNumberOfReceivers(), managedThreadFactory);
		for (final Namespace namespace : azureServiceBusConfiguration.getNamespaces()) {
			for (int x = 0; x < azureServiceBusConfiguration.getNumberOfReceivers(); x++) {
				executorService.execute(new Runnable() {
					public void run() {
						receive(namespace.getName(), namespace.getSecret());
					}
				});
			}
		}
		getLogger().info("Started View Manager");
	}

	@PreDestroy
	public final void PreDestroy() {
		getLogger().info("Stopping View Manager");
		isTerminationRequested = Boolean.TRUE;
		try {
			executorService.shutdown();
			executorService.awaitTermination(azureServiceBusConfiguration.getReceiverTimout() * 2, TimeUnit.MINUTES);
			if (!executorService.isTerminated()) {
				executorService.shutdownNow();
				executorService.awaitTermination(azureServiceBusConfiguration.getReceiverTimout() * 2,
						TimeUnit.MINUTES);
			}
		} catch (InterruptedException e) {
			executorService.shutdownNow();
		} finally {
			try {
				if (!executorService.isTerminated()) {
					executorService.shutdownNow();
					executorService.awaitTermination(azureServiceBusConfiguration.getReceiverTimout() * 2,
							TimeUnit.MINUTES);
				}
			} catch (InterruptedException e) {
				executorService.shutdownNow();
			}
			getLogger().info("Stopped View Manager");
		}
	}

	@SuppressWarnings("unchecked")
	private void receive(final String serviceName, final String secretName) {
		final ObjectMapper mapper = new ObjectMapper();
		ServiceBusContract service = null;
		while (service == null && !isTerminationRequested
				&& !((ManageableThread) Thread.currentThread()).isShutdown()) {
			try {
				service = getSubscriptionService(serviceName, secretName);
				if (service == null) {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						return;
					}
				}
			} catch (final Exception e) {
				getLogger().debug("Trying to get subscription");
			}
		}
		if (isTerminationRequested || ((ManageableThread) Thread.currentThread()).isShutdown()) {
			return;
		}
		final ReceiveMessageOptions receiveMessageOptions = ReceiveMessageOptions.DEFAULT;
		receiveMessageOptions.setReceiveMode(ReceiveMode.PEEK_LOCK);
		receiveMessageOptions.setTimeout(azureServiceBusConfiguration.getReceiverTimout() * 60);
		BrokeredMessage brokeredMessage = null;
		UserTransaction userTransaction = null;
		Boolean inTransaction = Boolean.FALSE;
		try {
			userTransaction = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
		} catch (final NamingException e) {
			getLogger().error("Unable to obtain UserTransaction - Terminating");
			return;
		}
		while (!isTerminationRequested && !((ManageableThread) Thread.currentThread()).isShutdown()) {
			try {
				final ReceiveSubscriptionMessageResult receiveSubscriptionMessageResult = service
						.receiveSubscriptionMessage(getAggregateClassName().toLowerCase(), getViewName().toLowerCase(),
								receiveMessageOptions);
				if (receiveSubscriptionMessageResult == null) {
					continue;
				}
				brokeredMessage = receiveSubscriptionMessageResult.getValue();
				if (brokeredMessage == null || brokeredMessage.getMessageId() == null) {
					brokeredMessage = null;
					continue;
				}
				final UUID aggregateID = UUID.fromString((String) brokeredMessage.getProperty("aggregateId"));
				final Long fromVersion = ((Number) brokeredMessage.getProperty("fromVersion")).longValue();
				final Long toVersion = ((Number) brokeredMessage.getProperty("toVersion")).longValue();
				final EventEntry[] eventEntryArray = mapper.readValue(brokeredMessage.getBody(), EventEntry[].class);
				final List<Event> events = new ArrayList<Event>();
				userTransaction.begin();
				inTransaction = Boolean.TRUE;
				final EntityManager entityManager = getEntityManager();
				Long viewVersion = getViewVersion(entityManager, aggregateID).getVersion();
				userTransaction.commit();
				inTransaction = Boolean.FALSE;
				Long startVersion = fromVersion;
				if (viewVersion < fromVersion - 1L) {
					startVersion = viewVersion + 1L;
					final InputEventStream.Builder builder = inputEventStreamBuilderFactory.create();
					builder.buildBucketID(getAggregateClassName()).buildStreamID(aggregateID)
							.buildFromVersion(startVersion).buildToVersion(fromVersion - 1L);
					final InputEventStream stream = builder.build();
					events.addAll((List<? extends Event>) (List<? extends Serializable>) stream.readAll());
				}
				for (final EventEntry eventEntry : eventEntryArray) {
					final Class<? extends Event> clazz = (Class<? extends Event>) Class
							.forName(eventEntry.getEventClassName());
					final Event event = mapper.readValue(eventEntry.getEvent(), clazz);
					events.add(event);
				}
				userTransaction.begin();
				inTransaction = Boolean.TRUE;
				final ViewVersion viewVersionObject = getViewVersion(entityManager, aggregateID);
				if (viewVersion.compareTo(viewVersionObject.getVersion()) != 0) {
					userTransaction.rollback();
					service.unlockMessage(brokeredMessage);
					brokeredMessage = null;
					continue;
				}
				if (viewVersion <= fromVersion - 1L) {
					apply(aggregateID, events);
					setViewVersion(entityManager, viewVersionObject, toVersion);
					userTransaction.commit();
				} else {
					userTransaction.rollback();
				}
				inTransaction = Boolean.FALSE;
				viewVersion = getViewVersion(entityManager, aggregateID).getVersion();
				if (viewVersion < toVersion) {
					service.unlockMessage(brokeredMessage);
				} else {
					service.deleteMessage(brokeredMessage);
				}
				brokeredMessage = null;
			} catch (final Exception e) {
				if (brokeredMessage != null && brokeredMessage.getMessageId() != null) {
					try {
						service.unlockMessage(brokeredMessage);
						getLogger().info("Unlocked message");
					} catch (final Exception e1) {
						getLogger().warn("Unable to unlock message - will auto-unlock on lock timout");
					}
				}
				brokeredMessage = null;
				if (userTransaction != null && inTransaction) {
					try {
						userTransaction.rollback();
						inTransaction = Boolean.FALSE;
						getLogger().info("Rolled back transaction");
					} catch (final Exception e1) {
						getLogger().warn("Problem with rollback");
					}
				}
				getLogger().error(e.getMessage(), e);
			}
		}
	}

	@Autowired
	@Qualifier("materialized-view-unit")
	public final void setEntityManagerFactory(final EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	@Autowired
	public final void setEventBusConfiguration(final AzureServiceBusConfiguration azureServiceBusConfiguration) {
		this.azureServiceBusConfiguration = azureServiceBusConfiguration;
	}

	@Autowired
	public final void setInputEventSteamBuilderFactory(
			final InputEventStream.BuilderFactory inputEventStreamBuilderFactory) {
		this.inputEventStreamBuilderFactory = inputEventStreamBuilderFactory;
	}

	@Autowired
	public final void setManagedThreadFactory(final ManagedThreadFactory managedThreadFactory) {
		this.managedThreadFactory = managedThreadFactory;
	}

	private void setViewVersion(final EntityManager entityManager, final ViewVersion viewVersion, final Long version) {
		viewVersion.setVersion(version);
		entityManager.flush();
	}
}
