package com.xycf.generate.entity.excel;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @FileName ExcelListResponse
 * @Author lin.jiang
 * @Date 2023/2/20 11:26
 * @Description:
 */
@Data
public class ExcelListResponse implements Serializable {
	private List<ExcelEntity> sheetOneList;

	private List<ExcelEntity> sheetTwoList;
}
