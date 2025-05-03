package pragmatech.digital.workshops.lab2.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 * A filter that logs incoming HTTP requests and their processing time.
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        String remoteAddr = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        
        // Log the incoming request
        logger.info("Incoming request: {} {} from {} ({})", 
                requestMethod, requestURI, remoteAddr, userAgent);
        
        // Measure request processing time
        Instant start = Instant.now();
        
        try {
            // Continue the filter chain
            filterChain.doFilter(request, response);
        } finally {
            // Calculate and log the processing time
            Duration duration = Duration.between(start, Instant.now());
            int status = response.getStatus();
            
            logger.info("Completed request: {} {} - {} in {} ms", 
                    requestMethod, requestURI, status, duration.toMillis());
        }
    }
}