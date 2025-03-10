package com.techlabs.platform.core.http.request.domain;

import com.techlabs.platform.core.http.response.domain.ErrorResponse;
import com.techlabs.platform.core.http.PlatformHttpStatus;
import lombok.Data;

@Data
public class ApiRequestCache
{
	private PlatformHttpStatus httpSttus;
	private String requestBody;
	private String responseBody;
	private ErrorResponse errorResponse;
}
