package com.sunbjx.demos.framework.log.instrument.lts;


import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.sunbjx.demos.framework.log.Span;
import com.sunbjx.demos.framework.log.SpanExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * lts的展开
 *
 * @author sunbjx
 * @since 2016/10/20 10:11
 */
public class LtsExtractor implements SpanExtractor<JobContext> {

    private static final Logger logger = LoggerFactory.getLogger(LtsExtractor.class);


    @Override
    public Span joinTrace(JobContext jobContext) {
        String traceIdHex = jobContext.getJob().getParam(Span.TRACE_ID_NAME);
        if (traceIdHex == null) {
            // can't build a Span without trace id
            return null;
        }
        long traceId = Span.hexToId(traceIdHex);
        long spanId = spanId(jobContext, traceId);
        Span.SpanBuilder spanBuilder = Span.builder().traceId(traceId).spanId(spanId);
        String parentIdHex = jobContext.getJob().getParam(Span.PARENT_ID_NAME);
        if (parentIdHex != null) {
            spanBuilder.parent(Span.hexToId(parentIdHex));
        }
        return spanBuilder.build();
    }

    private long spanId(JobContext jobContext, long traceId) {
        String spanId = jobContext.getJob().getParam(Span.SPAN_ID_NAME);
        if (spanId == null) {
            logger.debug("Request is missing a span id but it has a trace id. We'll assume that this is "
                    + "a root span with span id equal to trace id");
            return traceId;
        } else {
            return Span.hexToId(spanId);
        }
    }
}
