package com.microsoft.azure.framework.eventstore;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public interface InputEventStream extends EventStream, Closeable {

	interface Builder {

		InputEventStream build() throws IOException;

		Builder buildBucketID(String bucketID);

		Builder buildChangeSetID(UUID changeSetID);

		Builder buildFilter(Class<?> clazz);

		Builder buildFromVersion(Long fromVersion);

		Builder buildStreamID(UUID streamID);

		Builder buildToVersion(Long toVersion);

	}

	interface BuilderFactory {

		Builder create();

	}

	int available();

	Long getFromVersion();

	Long getToVersion();

	Serializable read() throws IOException;

	List<Serializable> readAll() throws IOException;

}
