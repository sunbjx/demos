/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sunbjx.demos.framework.log;

import java.lang.annotation.*;

/**
 * Annotation to provide the name for the span. You should annotate all your
 * custom {@link Runnable Runnable} or {@link java.util.concurrent.Callable Callable} classes
 * for the instrumentation logic to pick up how to name the span.
 * <p>
 *
 * Having for example the following code
 * <pre>{@code
 *     @SpanName("custom-operation")
 *     class CustomRunnable implements Runnable {
 *         @Override
 *         public void run() {
 *          // latency of this method will be recorded in a span named "custom-operation"
 *         }
 *      }
 * }</pre>
 *
 * Will result in creating a span with name {@code custom-operation}.
 * <p>
 *
 * When there's no @SpanName annotation, {@code toString} is used. Here's an
 * example of the above, but via an anonymous instance.
 * <pre>{@code
 *     return new Runnable() {
 *          -- snip --
 *
 *          @Override
 *          public String toString() {
 *              return "custom-operation";
 *          }
 *     };
 * }</pre>
 *
 * @author Marcin Grzejszczak
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SpanName {
	/**
	 * Name of the span to be resolved at runtime
	 */
	String value();
}
