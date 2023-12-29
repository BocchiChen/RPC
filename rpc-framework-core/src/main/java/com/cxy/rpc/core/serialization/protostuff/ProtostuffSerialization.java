package com.cxy.rpc.core.serialization.protostuff;

import com.cxy.rpc.core.exception.SerializeException;
import com.cxy.rpc.core.serialization.Serialization;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Protostuff 序列化算法实现类
 *
 * @author cxy
 * @version 1.0
 * @ClassName ProtostuffSerialization
 * 优点：
 * 性能： Protostuff 也是一个专注于性能的库，通常比一些通用序列化库性能更好。
 * 紧凑格式： 生成的二进制数据通常较小，传输效率较高。
 * 缺点：
 * 跨语言支持： 相对于一些支持多语言的库，Protostuff 可能在跨语言支持上有一些限制。
 * 可读性： 由于是二进制格式，不像 JSON 那样易于人类阅读和调试。
 */
public class ProtostuffSerialization implements Serialization {

    private final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> byte[] serialize(T object) {
        try {
            Schema schema = RuntimeSchema.getSchema(object.getClass());
            return ProtostuffIOUtil.toByteArray(object, schema, BUFFER);
        } catch (Exception e) {
            throw new SerializeException("Protostuff serialize failed.", e);
        } finally {
            // 重置 buffer
            BUFFER.clear();
        }
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        try {
            Schema<T> schema = RuntimeSchema.getSchema(clazz);
            T object = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(bytes, object, schema);
            return object;
        } catch (Exception e) {
            throw new SerializeException("Protostuff deserialize failed.", e);
        }
    }
}
