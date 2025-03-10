package com.techlabs.common.base.data;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommonConst {
    ////////////////////////////////////////////////////////////////////////////
    /*
     * ************************
     * 날짜 형식
     * ************************
     */
    public static final String DEFAULT_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String MILLIS_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DT_FM_DATE_DOW = "yyyy.M.d (E)";
    public static final String DT_FM_DATETIME = "yyyy-MM-dd HH:mm:ss.S";
    public static final String DT_FM_DATE_MONTH = "yyyy-MM";
    public static final String DT_FM_DATE = "yyyy-MM-dd";
    public static final String DT_FM_DATE_DOT = "yyyy.MM.dd";
    public static final String DT_FM_WEEK_OF_MONTH_DOT = "yyyy.MM W주차";
    public static final String DT_FM_MONTH_DOT = "yyyy.MM";
    
    public static final String DT_FM_DATE_HHMM = "yyyy-MM-dd HH:mm";
    
    public static final String DT_FM_YYYYMMDD = "yyyyMMdd";
    public static final String DT_FM_YYYYMMDDHH24 = "yyyyMMddHH";
    public static final String DT_FM_YYYYMMDDHH24MI = "yyyyMMddHHmm";
    public static final String DT_FM_YYYYMMDDHH24MISS = "yyyyMMddHHmmss";
    public static final String VCDR_DT_DF = "MMddyyyyHHmmssSSS";
    
    public static final String PTN_TIMESTAMP = "yyyy-MM-dd HH:mm:ss";
    public static final String PTN_E2E_TIMESTAMP = "yyyyMMddHHmmss";
    public static final String PTN_DATE_HOUR = "yyyy-MM-dd HH";
    public static final String PTN_DATE = "yyyy-MM-dd";
    
    ////////////////////////////////////////////////////////////////////////////
    /*
     * ************************
     * 정규식
     * ************************
     */
    public static final String REX_TIMESTAMP = "\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}";
    public static final String REX_DATE = "\\d{4}-\\d{2}-\\d{2}";
    
    public static final String REGEX_ENTER = "[\r\n]+";
    
    ////////////////////////////////////////////////////////////////////////////
}
