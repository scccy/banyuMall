package com.origin.banyu.coreflink.source;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 通用JSON反序列化器
 * 支持任何数据类型的JSON反序列化
 *
 * @author Linus Torvalds
 * @since 2024-01-01
 */
@Slf4j
@Component
public class GenericJsonDeserializer<T> implements DeserializationSchema<T> {
    
    private final Class<T> targetClass;
    
    public GenericJsonDeserializer(Class<T> targetClass) {
        this.targetClass = targetClass;
    }
    
    @Override
    public T deserialize(byte[] message) throws IOException {
        try {
            String json = new String(message);
            log.debug("Deserializing message: {}", json);
            
            T result = JSON.parseObject(json, targetClass);
            log.debug("Successfully deserialized to: {}", result);
            
            return result;
        } catch (Exception e) {
            log.error("Failed to deserialize message: {}", new String(message), e);
            throw new IOException("Failed to deserialize JSON to " + targetClass.getSimpleName(), e);
        }
    }
    
    @Override
    public boolean isEndOfStream(T nextElement) {
        return false;
    }
    
    @Override
    public TypeInformation<T> getProducedType() {
        return TypeInformation.of(targetClass);
    }
    
    /**
     * 创建反序列化器实例的静态工厂方法
     */
    public static <T> GenericJsonDeserializer<T> of(Class<T> targetClass) {
        return new GenericJsonDeserializer<>(targetClass);
    }
}
