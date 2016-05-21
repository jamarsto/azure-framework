package com.microsoft.azure.framework.precondition.impl;

import java.lang.reflect.InvocationTargetException;

import org.springframework.stereotype.Component;

import com.microsoft.azure.framework.precondition.PreconditionException;
import com.microsoft.azure.framework.precondition.PreconditionService;

@Component
public final class SimplePreconditionService implements PreconditionService {
	private <T extends Throwable> void generateException(final Class<T> clazz, final String message,
			final Object... args) throws T {
		try {
			throw clazz.getConstructor(String.class).newInstance(String.format(message, args));
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new PreconditionException(e);
		}
	}

	private Boolean isEmpty(final String value) {
		return value == null || "".equals(value.trim());
	}

	private <V extends Number & Comparable<V>> Boolean isEQ(final V value, final V target) {
		if (value == null || target == null) {
			return Boolean.FALSE;
		}
		return value.compareTo(target) == 0;
	}

	private <V> Boolean isEQ(final V value, final V target) {
		if (value == null && target == null) {
			return Boolean.TRUE;
		}
		if (value == null || target == null) {
			return Boolean.FALSE;
		}
		return value.equals(target);
	}

	private Boolean isFalse(final Boolean value) {
		return value != null && value.equals(Boolean.FALSE);
	}

	private <V extends Number & Comparable<V>> Boolean isGE(final V value, final V target) {
		return isEQ(value, target) || isGT(value, target);
	}

	private <V extends Number & Comparable<V>> Boolean isGT(final V value, final V target) {
		if (value == null || target == null) {
			return Boolean.FALSE;
		}
		return value.compareTo(target) > 0;
	}

	private <V extends Number & Comparable<V>> Boolean isLE(final V value, final V target) {
		return isEQ(value, target) || isLT(value, target);
	}

	private <V extends Number & Comparable<V>> Boolean isLT(final V value, final V target) {
		if (value == null || target == null) {
			return Boolean.FALSE;
		}
		return value.compareTo(target) < 0;
	}

	private <V extends Number & Comparable<V>> Boolean isNE(final V value, final V target) {
		return !isEQ(value, target);
	}

	private <V> Boolean isNE(final V value, final V target) {
		return !isEQ(value, target);
	}

	private Boolean isNotEmpty(final String value) {
		return value != null && !"".equals(value.trim());
	}

	private Boolean isNotFalse(final Boolean value) {
		return !isFalse(value);
	}

	private <V> Boolean isNotNull(final V value) {
		return !isNull(value);
	}

	private Boolean isNotTrue(final Boolean value) {
		return !isTrue(value);
	}

	private <V> Boolean isNull(final V value) {
		return value == null;
	}

	private Boolean isTrue(final Boolean value) {
		return value != null && value.equals(Boolean.TRUE);
	}

	@Override
	public <T extends Throwable> void requiresEmpty(final Class<T> clazz, final String message, final String value)
			throws T {
		if (isEmpty(value).equals(Boolean.FALSE)) {
			generateException(clazz, message, value);
		}
	}

	@Override
	public void requiresEmpty(final String message, final String value) {
		requiresEmpty(PreconditionException.class, message, value);
	}

	@Override
	public <T extends Throwable, V extends Number & Comparable<V>> void requiresEQ(final Class<T> clazz,
			final String message, final V value, final V target) throws T {
		if (isEQ(value, target).equals(Boolean.FALSE)) {
			generateException(clazz, message, value, target);
		}
	}

	@Override
	public <V extends Number & Comparable<V>> void requiresEQ(final String message, final V value, final V target) {
		requiresEQ(PreconditionException.class, message, value, target);
	}

	@Override
	public <T extends Throwable, V> void requiresEquals(final Class<T> clazz, final String message, final V value,
			final V target) throws T {
		if (isEQ(value, target).equals(Boolean.FALSE)) {
			generateException(clazz, message, value, target);
		}
	}

	@Override
	public <V> void requiresEquals(final String message, final V value, final V target) {
		requiresEquals(PreconditionException.class, message, value, target);
	}

	@Override
	public <T extends Throwable> void requiresFalse(final Class<T> clazz, final String message, final Boolean value)
			throws T {
		if (isFalse(value).equals(Boolean.FALSE)) {
			generateException(clazz, message, value);
		}
	}

	@Override
	public void requiresFalse(final String message, final Boolean value) {
		requiresFalse(PreconditionException.class, message, value);
	}

	@Override
	public <T extends Throwable, V extends Number & Comparable<V>> void requiresGE(final Class<T> clazz,
			final String message, final V value, final V target) throws T {
		if (isGE(value, target).equals(Boolean.FALSE)) {
			generateException(clazz, message, value, target);
		}
	}

	@Override
	public <V extends Number & Comparable<V>> void requiresGE(final String message, final V value, final V target) {
		requiresGE(PreconditionException.class, message, value, target);
	}

	@Override
	public <T extends Throwable, V extends Number & Comparable<V>> void requiresGT(final Class<T> clazz,
			final String message, final V value, final V target) throws T {
		if (isGT(value, target).equals(Boolean.FALSE)) {
			generateException(clazz, message, value, target);
		}
	}

	@Override
	public <V extends Number & Comparable<V>> void requiresGT(final String message, final V value, final V target) {
		requiresGT(PreconditionException.class, message, value, target);
	}

	@Override
	public <T extends Throwable, V extends Number & Comparable<V>> void requiresLE(final Class<T> clazz,
			final String message, final V value, final V target) throws T {
		if (isLE(value, target).equals(Boolean.FALSE)) {
			generateException(clazz, message, value, target);
		}
	}

	@Override
	public <V extends Number & Comparable<V>> void requiresLE(final String message, final V value, final V target) {
		requiresLE(PreconditionException.class, message, value, target);
	}

	@Override
	public <T extends Throwable, V extends Number & Comparable<V>> void requiresLT(final Class<T> clazz,
			final String message, final V value, final V target) throws T {
		if (isLT(value, target).equals(Boolean.FALSE)) {
			generateException(clazz, message, value, target);
		}
	}

	@Override
	public <V extends Number & Comparable<V>> void requiresLT(final String message, final V value, final V target) {
		requiresLT(PreconditionException.class, message, value, target);
	}

	@Override
	public <T extends Throwable, V extends Number & Comparable<V>> void requiresNE(final Class<T> clazz,
			final String message, final V value, final V target) throws T {
		if (isNE(value, target).equals(Boolean.FALSE)) {
			generateException(clazz, message, value, target);
		}
	}

	@Override
	public <V extends Number & Comparable<V>> void requiresNE(final String message, final V value, final V target) {
		requiresNE(PreconditionException.class, message, value, target);
	}

	@Override
	public <T extends Throwable> void requiresNotEmpty(final Class<T> clazz, final String message, final String value)
			throws T {
		if (isNotEmpty(value).equals(Boolean.FALSE)) {
			generateException(clazz, message, value);
		}
	}

	@Override
	public void requiresNotEmpty(final String message, final String value) {
		requiresNotEmpty(PreconditionException.class, message, value);
	}

	@Override
	public <T extends Throwable, V> void requiresNotEquals(final Class<T> clazz, final String message, final V value,
			final V target) throws T {
		if (isNE(value, target).equals(Boolean.FALSE)) {
			generateException(clazz, message, value, target);
		}
	}

	@Override
	public <V> void requiresNotEquals(final String message, final V value, final V target) {
		requiresNotEquals(PreconditionException.class, message, value, target);
	}

	@Override
	public <T extends Throwable> void requiresNotFalse(final Class<T> clazz, final String message, final Boolean value)
			throws T {
		if (isNotFalse(value).equals(Boolean.FALSE)) {
			generateException(clazz, message, value);
		}
	}

	@Override
	public void requiresNotFalse(final String message, final Boolean value) {
		requiresNotFalse(PreconditionException.class, message, value);
	}

	@Override
	public <T extends Throwable, V> void requiresNotNull(final Class<T> clazz, final String message, final V value)
			throws T {
		if (isNotNull(value).equals(Boolean.FALSE)) {
			generateException(clazz, message, value);
		}
	}

	@Override
	public <V> void requiresNotNull(final String message, final V value) {
		requiresNotNull(PreconditionException.class, message, value);
	}

	@Override
	public <T extends Throwable> void requiresNotTrue(final Class<T> clazz, final String message, final Boolean value)
			throws T {
		if (isNotTrue(value).equals(Boolean.FALSE)) {
			generateException(clazz, message, value);
		}
	}

	@Override
	public void requiresNotTrue(final String message, final Boolean value) {
		requiresNotTrue(PreconditionException.class, message, value);
	}

	@Override
	public <T extends Throwable, V> void requiresNull(final Class<T> clazz, final String message, final V value)
			throws T {
		if (isNull(value).equals(Boolean.FALSE)) {
			generateException(clazz, message, value);
		}
	}

	@Override
	public <V> void requiresNull(final String message, final V value) {
		requiresNull(PreconditionException.class, message, value);
	}

	@Override
	public <T extends Throwable> void requiresTrue(final Class<T> clazz, final String message, final Boolean value)
			throws T {
		if (isTrue(value).equals(Boolean.FALSE)) {
			generateException(clazz, message, value);
		}
	}

	@Override
	public void requiresTrue(final String message, final Boolean value) {
		requiresTrue(PreconditionException.class, message, value);
	}
}
