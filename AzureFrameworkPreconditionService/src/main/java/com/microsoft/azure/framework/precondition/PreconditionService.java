package com.microsoft.azure.framework.precondition;

public interface PreconditionService {
	<T extends Throwable> void requiresEmpty(Class<T> clazz, String message, String value) throws T;

	void requiresEmpty(String message, String value);

	<T extends Throwable, V extends Number & Comparable<V>> void requiresEQ(Class<T> clazz, String message, V value,
			V target) throws T;

	<V extends Number & Comparable<V>> void requiresEQ(String message, V value, V target);

	<T extends Throwable, V> void requiresEquals(Class<T> clazz, String message, V value, V target) throws T;

	<V> void requiresEquals(String message, V value, V target);

	<T extends Throwable> void requiresFalse(Class<T> clazz, String message, Boolean value) throws T;

	void requiresFalse(String message, Boolean value);

	<T extends Throwable, V extends Number & Comparable<V>> void requiresGE(Class<T> clazz, String message, V value,
			V target) throws T;

	<V extends Number & Comparable<V>> void requiresGE(String message, V value, V target);

	<T extends Throwable, V extends Number & Comparable<V>> void requiresGT(Class<T> clazz, String message, V value,
			V target) throws T;

	<V extends Number & Comparable<V>> void requiresGT(String message, V value, V target);

	<T extends Throwable, V extends Number & Comparable<V>> void requiresLE(Class<T> clazz, String message, V value,
			V target) throws T;

	<V extends Number & Comparable<V>> void requiresLE(String message, V value, V target);

	<T extends Throwable, V extends Number & Comparable<V>> void requiresLT(Class<T> clazz, String message, V value,
			V target) throws T;

	<V extends Number & Comparable<V>> void requiresLT(String message, V value, V target);

	<T extends Throwable, V extends Number & Comparable<V>> void requiresNE(Class<T> clazz, String message, V value,
			V target) throws T;

	<V extends Number & Comparable<V>> void requiresNE(String message, V value, V target);

	<T extends Throwable> void requiresNotEmpty(Class<T> clazz, String message, String value) throws T;

	void requiresNotEmpty(String message, String value);

	<T extends Throwable, V> void requiresNotEquals(Class<T> clazz, String message, V value, V target) throws T;

	<V> void requiresNotEquals(String message, V value, V target);

	<T extends Throwable> void requiresNotFalse(Class<T> clazz, String message, Boolean value) throws T;

	void requiresNotFalse(String message, Boolean value);

	<T extends Throwable, V> void requiresNotNull(Class<T> clazz, String message, V value) throws T;

	<V> void requiresNotNull(String message, V value);

	<T extends Throwable> void requiresNotTrue(Class<T> clazz, String message, Boolean value) throws T;

	void requiresNotTrue(String message, Boolean value);

	<T extends Throwable, V> void requiresNull(Class<T> clazz, String message, V value) throws T;

	<V> void requiresNull(String message, V value);

	<T extends Throwable> void requiresTrue(Class<T> clazz, String message, Boolean value) throws T;

	void requiresTrue(String message, Boolean value);

}
