package com.techlabs.common.base.http;

import com.techlabs.platform.core.http.PlatformHttpStatus;
import com.techlabs.common.base.http.exception.PlatformBadRequestException;
import com.techlabs.common.base.http.exception.PlatformHttpException;
import com.techlabs.platform.core.http.request.HttpRequestThreadLocal;
import com.techlabs.platform.core.http.response.domain.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RestExceptionHandler is only applied for RestController exceptions.
 *
 * @author yjkim
 */
@Slf4j
public class PlatformExceptionHandler
{
    @Autowired
    Environment env;
    
    @ExceptionHandler({DataIntegrityViolationException.class, BadSqlGrammarException.class})
    public ResponseEntity<ErrorResponse> handleDatabaseException(Exception exception)
    {
        log.error("DataIntegrityViolationException handler executed", exception);
        
        ErrorResponse errorResponse = new ErrorResponse();
        
        errorResponse.setErrorCode(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        errorResponse.setTitle(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        errorResponse.setErrorMessage("데이터베이스 오류 발생");
        
        String[] activeProfiles = env.getActiveProfiles();
        if (activeProfiles == null
            || activeProfiles.length == 0
            || List.of(activeProfiles).stream().anyMatch(o -> StringUtils.isEmpty(o)
                                                              || StringUtils.equalsAnyIgnoreCase(o, "dev"))
            )
        {
            List<String> detailMessageList = new ArrayList<>();
            detailMessageList.add(ExceptionUtils.getStackTrace(exception)); // TODO exceptionHandler : 개발 완료후 제거
            errorResponse.setDetails(detailMessageList);
        }
        
        HttpRequestThreadLocal.setRestApiResponse(PlatformHttpStatus.INTERNAL_SERVER_ERROR, errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.OK);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception)
    {
        log.error("Exception handler executed", exception);

        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setErrorCode(Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        errorResponse.setTitle(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        String message = StringUtils.isEmpty(exception.getMessage()) ? "Internal Server Error" : exception.getMessage() ; //exception.getMessage();

        errorResponse.setErrorMessage(message);
        
        String[] activeProfiles = env.getActiveProfiles();
        if (activeProfiles == null
            || activeProfiles.length == 0
            || List.of(activeProfiles).stream().anyMatch(o -> StringUtils.isEmpty(o)
                                                            || StringUtils.equalsAnyIgnoreCase(o, "dev"))
        )
        {
            List<String> detailMessageList = new ArrayList<>();
            detailMessageList.add(ExceptionUtils.getStackTrace(exception)); // TODO exceptionHandler : 개발 완료후 제거
            errorResponse.setDetails(detailMessageList);
        }

        HttpRequestThreadLocal.setRestApiResponse(PlatformHttpStatus.INTERNAL_SERVER_ERROR, errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.OK);
    }

    /**
     * [Macrogen] com.innerwave.Macrogen.core.exception.http.PlatformHttpException.class
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(PlatformHttpException.class)
    public ResponseEntity<ErrorResponse> handleHttpException(PlatformHttpException exception)
    {
        log.error("[Macrogen] PlatformHttpException handler executed", exception);

        ErrorResponse errorResponse = new ErrorResponse();
        if (StringUtils.isNotEmpty(exception.getErrorCode()))
        {
            errorResponse.setErrorCode(exception.getErrorCode());
        } else
        {
            errorResponse.setErrorCode(exception.getStatusCode().value() + "");
        }

        if (StringUtils.isEmpty(exception.getTitle()))
        {
            errorResponse.setTitle(exception.getStatusCode().getReasonPhrase());
        } else
        {
            errorResponse.setTitle(exception.getTitle());
        }

        errorResponse.setErrorMessage(exception.getMessage());

        List<String> detailMessageList = new ArrayList<>();

//        if (exception.getCause() != null)// TODO exceptionHandler : 개발 완료후 제거
//        {
//            detailMessageList.add(ExceptionUtils.getStackTrace(exception.getCause()));
//        }
        String[] activeProfiles = env.getActiveProfiles();
        log.info("{}", List.of(activeProfiles));
        if (exception.getCause() != null
            && (activeProfiles == null
            || activeProfiles.length == 0
            || List.of(activeProfiles).stream().anyMatch(o -> StringUtils.isEmpty(o)
                                                            || StringUtils.equalsAnyIgnoreCase(o, "dev")))
        )
        {
            detailMessageList.add(ExceptionUtils.getStackTrace(exception.getCause()));
        }
        

        if (exception.getDetails() != null && exception.getDetails().length > 0)
        {
            for (String detail : exception.getDetails())
            {
                detailMessageList.add(detail);
            }
        }
        errorResponse.setDetails(detailMessageList);

        HttpRequestThreadLocal.setRestApiResponse(PlatformHttpStatus.valueOf(exception.getStatusCode().value()),
            errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.OK);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpServletRequest request,
        HttpMessageNotReadableException exception)
    {
        
        logRequestBody(request);
        log.error("HttpMessageNotReadableException handler executed", exception);

        ErrorResponse errorMessage = new ErrorResponse();
        errorMessage.setErrorCode(Integer.toString(HttpStatus.BAD_REQUEST.value()));
        errorMessage.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
        String message = exception.getMessage();
        if (message == null || message.length() > 10)
        {
            message = exception.getClass().getSimpleName();
        }
        errorMessage.setErrorMessage(message);

        HttpRequestThreadLocal.setRestApiResponse(PlatformHttpStatus.BAD_REQUEST, errorMessage);
        return new ResponseEntity<>(errorMessage, HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(HttpServletRequest request,
        MethodArgumentNotValidException exception)
    {
        logRequestBody(request);
        BindingResult bindingResult = exception.getBindingResult();
        if (bindingResult.hasErrors())
        {
            String message = "Invalid Parameters";
            List<String> details = bindingResult.getAllErrors().stream().map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

            PlatformBadRequestException badRequestException = new PlatformBadRequestException(message, details);
            return handleHttpException(badRequestException);
        }

        HttpRequestThreadLocal.setRestApiResponse(PlatformHttpStatus.BAD_REQUEST);
        return handleException(exception);
    }
    
    private void logRequestBody(HttpServletRequest request)
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append("[METHOD] >>> ").append(request.getMethod())
        .append(" [URI] >>> ").append(request.getRequestURI())
        .append(" [PARAMS] >>> ").append(HttpRequestThreadLocal.getRestApiResponse().getRequestBody());
        log.error(sb.toString());
    }
}
