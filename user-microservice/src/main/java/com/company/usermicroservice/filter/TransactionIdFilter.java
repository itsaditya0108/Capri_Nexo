package com.company.usermicroservice.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class TransactionIdFilter implements Filter {

    public static final String TX_ID_HEADER = "X-TX-ID";

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;
        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        // 1. Read txId if already present
        String txId = httpRequest.getHeader(TX_ID_HEADER);

        // 2. Generate simple txId if missing
        if (txId == null || txId.isBlank()) {
            txId = "TX" + System.currentTimeMillis();
        }

        try {
            MDC.put("txId", txId);

            // 3. Add txId to response (Postman visibility)
            httpResponse.setHeader(TX_ID_HEADER, txId);

            chain.doFilter(request, response);

        } finally {
            MDC.clear();
        }
    }
}
