package com.techlabs.app.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RateLimitFilterTest {

    private RateLimitFilter rateLimitFilter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        rateLimitFilter = new RateLimitFilter();
        filterChain = mock(FilterChain.class);
    }

    @Test
    void allowsNonRateLimitedPaths() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/fortuneLife/plan");
        MockHttpServletResponse response = new MockHttpServletResponse();

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void allowsLoginWithinLimit() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/fortuneLife/auth/login");
        request.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertEquals(200, response.getStatus());
    }

    @Test
    void blocksAfterExceedingLimit() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/fortuneLife/auth/login");
        request.setRemoteAddr("10.0.0.1");

        // Make 10 allowed requests
        for (int i = 0; i < 10; i++) {
            MockHttpServletResponse response = new MockHttpServletResponse();
            rateLimitFilter.doFilterInternal(request, response, filterChain);
            assertEquals(200, response.getStatus());
        }

        // 11th request should be blocked
        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        rateLimitFilter.doFilterInternal(request, blockedResponse, filterChain);
        assertEquals(429, blockedResponse.getStatus());
        assertTrue(blockedResponse.getContentAsString().contains("Too many requests"));
    }

    @Test
    void rateLimitsOtpEndpoint() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/fortuneLife/auth/send-otp");
        request.setRemoteAddr("10.0.0.2");

        for (int i = 0; i < 10; i++) {
            MockHttpServletResponse response = new MockHttpServletResponse();
            rateLimitFilter.doFilterInternal(request, response, filterChain);
        }

        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        rateLimitFilter.doFilterInternal(request, blockedResponse, filterChain);
        assertEquals(429, blockedResponse.getStatus());
    }

    @Test
    void differentIpsHaveSeparateLimits() throws ServletException, IOException {
        // Exhaust limit for IP 1
        MockHttpServletRequest request1 = new MockHttpServletRequest("POST", "/fortuneLife/auth/login");
        request1.setRemoteAddr("10.0.0.10");
        for (int i = 0; i < 11; i++) {
            rateLimitFilter.doFilterInternal(request1, new MockHttpServletResponse(), filterChain);
        }

        // IP 2 should still be allowed
        MockHttpServletRequest request2 = new MockHttpServletRequest("POST", "/fortuneLife/auth/login");
        request2.setRemoteAddr("10.0.0.20");
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        rateLimitFilter.doFilterInternal(request2, response2, filterChain);
        assertEquals(200, response2.getStatus());
    }

    @Test
    void respectsXForwardedForHeader() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/fortuneLife/auth/login");
        request.setRemoteAddr("127.0.0.1");
        request.addHeader("X-Forwarded-For", "203.0.113.50, 70.41.3.18");

        for (int i = 0; i < 10; i++) {
            rateLimitFilter.doFilterInternal(request, new MockHttpServletResponse(), filterChain);
        }

        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        rateLimitFilter.doFilterInternal(request, blockedResponse, filterChain);
        assertEquals(429, blockedResponse.getStatus());

        // Same remote addr but different X-Forwarded-For should be allowed
        MockHttpServletRequest request2 = new MockHttpServletRequest("POST", "/fortuneLife/auth/login");
        request2.setRemoteAddr("127.0.0.1");
        request2.addHeader("X-Forwarded-For", "198.51.100.1");
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        rateLimitFilter.doFilterInternal(request2, response2, filterChain);
        assertEquals(200, response2.getStatus());
    }
}
