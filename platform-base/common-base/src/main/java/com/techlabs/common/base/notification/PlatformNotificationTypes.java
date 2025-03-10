package com.techlabs.common.base.notification;

public interface PlatformNotificationTypes {
    enum NotificationObject {
        // @formatter:off
        unknown
        , account
        , code
        ;
    	// formatter:on
    }

    enum Operation {
        Unknown, Created, Deleted, Updated, Exception
    }

    enum COLLECTOR_TYPE
    {
    	HTTP, SNMP, SYSLOG
    }
}