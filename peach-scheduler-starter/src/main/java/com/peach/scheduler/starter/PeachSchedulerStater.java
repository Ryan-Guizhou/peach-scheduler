package com.peach.scheduler.starter;

import com.peach.common.anno.MyBatisDao;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Indexed;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 06 3月 2025 22:23
 */
@Indexed
@Configuration
@ComponentScan(basePackages = {"com.peach.scheduler",}, lazyInit = true)
@MapperScan(lazyInitialization = "true", basePackages = "com.peach.scheduler.dao",
        annotationClass = MyBatisDao.class,sqlSessionFactoryRef = "mybatis-session")
public class PeachSchedulerStater {


}
