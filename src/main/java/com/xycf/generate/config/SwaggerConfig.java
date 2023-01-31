package com.xycf.generate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @Author ztc
 * @Description swagger配置类
 * @Date 2023/1/31 14:30
 */
@Configuration
public class SwaggerConfig {

    @Value("${project.name}")
    private String name;

    @Value("${project.version}")
    private String version;

    /**
     * 是否开启swagger
     */
    @Value("${swagger.enabled}")
    private boolean enabled;

//    /**
//     * 设置请求的统一前缀
//     */
//    @Value("${swagger.pathMapping}")
//    private String pathMapping;


    /**
     * 分组:base
     *
     * @return Docket
     */
    @Bean
    public Docket base_api_app() {
        return new Docket(DocumentationType.OAS_30)
                // 是否启用Swagger
                .enable(enabled)
                // 用来创建该API的基本信息，展示在文档的页面中（自定义展示的信息）
                .apiInfo(apiInfo("标题：doc管理模块_API", "doc"))
                // 设置哪些接口暴露给Swagger展示
                .select()
                //扫描
                .apis(RequestHandlerSelectors.basePackage("com.xycf.generate.contoller"))
                //.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                // .apis(RequestHandlerSelectors.any())
                //请求路径
                .paths(PathSelectors.any())
//                .paths(PathSelectors.ant("/system/**"))
                .build()
                //设置安全模式，swagger可以设置访问token
//                .securitySchemes(securitySchemes())
//                .securityContexts(securityContexts())
                .groupName("doc");
//                .pathMapping(pathMapping);
    }

    /**
     * 构建api文档的详细信息
     *
     * @param title       标题
     * @param description 描述
     * @return ApiInfo
     */
    private ApiInfo apiInfo(String title, String description) {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                // 作者信息
                .contact(new Contact(name, null, null))
                .version("版本号:" + version)
                .build();
    }
//
//    /**
//     * 分组: tool 管理
//     *
//     * @return Docket
//     */
//    @Bean
//    public Docket tool_api_app() {
//        return new Docket(DocumentationType.OAS_30)
//                .enable(enabled)
//                .apiInfo(apiInfo("标题：系统工具模块_API", "tool"))
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.ruoyi.project.tool.gen.controller"))
//                .paths(PathSelectors.ant("/tool/**"))
//                .build()
//                .securitySchemes(securitySchemes())
//                .securityContexts(securityContexts())
//                .groupName("tool")
//                .pathMapping(pathMapping + "/tool");
//    }
//
//    /**
//     * 安全模式，这里指定token通过Authorization头请求头传递
//     */
//    private List<SecurityScheme> securitySchemes() {
//        List<SecurityScheme> apiKeyList = new ArrayList<SecurityScheme>();
//        apiKeyList.add(new ApiKey("Authorization", "Authorization", In.HEADER.toValue()));
//        return apiKeyList;
//    }
//
//    /**
//     * 安全上下文
//     */
//    private List<SecurityContext> securityContexts() {
//        List<SecurityContext> securityContexts = new ArrayList<>();
//        securityContexts.add(
//                SecurityContext.builder()
//                        .securityReferences(defaultAuth())
//                        .operationSelector(o -> o.requestMappingPattern().matches("/.*"))
//                        .build());
//        return securityContexts;
//    }
//
//    /**
//     * 默认的安全上引用
//     */
//    private List<SecurityReference> defaultAuth() {
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        List<SecurityReference> securityReferences = new ArrayList<>();
//        securityReferences.add(new SecurityReference("Authorization", authorizationScopes));
//        return securityReferences;
//    }
}
