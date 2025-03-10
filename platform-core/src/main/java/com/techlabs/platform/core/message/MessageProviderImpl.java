package com.techlabs.platform.core.message;

import java.util.Locale;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageProviderImpl implements MessageProvider, InitializingBean
{
    private MessageSource messageSource;

    public MessageProviderImpl(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }

    @Override
    public String getMessage(String messageCode, String... parameters)
    {
        try
        {
            return getMessageInternal(messageCode, parameters);
        } catch (RuntimeException ex)
        {
            return ex.getMessage();
        }
    }

    @Override
    public String getErrorMessage(String messageCode, String... parameters)
    {
        try
        {
            return "Error (" + messageCode + ") : " + getMessageInternal(messageCode, parameters);
        } catch (RuntimeException ex)
        {
            return ex.getMessage();
        }
    }

    private String getMessageInternal(String messageCode, String... parameters)
    {
        return getMessageInternal(messageCode, LocaleContextHolder.getLocale(), parameters);
    }

    private String getMessageInternal(String messageCode, Locale locale, String... parameters)
    {
        if (messageCode == null)
        {
            log.debug("Invalid Message Key. Key is null");
            throw new RuntimeException("Invalid Request. message key is null.");
        }
        try
        {
            return messageSource.getMessage(messageCode, parameters, locale);

        } catch (Exception e)
        {
            System.out.println(e);
            log.debug("Invalid Message Key : {}", messageCode);
            throw new RuntimeException("Unknown message key: " + messageCode);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception // NOSONAR
    {
    }
}
