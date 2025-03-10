package com.techlabs.common.base.service;

import com.techlabs.common.base.http.exception.PlatformBadRequestException;
import com.techlabs.common.base.http.exception.PlatformServiceUnavailableException;
import com.techlabs.common.base.utill.validator.StringValidUtil;
import com.techlabs.platform.core.message.MessageProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class BaseService
{
    @Autowired
    protected MessageProvider messageProvider;

    public String getMessage(String messageCode, String... params)
    {
        return messageProvider.getMessage(messageCode, params);
    }

    public void assertNotNull(Object target, String... params)
    {
        if (target == null)
        {
            throw new PlatformBadRequestException(getMessage("LUCKY-0100", params));
        }
    }

    public void assertNotBlank(Object target, String... params)
    {
        if (target == null || StringUtils.isBlank(target.toString()))
        {
            throw new PlatformBadRequestException(getMessage("LUCKY-0101", params));
        }
    }

    public void assertNotEmpty(Object target, String... params)
    {
        if (ObjectUtils.isEmpty(target))
        {
            throw new PlatformBadRequestException(getMessage("LUCKY-0101", params));
        }
    }

    protected void assertNotEmpty(List<?> target, String... params)
    {
        if (target == null || target.isEmpty())
        {
            throw new PlatformBadRequestException(getMessage("LUCKY-0101", params));
        }
    }

    protected void assertNotEmpty(Object[] target, String... params)
    {
        if (target == null || target.length == 0)
        {
            throw new PlatformBadRequestException(getMessage("LUCKY-0101", params));
        }
    }

    protected void assertNotEmptyOr(Object... params)
    {
        if (params.length % 2 == 1)
        {
            throw new PlatformServiceUnavailableException(getMessage("LUCKY-0103"));
        }

        boolean notAllNull = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.length; i = i + 2)
        {
            sb.append(params[i + 1].toString());
            if (ObjectUtils.isNotEmpty(params[i]))
            {
                notAllNull = true;
                break;
            }
        }

        if (!notAllNull)
        {
            sb = sb.deleteCharAt(sb.length() - 1);
            throw new PlatformBadRequestException(getMessage("LUCKY-0102", sb.toString()));
        }
    }

    protected void assertIn(Object target, Object[] container, String... params)
    {
        List<Object> list = Arrays.asList(container);
        if (!list.contains(target))
        {
            throw new PlatformBadRequestException(getMessage("LUCKY-0104", params));
        }
    }

    protected void assertMaxLength(String str, int max, String param)
    {
        boolean valid = StringValidUtil.checkMaxLength(str, max);
        if(valid == false)
        {
            throw new PlatformBadRequestException(getMessage("LUCKY-0107",param, String.valueOf(max)));
        }
    }

    protected void assertMinLength(String str, int min, String param)
    {
        boolean valid = StringValidUtil.checkMinLength(str, min);
        if(valid == false)
        {
            throw new PlatformBadRequestException(getMessage("LUCKY-0108",param, String.valueOf(min)));
        }
    }
}
