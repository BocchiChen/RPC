package com.cxy.rpc.core.serialization.jdk;

import com.cxy.rpc.core.exception.SerializeException;
import com.cxy.rpc.core.serialization.Serialization;

import java.io.*;

/**
 * JDK 序列化算法
 *
 * @author cxy
 * @version 1.0
 * @ClassName JdkSerialization
 * 优点：
 * 标准Java API： JDK 序列化是 Java 标准库的一部分，不需要额外的依赖。
 * 对象图支持： 支持复杂对象图的序列化，包括循环引用等。
 * 缺点：
 * 性能： 相对于一些专注于性能的库，JDK 序列化的性能可能较差。
 * 版本兼容性： 对类定义的变化较敏感，可能需要在一些场景下处理版本兼容性问题。
 */
public class JdkSerialization implements Serialization {
    @Override
    public <T> byte[] serialize(T object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new SerializeException("Jdk serialize failed.", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializeException("Jdk deserialize failed.", e);
        }
    }
}
