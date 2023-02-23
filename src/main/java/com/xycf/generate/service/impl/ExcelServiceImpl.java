package com.xycf.generate.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.google.common.collect.Lists;
import com.xycf.generate.common.enums.RedisConstants;
import com.xycf.generate.common.enums.base.BaseResponseEnum;
import com.xycf.generate.common.req.OperationExcelReq;
import com.xycf.generate.config.exception.AppException;
import com.xycf.generate.entity.excel.ExcelEntity;
import com.xycf.generate.entity.excel.ExcelListResponse;
import com.xycf.generate.listener.ExcelDataListener;
import com.xycf.generate.operator.RedisOperator;
import com.xycf.generate.service.ExcelService;
import com.xycf.generate.service.base.BaseExcelService;
import com.xycf.generate.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

	@Resource
	private RedisOperator redisOperator;

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
		String key = IdUtil.fastUUID();
		redisOperator.setExcelDataSheet1(key,listenerOneList);
		redisOperator.setExcelDataSheet2(key,listenerTwoList);
		return buildSuccessListResponse(key,resultOne, resultTwo);
	}

	/**
	 * 操作excel表格数据
	 * @param req
	 */
	@Override
	public void operationExcel(OperationExcelReq req) {
		switch (req.getOperation()){
			case 0:
				//增
				if(ObjectUtil.isNotEmpty(req.getExcel())){
					List<ExcelEntity> excelEntities = Arrays.asList(req.getExcel());
					switch (req.getSheetNum()){
						case 1:
							redisOperator.setExcelDataSheet1(req.getKey(), excelEntities);
							break;
						case 2:
							redisOperator.setExcelDataSheet2(req.getKey(), excelEntities);
							break;
						default:
							logger.info("错误操作，入参sheet序列号为：{}",req.getSheetNum());
							throw new AppException(BaseResponseEnum.FAIL);
					}
				}
				break;
			case 1:
				//删
				break;
			case 2:
				//改
				break;
			default:
				logger.info("无法识别的操作类型：{}",req.getOperation());
		}
	}

	@Override
	public void download(HttpServletResponse response, HttpServletRequest request) {

	}

	private static ExcelListResponse buildSuccessListResponse(String key,List<ExcelEntity> excel, List<ExcelEntity> excel2) {
		ExcelListResponse excelListResponse = new ExcelListResponse();
        excelListResponse.setSheetOneList(excel);
        excelListResponse.setSheetTwoList(excel2);
        excelListResponse.setKey(key);
        return excelListResponse;
	}
}
