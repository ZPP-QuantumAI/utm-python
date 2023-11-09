package pl.mimuw.zpp.quantumai.utmpython.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import pl.mimuw.zpp.quantumai.utmpython.UtmPythonApplication;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static jakarta.servlet.http.HttpServletResponse.SC_CONFLICT;

@Slf4j
@WebFilter(value = "/solve")
public class SingleUseFilter implements Filter {

    private static final boolean USED = true;
    private static final boolean NOT_USED = false;
    private static final AtomicBoolean IS_USED = new AtomicBoolean(NOT_USED);

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        if (IS_USED.compareAndSet(NOT_USED, USED)) {
            log.info("successfully filtered request - the machine is currently not in use");
            chain.doFilter(request, response);
            UtmPythonApplication.shutdown();
        } else {
            log.info("the machine is already in use, not passing request to the next entity of filter chain");
            response.getWriter().write("machine currently busy");
            ((HttpServletResponse) response).setStatus(SC_CONFLICT);
        }
    }
}
