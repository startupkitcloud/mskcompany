package org.startupkit.company;

import java.util.Date;

import javax.persistence.Embeddable;

@Embeddable
public class ScheduleException {

	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
