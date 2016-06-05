package com.microsoft.azure.framework.eventstore;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public interface OutputEventStream extends EventStream, Closeable {

	interface Builder {

		OutputEventStream build();

		Builder buildBucketID(String bucketID);

		Builder buildFromVersion(Long fromVersion);

		Builder buildStreamID(UUID streamID);

	}
	
	interface BuilderFactory {

		Builder create();

	}
	
	void flush(UUID changeSetID) throws IOException;

	void write(Collection<Serializable> events) throws IOException;

	void write(Serializable event) throws IOException;
	
	void write(Serializable[] events) throws IOException;

}
