package com.xycf.generate.service;

import com.xycf.generate.entity.excel.ExcelEntity;
import com.xycf.generate.entity.excel.ExcelListResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @FileName ExcelService
 * @Author lin.jiang
 * @Date 2023/2/20 11:32
 * @Description:
 */
public interface ExcelService {
	/**
	 *  获取Excel列表
	 * @param file
	 * @return
	 */
	ExcelListResponse getExcelListResponse(MultipartFile file) throws IOException;

	void operationExcel(ExcelEntity excel);

	void download(HttpServletResponse response, HttpServletRequest request);
}
