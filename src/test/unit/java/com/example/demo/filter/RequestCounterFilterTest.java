package com.example.demo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class RequestCounterFilterTest {

    private RequestCounterFilter requestCounterFilter;
    private ServletRequest servletRequest;
    private ServletResponse servletResponse;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        requestCounterFilter = new RequestCounterFilter();
        servletRequest = mock(ServletRequest.class);
        servletResponse = mock(ServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @Test
    void shouldIncrementRequestCount() throws ServletException, IOException {
        assertEquals(0, requestCounterFilter.getRequestCount());

        requestCounterFilter.doFilter(servletRequest, servletResponse, filterChain);

        assertEquals(1, requestCounterFilter.getRequestCount());
    }
}
