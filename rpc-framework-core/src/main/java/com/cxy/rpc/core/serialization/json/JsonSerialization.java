package com.cxy.rpc.core.serialization.json;

import com.google.gson.*;
import com.cxy.rpc.core.exception.SerializeException;
import com.cxy.rpc.core.serialization.Serialization;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 基于 Gson 库实现的 JSON 序列化算法类
 *
 * @author cxy
 * @version 1.0
 * @ClassName JsonSerialization
 * 优点：
 * 可读性： JSON 是一种文本格式，易于人类阅读和调试。
 * 广泛支持： 在 Web 开发和 RESTful API 中广泛使用，被大多数编程语言支持。
 * 缺点：
 * 冗余性： 相对于二进制格式，JSON 的文本格式可能包含一些冗余信息，导致数据传输效率较低。
 * 复杂对象支持： 对于复杂对象结构的支持相对较差，可能需要额外的处理。
 */
public class JsonSerialization implements Serialization {

    /**
     * 自定义 JavClass 对象序列化，解决 Gson 无法序列化 Class 信息
     */
    static class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {
        @SneakyThrows
        @Override
        public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String name = json.getAsString();
            return Class.forName(name);
        }

        @Override
        public JsonElement serialize(Class<?> src, Type typeOfSrc, JsonSerializationContext context) {
            // class -> json
            return new JsonPrimitive(src.getName());
        }
    }

    @Override
    public <T> byte[] serialize(T object) {
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
            String json = gson.toJson(object);
            return json.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SerializeException("Json serialize failed.", e);
        }
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        try {
            Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
            String json = new String(bytes, StandardCharsets.UTF_8);
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            throw new SerializeException("Json deserialize failed.", e);
        }
    }
}
