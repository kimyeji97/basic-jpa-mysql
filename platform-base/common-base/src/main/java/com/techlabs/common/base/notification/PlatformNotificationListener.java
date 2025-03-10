package com.techlabs.common.base.notification;


import com.techlabs.common.base.notification.domain.PlatformNotification;

public interface PlatformNotificationListener
{
    boolean isAcceptable(PlatformNotificationTypes.NotificationObject eventType);

    void messageReceived(PlatformNotification message);
}
