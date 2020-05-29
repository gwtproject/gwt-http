/*
 * Copyright © 2020 The GWT Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gwtproject.http.server;

import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Only for test purposes. Do not use this class in production environments! */
public abstract class TestServlet extends HttpServlet {

  private static final long serialVersionUID = -3137962571481312983L;

  private static final Logger LOGGER = Logger.getLogger(TestServlet.class.getCanonicalName());

  private static final String METHOD_GET = "GET";
  private static final String METHOD_OPTIONS = "OPTIONS";
  private static final String METHOD_POST = "POST";
  private static final String METHOD_PUT = "PUT";
  private static final String METHOD_DELETE = "DELETE";

  private static final String HEADER_ACCEPT = "Accept";
  private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
  private static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";

  private static final String HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS =
      "Access-Control-Allow-Credentials";
  private static final String HEADER_ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
  private static final String HEADER_ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
  private static final String HEADER_ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  private static final String HEADER_ACCESS_CONTROL_EXPOSE_HEADERS =
      "Access-Control-Expose-Headers";
  private static final String HEADER_CACHE_CONTROL = "Cache-Control";
  private static final String HEADER_CONNECTION = "Connection";
  private static final String HEADER_CONTENT_TYPE = "Content-Type";
  private static final String HEADER_COOKIE = "Cookie";
  private static final String HEADER_HOST = "Host";
  private static final String HEADER_SEC_FETCH_DEST = "Sec-Fetch-Dest";
  private static final String HEADER_SEC_FETCH_USER = "Sec-Fetch-User";
  private static final String HEADER_SEC_FETCH_SITE = "Sec-Fetch-Site";
  private static final String HEADER_SEC_FETCH_MODE = "Sec-Fetch-Mode";
  private static final String HEADER_UPGRADE_INSECURE_REQUESTS = "Upgrade-Insecure-Requests";
  private static final String HEADER_USER_AGENT = "User-Agent";

  private static final Set<String> CUSTOM_HEADERS =
      new HashSet<String>(Arrays.asList("header", "anotherheader", "foo", "Foo"));

  private static final Set<String> HEADERS =
      new HashSet<String>(
          Arrays.asList(
              HEADER_ACCEPT,
              HEADER_ACCEPT_ENCODING,
              HEADER_ACCEPT_LANGUAGE,
              HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS,
              HEADER_CACHE_CONTROL,
              HEADER_CONNECTION,
              HEADER_CONTENT_TYPE,
              HEADER_COOKIE,
              HEADER_HOST,
              HEADER_SEC_FETCH_DEST,
              HEADER_SEC_FETCH_USER,
              HEADER_SEC_FETCH_SITE,
              HEADER_SEC_FETCH_MODE,
              HEADER_UPGRADE_INSECURE_REQUESTS,
              HEADER_USER_AGENT));

  private static final Set<String> METHODS =
      new HashSet<String>(
          Arrays.asList(METHOD_GET, METHOD_OPTIONS, METHOD_POST, METHOD_PUT, METHOD_DELETE));

  @Override
  protected void doOptions(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    LOGGER.info(METHOD_OPTIONS);

    processRequest(request, response);
    response.setStatus(HttpServletResponse.SC_OK);
  }

  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    LOGGER.info("processRequest()");

    logHeaderInfo(request);

    Set<String> allowedHeaderNames = new LinkedHashSet<String>();
    String origin = "null";

    Enumeration<String> headerNames = request.getHeaderNames();

    while (headerNames.hasMoreElements()) {

      String headerName = headerNames.nextElement();

      if (CUSTOM_HEADERS.contains(headerName.toLowerCase())) {
        response.setHeader(headerName, request.getHeader(headerName));
      }

      if (headerName.equalsIgnoreCase("Origin")) {
        origin = request.getHeader(headerName);
      }

      allowedHeaderNames.add(headerName);
    }

    allowedHeaderNames.addAll(HEADERS);
    allowedHeaderNames.addAll(CUSTOM_HEADERS);
    String allowedHeaders = getHeaderNamesString(allowedHeaderNames);

    response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_METHODS, getHeaderNamesString(METHODS));
    response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_HEADERS, allowedHeaders);
    response.setHeader(HEADER_ACCESS_CONTROL_ALLOW_ORIGIN, origin);
    response.setHeader(HEADER_ACCESS_CONTROL_EXPOSE_HEADERS, allowedHeaders);

    String queryString = request.getQueryString();
    if (nonNull(queryString) && queryString.contains("credentials")) {
      String[] credentialsParam = queryString.split("=");
      if ("true".equals(credentialsParam[1]))
        response.addHeader(HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    }
  }

  private static String getHeaderNamesString(Set<String> headerNames) {

    StringBuilder headers = new StringBuilder();

    int i = 0;

    for (String header : headerNames) {

      if (i > 0) {
        headers.append(", ");
      }

      headers.append(header);

      i++;
    }

    return headers.toString();
  }

  private void logHeaderInfo(HttpServletRequest request) {

    StringBuilder headerInfos = new StringBuilder();

    Enumeration<String> headerNames = request.getHeaderNames();

    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      headerInfos
          .append(headerName)
          .append(": ")
          .append(request.getHeader(headerName))
          .append("\r\n");
    }

    LOGGER.info("---------------------------------------");
    LOGGER.info(headerInfos.toString());
    LOGGER.info("---------------------------------------");
  }
}
