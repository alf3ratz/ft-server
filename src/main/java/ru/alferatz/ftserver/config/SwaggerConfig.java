//package ru.alferatz.ftserver.config;
//
//import static io.swagger.models.auth.In.HEADER;
//import static java.util.Collections.singletonList;
//
//import java.util.ArrayList;
//import java.util.List;
//import org.springframework.beans.factory.annotation.Configurable;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.ApiKey;
//import springfox.documentation.service.AuthorizationScope;
//import springfox.documentation.service.Contact;
//import springfox.documentation.service.SecurityReference;
//import springfox.documentation.service.SecurityScheme;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spi.service.contexts.SecurityContext;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger.web.DocExpansion;
//import springfox.documentation.swagger.web.ModelRendering;
//import springfox.documentation.swagger.web.OperationsSorter;
//import springfox.documentation.swagger.web.SecurityConfiguration;
//import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
//import springfox.documentation.swagger.web.TagsSorter;
//import springfox.documentation.swagger.web.UiConfiguration;
//import springfox.documentation.swagger.web.UiConfigurationBuilder;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//@Configuration
//@EnableSwagger2
//public class SwaggerConfig {
//
//  private final String jwtTokenName = "jwtToken";
//  private static final String AUTHORIZATION_HEADER = "Authorization";
//
//  @Bean
//  public Docket api() {
//    return new Docket(DocumentationType.SWAGGER_2)
//        .select()
//        .apis(RequestHandlerSelectors.basePackage("ru.alferatz.ftserver"))
//        .paths(PathSelectors.any())
//        .build().apiInfo(apiInfo());
////        .pathMapping("/")
////        .securitySchemes(singletonList(securityScheme()))
////        .securityContexts(singletonList(securityContext()))
////        .apiInfo(apiInfo())
////        .useDefaultResponseMessages(false)
////        .groupName("Default");
//  }
//
//  private ApiInfo apiInfo() {
//    Contact contact = new Contact("Fellow travellers app", null, "aapetropavlovskiy@edu.hse.ru");
//    return new ApiInfo(
//        "FT.app",
//        "",
//        "0.0.1",
//        "",
//        contact,
//        "",
//        "",
//        new ArrayList<>()
//    );
//  }
//
////  @Bean
////  public SecurityScheme securityScheme() {
////    //return new ApiKey(jwtTokenName, AUTHORIZATION_HEADER, HEADER.name());
////    return new ApiKey("mykey", "api_key", "header");
////  }
////
////  private SecurityContext securityContext() {
//////    return SecurityContext.builder()
//////        .securityReferences(defaultAuth())
//////        .forPaths(PathSelectors.any())
//////        .build();
////    return SecurityContext.builder()
////        .securityReferences(defaultAuth())
////        .forPaths(PathSelectors.regex("/anyPath.*"))
////        .build();
////  }
////
////  private List<SecurityReference> defaultAuth() {
//////    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//////    return List
//////        .of(new SecurityReference(jwtTokenName, new AuthorizationScope[]{authorizationScope}));
////    AuthorizationScope authorizationScope
////        = new AuthorizationScope("global", "accessEverything");
////    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
////    authorizationScopes[0] = authorizationScope;
////    return singletonList(
////        new SecurityReference("mykey", authorizationScopes));
////  }
////  @Bean
////  SecurityConfiguration security() {
////    return SecurityConfigurationBuilder.builder()
////        .clientId("test-app-client-id")
////        .clientSecret("test-app-client-secret")
////        .realm("test-app-realm")
////        .appName("test-app")
////        .scopeSeparator(",")
////        .additionalQueryStringParams(null)
////        .useBasicAuthenticationWithAccessCodeGrant(false)
////        .enableCsrfSupport(false)
////        .build();
////  }
////
////  @Bean
////  UiConfiguration uiConfig() {
////    return UiConfigurationBuilder.builder()
////        .deepLinking(true)
////        .displayOperationId(false)
////        .defaultModelsExpandDepth(1)
////        .defaultModelExpandDepth(1)
////        .defaultModelRendering(ModelRendering.EXAMPLE)
////        .displayRequestDuration(false)
////        .docExpansion(DocExpansion.NONE)
////        .filter(false)
////        .maxDisplayedTags(null)
////        .operationsSorter(OperationsSorter.ALPHA)
////        .showExtensions(false)
////        .showCommonExtensions(false)
////        .tagsSorter(TagsSorter.ALPHA)
////        .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
////        .validatorUrl(null)
////        .build();
////  }
//
//
//}
