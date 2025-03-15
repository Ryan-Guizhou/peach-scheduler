package com.peach.scheduler.starter;

import com.peach.common.anno.MyBatisDao;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Indexed;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 06 3月 2025 22:23
 */
@Slf4j
@Indexed
@Configuration
@ComponentScan(basePackages = {"com.peach.scheduler",}, lazyInit = true)
@MapperScan(lazyInitialization = "true", basePackages = "com.peach.scheduler.dao",
        annotationClass = MyBatisDao.class,sqlSessionFactoryRef = "mybatis-session")
public class PeachSchedulerStater {

    @Value("${knife4j.host:http://localhost:8888}")
    private String host;

    /**
     * 注册定时任务模块接口文档
     * @return
     */
    @Lazy
    @Bean
    public Docket schedulerApi() {
        Contact contact = new Contact("PEACH","https://github.com/Ryan-Guizhou","huanhuanshu48@gmail.com");
        Docket docket=new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("PEACH-API文档")
                        .description("PEACH-API文档")
                        .termsOfServiceUrl(host)
                        .contact(contact)
                        .version("PEACH-1.0.0")
                        .build())
                //分组名称
                .groupName("调度模块API")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.peach.scheduler"))
                .build();
        log.error("knife4j scheduler has been configured");
        return docket;
    }

}
