package com.peach.scheduler.qo;

import com.peach.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Mr Shu
 * @Version 1.0.0
 * @Description //TODO
 * @CreateTime 2025/3/10 0:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutomaticTaskLogQO extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -1979813656782894527L;

    /**
     * traceId
     */
    private String traceId;

    /**
     * traceIdList
     */
    private List<String> taskIdList;

}
