package com.techlabs.platform.core.http.response.domain;

import lombok.Data;

import java.util.List;

@Data
public class ErrorResponse
{
    private final String resultCode = "F";
    private String errorCode;

    private String title;
    private String errorMessage;
    private List<String> details;
}
