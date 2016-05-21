package com.microsoft.azure.framework.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

public abstract class AbstractCommand implements Command {
	public static abstract class AbstractBuilderFactory implements Command.BuilderFactory {
		private AutowireCapableBeanFactory autowireBeanFactory;

		@Autowired
		public final void setAutowireCapableBeanFactory(final AutowireCapableBeanFactory autowireBeanFactory) {
			this.autowireBeanFactory = autowireBeanFactory;
		}

		protected final <T> T inject(final Class<T> clazz, final T builder) {
			autowireBeanFactory.autowireBean(builder);
			return builder;
		}
	}
}
