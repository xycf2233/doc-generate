package com.xycf.generate.operator;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ReflectUtil;
import com.sun.javadoc.*;
import com.xycf.generate.entity.ClassEntry;
import com.xycf.generate.entity.FieldEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author ztc
 * @Description 解析class文件操作类
 * @Date 2023/1/31 17:03
 */
public class ClassOperator {

    private static Logger logger = LoggerFactory.getLogger(ClassOperator.class);

    private static RootDoc rootDoc;

    public static boolean start(RootDoc root) {
        rootDoc = root;
        return true;
    }


    /**
     * 解析类文件
     * @param javaBeanFilePath 类的绝对路径
     * @return 类文件信息：类名、类注释、属性名、属性类型、属性注释
     */
    public ClassEntry getClassEntry(String javaBeanFilePath) {
        InitClassDoc initClassDoc = new InitClassDoc().invoke(javaBeanFilePath);
        if (initClassDoc.is()) {
            return initClassDoc.getClassEntry();
        }
        ClassEntry classEntry = initClassDoc.getClassEntry();
        ClassDoc classDoc = initClassDoc.getClassDoc();
        // 获取类的名称
        classEntry.setModelClassName(getClassName(classDoc));
        // 获取类上的所有注释
        classEntry.setModelCommentText(getClassComment(classDoc));
        // 获取属性名称和注释
        classEntry.setFieldEntryList(getFieldEntrys(classDoc));
        return classEntry;
    }

    /**
     * 获取类中属性名称、类型和注释
     * @param classDoc 类信息实体
     * @return 属性信息集合
     */
    public List<FieldEntry> getFieldEntrys(ClassDoc classDoc){
        List<FieldEntry> entrys = new ArrayList<>();
        FieldDoc[] fields = classDoc.fields(false);
        for (FieldDoc field : fields) {
            entrys.add(new FieldEntry(field.name(), field.type().typeName(), field.commentText()));
        }
        return entrys;
    }

    /**
     * 获取类中属性名称、类型和注释
     * @param javaBeanFilePath 类绝对路径
     * @return 属性信息集合
     */
    public List<FieldEntry> getFieldEntrys(String javaBeanFilePath){
        ClassDoc classDoc = getClassDoc(javaBeanFilePath);
        List<FieldEntry> entrys = new ArrayList<>();
        FieldDoc[] fields = classDoc.fields(false);
        for (FieldDoc field : fields) {
            entrys.add(new FieldEntry(field.name(), field.type().typeName(), field.commentText()));
        }
        return entrys;
    }

    /**
     * 获取类上注释
     * @param classDoc 类信息实体
     * @return 类注释
     */
    public String getClassComment(ClassDoc classDoc) {
        String classComment = ReflectUtil.getFieldValue(classDoc, "documentation").toString();
        String spitStr = "\n";
        StringBuilder classDescription = new StringBuilder();
        for (String msg : classComment.split(spitStr)) {
            if (!msg.trim().startsWith("@") && msg.trim().length() > 0) {
                classDescription.append(msg.trim());
            }
        }
        return classDescription.toString();
    }

    /**
     * 获取类上注释
     * @param javaBeanFilePath 类绝对路径
     * @return 类注释
     */
    public String getClassComment(String javaBeanFilePath) {
        String classComment = ReflectUtil.getFieldValue(getClassDoc(javaBeanFilePath), "documentation").toString();
        String spitStr = "\n";
        StringBuilder classDescription = new StringBuilder();
        for (String msg : classComment.split(spitStr)) {
            if (!msg.trim().startsWith("@") && msg.trim().length() > 0) {
                classDescription.append(msg.trim());
            }
        }
        return classDescription.toString();
    }

    /**
     * 获取类名
     * @param classDoc 类信息实体
     * @return
     */
    public String getClassName(ClassDoc classDoc){
        return classDoc.name();
    }

    /**
     * 获取类名
     * @param javaBeanFilePath 类的绝对路径
     * @return
     */
    public String getClassName(String javaBeanFilePath){
        ClassDoc classDoc = getClassDoc(javaBeanFilePath);
        return classDoc.name();
    }

    /**
     * 获取ClassDoc类文档实体
     * @param javaBeanFilePath 类绝对类路径
     * @return
     */
    public ClassDoc getClassDoc(String javaBeanFilePath){
        InitClassDoc initClassDoc = new InitClassDoc().invoke(javaBeanFilePath);
        return initClassDoc.getClassDoc();
    }

    public static void main(String[] args) {
        ClassOperator classOperator = new ClassOperator();
        ClassEntry classEntry = classOperator.getClassEntry("D:\\project\\generate-interface-document\\src\\main\\java\\com\\xycf\\generate\\entity\\ControllerOperatorBean.java");
        System.out.println("类注释："+classEntry.getModelCommentText());
        System.out.println("类名："+classEntry.getModelClassName());
        List<FieldEntry> fieldEntryList = classEntry.getFieldEntryList();
        for (FieldEntry fieldEntry : fieldEntryList) {
            System.out.println("属性名:"+fieldEntry.getFieldName()+",属性类型:"+fieldEntry.getFieldType()+",属性注释:"+fieldEntry.getFieldExplain());
        }
//        com.sun.tools.javadoc.Main.execute(new String[]{"-doclet", ClassOperator.class.getName(),
//                "-encoding", "utf-8", "-classpath",
//                "D:\\maven\\repository\\org\\springframework\\spring-web\\5.3.25\\spring-web-5.3.25.jar"});
//        ClassDoc[] classes = rootDoc.classes();
//        ClassDoc doc = classes[0];
//        System.out.println(doc.getClass());
    }

    /**
     * 获取类中方法集合
     * @param javaBeanFilePath 类绝对路径
     * @return
     */
    public MethodDoc[] getClassMethods(String javaBeanFilePath){
        ClassDoc classDoc = getClassDoc(javaBeanFilePath);
        return classDoc.methods();
    }

    /**
     * 获取类中方法集合
     * @param classDoc 类信息文档
     * @return
     */
    public MethodDoc[] getClassMethods(ClassDoc classDoc){
        return classDoc.methods();
    }

    // TODO: 2023/2/3 解析类中方法的入参出参


    /**
     * 判断方法是否是mvc方法 todo
     * @return 是否是mvc方法
     */
    public boolean isMvcMethod(){

        return false;
    }

    /**
     * 获取接口请求路径  todo
     * @return 接口路径
     */
    public String getRequestPath() {

        return null;
    }











    private class InitClassDoc {
        private boolean myResult;
        private ClassEntry classEntry;
        private List<FieldEntry> entrys= new ArrayList<>();
        private ClassDoc classDoc;

        boolean is() {
            return myResult;
        }

        public ClassEntry getClassEntry() {
            return classEntry;
        }

        public List<FieldEntry> getEntrys() {
            return entrys;
        }

        public ClassDoc getClassDoc() {
            return classDoc;
        }

        public InitClassDoc invoke(String javaBeanFilePath) {
            classEntry = new ClassEntry();
            com.sun.tools.javadoc.Main.execute(new String[]{"-doclet", ClassOperator.class.getName(), "-docletpath",
                    ClassOperator.class.getResource("/").getPath(), "-encoding", "utf-8", javaBeanFilePath});
            ClassDoc[] classes = rootDoc.classes();

            if (classes == null || classes.length == 0) {
                logger.warn(javaBeanFilePath + " 无ClassDoc信息");
                myResult = true;
                return this;
            }

            entrys = new ArrayList<>();
            classDoc = classes[0];
            myResult = false;
            return this;
        }
    }
}
