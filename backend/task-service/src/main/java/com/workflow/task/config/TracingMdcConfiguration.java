package com.workflow.task.config;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class TracingMdcConfiguration implements WebMvcConfigurer {
    
    private final Tracer tracer;
    
    public TracingMdcConfiguration(Tracer tracer) {
        this.tracer = tracer;
    }
    
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
                // Add trace ID and span ID to MDC
                Span currentSpan = tracer.currentSpan();
                if (currentSpan != null) {
                    String traceId = currentSpan.context().traceId();
                    String spanId = currentSpan.context().spanId();
                    MDC.put("traceId", traceId);
                    MDC.put("spanId", spanId);
                }
                return true;
            }
            
            @Override
            public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
                // Clean up MDC after request completes
                MDC.remove("traceId");
                MDC.remove("spanId");
            }
        });
    }
}
