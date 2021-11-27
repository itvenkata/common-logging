/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tvc.common.logging;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.MDC;


import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A servlet filter that inserts various values retrieved from the incoming http request into the MDC.
 *
 * <p>The values are removed after the request is processed.</p>
 */
public class ExtendedMDCInsertingServletFilter implements Filter {

    static final String REQUEST_REMOTE_HOST_MDC_KEY = "remoteHost";

    static final String REQUEST_USER_AGENT_MDC_KEY = "userAgent";

    static final String REQUEST_REQUEST_URI = "requestURI";

    static final String REQUEST_QUERY_STRING = "queryString";

    static final String REQUEST_REQUEST_URL = "requestURL";

    static final String REQUEST_METHOD = "method";

    static final String REQUEST_X_FORWARDED_FOR = "xForwardedFor";

    @Setter
    @Getter
    private String prefix = "req_";

    private static void putIfNotNull(final String key, final String value) {
        if (value != null && value.isEmpty() == false) {
            MDC.put(key, value);
        }
    }

    @Override
    public void init(final javax.servlet.FilterConfig filterConfig) throws javax.servlet.ServletException {
        // nothing to do
    }

    @Override
    public void destroy() {
        // nothing to do
    }

    @Override
    public void doFilter(final javax.servlet.ServletRequest request, final javax.servlet.ServletResponse response,
                         final javax.servlet.FilterChain chain)
            throws IOException, javax.servlet.ServletException {
        try {
            insertIntoMDC(request);
            chain.doFilter(request, response);
        } finally {
            clearMDC();
        }
    }

    void insertIntoMDC(final javax.servlet.ServletRequest request) {
        putIfNotNull(prefix + REQUEST_REMOTE_HOST_MDC_KEY, request.getRemoteHost());

        if (request instanceof HttpServletRequest) {
            final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            putIfNotNull(prefix + REQUEST_REQUEST_URI, httpServletRequest.getRequestURI());
            final StringBuffer requestURL = httpServletRequest.getRequestURL();
            if (requestURL != null) {
                putIfNotNull(prefix + REQUEST_REQUEST_URL, requestURL.toString());
            }
            putIfNotNull(prefix + REQUEST_METHOD, httpServletRequest.getMethod());
            putIfNotNull(prefix + REQUEST_QUERY_STRING, httpServletRequest.getQueryString());
            putIfNotNull(prefix + REQUEST_USER_AGENT_MDC_KEY, httpServletRequest.getHeader("User-Agent"));
            putIfNotNull(prefix + REQUEST_X_FORWARDED_FOR, httpServletRequest.getHeader("X-Forwarded-For"));
        }
    }

    void clearMDC() {
        MDC.remove(prefix + REQUEST_REMOTE_HOST_MDC_KEY);
        MDC.remove(prefix + REQUEST_REQUEST_URI);
        MDC.remove(prefix + REQUEST_QUERY_STRING);
        // removing possibly inexistent item is OK
        MDC.remove(prefix + REQUEST_REQUEST_URL);
        MDC.remove(prefix + REQUEST_METHOD);
        MDC.remove(prefix + REQUEST_USER_AGENT_MDC_KEY);
        MDC.remove(prefix + REQUEST_X_FORWARDED_FOR);
    }
}
