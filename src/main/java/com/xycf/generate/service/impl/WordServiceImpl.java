package com.xycf.generate.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.spire.doc.*;
import com.spire.doc.collections.CellCollection;
import com.spire.doc.collections.RowCollection;
import com.spire.doc.collections.SectionCollection;
import com.spire.doc.collections.TableCollection;
import com.spire.doc.documents.Paragraph;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.spire.doc.*;
import com.spire.doc.collections.*;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.fields.TextRange;
import com.xycf.generate.common.enums.RedisConstants;
import com.xycf.generate.common.enums.base.DocModelEnum;
import com.xycf.generate.config.DocConfig;
import com.xycf.generate.config.exception.AppException;
import com.xycf.generate.entity.ClassEntry;
import com.xycf.generate.entity.FieldEntry;
import com.xycf.generate.entity.InterfaceBean;
import com.xycf.generate.entity.*;
import com.xycf.generate.operator.ClassOperator;
import com.xycf.generate.service.UploadService;
import com.xycf.generate.service.WordService;
import com.xycf.generate.util.FileUtil;
import com.xycf.generate.util.RedisUtils;
import io.swagger.models.auth.In;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @Author ztc
 * @Description word文档处理实现类
 * @Date 2023/1/31 14:09
 */
@Service
@Slf4j
public class WordServiceImpl implements WordService {

    @Resource
    private DocConfig docConfig;
    @Resource
    private UploadService uploadService;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private ClassOperator classOperator;

    /**
     * 将上传的文件转换成xml文档
     *
     * @param file 上传的word文件
     * @return xml文件路径
     */
    public void changeToXml(File file) {
        Document document = new Document();
        BufferedInputStream bis = null;
        String originalFilename = file.getName();
        if (StrUtil.isEmpty(originalFilename)) {
            throw new RuntimeException("文件名不合法!");
        }
        String fileName = originalFilename.substring(0, originalFilename.indexOf("."));
        String xmlFileName = docConfig.getXmlFilePath() + File.separator + fileName + ".xml";
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            document.loadFromStream(bis, FileFormat.Word_Xml);
            document.saveToFile(xmlFileName);
            document.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("word转换xml文档成功!");
    }

    @Override
    public void parseXml(String filePath) {
//        try {
//            // 创建SAXReader对象
//            SAXReader reader = new SAXReader();
//            // 加载xml文件
//            org.dom4j.Document dc= reader.read(new File(filePath));
//             //获取根节点
//            Element eroot = dc.getRootElement();
//            Element sections = eroot.element("sections");
//            Element section = sections.element("section");
//            Element body = section.element("body");
//            Element paragraphs = body.element("paragraphs");
//            Iterator iterator = paragraphs.elementIterator();
//            while(iterator.hasNext()){
//                Element el1 = (Element) iterator.next();
//                List<Element> el1Elements = el1.elements();
//                if(CollUtil.isNotEmpty(el1Elements)){
//
//                }
//            }
//            System.out.println();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println();
    }

    public void test(String key, Map<String, InterfaceBean> map) {
        Document document = new Document();
        map.forEach((interfaceName, v) -> {
            //接口名
            String title = interfaceName;
            //入参
            List<ClassEntry> request = v.getRequest();
            //出参
            ClassEntry response = v.getResponse();
            //请求方式
            String method = v.getMethod();
            //请求地址
            String requestPath = v.getPath();

            Section section = document.addSection();

            //接口名
            Paragraph titleParagraph = section.addParagraph();
            TextRange textRange = titleParagraph.appendText(title);
            textRange.getCharacterFormat().setFontSize(20f);
            textRange.getCharacterFormat().setBold(true);

            //访问地址
            Paragraph requestPathParagraph = section.addParagraph();
            requestPathParagraph.appendText(requestPath);

            //请求方式
            Paragraph requestMethodParagraph = section.addParagraph();
            requestMethodParagraph.appendText(method);

            //输入参数
            Paragraph requestParamParagraph = section.addParagraph();
            requestParamParagraph.appendText("输入参数：");

            //插入表格
            Table table = section.addTable();
            int rowNum = calculateRequestNum(request);
            log.info("表格需要添加{}行", rowNum);

            for (int row = 0; row < rowNum; row++) {
                ClassEntry classEntry = request.get(row);
                List<FieldEntry> fieldEntryList = classEntry.getFieldEntryList();
                FieldEntry fieldEntry = fieldEntryList.get(row);
                TableRow tableRow = table.addRow();
                //${in-paramName}	${in-type}	${in-isMust}	${in-description}	${in-remarks}
                log.info("表格当前行数为：{}", row + 1);
                for (int j = 0; j < 5; j++) {
                    TableCell tableCell = tableRow.addCell();
                    Paragraph inParamName = tableCell.addParagraph();
                    switch (j) {
                        case 0:
                            inParamName.appendText(fieldEntry.getFieldName());
                            break;
                        case 1:
                            inParamName.appendText(fieldEntry.getFieldType());
                            break;
                        case 2:
                            inParamName.appendText("是否必须");
                            break;
                        case 3:
                            inParamName.appendText(fieldEntry.getFieldExplain());
                            break;
                        case 4:
                            inParamName.appendText("备注");
                            break;
                    }
                }
                row = subFieldAddRow(table, row, fieldEntry);
            }

            document.saveToFile("template" + File.separator + key + File.separator + "test" + FileUtil.createFileId() + ".docx", FileFormat.Doc);
        });
        System.out.println();
    }

    private int subFieldAddRow(Table table, int row, FieldEntry fieldEntry) {
        while (CollUtil.isNotEmpty(fieldEntry.getFields())) {
            List<FieldEntry> fields = fieldEntry.getFields();
            for (FieldEntry field : fields) {
                TableRow tableRow2 = table.addRow();
                row++;
                log.info("表格当前行数为：{}", row + 1);
                for (int j = 0; j < 5; j++) {
                    TableCell tableCell = tableRow2.addCell();
                    Paragraph inParamName = tableCell.addParagraph();
                    switch (j) {
                        case 0:
                            inParamName.appendText(field.getFieldName());
                            break;
                        case 1:
                            inParamName.appendText(field.getFieldType());
                            break;
                        case 2:
                            inParamName.appendText("是否必须");
                            break;
                        case 3:
                            inParamName.appendText(field.getFieldExplain());
                            break;
                        case 4:
                            inParamName.appendText("备注");
                            break;
                    }
                }
                row = subFieldAddRow(table, row, field);
            }
        }
        return row;
    }

    /**
     * 计算请求入参的行数
     *
     * @param request
     * @return
     */
    private int calculateRequestNum(List<ClassEntry> request) {
        AtomicInteger num = new AtomicInteger(0);
        for (ClassEntry classEntry : request) {
            calculateFieldEntryNum(classEntry.getFieldEntryList(), num);
        }
        return num.get();
    }

    private void calculateFieldEntryNum(List<FieldEntry> fieldEntries, AtomicInteger num) {
        if (CollUtil.isNotEmpty(fieldEntries)) {
            num.addAndGet(fieldEntries.size());
        }
        for (FieldEntry fieldEntry : fieldEntries) {
            if (CollUtil.isNotEmpty(fieldEntry.getFields())) {
                calculateFieldEntryNum(fieldEntry.getFields(), num);
            }
        }
    }

    /**
     * 填充单元格
     *
     * @param table
     * @param i
     * @param j
     * @param fieldEntry
     * @param prefix
     */
    private static void setField(Table table, int j, FieldEntry fieldEntry, String prefix, AtomicInteger rows) {
        TableRow tableRow = table.addRow();
        rows.addAndGet(1);
        CellCollection cells = tableRow.getCells();
        TableCell tableCell = cells.get(j);
        Paragraph para = tableCell.getParagraphs().get(0);
        para.setText(prefix + fieldEntry.getFieldName());
        if (CollUtil.isNotEmpty(fieldEntry.getFields())) {
            List<FieldEntry> fields = fieldEntry.getFields();
            for (FieldEntry field : fields) {
                if (!CollUtil.isNotEmpty(field.getFields())) {
                    setField(table, j, field, prefix + "-", rows);
                }
            }
        }
    }

    @Override
    public void uploadTemplate(MultipartFile multipartFile) {

    }

    @Override
    public void generateDocument(String key, HttpServletResponse response) {

    }

    /**
     * 生成word文档
     * 1.解析解压后的文件 找出实体层文件和控制层文件
     * 2.解析控制层文件 得到 控制层中所有接口的信息 （入参、出参、请求方式、接口路径）
     *
     * @param key            每次操作的唯一标识   保证上传操作和生成文档操作的一致性，前端传
     * @param controllerDirs 用户声明的控制层文件夹 可为null
     * @param entityDirs     用户声明的实体层文件夹 不可为null
     * @return
     */
    @Override
    public String generateDocument(String key, List<String> controllerDirs, List<String> entityDirs) {
        if (CollUtil.isEmpty(entityDirs)) {
            throw new AppException("声明的实体层文件夹不可为空！");
        }
        String redisKey = RedisConstants.UPLOAD_UNZIP + key;
        //获取解压的文件夹路径 redisUtils.setCacheObject(RedisConstants.UPLOAD_UNZIP+res,unZipDir);
        String unzipDir = redisUtils.getCacheObject(redisKey);

        //控制层文件map  k：类名  v：文件路径
        Map<String, String> controllerFileMap = new HashMap<>();
        //实体层文件map k：类名 v：文件路径
        Map<String, String> entityFileMap = new HashMap<>();

        uploadService.scanUnZipDir(new File(unzipDir), controllerDirs, entityDirs, controllerFileMap, entityFileMap);

        //获取解压后的控制层、实体层文件 -> 存入缓存
        redisUtils.setCacheMap(RedisConstants.CONTROLLER_DIR + key, controllerFileMap);
        redisUtils.setCacheMap(RedisConstants.ENTITY_DIR + key, entityFileMap);

        //解析控制层文件
        Map<String, InterfaceBean> interfaceBeanMap = new HashMap<>();
        controllerFileMap.entrySet().stream().forEach(t -> {
            //类名
            String className = t.getKey();
            //路径
            String value = t.getValue();
            //获取类中接口信息
            interfaceBeanMap.putAll(classOperator.getMethodsInfo(key, value));
            ;
        });
        // TODO: 2023/2/4 处理xml文件
        System.out.println(JSON.toJSONString(interfaceBeanMap));

        test(key, interfaceBeanMap);
        return null;
    }

    /**
     * 根据用户上传的模板生成word文档
     * 1.解析解压后的文件 找出实体层文件和控制层文件
     * 2.解析控制层文件 得到 控制层中所有接口的信息 （入参、出参、请求方式、接口路径）
     *
     * @param key            每次操作的唯一标识   保证上传操作和生成文档操作的一致性，前端传
     * @param controllerDirs 用户声明的控制层文件夹 可为null
     * @param entityDirs     用户声明的实体层文件夹 不可为null
     * @return
     */
    @Override
    public String generateDocumentForTemplate(String key, List<String> controllerDirs, List<String> entityDirs) {
        if (CollUtil.isEmpty(entityDirs)) {
            throw new AppException("声明的实体层文件夹不可为空！");
        }
        String redisKey = RedisConstants.UPLOAD_UNZIP + key;
        //获取解压的文件夹路径 redisUtils.setCacheObject(RedisConstants.UPLOAD_UNZIP+res,unZipDir);
        String unzipDir = redisUtils.getCacheObject(redisKey);

        //控制层文件map  k：类名  v：文件路径
        Map<String, String> controllerFileMap = new HashMap<>();
        //实体层文件map k：类名 v：文件路径
        Map<String, String> entityFileMap = new HashMap<>();

        uploadService.scanUnZipDir(new File(unzipDir), controllerDirs, entityDirs, controllerFileMap, entityFileMap);

        //获取解压后的控制层、实体层文件 -> 存入缓存
        redisUtils.setCacheMap(RedisConstants.CONTROLLER_DIR + key, controllerFileMap);
        redisUtils.setCacheMap(RedisConstants.ENTITY_DIR + key, entityFileMap);

        //解析控制层文件
        Map<String, InterfaceBean> interfaceBeanMap = new HashMap<>();
        controllerFileMap.entrySet().stream().forEach(t -> {
            //类名
            String className = t.getKey();
            //路径
            String value = t.getValue();
            //获取类中接口信息
            interfaceBeanMap.putAll(classOperator.getMethodsInfo(key, value));
        });
        log.info("解析出来的map：{}", JSON.toJSONString(interfaceBeanMap));

        //根据模板生成word
        dealTemplate(key, interfaceBeanMap);

        //合并word
        FileUtil.mergeWord(docConfig.getOutputDir()+File.separator+key, false);
        return null;
    }

    /**
     * 解析用户上传的模板 根据模板生成word文档
     *
     * @param key
     * @param interfaceBeanMap
     */
    private String dealTemplate(String key, Map<String, InterfaceBean> interfaceBeanMap) {
        //获取Word模板，模板存放路径在项目的resources目录下
        String templatePath = redisUtils.getCacheObject(RedisConstants.TEMPLATE_DIR + key);
        File file = null;
        if (CharSequenceUtil.isEmpty(templatePath)) {
          log.warn("未检测到模板内容。使用默认模板");
            String property = System.getProperty("user.dir");
            file = new File(property + "/testDocument/测试文档 - 副本.docx");
        } else {
            file = new File(templatePath);
        }

        Map<String, String> redisMap = new HashMap<>();
        File finalFile = file;
        interfaceBeanMap.forEach((k, v) -> {
            InputStream ins = null;
            FileOutputStream out = null;
            try {
                ins = new FileInputStream(finalFile);
                //注册xdocreport实例并加载FreeMarker模板引擎
                IXDocReport report = XDocReportRegistry.getRegistry().loadReport(ins,
                        TemplateEngineKind.Freemarker);
                List<RequestParam> requestParams = new ArrayList<>();
                List<ResponseParam> responseParams = new ArrayList<>();

                //入参
                List<ClassEntry> request = v.getRequest();
                request.forEach(t -> {
                    List<FieldEntry> fieldEntryList = t.getFieldEntryList();
                    fieldEntryList.forEach(field -> {
                        addRequestParamList(field, requestParams, "");
                    });
                });
                //出参
                ClassEntry response = v.getResponse();
                List<FieldEntry> fieldEntryList = response.getFieldEntryList();
                if (CollUtil.isNotEmpty(fieldEntryList)) {
                    fieldEntryList.forEach(t -> {
                        addResponseParamList(t, responseParams, "");
                    });
                }

                //创建xdocreport上下文对象
                IContext context = report.createContext();

                // TODO: 2023/2/10 构造入参和出参
                String requestBody = "";
                String responseBody = "";
                context.put(DocModelEnum.TITLE.getCode(), k);
                context.put(DocModelEnum.INTERFACE_ADDRESS.getCode(), CharSequenceUtil.isEmpty(v.getPath()) ? "" : v.getPath());
                context.put(DocModelEnum.REQUEST_PARAM.getCode(), requestParams);
                context.put(DocModelEnum.RESPONSE_PARAM.getCode(), responseParams);
                context.put(DocModelEnum.REQUEST_BODY.getCode(), requestBody);
                context.put(DocModelEnum.RESPONSE_BODY.getCode(), responseBody);
                context.put(DocModelEnum.METHOD.getCode(), CharSequenceUtil.isEmpty(v.getMethod()) ? "" : v.getMethod());

                //创建字段元数据
                FieldsMetadata fm = report.createFieldsMetadata();
                //Word模板中的表格数据对应的集合类型
                fm.load(DocModelEnum.REQUEST_PARAM.getCode(), RequestParam.class, true);
                fm.load(DocModelEnum.RESPONSE_PARAM.getCode(), ResponseParam.class, true);
                report.setFieldsMetadata(fm);

                //输出到本地目录
                String dir = docConfig.getOutputDir() + File.separator + key;
                FileUtil.mkDir(dir);
                String outPutPath = dir + File.separator + FileUtil.createFileId() + finalFile.getName();
                out = new FileOutputStream(new File(outPutPath));
                report.process(context, out);

                redisMap.put(k, outPutPath);
                log.info("导出成功");
            } catch (XDocReportException | IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (ins != null) {
                            ins.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        redisUtils.setCacheMap(RedisConstants.OUTPUT_DIR + key, redisMap);
        log.info("存入缓存成功");
        return null;
    }

    private void addResponseParamList(FieldEntry fieldEntry, List<ResponseParam> responseParams, String prefix) {
        ResponseParam build = new ResponseParam();
        if (CharSequenceUtil.isEmpty(fieldEntry.getFieldExplain())) {
            build.setRemarks("");
        } else {
            build.setRemarks(fieldEntry.getFieldExplain());
        }
        build.setName(fieldEntry.getFieldName());
        build.setType(fieldEntry.getFieldType());
        responseParams.add(build);
        if (CollUtil.isNotEmpty(fieldEntry.getFields())) {
            fieldEntry.getFields().forEach(t -> {
                addResponseParamList(t, responseParams, "-" + prefix);
            });
        }
    }

    private void addRequestParamList(FieldEntry fieldEntry, List<RequestParam> requestParams, String prefix) {
        RequestParam build = new RequestParam();
        build.setDefaultValue("");
        build.setMust("");
        if (CharSequenceUtil.isEmpty(fieldEntry.getFieldExplain())) {
            build.setRemarks("");
        } else {
            build.setRemarks(fieldEntry.getFieldExplain());
        }
        build.setName(fieldEntry.getFieldName());
        build.setType(fieldEntry.getFieldType());

        requestParams.add(build);
        if (CollUtil.isNotEmpty(fieldEntry.getFields())) {
            fieldEntry.getFields().forEach(t -> {
                addRequestParamList(t, requestParams, "-" + prefix);
            });
        }
    }
}
