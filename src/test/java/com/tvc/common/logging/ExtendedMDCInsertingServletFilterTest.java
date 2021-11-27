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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.slf4j.MDC;

public class ExtendedMDCInsertingServletFilterTest {

    private static final String REQ = "req_";

    private static final String REMOTE_HOST = "192.0.2.0"; // NOPMD AvoidUsingHardCodedIP

    private static final String X_FORWARDED_FOR = "198.51.100.0"; // NOPMD AvoidUsingHardCodedIP

    ExtendedMDCInsertingServletFilter sut = new ExtendedMDCInsertingServletFilter();


    @org.junit.Test
    public void testDoFilter() throws Exception {
        // setup
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foobar");
        request.setQueryString("baz=qux");
        request.setRemoteHost(REMOTE_HOST);
        request.addHeader("User-Agent", "sample-ua");
        request.addHeader("X-Forwarded-For", X_FORWARDED_FOR);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final FilterChain chain = spy(new FilterChain() {
            @Override
            public void doFilter(final ServletRequest req, final ServletResponse res) {
                assertThat(MDC.get(REQ + ExtendedMDCInsertingServletFilter.REQUEST_REMOTE_HOST_MDC_KEY))
                        .isEqualTo(REMOTE_HOST);
                assertThat(MDC.get(REQ + ExtendedMDCInsertingServletFilter.REQUEST_USER_AGENT_MDC_KEY))
                        .isEqualTo("sample-ua");
                assertThat(MDC.get(REQ + ExtendedMDCInsertingServletFilter.REQUEST_METHOD))
                        .isEqualTo("GET");
                assertThat(MDC.get(REQ + ExtendedMDCInsertingServletFilter.REQUEST_REQUEST_URI))
                        .isEqualTo("/foobar");
                assertThat(MDC.get(REQ + ExtendedMDCInsertingServletFilter.REQUEST_REQUEST_URL))
                        .isEqualTo("http://localhost/foobar");
                assertThat(MDC.get(REQ + ExtendedMDCInsertingServletFilter.REQUEST_QUERY_STRING))
                        .isEqualTo("baz=qux");
                assertThat(MDC.get(REQ + ExtendedMDCInsertingServletFilter.REQUEST_X_FORWARDED_FOR))
                        .isEqualTo(X_FORWARDED_FOR);
            }
        });
        // exercise
        sut.doFilter(request, response, chain);
        // verify
        verify(chain).doFilter(request, response);

        assertThat(MDC.get(REQ + ExtendedMDCInsertingServletFilter.REQUEST_REMOTE_HOST_MDC_KEY)).isNull();
        assertThat(MDC.get(REQ + ExtendedMDCInsertingServletFilter.REQUEST_USER_AGENT_MDC_KEY)).isNull();
        assertThat(MDC.get(REQ + ExtendedMDCInsertingServletFilter.REQUEST_METHOD)).isNull();
        assertThat(MDC.get(REQ + ExtendedMDCInsertingServletFilter.REQUEST_REQUEST_URI)).isNull();
        assertThat(MDC.get(REQ + ExtendedMDCInsertingServletFilter.REQUEST_REQUEST_URL)).isNull();
        assertThat(MDC.get(REQ + ExtendedMDCInsertingServletFilter.REQUEST_QUERY_STRING)).isNull();
        assertThat(MDC.get(REQ + ExtendedMDCInsertingServletFilter.REQUEST_X_FORWARDED_FOR)).isNull();
    }
}
