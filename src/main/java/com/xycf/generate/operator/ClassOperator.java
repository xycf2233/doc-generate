package com.xycf.generate.operator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.sun.javadoc.*;
import com.xycf.generate.common.enums.*;
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
     *
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
        if (classDoc == null) {
            return null;
        }
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
     *
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
     *
     * @param classDoc 类信息实体
     * @return 属性信息集合
     */
    public List<FieldEntry> getFieldEntrys(ClassDoc classDoc) {
        List<FieldEntry> entrys = new ArrayList<>();
        FieldDoc[] fields = classDoc.fields(false);

        for (FieldDoc field : fields) {
            boolean isMust = false;
            AnnotationDesc[] annotations = field.annotations();
            for (AnnotationDesc annotation : annotations) {
                String annotationName = annotation.annotationType().typeName();
                isMust = IsMustEnum.isMust(annotationName);
            }
            FieldEntry build = FieldEntry.builder()
                    .fieldName(field.name())
                    .fieldType(field.type().typeName())
                    .fieldExplain(field.commentText())
                    .must(isMust ? "必须" : "非必须")
                    .defaultValue(DefaultEnum.getDefaultValue(field.type().toString()))
                    .build();
            entrys.add(build);
        }
        return entrys;
    }

    /**
     * 获取类中属性名称、类型和注释
     *
     * @param javaBeanFilePath 类绝对路径
     * @return 属性信息集合
     */
    public List<FieldEntry> getFieldEntrys(String javaBeanFilePath) {
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
     *
     * @param classDoc 类信息实体
     * @return 类注释
     */
    public String getClassComment(ClassDoc classDoc) {
        Object documentation = ReflectUtil.getFieldValue(classDoc, "documentation");
        String classComment = documentation == null ? "" : documentation.toString();
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
     *
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
     *
     * @param classDoc 类信息实体
     * @return
     */
    public String getClassName(ClassDoc classDoc) {
        return classDoc.name();
    }

    /**
     * 获取类名
     *
     * @param javaBeanFilePath 类的绝对路径
     * @return
     */
    public String getClassName(String javaBeanFilePath) {
        ClassDoc classDoc = getClassDoc(javaBeanFilePath);
        return classDoc.name();
    }

    /**
     * 获取ClassDoc类文档实体
     *
     * @param javaBeanFilePath 类绝对类路径
     * @return
     */
    public ClassDoc getClassDoc(String javaBeanFilePath) {
        if (!javaBeanFilePath.endsWith(".java")) {
            return null;
        }
        InitClassDoc initClassDoc = new InitClassDoc().invoke(javaBeanFilePath);
        return initClassDoc.getClassDoc();
    }

    /**
     * 获取类中方法集合
     *
     * @param javaBeanFilePath 类绝对路径
     * @return
     */
    public MethodDoc[] getClassMethods(String javaBeanFilePath) {
        ClassDoc classDoc = getClassDoc(javaBeanFilePath);
        return classDoc.methods();
    }

    /**
     * 是否是控制层文件
     *
     * @param classDoc 类信息文档
     * @return true/false
     */
    public boolean isController(ClassDoc classDoc) {
        AnnotationDesc[] annotations = classDoc.annotations();
        for (AnnotationDesc annotation : annotations) {
            AnnotationTypeDoc annotationTypeDoc = annotation.annotationType();
            String annotationName = annotationTypeDoc.typeName();
            boolean isControllerAnnotation = ControllerAnnotationEnum.isControllerAnnotation(annotationName);
            if (isControllerAnnotation) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是控制层文件
     *
     * @param javaBeanFilePath 类绝对路径
     * @return true/false
     */
    public boolean isController(String javaBeanFilePath) {
        ClassDoc classDoc = getClassDoc(javaBeanFilePath);
        AnnotationDesc[] annotations = classDoc.annotations();
        for (AnnotationDesc annotation : annotations) {
            AnnotationTypeDoc annotationTypeDoc = annotation.annotationType();
            String annotationName = annotationTypeDoc.typeName();
            boolean isControllerAnnotation = ControllerAnnotationEnum.isControllerAnnotation(annotationName);
            if (isControllerAnnotation) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取类中方法集合
     *
     * @param classDoc 类信息文档
     * @return
     */
    public MethodDoc[] getClassMethods(ClassDoc classDoc) {
        return classDoc.methods();
    }

    /**
     * 解析类中方法的入参出参
     *
     * @param key              唯一标识
     * @param javaBeanFilePath 类绝对路径
     * @return
     */
    public Map<String, InterfaceBean> getMethodsInfo(String key, String javaBeanFilePath) {

        Map<String, InterfaceBean> interfaceBeanMap = new HashMap<>();
        ClassDoc classDoc = getClassDoc(javaBeanFilePath);
        String classRequestPath = getRequestMappingValue(classDoc);

        MethodDoc[] classMethods = getClassMethods(classDoc);
        for (MethodDoc classMethod : classMethods) {
            //接口名称
            String classMethodName = classMethod.name();
            //接口解析类
            InterfaceBean interfaceBean = new InterfaceBean();
            //表示当前方法是否是一个 接口方法
            boolean flag = false;

            String requestMethod = null;
            StringBuilder requestPath = new StringBuilder();

            //注解 数组
            AnnotationDesc[] annotations = classMethod.annotations();
            for (AnnotationDesc annotation : annotations) {
                AnnotationTypeDoc annotationTypeDoc = annotation.annotationType();
                //注解名称 PostMapping
                String annotationName = annotationTypeDoc.typeName();
                requestMethod = RequestMethodEnum.isRequestMethod(annotationName);
                if (CharSequenceUtil.isNotEmpty(requestMethod)) {
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
                        if (isRequestPath) {
                            requestPath.append(classRequestPath.replace("\"",""))
                                    .append(StrUtil.str(annotationValue, Charset.defaultCharset()).replace("\"",""));
                            interfaceBean.setPath(requestPath.toString());
                        }
                    }
                }
            }
            if (!flag) {
                continue;
            }
            //封装请求方式
            interfaceBean.setMethod(requestMethod);
            //入参信息
            List<ClassEntry> requestInfo = getRequestInfo(key, classMethod);
            interfaceBean.setRequest(requestInfo);
            //返回类型
            ClassEntry responseClassEntry = getResponseInfo(key, classMethod);
            interfaceBean.setResponse(responseClassEntry);

            //入参示例json格式
            interfaceBean.setRequestBody(getRequestBody(requestInfo));
            //出参示例json格式
            interfaceBean.setResponseBody(getResponseBody(responseClassEntry));
            interfaceBeanMap.put(classMethodName, interfaceBean);
        }
        return interfaceBeanMap;
    }

    private String getRequestMappingValue(ClassDoc classDoc) {
        AnnotationDesc[] classAnnotation = classDoc.annotations();
        for (AnnotationDesc annotationDesc : classAnnotation) {
            if("RequestMapping".equals(annotationDesc.annotationType().typeName())){
                AnnotationDesc.ElementValuePair[] elementValuePairs = annotationDesc.elementValues();
                for (AnnotationDesc.ElementValuePair elementValuePair : elementValuePairs) {
                    String name = elementValuePair.element().name();
                    if("value".equals(name)){
                        return elementValuePair.value().toString();
                    }
                }
            }
        }
        return "";
    }

    /**
     * 根据返回类生成返回示例
     *
     * @param classEntry
     * @return
     */
    private String getResponseBody(ClassEntry classEntry) {
        Map<String, Object> map;
        if (!DefaultEnum.contains(classEntry.getModelClassName())) {
            //是自定义类
            List<FieldEntry> fieldEntryList = classEntry.getFieldEntryList();
            map = getJsonMap(fieldEntryList);
        } else {
            //如果不是自定义类 那么 返回默认值
            if (CollUtil.isEmpty(classEntry.getFieldEntryList())) {
                return "";
            }
            return classEntry.getFieldEntryList().get(0).getDefaultValue();
        }
        return map.size() == 0 ? "" : JSON.toJSONString(map);
    }

    /**
     * 根据入参类生成入参示例 设置字段默认值
     *
     * @param requestInfo
     * @return
     */
    private String getRequestBody(List<ClassEntry> requestInfo) {
        Map<String, Object> map = new HashMap<>();
        for (ClassEntry classEntry : requestInfo) {
            if (!DefaultEnum.contains(classEntry.getModelClassName())) {
                //是自定义类
                List<FieldEntry> fieldEntryList = classEntry.getFieldEntryList();
                map = getJsonMap(fieldEntryList);
            } else {
                FieldEntry fieldEntry = classEntry.getFieldEntryList().get(0);
                //如果不是自定义类 那么 返回默认值
                return fieldEntry.getDefaultValue();
            }
        }
        return map.size() == 0 ? "" : JSON.toJSONString(map);
    }

    private Map<String, Object> getJsonMap(List<FieldEntry> fieldEntryList) {
        if (CollUtil.isEmpty(fieldEntryList)) {
            return new HashMap<>();
        }
        Map<String, Object> map = new HashMap<>();
        for (FieldEntry fieldEntry : fieldEntryList) {
            if (DefaultEnum.contains(fieldEntry.getFieldType())) {
                map.put(fieldEntry.getFieldName(), fieldEntry.getDefaultValue());
            } else {
                List<FieldEntry> fields = fieldEntry.getFields();
                if (CollUtil.isNotEmpty(fields)) {
                    Map<String, Object> jsonMap = getJsonMap(fields);
                    map.put(fieldEntry.getFieldName(), jsonMap);
                }
            }
        }
        return map;
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
        if (CharSequenceUtil.isNotEmpty(requestEntityPath)) {
            //获取类信息
            responseClassEntry = getClassEntry(requestEntityPath);
        } else {
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
            //获取该入参参数 是否必须
            boolean isMust = getIsMust(parameter);
            //是否在实体层文件中找到了该入参类型
            if (CharSequenceUtil.isNotEmpty(requestEntityPath)) {
                //获取类信息
                ClassEntry classEntry = getClassEntry(requestEntityPath);
                classEntry.setMust(isMust ? "必须" : "非必须");
                if (classEntry != null) {
                    request.add(classEntry);
                }
            } else {
                //未找到 则默认是 非用户定义的实体 比如 String、Integer
                ClassEntry classEntry = new ClassEntry();
                classEntry.setMust(isMust ? "必须" : "非必须");

                List<FieldEntry> fieldEntryList = new ArrayList<>();
                FieldEntry fieldEntry = new FieldEntry();
                classEntry.setModelClassName(parameterType);
                fieldEntry.setFieldType(parameterType);
                fieldEntry.setFieldName(name);
                //todo 获取该入参参数默认值
                fieldEntry.setDefaultValue("");

                fieldEntryList.add(fieldEntry);

                classEntry.setFieldEntryList(fieldEntryList);

                request.add(classEntry);
            }
        }
        return request;
    }

    private boolean getIsMust(Parameter parameter) {
        boolean isMust = false;
        AnnotationDesc[] annotations = parameter.annotations();
        for (AnnotationDesc annotation : annotations) {
            String annotationName = annotation.annotationType().typeName();
            isMust = IsMustEnum.isMust(annotationName);
        }
        return isMust;
    }


    /**
     * 判断方法是否是mvc方法 todo
     *
     * @return 是否是mvc方法
     */
    public boolean isMvcMethod() {

        return false;
    }

    /**
     * 获取接口请求路径  todo
     *
     * @return 接口路径
     */
    public String getRequestPath() {

        return null;
    }


    private class InitClassDoc {
        private boolean myResult;
        private ClassEntry classEntry;
        private List<FieldEntry> entrys = new ArrayList<>();
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
                    ClassOperator.class.getResource("/").getPath(), "-encoding", "utf-8", "-XDuseUnsharedTable", javaBeanFilePath});
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
