package com.xycf.generate.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.google.common.collect.Lists;
import com.xycf.generate.config.exception.AppException;
import com.xycf.generate.entity.excel.ExcelEntity;
import com.xycf.generate.entity.excel.ExcelListResponse;
import com.xycf.generate.listener.ExcelDataListener;
import com.xycf.generate.service.ExcelService;
import com.xycf.generate.service.base.BaseExcelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @FileName ExcelServiceImpl
 * @Author lin.jiang
 * @Date 2023/2/20 13:37
 * @Description:
 */
@Service
@SuppressWarnings("all")
public class ExcelServiceImpl extends BaseExcelService implements ExcelService {
	private static final Logger logger = LoggerFactory.getLogger(ExcelServiceImpl.class);
	@Override
	public ExcelListResponse getExcelListResponse(MultipartFile file) throws IOException {
		logger.info("读取文件开始:[{reading}]");
		InputStream inputStream = file.getInputStream();
		try {
			super.checkReadExcel(inputStream);
		} catch (Exception e) {
			logger.error("Error reading :[{}]", e.getMessage());
			return null;
		}
		List<ExcelEntity> listenerOneList = excelDataListener.getList();
		List<ExcelEntity> listenerTwoList = excelDataListener2.getList();
		logger.info("ExcelDataListener sheet:[1] getList: " + listenerOneList);
		logger.info("ExcelDataListener sheet:[2] getList: " + listenerTwoList);
		List<ExcelEntity> resultOne = Lists.newArrayList(listenerOneList);
		List<ExcelEntity> resultTwo = Lists.newArrayList(listenerTwoList);
		excelDataListener.doClearList();
		excelDataListener2.doClearList();
		return buildSuccessListResponse(resultOne, resultTwo);
	}

	private static ExcelListResponse buildSuccessListResponse(List<ExcelEntity> excel, List<ExcelEntity> excel2) {
		ExcelListResponse excelListResponse = new ExcelListResponse();
        excelListResponse.setSheetOneList(excel);
        excelListResponse.setSheetTwoList(excel2);
        return excelListResponse;
	}
}
