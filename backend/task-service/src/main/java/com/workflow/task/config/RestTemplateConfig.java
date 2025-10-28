package com.workflow.task.config;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.NewSpan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class RestTemplateConfig {
    
    @Autowired
    private Tracer tracer;
    
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Add tracing interceptor to propagate trace context and add span information
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add((request, body, execution) -> {
            // Create a new span for the outgoing request
            String serviceName = extractServiceName(request.getURI().toString());
            String operationName = String.format("http %s %s", request.getMethod(), serviceName);
            
            // Propagate trace context headers
            if (tracer.currentSpan() != null) {
                request.getHeaders().add("X-Trace-Id", tracer.currentSpan().context().traceId());
                request.getHeaders().add("X-Span-Id", tracer.currentSpan().context().spanId());
            }
            
            return execution.execute(request, body);
        });
        
        return restTemplate;
    }
    
    private String extractServiceName(String url) {
        if (url.contains("camunda")) {
            return "camunda";
        } else if (url.contains("auth-service")) {
            return "auth-service";
        } else if (url.contains("workflow-service")) {
            return "workflow-service";
        } else if (url.contains("task-service")) {
            return "task-service";
        }
        return "external-service";
    }
}
