package com.cxy.rpc.core.factory;

import com.cxy.rpc.core.config.ThreadPoolConfig;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolFactory {
    private static final int AVAILABLE_PROCESSOR_NUMBER = Runtime.getRuntime().availableProcessors();

    private static ThreadPoolConfig threadPoolConfig;

    public ThreadPoolFactory() {
        threadPoolConfig = new ThreadPoolConfig();
    }

    public static ThreadPoolExecutor getDefaultThreadPool() {
        return new ThreadPoolExecutor(threadPoolConfig.getCorePoolSize(),
                threadPoolConfig.getMaximumPoolSize(),
                threadPoolConfig.getKeepAliveTime(),
                threadPoolConfig.getTimeUnit(),
                threadPoolConfig.getWorkQueue());
    }
}
