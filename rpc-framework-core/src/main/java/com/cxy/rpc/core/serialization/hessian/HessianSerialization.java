package com.cxy.rpc.core.serialization.hessian;

import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import com.cxy.rpc.core.exception.SerializeException;
import com.cxy.rpc.core.serialization.Serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian 序列化算法
 *
 * @author cxy
 * @version 1.0
 * @ClassName HessianSerialization
 * 优点：
 * 跨语言支持： Hessian 提供了多语言的支持，因此可以在不同语言的系统中使用相同的序列化格式。
 * 紧凑的二进制格式： Hessian 使用紧凑的二进制格式，相比于文本格式的 JSON，数据传输更高效。
 * 缺点：
 * 性能： 尽管 Hessian 性能较好，但相对于一些专注于性能的序列化库，可能会有一些性能上的折衷。
 * 可读性： 由于是二进制格式，不像 JSON 那样易于人类阅读和调试。
 */
public class HessianSerialization implements Serialization {
    @Override
    public <T> byte[] serialize(T object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            HessianSerializerOutput hso = new HessianSerializerOutput(baos);
            hso.writeObject(object);
            hso.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new SerializeException("Hessian serialize failed.", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            HessianSerializerInput hsi = new HessianSerializerInput(bis);
            return (T) hsi.readObject();
        } catch (IOException e) {
            throw new SerializeException("Hessian deserialize failed.", e);
        }
    }
}
