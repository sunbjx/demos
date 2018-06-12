package com.sunbjx.demos.framework.log.instrument.lts;

import com.github.ltsopensource.spring.tasktracker.JobDispatcher;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.logger.BizLogger;
import com.sunbjx.demos.framework.log.Span;
import com.sunbjx.demos.framework.log.Tracer;

/**
 * 主要是 具有 trace 功能的 dispatcher 功能
 *
 * @author sunbjx
 * @since 2016/10/20 10:07
 */
public class TraceJobDispatcher extends JobDispatcher {

    private LtsExtractor ltsExtractor = new LtsExtractor();

    private final Tracer tracer;

    public TraceJobDispatcher(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public com.github.ltsopensource.tasktracker.Result run(com.github.ltsopensource.tasktracker.runner.JobContext jobContext)
            throws Throwable {
        Span span = ltsExtractor.joinTrace(jobContext);
        if (span == null) {
            span = this.tracer.createSpan(jobContext.getJob().getTaskId());
        }
        BizLogger bizLogger = jobContext.getBizLogger();
        bizLogger.info("trackerId:" + span.getTraceId() + ",spanId:" + span.getSpanId());
        try {
            Result result = super.run(jobContext);
            return result;
        } finally {
            if (span != null) {
                this.tracer.close(span);
            }
        }

    }
}
