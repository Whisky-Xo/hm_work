package com.crm.service;

import com.crm.model.Notification;

public interface NotificationService {

	public int createNotification(Notification notification);

	public void setNotificationParam(String paramName, String paramValue, Notification notification);

}
