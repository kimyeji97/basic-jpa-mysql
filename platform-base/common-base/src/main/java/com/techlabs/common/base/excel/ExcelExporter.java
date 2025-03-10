package com.techlabs.common.base.excel;

import com.techlabs.common.base.data.CommonConst;
import com.techlabs.common.base.excel.SheetSetting.CellType;
import com.techlabs.common.base.excel.SheetSetting.CELL_TYPE;
import com.techlabs.common.base.utill.RestUtil;
import com.techlabs.platform.core.domain.CommonCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// @Service
@Slf4j
public class ExcelExporter
{
    private static final String DEFAULT_FILENAME = "excel_";
    private static final String EXCEL_EXT = ".xlsx";

    // public static final String PRICE = "PRICE";
    // public static final String NUMBER = "NUMBER";
    // public static final String DATE = "DATE";
    // public static final String DATETIME = "DATETIME";

    private SXSSFWorkbook workbook;

    // CellStyle headerStyle;
    // CellStyle bodyStyleBasic;
    // CellStyle bodyStylePrice;
    // CellStyle bodyStyleNumber;
    // CellStyle bodyStyleDateTime;
    // CellStyle bodyStyleDate;
    // CellStyle summaryStyle;
    public static final String HEADER_STYLE = "HEADER_STYLE";
    public static final String BODY_STYLE_BASIC = "BODY_STYLE_BASIC";
    public static final String BODY_STYLE_PRICE = "BODY_STYLE_PRICE";
    public static final String BODY_STYLE_NUMBER = "BODY_STYLE_NUMBER";
    public static final String BODY_STYLE_DATETIME = "BODY_STYLE_DATETIME";
    public static final String BODY_STYLE_DATE = "BODY_STYLE_DATE";
    public static final String SUMMARY_STYLE = "SUMMARY_STYLE";

    Map<String, CellStyle> cellStyleMap;
    String fileName;
    Date startDt;

    public ExcelExporter()
    {
        workbook = new SXSSFWorkbook();
        startDt = new Date();
        cellStyleMap = new HashMap<>();
        setStyle(workbook);
    }

    public ExcelExporter(String fileName)
    {
        this.fileName = fileName;
        workbook = new SXSSFWorkbook();
        startDt = new Date();
        cellStyleMap = new HashMap<>();
        setStyle(workbook);
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String writeData(SheetSetting sheetSet, List<?> list)
    {
        return writeData(null, sheetSet, list);
    }

    public String writeData(String sheetName, SheetSetting sheetSet, List<?> list)
    {
        SXSSFSheet sheet = null;

        if (StringUtils.isEmpty(sheetName))
        {
            sheet = workbook.createSheet();
        } else
        {
            sheet = workbook.createSheet(sheetName);
        }
        sheet.trackAllColumnsForAutoSizing();

        int rowIndex = 0;
        int colIndex = 0;

        Row row = sheet.createRow(rowIndex++);
        for (CellType ct : sheetSet.getCellTypeList())
        {
            Cell cell = row.createCell(colIndex);
            cell.setCellValue(ct.getHeader());
            cell.setCellStyle(cellStyleMap.get(HEADER_STYLE));
            colIndex++;
        }

        Map<String, Method> methodMap = new HashMap<>();
        if (list.isEmpty() == false)
        {
            Object obj = list.get(0);
            Set<String> fList = new HashSet<>();
            fList.addAll(getAllFields(obj.getClass()));
            for (CellType ct : sheetSet.getCellTypeList())
            {
                setMethod(methodMap, obj, fList, ct.getField());
            }
        }

        for (Object obj : list)
        {
            row = sheet.createRow(rowIndex++);

            colIndex = 0;
            for (CellType ct : sheetSet.getCellTypeList())
            {
                CELL_TYPE type = ct.getType();
                String field = ct.getField();

                Cell cell = row.createCell(colIndex++);

                setCellValue(methodMap, obj, type, field, cell);
            }

            if (rowIndex % 10000 == 0)
            {
                try
                {
                    ((SXSSFSheet) sheet).flushRows(10000);
                } catch (IOException e)
                {
                    log.warn(e.getMessage(), e);
                }
            }

        }

        colIndex = 0;
        for (CellType ct : sheetSet.getCellTypeList())
        {
            // if (ct.getWidth() != null)
            // {
            // sheet.setColumnWidth(colIndex, ct.getWidth());
            // } else
            // {
            // sheet.autoSizeColumn(colIndex);
            // }
            //
            sheet.setColumnWidth(colIndex, sheet.getColumnWidth(colIndex) + ct.getWidth());
            sheet.autoSizeColumn(colIndex);
            colIndex++;
        }

        return sheet.getSheetName();
    }

    @FunctionalInterface
    public interface Processor
    {
        void process(SXSSFSheet sheet, Map<String, CellStyle> cellStyleMap);
    }

    public void customWriteData(String sheetName, Processor processor)
    {
        SXSSFSheet sheet = workbook.getSheet(sheetName);
        processor.process(sheet, cellStyleMap);
    }

    private Set<String> getAllFields(Class<? extends Object> clz)
    {
        Set<String> fList = new HashSet<>();

        if (clz != null)
        {
            Field[] fields = clz.getDeclaredFields();

            fList.addAll(Stream.of(fields).filter(o -> fList.contains(o.getName()) == false).map(o -> o.getName())
                .collect(Collectors.toList()));
            fList.addAll(getAllFields(clz.getSuperclass()));
        }
        return fList;
    }

    private void setMethod(Map<String, Method> methodMap, Object obj, Set<String> fList, String field)
    {
        setMethod(methodMap, obj, fList, field, null);
    }

    private void setMethod(Map<String, Method> methodMap, Object obj, Set<String> fList, String field,
        String beforeField)
    {
        try
        {
            if (StringUtils.contains(field, "."))
            {
                String prefix = StringUtils.substringBefore(field, ".");
                String suffix = StringUtils.substringAfter(field, ".");

                if (fList.contains(prefix) == false)
                {
                    return;
                }

                String methodName = getMethodName(prefix);
                Method runMethod = obj.getClass().getMethod(methodName);

                String fullField =
                    StringUtils.isEmpty(beforeField) ? prefix : StringUtils.join(beforeField, ".", prefix);
                methodMap.put(fullField, runMethod);

                Object subObj = runMethod.invoke(obj);

                if (subObj == null)
                {
                    return;
                }

                Set<String> subFList = getAllFields(subObj.getClass());
                setMethod(methodMap, subObj, subFList, suffix, fullField);
            } else
            {
                if (fList.contains(field) == false)
                {
                    return;
                }
                String methodName = getMethodName(field);
                Method runMethod = obj.getClass().getMethod(methodName);

                String fullField = StringUtils.isEmpty(beforeField) ? field : StringUtils.join(beforeField, ".", field);
                methodMap.put(fullField, runMethod);
            }

        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException e)
        {
            log.error(e.getMessage(), e);
        }
    }

    private void setCellValue(Map<String, Method> methodMap, Object obj, CELL_TYPE type, String field, Cell cell)
    {
        setCellValue(methodMap, obj, type, field, null, cell);
    }

    private void setCellValue(Map<String, Method> methodMap, Object obj, CELL_TYPE type, String field,
        String beforeField, Cell cell)
    {
        try
        {
            if (StringUtils.contains(field, "."))
            {
                String prefix = StringUtils.substringBefore(field, ".");
                String suffix = StringUtils.substringAfter(field, ".");

                String fullField =
                    StringUtils.isEmpty(beforeField) ? prefix : StringUtils.join(beforeField, ".", prefix);
                if (methodMap.containsKey(fullField) == false)
                {
                    cell.setCellStyle(cellStyleMap.get(BODY_STYLE_BASIC));
                    return;
                }
                Method runMethod = methodMap.get(fullField);
                Object subObj = runMethod.invoke(obj);
                setCellValue(methodMap, subObj, type, suffix, fullField, cell);
            } else
            {
                String fullField = StringUtils.isEmpty(beforeField) ? field : StringUtils.join(beforeField, ".", field);

                if (obj == null || methodMap.containsKey(fullField) == false)
                {
                    cell.setCellStyle(cellStyleMap.get(BODY_STYLE_BASIC));
                    return;
                }
                Method runMethod = methodMap.get(fullField);
                Object value = runMethod.invoke(obj);

                if (value instanceof Boolean)
                {
                    cell.setCellStyle(cellStyleMap.get(BODY_STYLE_BASIC));
                    cell.setCellValue((Boolean) value ? "Y" : "N");
                } else if (value instanceof Integer || value instanceof Long || value instanceof Double || value instanceof BigDecimal)
                {
                    if (type == CELL_TYPE.PRICE)
                    {
                        cell.setCellStyle(cellStyleMap.get(BODY_STYLE_PRICE));

                    } else if (type == CELL_TYPE.NUMBER)
                    {
                        cell.setCellStyle(cellStyleMap.get(BODY_STYLE_NUMBER));
                    } else
                    {
                        cell.setCellStyle(cellStyleMap.get(BODY_STYLE_BASIC));
                    }

                    if (value instanceof Integer)
                    {
                        cell.setCellValue((Integer) value);
                    } else if (value instanceof Long)
                    {
                        cell.setCellValue((Long) value);
                    } else if (value instanceof Double)
                    {
                        cell.setCellValue((Double) value);
                    } else if (value instanceof BigDecimal)
                    {
                        cell.setCellValue(((BigDecimal) value).doubleValue());
                    }
                } else if (value instanceof Date)
                {
                    if (type == CELL_TYPE.DATE)
                    {
                        cell.setCellStyle(cellStyleMap.get(BODY_STYLE_DATE));
                        cell.setCellValue(com.techlabs.common.base.utill.DateUtil.formatDate((Date) value, CommonConst.DT_FM_DATE_MONTH));
                    } else
                    {
                        cell.setCellStyle(cellStyleMap.get(BODY_STYLE_DATETIME));
                        cell.setCellValue(com.techlabs.common.base.utill.DateUtil.formatDate((Date) value, CommonConst.DEFAULT_TIMESTAMP_FORMAT));
                    }
                } else if (value instanceof CommonCode)
                {
                    cell.setCellStyle(cellStyleMap.get(BODY_STYLE_BASIC));
                    cell.setCellValue(((CommonCode) value).getValue());
                } else if (value instanceof String)
                {
                    if (type == CELL_TYPE.DATETIME)
                    {
                        cell.setCellStyle(cellStyleMap.get(BODY_STYLE_DATETIME));
                    } else if (type == CELL_TYPE.DATE)
                    {
                        cell.setCellStyle(cellStyleMap.get(BODY_STYLE_DATE));
                    } else
                    {
                        cell.setCellStyle(cellStyleMap.get(BODY_STYLE_BASIC));
                    }
                    cell.setCellValue((String) value);
                } else
                {
                    cell.setCellStyle(cellStyleMap.get(BODY_STYLE_BASIC));
                }
            }
        } catch (IllegalAccessException e)
        {
            log.error(e.getMessage(), e);
        } catch (IllegalArgumentException e)
        {
            log.error(e.getMessage(), e);
        } catch (InvocationTargetException e)
        {
            log.error(e.getMessage(), e);
        } catch (SecurityException e)
        {
            log.error(e.getMessage(), e);
        }
    }

    private String getMethodName(String field)
    {

        return StringUtils.join("get", StringUtils.capitalize(field));
    }

    public void setStyle(SXSSFWorkbook workbook)
    {
        // 1.셀 스타일 및 폰트 설정
        CellStyle headerStyle = workbook.createCellStyle();
        // 정렬
        headerStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 높이 가운데 정렬

        // 배경
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index); // 색 설정
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 색 패턴 설정

        // 테두리 선 (우,좌,위,아래)
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        // 폰트 설정
        Font headerFont = workbook.createFont();
        headerFont.setFontName("Verdana"); // 글씨체
        headerFont.setFontHeightInPoints((short) 10); // 사이즈
        headerFont.setBold(true); // 볼드 (굵게)
        headerStyle.setWrapText(true);
        headerStyle.setFont(headerFont);

        cellStyleMap.put(HEADER_STYLE, headerStyle);

        // 1.셀 스타일 및 폰트 설정
        CellStyle bodyStyleBasic = workbook.createCellStyle();
        // 정렬
        bodyStyleBasic.setAlignment(HorizontalAlignment.LEFT); // 가운데 정렬
        bodyStyleBasic.setVerticalAlignment(VerticalAlignment.CENTER); // 높이 가운데 정렬

        // 테두리 선 (우,좌,위,아래)
        bodyStyleBasic.setBorderRight(BorderStyle.THIN);
        bodyStyleBasic.setBorderLeft(BorderStyle.THIN);
        bodyStyleBasic.setBorderTop(BorderStyle.THIN);
        bodyStyleBasic.setBorderBottom(BorderStyle.THIN);
        // 폰트 설정
        Font bodyBasicFont = workbook.createFont();
        bodyBasicFont.setFontName("Verdana"); // 글씨체
        bodyBasicFont.setFontHeightInPoints((short) 9); // 사이즈
        bodyStyleBasic.setWrapText(true);
        bodyStyleBasic.setFont(bodyBasicFont);

        cellStyleMap.put(BODY_STYLE_BASIC, bodyStyleBasic);

        // 1.셀 스타일 및 폰트 설정
        CellStyle bodyStylePrice = workbook.createCellStyle();
        // 정렬
        bodyStylePrice.setAlignment(HorizontalAlignment.RIGHT); // 가운데 정렬
        bodyStylePrice.setVerticalAlignment(VerticalAlignment.CENTER); // 높이 가운데 정렬

        // 테두리 선 (우,좌,위,아래)
        bodyStylePrice.setBorderRight(BorderStyle.THIN);
        bodyStylePrice.setBorderLeft(BorderStyle.THIN);
        bodyStylePrice.setBorderTop(BorderStyle.THIN);
        bodyStylePrice.setBorderBottom(BorderStyle.THIN);
        // 폰트 설정
        Font bodyPriceFont = workbook.createFont();
        bodyPriceFont.setFontName("Verdana"); // 글씨체
        bodyPriceFont.setFontHeightInPoints((short) 9); // 사이즈
        bodyStylePrice.setWrapText(true);
        bodyStylePrice.setFont(bodyPriceFont);

        bodyStylePrice.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

        cellStyleMap.put(BODY_STYLE_PRICE, bodyStylePrice);

        // 1.셀 스타일 및 폰트 설정
        CellStyle bodyStyleNumber = workbook.createCellStyle();
        // 정렬
        bodyStyleNumber.setAlignment(HorizontalAlignment.RIGHT); // 가운데 정렬
        bodyStyleNumber.setVerticalAlignment(VerticalAlignment.CENTER); // 높이 가운데 정렬

        // 테두리 선 (우,좌,위,아래)
        bodyStyleNumber.setBorderRight(BorderStyle.THIN);
        bodyStyleNumber.setBorderLeft(BorderStyle.THIN);
        bodyStyleNumber.setBorderTop(BorderStyle.THIN);
        bodyStyleNumber.setBorderBottom(BorderStyle.THIN);
        // 폰트 설정
        Font bodyNumberFont = workbook.createFont();
        bodyNumberFont.setFontName("Verdana"); // 글씨체
        bodyNumberFont.setFontHeightInPoints((short) 9); // 사이즈
        bodyStyleNumber.setWrapText(true);
        bodyStyleNumber.setFont(bodyNumberFont);

        cellStyleMap.put(BODY_STYLE_NUMBER, bodyStyleNumber);

        // 1.셀 스타일 및 폰트 설정
        CellStyle bodyStyleDatetime = workbook.createCellStyle();
        // 정렬
        bodyStyleDatetime.setAlignment(HorizontalAlignment.RIGHT); // 가운데 정렬
        bodyStyleDatetime.setVerticalAlignment(VerticalAlignment.CENTER); // 높이 가운데 정렬

        // 테두리 선 (우,좌,위,아래)
        bodyStyleDatetime.setBorderRight(BorderStyle.THIN);
        bodyStyleDatetime.setBorderLeft(BorderStyle.THIN);
        bodyStyleDatetime.setBorderTop(BorderStyle.THIN);
        bodyStyleDatetime.setBorderBottom(BorderStyle.THIN);
        // 폰트 설정
        Font bodyDateTimeFont = workbook.createFont();
        bodyDateTimeFont.setFontName("Verdana"); // 글씨체
        bodyDateTimeFont.setFontHeightInPoints((short) 9); // 사이즈
        bodyStyleDatetime.setWrapText(true);
        bodyStyleDatetime.setFont(bodyDateTimeFont);

        CreationHelper creationHelper = workbook.getCreationHelper();

        bodyStyleDatetime.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));

        cellStyleMap.put(BODY_STYLE_DATETIME, bodyStyleDatetime);

        // 1.셀 스타일 및 폰트 설정
        CellStyle bodyStyleDate = workbook.createCellStyle();
        // 정렬
        bodyStyleDate.setAlignment(HorizontalAlignment.RIGHT); // 가운데 정렬
        bodyStyleDate.setVerticalAlignment(VerticalAlignment.CENTER); // 높이 가운데 정렬

        // 테두리 선 (우,좌,위,아래)
        bodyStyleDate.setBorderRight(BorderStyle.THIN);
        bodyStyleDate.setBorderLeft(BorderStyle.THIN);
        bodyStyleDate.setBorderTop(BorderStyle.THIN);
        bodyStyleDate.setBorderBottom(BorderStyle.THIN);
        // 폰트 설정
        Font bodyDateFont = workbook.createFont();
        bodyDateFont.setFontName("Verdana"); // 글씨체
        bodyDateFont.setFontHeightInPoints((short) 9); // 사이즈
        bodyStyleDate.setWrapText(true);
        bodyStyleDate.setFont(bodyDateFont);

        bodyStyleDate.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-mm-dd"));

        cellStyleMap.put(BODY_STYLE_DATE, bodyStyleDate);

        // 1.셀 스타일 및 폰트 설정
        CellStyle summaryStyle = workbook.createCellStyle();
        // 정렬
        summaryStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬
        summaryStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 높이 가운데 정렬

        // 배경
        summaryStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index); // 색 설정
        summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 색 패턴 설정

        // 테두리 선 (우,좌,위,아래)
        summaryStyle.setBorderRight(BorderStyle.THIN);
        summaryStyle.setBorderLeft(BorderStyle.THIN);
        summaryStyle.setBorderTop(BorderStyle.THIN);
        summaryStyle.setBorderBottom(BorderStyle.THIN);
        // 폰트 설정
        Font summaryFont = workbook.createFont();
        summaryFont.setFontName("Verdana"); // 글씨체
        summaryFont.setFontHeightInPoints((short) 10); // 사이즈
        summaryFont.setBold(true); // 볼드 (굵게)
        summaryStyle.setWrapText(true);
        summaryStyle.setFont(summaryFont);

        cellStyleMap.put(SUMMARY_STYLE, summaryStyle);
    }

    public void export(HttpServletResponse response) throws IOException
    {
        if (StringUtils.isEmpty(fileName))
        {
            fileName = DEFAULT_FILENAME;
        }

        fileName = StringUtils.join(fileName, com.techlabs.common.base.utill.DateUtil.now().format(CommonConst.DT_FM_YYYYMMDDHH24MI), EXCEL_EXT);

        RestUtil.setBinaryFileHeader(response, fileName);
        workbook.write(response.getOutputStream());
        response.flushBuffer();

        // workbook.dispose();
        IOUtils.closeQuietly(workbook);

        long diff = com.techlabs.common.base.utill.DateUtil.now().diff(com.techlabs.common.base.utill.DateUtil.MILLISECOND, startDt);

        log.info("Excel Download end : {} ms", diff);
    }

    public void export(HttpServletResponse response, String lockPwd) throws IOException
    {
        if (StringUtils.isEmpty(fileName))
        {
            fileName = DEFAULT_FILENAME;
        }

        fileName = StringUtils.join(fileName, com.techlabs.common.base.utill.DateUtil.now().format(CommonConst.DT_FM_YYYYMMDD), EXCEL_EXT);

        ByteArrayOutputStream fileOut = null;
        InputStream filein = null;
        OPCPackage opc = null;
        POIFSFileSystem fileSystem = null;

        try
        {
            EncryptionInfo encryptionInfo = new EncryptionInfo(EncryptionMode.agile);
            Encryptor encryptor = encryptionInfo.getEncryptor();
            encryptor.confirmPassword(lockPwd);

            fileOut = new ByteArrayOutputStream();
            workbook.write(fileOut);

            filein = new ByteArrayInputStream(fileOut.toByteArray());
            opc = OPCPackage.open(filein);
            fileSystem = new POIFSFileSystem();

            opc.save(encryptor.getDataStream(fileSystem));

            RestUtil.setBinaryFileHeader(response, fileName);
            fileSystem.writeFilesystem(response.getOutputStream());
        } catch (InvalidFormatException e)
        {
            log.error(e.getMessage(), e);
        } catch (IOException e)
        {
            log.error(e.getMessage(), e);
        } catch (GeneralSecurityException e)
        {
            log.error(e.getMessage(), e);
        } finally
        {
            IOUtils.closeQuietly(fileSystem);
            IOUtils.closeQuietly(opc);
            IOUtils.closeQuietly(filein);
            IOUtils.closeQuietly(fileOut);

            response.flushBuffer();
            IOUtils.closeQuietly(workbook);

            long diff = com.techlabs.common.base.utill.DateUtil.now().diff(com.techlabs.common.base.utill.DateUtil.MILLISECOND, startDt);
            log.info("Excel Download end : {} ms", diff);
        }
    }
}
