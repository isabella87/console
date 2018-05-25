package com.banhui.console.rpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

public abstract class AbstractProxy
        implements Proxy {
    private final Logger logger = LoggerFactory.getLogger(AbstractProxy.class);

    protected final Http http() {
        return HttpManager.getHttp();
    }

    protected void addHistory(
            Result result,
            Throwable throwable
    ) {
        if (throwable != null) {
            // 后端调用时发生了异常。
            if (throwable instanceof Error) {
                // 错误，应当立刻退出应用程序。
                logger.error("system error", throwable);
                System.exit(1);
            }

            if (throwable instanceof ExecutionException) {
                throwable = throwable.getCause();
            }
            if (throwable instanceof InvocationTargetException) {
                throwable = throwable.getCause();
            }

            if (throwable instanceof RpcException) {
                // Rpc异常，应当记录到历史中。
            } else if (throwable instanceof RuntimeException) {
                // 其它运行时异常，应当重新抛出。
                throw (RuntimeException) throwable;
            } else {
                // 受检异常，应当包装后抛出。
                throw new IllegalStateException(throwable);
            }
        } else {
            // 后端调用未发生异常。
        }
    }
}
