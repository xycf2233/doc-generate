package com.xycf.generate.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.spire.doc.*;
import com.spire.doc.collections.CellCollection;
import com.spire.doc.collections.SectionCollection;
import com.spire.doc.collections.TableCollection;
import com.spire.doc.documents.Paragraph;
import com.xycf.generate.common.enums.RedisConstants;
import com.xycf.generate.common.enums.base.DocModelEnum;
import com.xycf.generate.config.DocConfig;
import com.xycf.generate.config.exception.AppException;
import com.xycf.generate.entity.ClassEntry;
import com.xycf.generate.entity.FieldEntry;
import com.xycf.generate.entity.InterfaceBean;
import com.xycf.generate.operator.ClassOperator;
import com.xycf.generate.service.UploadService;
import com.xycf.generate.service.WordService;
import com.xycf.generate.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
     * @param multipartFile 上传的word文件
     * @return xml文件路径
     */
    @Override
    public String changeToXml(MultipartFile multipartFile) {
        Document document = new Document();
        InputStream inputStream = null;
        String originalFilename = multipartFile.getOriginalFilename();
        if (StrUtil.isEmpty(originalFilename)) {
            throw new RuntimeException("文件名不合法!");
        }
        String fileName = originalFilename.substring(0, originalFilename.indexOf("."));
        String xmlFileName = docConfig.getXmlFilePath() + File.separator + fileName + ".xml";
        try {
            inputStream = multipartFile.getInputStream();
            document.loadFromStream(inputStream, FileFormat.Word_Xml);
            document.saveToFile(xmlFileName);
            document.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("word转换xml文档成功!");
        return xmlFileName;
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

    public static void main(String[] args) {
        JSONObject jsonObject = JSON.parseObject("{\n" +
                "    \"generateDocument\": {\n" +
                "        \"method\": \"post\",\n" +
                "        \"path\": \"\\\"/generateDocument\\\"\",\n" +
                "        \"request\": [\n" +
                "            {\n" +
                "                \"fieldEntryList\": [\n" +
                "                    {\n" +
                "                        \"fieldExplain\": \"唯一标识\",\n" +
                "                        \"fieldName\": \"key\",\n" +
                "                        \"fieldType\": \"String\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"fieldExplain\": \"控制层文件路径\",\n" +
                "                        \"fieldName\": \"controllerDirs\",\n" +
                "                        \"fieldType\": \"List\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"fieldExplain\": \"实体层文件路径\",\n" +
                "                        \"fieldName\": \"entityDirs\",\n" +
                "                        \"fieldType\": \"List\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"modelClassName\": \"GenerateDocumentReq\",\n" +
                "                \"modelCommentText\": \"\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"response\": {\n" +
                "            \"modelClassName\": \"void\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"test2\": {\n" +
                "        \"method\": \"post\",\n" +
                "        \"path\": \"\\\"/test2\\\"\",\n" +
                "        \"request\": [\n" +
                "            {\n" +
                "                \"fieldEntryList\": [\n" +
                "                    {\n" +
                "                        \"fieldName\": \"a\",\n" +
                "                        \"fieldType\": \"String\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"modelClassName\": \"String\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"fieldEntryList\": [\n" +
                "                    {\n" +
                "                        \"fieldName\": \"b\",\n" +
                "                        \"fieldType\": \"String\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"modelClassName\": \"String\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"fieldEntryList\": [\n" +
                "                    {\n" +
                "                        \"fieldName\": \"c\",\n" +
                "                        \"fieldType\": \"Integer\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"modelClassName\": \"Integer\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"response\": {\n" +
                "            \"modelClassName\": \"void\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"uploadZip\": {\n" +
                "        \"method\": \"post\",\n" +
                "        \"path\": \"\\\"/zip\\\"\",\n" +
                "        \"request\": [\n" +
                "            {\n" +
                "                \"fieldEntryList\": [\n" +
                "                    {\n" +
                "                        \"fieldName\": \"file\",\n" +
                "                        \"fieldType\": \"MultipartFile\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"modelClassName\": \"MultipartFile\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"response\": {\n" +
                "            \"modelClassName\": \"String\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"test\": {\n" +
                "        \"method\": \"post\",\n" +
                "        \"path\": \"\\\"/test\\\"\",\n" +
                "        \"request\": [\n" +
                "            {\n" +
                "                \"fieldEntryList\": [\n" +
                "                    {\n" +
                "                        \"fieldName\": \"file\",\n" +
                "                        \"fieldType\": \"MultipartFile\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"modelClassName\": \"MultipartFile\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"response\": {\n" +
                "            \"modelClassName\": \"String\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"test1\": {\n" +
                "        \"method\": \"post\",\n" +
                "        \"path\": \"\\\"/test1\\\"\",\n" +
                "        \"request\": [],\n" +
                "        \"response\": {\n" +
                "            \"modelClassName\": \"void\"\n" +
                "        }\n" +
                "    }\n" +
                "}");
//        jsonObject
//
//        String path = "D:\\project\\generate-interface-document\\testDocument\\测试文档.docx";
//        Document document = new Document();
//        document.loadFromFile(path);
//        SectionCollection sections = document.getSections();
//        Section section = sections.get(0);
//        TableCollection tables = section.getTables();
//        Table table = tables.get(0);
//        //规定范式在表格中的顺序
//        List<String> order = new ArrayList<>();
//
//        map.forEach((interfaceName,v)->{
//            //接口名
//            String title = interfaceName;
//            //入参
//            List<ClassEntry> request = v.getRequest();
//            //出参
//            ClassEntry response = v.getResponse();
//            //请求方式
//            String method = v.getMethod();
//            //请求地址
//            String requestPath = v.getPath();
//
//
//            //遍历表格中的行
//            for (int i = 0; i < table.getRows().getCount(); i++)
//            {
//                TableRow row = table.getRows().get(i);
//                //遍历每行中的单元格
//                for (int j = 0; j < row.getCells().getCount(); j++)
//                {
//                    TableCell cell = row.getCells().get(j);
//                    //遍历单元格中的段落
//                    for (int k = 0; k < cell.getParagraphs().getCount(); k++)
//                    {
//                        Paragraph paragraph = cell.getParagraphs().get(k);
//                        //${in-paramName}
//                        String text = paragraph.getText();
//                        if(i==0){
//                            order.add(text);
//                            paragraph.setText(DocModelEnum.getMessageByCode(text));
//                        }else{
//                            //根据order的顺序 插入元素
//                            if("${in-paramName}".equals(order.get(k))){
//                                ClassEntry classEntry = request.get(k);
//                                List<FieldEntry> fieldEntryList = classEntry.getFieldEntryList();
//                                for (FieldEntry fieldEntry : fieldEntryList) {
//                                    String prefix = "";
//                                    setField(table, j, fieldEntry, prefix);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        });
//
//        document.saveToFile("template/test.docx",FileFormat.Doc);

        System.out.println();
    }

    /**
     * 填充单元格
     * @param table
     * @param i
     * @param j
     * @param fieldEntry
     * @param prefix
     */
    private static void setField(Table table, int j, FieldEntry fieldEntry, String prefix) {
        TableRow tableRow = table.addRow();
        CellCollection cells = tableRow.getCells();
        TableCell tableCell = cells.get(j);
        Paragraph para = tableCell.getParagraphs().get(0);
        para.setText(prefix+fieldEntry.getFieldName());
        if(CollUtil.isNotEmpty(fieldEntry.getFields())){
            List<FieldEntry> fields = fieldEntry.getFields();
            for (FieldEntry field : fields) {
                if(!CollUtil.isNotEmpty(field.getFields())){
                    setField(table,j,field,prefix+"-");
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
     * @param key            每次操作的唯一标识   保证上传操作和生成文档操作的一致性，前端传
     * @param controllerDirs 用户声明的控制层文件夹 可为null
     * @param entityDirs     用户声明的实体层文件夹 不可为null
     * @return
     */
    @Override
    public String generateDocument(String key, List<String> controllerDirs, List<String> entityDirs) {
        if(CollUtil.isEmpty(entityDirs)){
            throw new AppException("声明的实体层文件夹不可为空！");
        }
        String redisKey = RedisConstants.UPLOAD_UNZIP + key;
        //获取解压的文件夹路径 redisUtils.setCacheObject(RedisConstants.UPLOAD_UNZIP+res,unZipDir);
        String unzipDir = redisUtils.getCacheObject(redisKey);

        //控制层文件map  k：类名  v：文件路径
        Map<String, String> controllerFileMap = new HashMap<>();
        //实体层文件map k：类名 v：文件路径
        Map<String, String> entityFileMap = new HashMap<>();

        uploadService.scanUnZipDir(new File(unzipDir), controllerDirs, entityDirs,controllerFileMap,entityFileMap);

        //获取解压后的控制层、实体层文件 -> 存入缓存
        redisUtils.setCacheMap(RedisConstants.CONTROLLER_DIR+key,controllerFileMap);
        redisUtils.setCacheMap(RedisConstants.ENTITY_DIR+key,entityFileMap);

        //解析控制层文件
        Map<String, InterfaceBean> interfaceBeanMap = new HashMap<>();
        controllerFileMap.entrySet().stream().forEach(t->{
            //类名
            String className = t.getKey();
            //路径
            String value = t.getValue();
            //获取类中接口信息
            interfaceBeanMap.putAll(classOperator.getMethodsInfo(key, value));;
        });
        // TODO: 2023/2/4 处理xml文件
        System.out.println(JSON.toJSONString(interfaceBeanMap));
        return null;
    }
}
