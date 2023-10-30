package pl.mimuw.zpp.quantumai.utmpython;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@Configuration
public class ServletConfiguration {

    private final AtomicBoolean isSolving = new AtomicBoolean(false);

    @Bean
    OncePerRequestFilter mdcFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                if (request.getRequestURI().equals("/solve")) {
                    if (isSolving.compareAndSet(false, true)) {
                        try {
                            filterChain.doFilter(request, response);
                        } finally {
                            isSolving.set(false);
                        }
                    } else {
                        response.setStatus(javax.servlet.http.HttpServletResponse.SC_CONFLICT);
                        response.getWriter().write("Turing machine is busy with another task, try again later");
                    }
                } else {
                    filterChain.doFilter(request, response);
                }

            }
        };
    }
}
