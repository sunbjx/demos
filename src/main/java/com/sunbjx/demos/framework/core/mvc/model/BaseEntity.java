package com.sunbjx.demos.framework.core.mvc.model;

import com.baomidou.mybatisplus.activerecord.Model;

import java.io.Serializable;

/**
 * @author sunbjx
 * @since 2018/6/11 14:30
 */
public class BaseEntity<T extends Model> extends Model<T> {
    private static final long serialVersionUID = 6306169794027088854L;

    private Long id;

    /**
     * 主键值
     */
    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
