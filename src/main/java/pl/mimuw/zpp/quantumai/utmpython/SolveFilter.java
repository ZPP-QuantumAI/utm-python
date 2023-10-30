package pl.mimuw.zpp.quantumai.utmpython;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@WebFilter("/solve")
public class SolveFilter extends OncePerRequestFilter {

    private final AtomicBoolean isSolving = new AtomicBoolean(false);
    @Override
    public void destroy() {
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
            if (request.getRequestURI().equals("/solve")) {
                if ( isSolving.compareAndSet(false, true)) {
                    try {
                        filterChain.doFilter(request, response);
                    } finally {
                        isSolving.set(false);
                    }
                } else {
                    HttpServletResponse httpResponse = (HttpServletResponse) response;
                    httpResponse.setStatus(HttpServletResponse.SC_CONFLICT);
                    httpResponse.getWriter().write("Turing machine is busy with another task, try again later");
                }
            } else {
                // For requests that don't match /solve, continue with the filter chain
                filterChain.doFilter(request, response);
            }
    }
}
