package com.techlabs.admin.base.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.techlabs.platform.core.http.request.HttpRequestThreadLocal;
import com.techlabs.platform.core.http.response.ResponseWrapper;
import com.techlabs.platform.core.http.response.domain.PaginationResponse;
import com.techlabs.platform.core.http.response.domain.SuccessResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * API Response를 생성하는데 사용합니다.
 * 각 사이트 또는 프로젝트에서 별도의 API Output유형이 필요한 경우 사이트에 맞는 {@link ResponseWrapper}를 구현하여 Bean으로 등록하여 사용 합니다.
 *
 *
 * <br />
 * @author yjkim
 */
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class AdminResponseBuilder implements InitializingBean
{
    @Autowired(required = false)
    private ResponseWrapper templateWrapper;
    
    private static ResponseWrapper wrapper;
    
    private final static ResponseWrapper defaultWrapper = val -> new ResponseEntity(val , HttpStatus.OK);
    
    public static ResponseEntity<SuccessResponse> build() {
        SuccessResponse val = new SuccessResponse();
        setResponseBodyThreadLocal(val);
        if (wrapper == null) {
            return defaultWrapper.wrap(val);
        } else {
            return wrapper.wrap(val);
        }
    }
    
    public static <T> ResponseEntity<SuccessResponse<List<T>>> build(Page<T> data) {
        SuccessResponse<List<T>> val = new SuccessResponse<>(data.getContent() , new PaginationResponse(data.getPageable() , data.getNumberOfElements() , (int) data.getTotalElements()));
        setResponseBodyThreadLocal(val);
        if (wrapper == null) {
            return defaultWrapper.wrap(val);
        } else {
            return wrapper.wrap(val);
        }
    }
    
    public static <T> ResponseEntity<SuccessResponse<T>> build(T data) {
        SuccessResponse<T> val = new SuccessResponse<T>(data);
        setResponseBodyThreadLocal(val);
        if (wrapper == null) {
            return defaultWrapper.wrap(val);
        } else {
            return wrapper.wrap(val);
        }
    }
    
    public static <T> ResponseEntity<SuccessResponse<T>> build(T data , PaginationResponse page) {
        SuccessResponse<T> val = new SuccessResponse<T>(data , page);
        setResponseBodyThreadLocal(val);
        if (wrapper == null) {
            return defaultWrapper.wrap(val);
        } else {
            return wrapper.wrap(val);
        }
    }
    
    public static <T> ResponseEntity<SuccessResponse<T>> build(SuccessResponse<T> val) {
        setResponseBodyThreadLocal(val);
        if (wrapper == null) {
            return defaultWrapper.wrap(val);
        } else {
            return wrapper.wrap(val);
        }
    }
    
    private static void setResponseBodyThreadLocal(Object val) {
        try {
            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            HttpRequestThreadLocal.setResponseBody(objectMapper.writeValueAsString(val));
        } catch (JsonProcessingException e) {
            //            log.error(e.getMessage() , e);
        }
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        AdminResponseBuilder.wrapper = templateWrapper;
    }
}
