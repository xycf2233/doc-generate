package com.xycf.generate;

import com.xycf.generate.operator.ClassOperator;
import com.xycf.generate.util.WordToPdfUtils;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @Author ztc
 * @Description TODO
 * @Date 2023/2/1 13:52
 */
@SpringBootTest(classes = GenerateApplication.class)
public class Test {

    @Resource
    private ClassOperator classOperator;

    @org.junit.jupiter.api.Test
    public void test() {

    }
    public static void main(String[] args) {
        String wordUrl = "C:\\Users\\张天成\\Desktop\\新建文件夹\\KBD 检测最近一次售点任务是否合格.docx";
        String pdfUrl = "C:\\Users\\张天成\\Desktop\\新建文件夹\\pdf111.pdf";
        WordToPdfUtils.docToPdf(wordUrl,pdfUrl);
    }
}
