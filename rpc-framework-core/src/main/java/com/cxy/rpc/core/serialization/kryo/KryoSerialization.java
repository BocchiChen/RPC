package com.cxy.rpc.core.serialization.kryo;

import com.cxy.rpc.core.common.RpcRequest;
import com.cxy.rpc.core.common.RpcResponse;
import com.cxy.rpc.core.exception.SerializeException;
import com.cxy.rpc.core.serialization.Serialization;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Kryo 序列化算法
 * <p>
 * <a href="https://www.cnblogs.com/lxyit/p/12511645.html">相关简介</a><br>
 * <a href="https://github.com/EsotericSoftware/kryo">github地址</a>
 * </p>
 *
 * @author cxy
 * @version 1.0
 * @ClassName KryoSerialization
 * 优点：
 * 性能： Kryo 是一个专注于性能的序列化库，通常比一些通用序列化库性能更好。
 * 紧凑格式： 生成的二进制数据通常较小，传输效率较高。
 * 缺点：
 * 跨语言支持： 相对于一些支持多语言的库，Kryo 可能在跨语言支持上有一些限制。
 * 版本兼容性： 与一些支持版本兼容性的库相比，可能需要额外的处理。
 */
public class KryoSerialization implements Serialization {

    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        return kryo;
    });

    @Override
    public <T> byte[] serialize(T object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Output output = new Output(baos);
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, object);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializeException("Kryo serialize failed.", e);
        }
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            Input input = new Input(bais);
            Kryo kryo = kryoThreadLocal.get();
            T object = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return object;
        } catch (Exception e) {
            throw new SerializeException("Kryo deserialize failed.", e);
        }
    }
}
