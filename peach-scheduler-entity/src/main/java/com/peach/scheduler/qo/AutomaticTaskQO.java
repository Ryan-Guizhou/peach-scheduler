package com.peach.scheduler.qo;

import com.peach.common.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 2025/3/7 15:26
 */
@Data
public class AutomaticTaskQO extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -1979813656782894527L;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务di集合
     */
    private List<String> taskIdList;

}
