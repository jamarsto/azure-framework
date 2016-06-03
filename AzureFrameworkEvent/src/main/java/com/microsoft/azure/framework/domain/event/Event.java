package com.microsoft.azure.framework.domain.event;

import java.io.Serializable;
import java.util.Calendar;

public interface Event extends Serializable {
	Calendar getCreatedDateTime();
}
