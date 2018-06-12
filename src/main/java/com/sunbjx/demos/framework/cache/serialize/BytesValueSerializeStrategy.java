package com.sunbjx.demos.framework.cache.serialize;

import com.sunbjx.demos.framework.cache.util.InternalCacheSerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectStreamConstants;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * Bytes 的序列化策略
 *
 * @author sunbjx
 * @since 2016/6/13 17:28
 */
public class BytesValueSerializeStrategy implements ValueSerializeStrategy<byte[]> {

    private static final Logger logger = LoggerFactory.getLogger(BytesValueSerializeStrategy.class);


    @Override
    public ValueSerializeResultWrapper<byte[]> valueSerialize(Serializable serializable) throws IOException {
        byte[] serializeValue = InternalCacheSerializeUtils.objectToBytes(serializable);
        return new ValueSerializeResultWrapper<byte[]>(serializeValue, serializable);
    }

    @Override
    public <T> T valueDeSerialize(byte[] serValue, Type type) throws IOException, ClassNotFoundException {
        if (serValue == null) {
            return null;
        }
        if (checkIsJavaSerialize(serValue)) {
            if (logger.isTraceEnabled()) {
                logger.trace("serValue 符合java serialize 规则");
            }
            return (T) InternalCacheSerializeUtils.bytesToObject(serValue);
        }
        logger.debug("serValue 不符合java serialize 规则，使用String 序列化返回");
        return normalDeSerialize(serValue, type);

    }

    /**
     * 普通的
     */
    public <T> T normalDeSerialize(byte[] serValue, Type type) {
        String strValue = new String(serValue);
        if (type == String.class) {
            return (T) strValue;
        }
        if (type == Integer.class) {
            return (T) Integer.valueOf(strValue);
        }
        if (type == Long.class) {
            return (T) Long.valueOf(strValue);
        }
        if (type == BigDecimal.class) {
            return (T) BigDecimal.valueOf(Long.parseLong(strValue));
        }
        if (type == Boolean.class) {
            return (T) Boolean.valueOf(strValue);
        }
        if (type == Short.class) {
            return (T) Short.valueOf(strValue);
        }
        return (T) strValue;
    }

    /**
     * 验证 数组的魔数 和 版本~
     */
    public boolean checkIsJavaSerialize(byte[] serValue) {
        if (serValue.length >= 4) {
            // 不是java 类型
            short magicNum = (short) (((serValue[0] & 0xFF) << 8) + (serValue[1] & 0xFF));// 获取魔数
            short versionNum = (short) (((serValue[2] & 0xFF) << 8) + (serValue[3] & 0xFF));// 获取版本
            if (magicNum == ObjectStreamConstants.STREAM_MAGIC && versionNum == ObjectStreamConstants.STREAM_VERSION) {
                return true;
            }
        }
        return false;
    }
}
