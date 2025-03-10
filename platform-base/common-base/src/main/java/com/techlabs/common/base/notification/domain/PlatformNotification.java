package com.techlabs.common.base.notification.domain;

import com.techlabs.common.base.notification.PlatformNotificationTypes;
import lombok.Data;

@Data
public class PlatformNotification
{
    private String notificationId;
    private PlatformNotificationTypes.NotificationObject notificationObject;
    private PlatformNotificationTypes.Operation operation;
    private String message;
    private String targetId;

    /**
     * Notification Resource 위치 정보.
     */
    private Link link;

    public PlatformNotification()
    {

    }

    public PlatformNotification(String notificationId, PlatformNotificationTypes.NotificationObject notificationObject)
    {
        this.notificationId = notificationId;
        this.notificationObject = notificationObject;
    }
}
