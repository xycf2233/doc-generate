package com.xycf.generate.util;

import com.xycf.generate.config.DocConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.util.Map;
import java.util.UUID;

/**
 * @author 
 * @date 
 * @apiNote 生成动态文档帮助类
 */
@Data
@Component
public class WordUtil {

    private Configuration configuration = null;

    public WordUtil(){
        configuration = new Configuration();
        configuration.setDefaultEncoding("UTF-8");
    }

    /**
     * 转换成word
     * @param dataMap
     * @param baseDir 模板文件存放的目录
     * @param outputDir word生成的输出目录
     * @param templateFile 模板文件名称(.ftl文件)
     * @param outputFileName 生成word的文件名称
     * @return
     */
    public String createWord(Map<String,Object> dataMap,String baseDir,String outputDir,String templateFile,String outputFileName){
        //模板文件所在路径
        configuration.setClassForTemplateLoading(this.getClass(), "");

        Template t = null;

        try {
            //得到模板文件
            configuration.setDirectoryForTemplateLoading(new File(baseDir));
            t = configuration.getTemplate(templateFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //导出文件
        File outFile = new File(outputDir+ outputFileName + ".docx");
        Writer out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        try {
            //将填充数据填入模板文件并输出到目标文件
            assert t != null;
            t.process(dataMap, out);
            return outFile.getPath();
        } catch (TemplateException | IOException e) {
            e.printStackTrace();
        }
        return "文档生成失败";
    }

//    public static void main(String[] args) {
//        // 调用word文档帮助类
//        WordUtil wordUtil = new WordUtil();
//        // 模板文件存放的目录
//        wordUtil.setBaseDir("E:/ww");
//        // 模板文件名称
//        wordUtil.setTemplateFile("采购计划表模板.ftl");
//        // word生成的输出目录
//        wordUtil.setOutputDir("E:/image/");
//        // 初始化数据map
//        Map<String,Object> dataMap = new HashMap<>();
//
//        // 录入采购基本数据
//        dataMap.put("data1", "XX公司");
//        dataMap.put("data2", "XX项目");
//        dataMap.put("data3", "2022.01.01");
//        dataMap.put("data4", "采购部");
//        dataMap.put("data5", "张三");
//        dataMap.put("data6", "189XXXXXXX");
//        dataMap.put("data7", "李四");
//        dataMap.put("data8", "张主任");
//        dataMap.put("data9", "王总");
//        dataMap.put("data10", "王某");
//        dataMap.put("data11", "王某");
//        dataMap.put("data12", "张三");
//
//
//        // 录入表格数据(3条数据循环三次)
//        for (int i = 1; i <= 3; i++) {
//            dataMap.put("dataName"+i, "笔记本电脑"+i);
//            dataMap.put("dataBand"+i, "金山牌"+i);
//            dataMap.put("model"+i, "JHHJ6"+i);
//            dataMap.put("price"+i, 5000+i);
//            dataMap.put("quantity"+i, 3+i);
//            dataMap.put("total"+i, 15000+i);
//        }
//
//        //处理定价方式复选框
//        dataMap.put("select", "☑境内采购" + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + "□境外采购");
//
//        String oldPath = wordUtil.createWord(dataMap);
//
//        if (oldPath.equals("操作失败")){
//            System.out.println("操作失败");
//        }
//
//        //输出生成后的文件路径
//        System.out.println(oldPath);
//    }


}

