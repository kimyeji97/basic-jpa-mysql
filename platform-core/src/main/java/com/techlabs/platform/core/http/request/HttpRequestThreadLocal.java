package com.techlabs.platform.core.http.request;

import com.techlabs.platform.core.http.PlatformHttpStatus;
import com.techlabs.platform.core.http.request.domain.ApiRequestCache;
import com.techlabs.platform.core.http.response.domain.ErrorResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpRequestThreadLocal
{

	private final static ThreadLocal<ApiRequestCache> restApiResponseThreadLocal = new ThreadLocal<ApiRequestCache>()
	{
		@Override
		protected ApiRequestCache initialValue()
		{
			return new ApiRequestCache();
		}

		;
	};

	public static ApiRequestCache getRestApiResponse()
	{
		return restApiResponseThreadLocal.get();
	}

	public static void setResponseBody(String responseBody)
    {
	    ApiRequestCache restApiResponse = restApiResponseThreadLocal.get();
        if (restApiResponse == null)
        {
            restApiResponse = new ApiRequestCache();
            restApiResponseThreadLocal.set(restApiResponse);
        }
        
        restApiResponse.setHttpSttus(PlatformHttpStatus.OK);
        restApiResponse.setResponseBody(responseBody);
    }
	
	public static void setRestApiResponse(PlatformHttpStatus httpSttus)
	{
		setRestApiResponse(httpSttus, null);
	}

	public static void setRestApiResponse(PlatformHttpStatus httpSttus, ErrorResponse errorResponse)
	{
		ApiRequestCache restApiResponse = restApiResponseThreadLocal.get();
		if (restApiResponse == null)
		{
			restApiResponse = new ApiRequestCache();
			restApiResponseThreadLocal.set(restApiResponse);
		}

		restApiResponse.setHttpSttus(httpSttus);
		restApiResponse.setErrorResponse(errorResponse);
	}

	public static void setRequestBody(String requestBody)
	{
		ApiRequestCache restApiResponse = restApiResponseThreadLocal.get();
		if (restApiResponse == null)
		{
			restApiResponse = new ApiRequestCache();
			restApiResponseThreadLocal.set(restApiResponse);
		}
		restApiResponse.setRequestBody(requestBody);
	}

	public static void remove()
	{
		restApiResponseThreadLocal.remove();
	}
}
