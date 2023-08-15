package com.xycf.generate.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.ListUtils;
import com.xycf.generate.common.enums.RedisConstants;
import com.xycf.generate.common.enums.base.BaseResponseEnum;
import com.xycf.generate.common.req.OperationExcelReq;
import com.xycf.generate.config.exception.AppException;
import com.xycf.generate.entity.excel.ExcelEntity;
import com.xycf.generate.entity.excel.ExcelListResponse;
import com.xycf.generate.listener.ExcelDataListener;
import com.xycf.generate.listener.NoModelDataListener;
import com.xycf.generate.operator.RedisOperator;
import com.xycf.generate.service.ExcelService;
import com.xycf.generate.service.base.BaseExcelService;
import com.xycf.generate.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

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
        logger.info("读取excel完成");
        List<ExcelEntity> resultOne = new ArrayList<>(listenerOneList);
        List<ExcelEntity> resultTwo = new ArrayList<>(listenerTwoList);
        excelDataListener.doClearList();
        excelDataListener2.doClearList();
        String key = IdUtil.fastUUID();
        redisOperator.setExcelDataSheet1(key, listenerOneList);
        redisOperator.setExcelDataSheet2(key, listenerTwoList);
        logger.info("数据存储进缓存成功");
        return buildSuccessListResponse(key, resultOne, resultTwo);
    }

    /**
     * 操作excel表格数据
     *
     * @param req
     */
    @Override
    public void mergeExcel(OperationExcelReq req, HttpServletResponse response) {
        String excelDataSheet1Key = redisOperator.getExcelDataSheet1Key(req.getKey());
        String excelDataSheet2Key = redisOperator.getExcelDataSheet2Key(req.getKey());

        Integer columnToMerge = req.getColumnToMerge();
        //获得两个sheet页的数据
        List<ExcelEntity> excelDataSheet1 = redisOperator.getExcelDataSheet(excelDataSheet1Key);
        List<ExcelEntity> excelDataSheet2 = redisOperator.getExcelDataSheet(excelDataSheet2Key);
        List<ExcelEntity> res = new ArrayList<>();
        //将相同的数据合并
        for (ExcelEntity entity1 : excelDataSheet1) {
            String columnValue1 = entity1.getColumnValue(columnToMerge);
            if (StrUtil.isEmpty(columnValue1)) {
                continue;
            }
            for (ExcelEntity entity2 : excelDataSheet2) {
                String columnValue2 = entity2.getColumnValue(columnToMerge);
                if (StrUtil.isEmpty(columnValue2)) {
                    continue;
                }
                if (columnValue1.equals(columnValue2)) {
                    ExcelEntity excelEntity = new ExcelEntity();
                    BeanUtils.copyProperties(entity1, excelEntity);

                }
            }
        }

    }

    @Override
    public void mergeExcel(MultipartFile file,Integer columnToMerge, HttpServletResponse response) {
        try {
            EasyExcel.read(file.getInputStream(), noModelDataListener).sheet(0).doRead();
            List<Map<Integer, String>> list1 = noModelDataListener.getList();
            noModelDataListener.clear();
            EasyExcel.read(file.getInputStream(), noModelDataListener).sheet(1).doRead();
            List<Map<Integer, String>> list2 = noModelDataListener.getList();

            List<List<String>> res = new ArrayList<>();
            for (Map<Integer, String> map1 : list1) {
                String data1 = map1.get(columnToMerge);
                if(StrUtil.isEmpty(data1)){
                    continue;
                }
                for (Map<Integer, String> map2 : list2) {
                    String data2 = map2.get(columnToMerge);
                    if(StrUtil.isEmpty(data2)){
                        continue;
                    }
                    List<String> list = new ArrayList<>();
                    if(data1.equals(data2)){
                        list.addAll(map1.values());
                        list.addAll(map2.values());
                        res.add(list);
                    }
                }
            }
            // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + System.currentTimeMillis() + ".xlsx");
            EasyExcel.write(response.getOutputStream()).head(head(res)).sheet("模板").doWrite(res);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private List<List<String>> head(List<List<String>> res) {
        List<List<String>> list = ListUtils.newArrayList();
        if(CollUtil.isNotEmpty(res)){
            //获取第一行所有列
            List<String> headList = res.get(0);
            for (int i = 0; i < headList.size(); i++) {
                List<String> headNum = ListUtils.newArrayList();
                headNum.add("第"+(i+1)+"列");
                list.add(headNum);
            }
        }
        return list;
    }

    @Override
    public void download(HttpServletResponse response, HttpServletRequest request) {

    }

    private static ExcelListResponse buildSuccessListResponse(String key, List<ExcelEntity> excel, List<ExcelEntity> excel2) {
        ExcelListResponse excelListResponse = new ExcelListResponse();
        excelListResponse.setSheetOneList(excel);
        excelListResponse.setSheetTwoList(excel2);
        excelListResponse.setKey(key);
        return excelListResponse;
    }
}
