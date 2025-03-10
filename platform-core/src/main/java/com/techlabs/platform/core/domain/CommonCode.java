package com.techlabs.platform.core.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

@JsonFormat(shape = Shape.OBJECT)
public interface CommonCode
{
    default Long getPcode() {
    	return null;
    }

    Long getCode();

    String getValue();
}
