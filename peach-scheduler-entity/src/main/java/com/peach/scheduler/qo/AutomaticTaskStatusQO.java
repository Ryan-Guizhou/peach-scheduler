package com.peach.scheduler.qo;

import com.peach.common.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 2025/03/06 23:43
 */
@Data
public class AutomaticTaskStatusQO extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 8109826533506073671L;

    private String taskId;

    private List<String> taskIdList;
}
