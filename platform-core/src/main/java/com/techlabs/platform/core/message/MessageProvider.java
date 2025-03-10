package com.techlabs.platform.core.message;

public interface MessageProvider
{
    public String getMessage(String messageCode, String... parameters);

    public String getErrorMessage(String messageCode, String... parameters);
}
