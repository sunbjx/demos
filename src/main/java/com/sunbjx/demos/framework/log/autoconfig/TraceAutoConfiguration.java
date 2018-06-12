package com.sunbjx.demos.framework.log.autoconfig;


import com.sunbjx.demos.framework.log.DefaultSpanNamer;
import com.sunbjx.demos.framework.log.SpanNamer;
import com.sunbjx.demos.framework.log.Tracer;
import com.sunbjx.demos.framework.log.log.Slf4jSpanLogger;
import com.sunbjx.demos.framework.log.log.SpanLogger;
import com.sunbjx.demos.framework.log.tracer.DefaultTracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

/**
 * 自动注入trace
 *
 * @auther: Feng Yapeng
 * @since: 2016/9/21 11:21
 */
@Configuration
public class TraceAutoConfiguration {

	@Bean
	public Random randomForSpanIds() {
		return new Random();
	}

	@Bean
	public SpanNamer spanNamer() {
		return new DefaultSpanNamer();
	}

	@Bean
	public SpanLogger spanLogger() {
		return new Slf4jSpanLogger();
	}

	@Bean(name = "tracer")
	public Tracer sleuthTracer(Random random, SpanNamer spanNamer, SpanLogger spanLogger) {

		return new DefaultTracer(random, spanNamer, spanLogger);
	}

}
