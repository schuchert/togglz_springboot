package org.shoe.togglz;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RequestTokenValidator extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (hasToken(request, response)) {
            filterChain.doFilter(request, response);
        }
    }

    private boolean hasToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = request.getHeader("authorization");

        if (token == null) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Required authorization header/token missing");
            return false;
        }

        return true;
    }

}
