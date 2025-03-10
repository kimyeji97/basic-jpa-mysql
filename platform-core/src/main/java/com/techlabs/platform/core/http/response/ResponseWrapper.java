package com.techlabs.platform.core.http.response;

import com.techlabs.platform.core.http.response.domain.SuccessResponse;
import org.springframework.http.ResponseEntity;

public interface ResponseWrapper
{
    ResponseEntity wrap(SuccessResponse val);
}
