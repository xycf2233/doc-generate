package com.xycf.generate.operator;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.sun.javadoc.*;
import com.xycf.generate.common.enums.ControllerAnnotationEnum;
import com.xycf.generate.common.enums.RedisConstants;
import com.xycf.generate.common.enums.RequestMethodEnum;
import com.xycf.generate.common.enums.RequestPathEnum;
import com.xycf.generate.config.exception.AppException;
import com.xycf.generate.entity.ClassEntry;
import com.xycf.generate.entity.ControllerOperatorBean;
import com.xycf.generate.entity.FieldEntry;
import com.xycf.generate.entity.InterfaceBean;
import com.xycf.generate.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author ztc
 * @Description 解析class文件操作类
 * @Date 2023/1/31 17:03
 */
@Component
public class ClassOperator {

    @Resource
    private RedisUtils redisUtils;

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
     * 解析类文件
     * @param classDoc 类信息文档
     * @return 类文件信息：类名、类注释、属性名、属性类型、属性注释
     */
    public ClassEntry getClassEntry(ClassDoc classDoc) {
        ClassEntry classEntry = new ClassEntry();
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
        if(!javaBeanFilePath.endsWith(".java")){
            return null;
        }
        InitClassDoc initClassDoc = new InitClassDoc().invoke(javaBeanFilePath);
        return initClassDoc.getClassDoc();
    }

    public static void main(String[] args) {
        String javaBeanFilePath = "E:\\Project\\doc-generate\\src\\main\\java\\com\\xycf\\generate\\contoller\\TestController.java";
//        ClassOperator classOperator = new ClassOperator();
//        ClassEntry classEntry = classOperator.getClassEntry("D:\\project\\generate-interface-document\\src\\main\\java\\com\\xycf\\generate\\entity\\ControllerOperatorBean.java");
//        System.out.println("类注释："+classEntry.getModelCommentText());
//        System.out.println("类名："+classEntry.getModelClassName());
//        List<FieldEntry> fieldEntryList = classEntry.getFieldEntryList();
//        for (FieldEntry fieldEntry : fieldEntryList) {
//            System.out.println("属性名:"+fieldEntry.getFieldName()+",属性类型:"+fieldEntry.getFieldType()+",属性注释:"+fieldEntry.getFieldExplain());
//        }
        ClassOperator classOperator = new ClassOperator();
        classOperator.getMethodsInfo("asdasf",javaBeanFilePath);
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
     * 是否是控制层文件
     * @param classDoc 类信息文档
     * @return true/false
     */
    public boolean isController(ClassDoc classDoc){
        AnnotationDesc[] annotations = classDoc.annotations();
        for (AnnotationDesc annotation : annotations) {
            AnnotationTypeDoc annotationTypeDoc = annotation.annotationType();
            String annotationName = annotationTypeDoc.typeName();
            boolean isControllerAnnotation = ControllerAnnotationEnum.isControllerAnnotation(annotationName);
            if(isControllerAnnotation){
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是控制层文件
     * @param javaBeanFilePath 类绝对路径
     * @return true/false
     */
    public boolean isController(String javaBeanFilePath){
        ClassDoc classDoc = getClassDoc(javaBeanFilePath);
        AnnotationDesc[] annotations = classDoc.annotations();
        for (AnnotationDesc annotation : annotations) {
            AnnotationTypeDoc annotationTypeDoc = annotation.annotationType();
            String annotationName = annotationTypeDoc.typeName();
            boolean isControllerAnnotation = ControllerAnnotationEnum.isControllerAnnotation(annotationName);
            if(isControllerAnnotation){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取类中方法集合
     * @param classDoc 类信息文档
     * @return
     */
    public MethodDoc[] getClassMethods(ClassDoc classDoc){
        return classDoc.methods();
    }

    /**
     * 解析类中方法的入参出参
     * @param key 唯一标识
     * @param javaBeanFilePath 类绝对路径
     * @return
     */
    public ControllerOperatorBean getMethodsInfo(String key,String javaBeanFilePath){

        Map<String,InterfaceBean> interfaceBeanMap = new HashMap<>();
        ClassDoc classDoc = getClassDoc(javaBeanFilePath);
        MethodDoc[] classMethods = getClassMethods(classDoc);
        for (MethodDoc classMethod : classMethods) {
            //接口名称
            String classMethodName = classMethod.name();
            //接口解析类
            InterfaceBean interfaceBean = new InterfaceBean();
            //表示当前方法是否是一个 接口方法
            boolean flag = false;

            String requestMethod  = null;
            String requestPath = null;

            //注解 数组
            AnnotationDesc[] annotations = classMethod.annotations();
            for (AnnotationDesc annotation : annotations) {
                AnnotationTypeDoc annotationTypeDoc = annotation.annotationType();
                //注解名称 PostMapping
                String annotationName = annotationTypeDoc.typeName();
                requestMethod = RequestMethodEnum.isRequestMethod(annotationName);
                if(CharSequenceUtil.isNotEmpty(requestMethod)){
                    flag = true;
                    AnnotationDesc.ElementValuePair[] elementValuePairs = annotation.elementValues();
                    for (AnnotationDesc.ElementValuePair elementValuePair : elementValuePairs) {
                        AnnotationValue value = elementValuePair.value();
                        //注解值 "word\u8f6c\u6362xml"
                        String annotationValue = value.toString();
                        AnnotationTypeElementDoc element = elementValuePair.element();
                        //注解属性 value
                        String annotationValueName = element.name();

                        boolean isRequestPath = RequestPathEnum.isRequestPath(annotationValueName);
                        if(isRequestPath){
                            requestPath = StrUtil.str(annotationValue, Charset.defaultCharset());
                            interfaceBean.setPath(requestPath);
                        }
                    }
                }
            }
            if(!flag){
                continue;
            }
            //封装请求方式
            interfaceBean.setMethod(requestMethod);
            //入参信息
            List<ClassEntry> requestInfo = getRequestInfo(key, classMethod);
            interfaceBean.setRequest(requestInfo);
            //返回类型
            ClassEntry responseClassEntry  = getResponseInfo(key, classMethod);
            interfaceBean.setResponse(responseClassEntry);

            interfaceBeanMap.put(classMethodName,interfaceBean);
        }
        return new ControllerOperatorBean(interfaceBeanMap);
    }

    private ClassEntry getResponseInfo(String key, MethodDoc classMethod) {
        ClassEntry responseClassEntry = new ClassEntry();
        Type returnType = classMethod.returnType();
//            String s = returnType.simpleTypeName();  String
//            String s1 = returnType.qualifiedTypeName(); java.lang.String
//            String s2 = returnType.toString(); java.lang.String
        String returnClassName = returnType.simpleTypeName();

        //获取缓存中所有的实体文件 如果存在 parameterType.class的文件 那么获取类文档信息
        String requestEntityPath = redisUtils.getCacheMapValue(RedisConstants.ENTITY_DIR + key, returnClassName);
        //是否在实体层文件中找到了该出参类型
        if(CharSequenceUtil.isNotEmpty(requestEntityPath)){
            //获取类信息
            responseClassEntry = getClassEntry(requestEntityPath);
        }else{
            //未找到 则默认是 非用户定义的实体 比如 String、Integer
            responseClassEntry.setModelClassName(returnClassName);
        }
        return responseClassEntry;
    }

    private List<ClassEntry> getRequestInfo(String key, MethodDoc classMethod) {
        List<ClassEntry> request = new ArrayList<>();
        //入参数组
        Parameter[] parameters = classMethod.parameters();
        for (Parameter parameter : parameters) {
            //入参类型
            String parameterType = parameter.typeName();
            //入参 参数名
            String name = parameter.name();
            //获取缓存中所有的实体文件 如果存在 parameterType.class的文件 那么获取类文档信息
            String requestEntityPath = redisUtils.getCacheMapValue(RedisConstants.ENTITY_DIR + key, parameterType);
            //是否在实体层文件中找到了该入参类型
            if(CharSequenceUtil.isNotEmpty(requestEntityPath)){
                //获取类信息
                ClassEntry classEntry = getClassEntry(requestEntityPath);
                request.add(classEntry);
            }else{
                //未找到 则默认是 非用户定义的实体 比如 String、Integer
                ClassEntry classEntry = new ClassEntry();
                List<FieldEntry> fieldEntryList = new ArrayList<>();
                FieldEntry fieldEntry = new FieldEntry();
                classEntry.setModelClassName(parameterType);
                fieldEntry.setFieldType(parameterType);
                fieldEntry.setFieldName(name);
                fieldEntryList.add(fieldEntry);
                classEntry.setFieldEntryList(fieldEntryList);
                request.add(classEntry);
            }
        }
        return request;
    }


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
