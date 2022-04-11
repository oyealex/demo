/*
 * Copyright (c) 2020. oyealex. All rights reserved.
 */

package com.oyealex.server.type.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oyealex.server.entity.TaskHugePath;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 大目录中目录对象的DAO映射处理器
 *
 * @author oyealex
 * @version 1.0
 * @since 2020/8/25
 */
@Slf4j
public class HugePathTypeHandler implements TypeHandler<List<TaskHugePath.HugePath>> {
    private final Gson gson = new Gson();

    @Override
    public void setParameter(PreparedStatement ps, int i, List<TaskHugePath.HugePath> parameter, JdbcType jdbcType)
        throws SQLException {
        log.info("set parameter: {}", parameter);
        ps.setString(i, gson.toJson(parameter));
    }

    @Override
    public List<TaskHugePath.HugePath> getResult(ResultSet rs, String columnName) throws SQLException {
        final String value = rs.getString(columnName);
        log.info("get result: {}", value);
        Type type = new TypeToken<ArrayList<TaskHugePath.HugePath>>() {
        }.getType();
        return gson.fromJson(value, type);
    }

    @Override
    public List<TaskHugePath.HugePath> getResult(ResultSet rs, int columnIndex) throws SQLException {
        final String value = rs.getString(columnIndex);
        log.info("get result: {}", value);
        Type type = new TypeToken<ArrayList<TaskHugePath.HugePath>>() {
        }.getType();
        return gson.fromJson(value, type);
    }

    @Override
    public List<TaskHugePath.HugePath> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        final String value = cs.getString(columnIndex);
        log.info("get result: {}", value);
        Type type = new TypeToken<ArrayList<TaskHugePath.HugePath>>() {
        }.getType();
        return gson.fromJson(value, type);
    }
}
