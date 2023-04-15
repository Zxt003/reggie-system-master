package com.zgc.app.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.zgc.app.common.Constant;
import com.zgc.app.utils.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MybatisMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)
        this.strictInsertFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class);
        // 从【1. 静态变量 / 2. 线程本身】获取当前登录用户的ID
        // 1. Long logerId = Constant.map.get(Thread.currentThread().getName());
        Long logerId = BaseContext.getCurrentId();
        this.strictInsertFill(metaObject, "createUser", () -> logerId, Long.class);
        this.strictInsertFill(metaObject, "updateUser", () -> logerId, Long.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class);
        //Long logerId = Constant.map.get(Thread.currentThread().getName());
        Long logerId = BaseContext.getCurrentId();
        log.warn("修改时"+logerId);
        this.strictUpdateFill(metaObject, "updateUser", () -> logerId, Long.class);
    }
}
