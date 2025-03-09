package com.peach.scheduler;

import com.peach.common.generator.EntityGenerator;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 2025/3/9 23:51
 */
public class Generator {

    public static void main(String[] args) {
        //生成实体类
        EntityGenerator.generateEntity("automatic_task_log");

        //生成Mapper.xml
//        System.out.println(MapperGenerator.genMapper(AutomaticTaskLogDO.class));
    }
}
