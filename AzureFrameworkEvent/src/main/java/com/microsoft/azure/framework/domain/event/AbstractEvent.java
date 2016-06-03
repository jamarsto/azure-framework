package com.microsoft.azure.framework.domain.event;

import java.util.Calendar;

public abstract class AbstractEvent implements Event {
	private static final long serialVersionUID = 1L;
	private Calendar createdDateTime;
	
	@Override
	public final Calendar getCreatedDateTime() {
		return createdDateTime;
	}
	
	public final void setCreatedDateTime(Calendar createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

}
