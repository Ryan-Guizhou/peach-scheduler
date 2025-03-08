package com.peach.scheduler;

import com.peach.scheduler.datasource.DataSourceProperties;
import com.peach.scheduler.factory.PeachJobFactory;
import com.peach.scheduler.listener.TaskJobListener;
import com.peach.scheduler.listener.TaskTriggerListener;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.MultipartConfigElement;
import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 16 10月 2024 00:00
 */
@Slf4j
@SpringBootApplication
@EnableConfigurationProperties
public class PeachSchedulerApplication implements WebMvcConfigurer {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PeachSchedulerApplication.class)
                .bannerMode(Banner.Mode.CONSOLE)
                .web(WebApplicationType.SERVLET)
                .run(args);
        log.info("PeachSchedulerApplication has been started ...");
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new ByteArrayHttpMessageConverter());
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 单个数据大小
        factory.setMaxFileSize(DataSize.parse("102400KB")); // KB,MB
        // 总上传数据大小
        factory.setMaxRequestSize(DataSize.parse("1024000KB"));
        return factory.createMultipartConfig();
    }

    @Bean("datasource")
    @Primary
    public DataSource dataSource(DataSourceProperties dsp) {
        HikariConfig config = new HikariConfig(dsp);
        //禁止自动提交
        config.setAutoCommit(false);
        return new HikariDataSource(config);
    }

    @Bean("mybatis-session")
    @Primary
    public SqlSessionFactoryBean sqlSessionFactoryBean(@Qualifier("datasource") DataSource dataSource, DatabaseIdProvider idProvider) {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDatabaseIdProvider(idProvider);
        factory.setConfigLocation(new DefaultResourceLoader().getResource("classpath:mybatis-config.xml"));
        factory.setDataSource(dataSource);
        return factory;
    }

    @Bean("transactionManager")
    @Primary
    public PlatformTransactionManager ptyTxManager(@Qualifier("datasource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean("transactionTemplate")
    @Primary
    public TransactionTemplate ptyTransactionTemplateBean(
            @Qualifier("transactionManager") PlatformTransactionManager transactionManager) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        return transactionTemplate;
    }

    /**
     * 自动识别使用的数据库类型
     * 在mapper.xml中databaseId的值就是跟这里对应，
     * 如果没有databaseId选择则说明该sql适用所有数据库
     */
    @Bean
    public DatabaseIdProvider getDatabaseIdProvider() {
        DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
        Properties properties = new Properties();
        properties.setProperty("Oracle", "oracle");
        properties.setProperty("MySQL", "mysql");
        properties.setProperty("DB2", "db2");
        properties.setProperty("Derby", "derby");
        properties.setProperty("H2", "h2");
        properties.setProperty("HSQL", "hsql");
        properties.setProperty("Informix", "informix");
        properties.setProperty("MS-SQL", "ms-sql");
        properties.setProperty("PostgreSQL", "postgresql");
        properties.setProperty("Sybase", "sybase");
        properties.setProperty("Hana", "hana");
        properties.setProperty("DM", "oracle");
        databaseIdProvider.setProperties(properties);
        return databaseIdProvider;
    }

    @Bean
    @ConditionalOnProperty(prefix = "peach-scheduler",name = "cluster" ,havingValue = "false" ,matchIfMissing = true)
    public Scheduler scheduler(PeachJobFactory peachJobFactory,TaskJobListener jobListener,TaskTriggerListener triggerListener) throws Exception {
        Scheduler scheduler =  StdSchedulerFactory.getDefaultScheduler();
        scheduler.setJobFactory(peachJobFactory);
        scheduler.getListenerManager().addJobListener(
                jobListener,
                GroupMatcher.anyGroup()
        );
        scheduler.getListenerManager().addTriggerListener(
                triggerListener,
                GroupMatcher.anyGroup()
        );
        return scheduler;
    }

    @Bean
    @ConditionalOnProperty(prefix = "peach-scheduler",name = "cluster" ,havingValue = "true" ,matchIfMissing = false)
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource,PeachJobFactory peachJobFactory) throws Exception {
        PropertiesFactoryBean factoryBean = new PropertiesFactoryBean();
        factoryBean.setLocation(new ClassPathResource("/peach-quartz.properties"));
        factoryBean.afterPropertiesSet();
        Properties properties = factoryBean.getObject();
        properties.setProperty("org.quartz.jobStore.isClustered", "true");
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setQuartzProperties(properties);
        factory.setOverwriteExistingJobs(true);
        factory.setAutoStartup(false);

        //延时启动
        factory.setStartupDelay(600);
        factory.setWaitForJobsToCompleteOnShutdown(true);
        factory.setJobFactory(peachJobFactory);
        factory.setDataSource(dataSource);
        return factory;
    }


}
