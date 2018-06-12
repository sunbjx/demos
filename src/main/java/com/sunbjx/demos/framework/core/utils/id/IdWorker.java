package com.sunbjx.demos.framework.core.utils.id;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 全局唯一ID生成器 (分布式唯一ID生成器)
 * 集群环境中使用，请为每台服务器分配不同的workid，每个应用使用不同的ApplicationId
 * @author sunbjx
 * @since 2018/6/12 10:45
 */
public class IdWorker {
    protected long workerId;
    protected long applicationId;
    protected long sequence           = 0L;
    protected long twepoch            = 1475251200000L; //Sat Oct 01 00:00:00 CST 2016
    protected long workerIdBits       = 4L; //节点ID长度
    protected long applicationIdBits  = 6L; //应用ID长度
    protected long maxWorkerId        = -1L ^ (-1L << workerIdBits); //最大支持机器节点数0~15，一共16个
    protected long maxApplicationId   = -1L ^ (-1L << applicationIdBits); //最大支持数据中心节点数0~63，一共63个
    protected long sequenceBits       = 12L; //序列号12位
    protected long workerIdShift      = sequenceBits; //机器节点左移12位
    protected long ApplicationIdShift = sequenceBits + workerIdBits; //数据中心节点左移16位
    protected long timestampLeftShift = sequenceBits + workerIdBits + applicationIdBits; //时间毫秒数左移22位
    protected long sequenceMask       = -1L ^ (-1L << sequenceBits); //每毫秒产生的序列数
    protected long lastTimestamp      = -1L;

    /**
     * 不支持一个系统中既实用单机又使用分布式来初始化
     */
    private volatile static IdWorker instance; //声明成 volatile

    /**
     * 是否已经初始化过
     */
    private AtomicBoolean initialized = new AtomicBoolean(false);

    /**
     *
     * @see #getInstance()
     */
    @Deprecated
    public static IdWorker getInstance(boolean isMutiServer) {
        if (instance == null) {
            synchronized (IdWorker.class) {
                if (instance == null) {
                    if (isMutiServer) {
                        instance = new IdWorker(IdWorkerConfig.getWorkerId(), IdWorkerConfig.getApplicationId());
                    } else {
                        instance = new IdWorker();
                    }

                }
            }
        }
        return instance;
    }

    /**
     * 构造默认的instance。如果IdWorkerConfig 的workId 和applicationId 不为空的话，则构造多机器指定的IdWork。
     * 否则构造默认的id work
     * @return
     */
    public static IdWorker getInstance() {
        if (instance == null) {
            synchronized (IdWorker.class) {
                if (instance == null) {
                    if (IdWorkerConfig.getWorkerId() != null && IdWorkerConfig.getApplicationId() != null) {
                        instance = new IdWorker(IdWorkerConfig.getWorkerId(), IdWorkerConfig.getApplicationId());
                    }else{
                        throw new  UnsupportedOperationException();
                    }

                }
            }
        }
        return instance;
    }

    /**
     * 仅仅单机环境下可以使用
     *
     * @return the id worker
     */
    @Deprecated
    public static IdWorker get() {
        return getInstance(false);
    }

    /**
     * Instantiates a new Id gen.
     */
    private IdWorker() {
        this(0, 0L);
    }

    /**
     * Instantiates a new Id gen.
     *
     * @param workerId      the worker id
     * @param ApplicationId the Application id
     */
    public IdWorker(long workerId, long ApplicationId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (ApplicationId > maxApplicationId || ApplicationId < 0) {
            throw new IllegalArgumentException(String.format("Application Id can't be greater than %d or less than 0", maxApplicationId));
        }
        this.workerId = workerId;
        this.applicationId = ApplicationId;
    }

    public synchronized long nextId() {
        long timestamp = timeGen(); //获取当前毫秒数
        //如果服务器时间有问题(时钟后退) 报错。
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        //如果上次生成时间和当前时间相同,在同一毫秒内
        if (lastTimestamp == timestamp) {
            //sequence自增，因为sequence只有12bit，所以和sequenceMask相与一下，去掉高位
            sequence = (sequence + 1) & sequenceMask;
            //判断是否溢出,也就是每毫秒内超过4095，当为4096时，与sequenceMask相与，sequence就等于0
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp); //自旋等待到下一毫秒
            }
        } else {
            sequence = new Random().nextInt(9); //如果和上次生成时间不同,重置sequence，就是下一毫秒开始，sequence计数重新从[0-9]开始累加
        }
        lastTimestamp = timestamp;
        // 最后按照规则拼出ID。
        // 000000000000000000000000000000000000000000  00000            00000       000000000000
        // time    applicationId   workerId    sequence
        return ((timestamp - twepoch) << timestampLeftShift) | (applicationId << ApplicationIdShift) | (workerId << workerIdShift)
                | sequence;
    }

    public String nextStringId() {
        return Long.toString(nextId());
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
