package com.xycf.generate.service.base;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.xycf.generate.config.exception.AppException;
import com.xycf.generate.entity.excel.ExcelEntity;
import com.xycf.generate.listener.ExcelDataListener;
import com.xycf.generate.listener.ExcelDataListener2;
import com.xycf.generate.listener.NoModelDataListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Objects;

/**
 * @FileName BaseExcelService
 * @Author lin.jiang
 * @Date 2023/2/20 13:37
 * @Description:
 */
@Component
public class BaseExcelService {
	private static final Logger logger = LoggerFactory.getLogger(BaseExcelService.class);
	@Autowired
	public ExcelDataListener excelDataListener;
	@Autowired
	public ExcelDataListener2 excelDataListener2;
	@Autowired
	public NoModelDataListener noModelDataListener;

	public void checkReadExcel(InputStream inputStream) {
		ExcelReader excelReader = null;
		try {
			excelReader = EasyExcel.read(inputStream).build();
			ReadSheet readOneSheet = EasyExcel.readSheet(0).head(ExcelEntity.class).registerReadListener(excelDataListener).build();
			ReadSheet readTwoSheet = EasyExcel.readSheet(1).head(ExcelEntity.class).registerReadListener(excelDataListener2).build();

			if (Objects.isNull(readOneSheet) && Objects.isNull(readTwoSheet)) {
				throw new AppException("未检测到两个sheet，请查看");
			}
			excelReader.read(readOneSheet, readTwoSheet);
		} catch (Exception e) {
			logger.error("文件读取失败：[{}}", e.getMessage());
		} finally {
			if (excelReader != null) {
				excelReader.finish();
			}
		}
	}
}
