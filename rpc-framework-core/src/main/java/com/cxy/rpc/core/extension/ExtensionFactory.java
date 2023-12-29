package com.cxy.rpc.core.extension;

@SPI
public interface ExtensionFactory {

    <T> T getExtension(Class<?> type, String name);

}
