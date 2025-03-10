package com.techlabs.common.base.excel;

import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class SheetSetting
{
    private static final Integer DEFAULT_WIDTH = 1000;

    public static enum CELL_TYPE
    {
        BASIC(0), NUMBER(0), PRICE(0), DATE(3000), DATETIME(3000);
//        BASIC, NUMBER, PRICE, DATE, DATETIME;
        
        private Integer width;

        CELL_TYPE(Integer width)
        {
            this.width = width;
        }

        public Integer getWidth()
        {
            return this.width;
        }
    }

    List<CellType> cellTypeList = new ArrayList<>();

    @Data
    class CellType
    {
        String header;
        String field;
        Integer width;
        CELL_TYPE type;

        CellType(String header, String field, Integer width, CELL_TYPE type)
        {
            this.header = header;
            this.width = width;
            this.field = field;
            this.type = type;
        }

        public Integer getWidth()
        {
            return NumberUtils.max((width == null ? 0 : width), (type == null ? 0 : type.getWidth()));
        }
    }

    public void addCellType(String header, String field)
    {
        addCellType(header, field, DEFAULT_WIDTH, null);
    }

    public void addCellType(String header, String field, Integer width)
    {
        addCellType(header, field, width, null);
    }

    public void addCellType(String header, String field, CELL_TYPE type)
    {
        addCellType(header, field, DEFAULT_WIDTH, type);
    }

    public void addCellType(String header, String field, Integer width, CELL_TYPE type)
    {
        cellTypeList.add(new CellType(header, field, width, type));
    }

    public List<String> getHeaderList()
    {
        return cellTypeList.stream().map(o -> o.getHeader()).collect(Collectors.toList());
    }
}
