package com.sunbjx.demos.framework.cache.lock;

/**
 * cache lock 异常
 *
 * @author sunbjx
 * @since 2017/10/9 15:35
 */
public class LockException extends RuntimeException {

    private static final long serialVersionUID = 2091006936161389887L;

    public LockException() {
    }

    public LockException(String message) {
        super(message);
    }

    public LockException(Throwable cause) {
        super(cause);
    }
}
