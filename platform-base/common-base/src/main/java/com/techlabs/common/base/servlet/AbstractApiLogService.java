package com.techlabs.common.base.servlet;

import com.techlabs.common.base.service.BaseService;

/**
 * API Log 를 상위에 정의하기 위한 클래스. 각 service별로 용도에 맞게 구현해서 사용할 것!
 * 
 * @author jajakk
 *
 */
public abstract class AbstractApiLogService extends BaseService
{
    public void insertApiLog(ApiLogBase apiLog)
    {
        recordApiLog(apiLog);
    }

    public abstract void recordApiLog(ApiLogBase apiLog);
}
