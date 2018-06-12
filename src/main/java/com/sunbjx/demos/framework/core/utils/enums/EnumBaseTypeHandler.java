package com.sunbjx.demos.framework.core.utils.enums;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 所有统一集成EnumBase 的enum 可以不用再写TypeHandler
 *
 * @author sunbjx
 * @since 2018/6/12 11:06
 */
public class EnumBaseTypeHandler<T extends Enum & EnumBase> extends BaseTypeHandler<T> {

    private ConcurrentHashMap<Class, Type> typeCodeMapping = new ConcurrentHashMap();

    private Class<T> type = null;

    public EnumBaseTypeHandler() {
    }

    public EnumBaseTypeHandler(Class<T> type) throws NoSuchMethodException {
        this.type = type;
        Method codeMethod = type.getMethod("getCode");
        Type returnCls = codeMethod.getGenericReturnType();
        typeCodeMapping.put(type, returnCls);
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, T t, JdbcType jdbcType) throws SQLException {
        preparedStatement.setObject(i, t.getCode());
    }

    @Override
    public T getNullableResult(ResultSet resultSet, String s) throws SQLException {
        Type codeType = typeCodeMapping.get(type);
        if (codeType == String.class) {
            return EnumBaseUtils.getByCode(resultSet.getString(s), type);
        }
        if (codeType == Integer.class) {
            return EnumBaseUtils.getByCode(resultSet.getInt(s), type);
        }
        if (codeType == Long.class) {
            return EnumBaseUtils.getByCode(resultSet.getLong(s), type);
        }
        if (codeType == BigDecimal.class) {
            return EnumBaseUtils.getByCode(resultSet.getBigDecimal(s), type);
        }
        if (codeType == Boolean.class) {
            return EnumBaseUtils.getByCode(resultSet.getBoolean(s), type);
        }
        if (codeType == Short.class) {
            return EnumBaseUtils.getByCode(resultSet.getShort(s), type);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public T getNullableResult(ResultSet resultSet, int i) throws SQLException {
        Type codeType = typeCodeMapping.get(type);
        if (codeType == String.class) {
            return EnumBaseUtils.getByCode(resultSet.getString(i), type);
        }
        if (codeType == Integer.class) {
            return EnumBaseUtils.getByCode(resultSet.getInt(i), type);
        }
        if (codeType == Long.class) {
            return EnumBaseUtils.getByCode(resultSet.getLong(i), type);
        }
        if (codeType == BigDecimal.class) {
            return EnumBaseUtils.getByCode(resultSet.getBigDecimal(i), type);
        }
        if (codeType == Boolean.class) {
            return EnumBaseUtils.getByCode(resultSet.getBoolean(i), type);
        }
        if (codeType == Short.class) {
            return EnumBaseUtils.getByCode(resultSet.getShort(i), type);
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public T getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        Type codeType = typeCodeMapping.get(type);
        if (codeType == String.class) {
            return EnumBaseUtils.getByCode(callableStatement.getString(i), type);
        }
        if (codeType == Integer.class) {
            return EnumBaseUtils.getByCode(callableStatement.getInt(i), type);
        }
        if (codeType == Long.class) {
            return EnumBaseUtils.getByCode(callableStatement.getLong(i), type);
        }
        if (codeType == BigDecimal.class) {
            return EnumBaseUtils.getByCode(callableStatement.getBigDecimal(i), type);
        }
        if (codeType == Boolean.class) {
            return EnumBaseUtils.getByCode(callableStatement.getBoolean(i), type);
        }
        if (codeType == Short.class) {
            return EnumBaseUtils.getByCode(callableStatement.getShort(i), type);
        }
        throw new UnsupportedOperationException();
    }
}
