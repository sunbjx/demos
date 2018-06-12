package com.sunbjx.demos.framework.cache.lock;

import com.sunbjx.demos.framework.cache.CacheResult;
import com.sunbjx.demos.framework.cache.CacheTimeUnit;
import com.sunbjx.demos.framework.cache.ICache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;

/**
 * 使用icache 实现重入分布式锁.lock 多少次，记得unlock 多少次。
 *
 * @author sunbjx
 * @since 2016 -11-01 15:21:00
 */
public class ICacheReentrantLock implements Lock {

    private static final Logger logger = LoggerFactory.getLogger(ICacheReentrantLock.class);


    // lock flag stored in redis
    private static final String LOCKED_VALUE = "TRUE";

    private ICache iCache;


    // cache expires (seconds)
    private int lockedExpires = 30;

    //  try lock time(300 SECONDS,5分钟)
    private static int DEFAULT_TIME_OUT = 30000;

    /**
     * 重试等待时间
     */
    protected int retryAwait = 300;

    // private icache
    private LockValue key;

    private AtomicInteger lockCounts = new AtomicInteger();

    public ICacheReentrantLock(ICache iCache, String key) {
        this.iCache = iCache;
        this.key = new LockValue(key);
    }

    public ICacheReentrantLock(ICache iCache, int lockedExpires, LockValue key) {
        this.iCache = iCache;
        this.lockedExpires = lockedExpires;
        this.key = key;
    }

    @Override
    public void lock() {
        try {
            lockInterruptibly();
        } catch (InterruptedException e) {
            throw new LockException(e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        boolean locked = tryLock(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        if (locked) {
            return;
        }
        throw new InterruptedException("尝试lock资源出现错误");
    }

    @Override
    public boolean tryLock() {
        try {
            tryLock(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warn("try lock key: {} had time out。", key);
        }
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long startMillis = System.currentTimeMillis();
        Long millisToWait = (unit != null) ? unit.toMillis(time) : null;
        int counts = lockCounts.incrementAndGet();
        if (counts > 1) {
            iCache.resetExpire(key.toString(), lockedExpires, CacheTimeUnit.SECOND);
            return true;
        }
        if (counts == 1) {
            boolean locked = false;
            while (!locked) {
                CacheResult cacheResult = iCache.putIfAbsent(key.toString(), LOCKED_VALUE, lockedExpires, CacheTimeUnit.SECOND);
                if (cacheResult.isSuccess()) {
                    locked = true;
                    break;
                } else {
                    if (System.currentTimeMillis() - startMillis - retryAwait > millisToWait) {
                        break;
                    }
                    LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(retryAwait));
                }
            }
            return locked;
        }
        throw new LockException("Lock count has gone negative for lock: " + key.key);
    }


    @Override
    public void unlock() {
        int i = lockCounts.decrementAndGet();
        if (i < 0) {
            throw new LockException("Lock count has gone negative for lock: " + key.key);
        }
        if (i > 0) {
            return;
        }
        if (i == 0) {
            CacheResult delete = iCache.delete(key.toString());
            if (delete.isSuccess()) {
                return;
            } else {
                throw new LockException("unlock key :" + key.key + " is not success");
            }
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }


    public static class LockValue {

        private String key;

        private final String lockedNameSpace = ":locked:";

        public LockValue(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return lockedNameSpace + key;
        }
    }


}
