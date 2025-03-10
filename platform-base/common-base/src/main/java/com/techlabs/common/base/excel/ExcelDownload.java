package com.techlabs.common.base.excel;

import java.util.Map;

public interface ExcelDownload {

	public String getName();
	
	public ExcelExporter makeExcel(String target, Map<String, String> params);

}
