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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet component of the {@link org.gwtproject.http.client.RequestBuilderTest}.
 *
 * <p>Only for test purposes. Do not use this class in production environments!
 */
@SuppressWarnings("serial")
public class RequestBuilderTestServlet extends TestServlet {

  private static final Logger LOGGER =
      Logger.getLogger(RequestBuilderTestServlet.class.getCanonicalName());

  public static final String SERVLET_DELETE_RESPONSE = "delete";
  public static final String SERVLET_GET_RESPONSE = "get";
  public static final String SERVLET_POST_RESPONSE = "post";
  // W3C's XMLHttpRequest requires it be the empty string
  public static final String SERVLET_HEAD_RESPONSE = "";
  public static final String SERVLET_PUT_RESPONSE = "put";

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    processRequest(request, response);

    try {
      response.setStatus(HttpServletResponse.SC_OK);
      response.getWriter().print(SERVLET_DELETE_RESPONSE);
    } catch (IOException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    LOGGER.info("doGet");

    processRequest(request, response);

    String pathInfo = request.getPathInfo();
    switch (pathInfo) {
      case "/setRequestHeader":
        String value = request.getHeader("Foo");
        if (value.equals("Bar1")) {
          response.setStatus(HttpServletResponse.SC_OK);
          response.getWriter().print(SERVLET_GET_RESPONSE);
        } else {
          response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        break;
      case "/send_GET":
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(SERVLET_GET_RESPONSE);
        break;
      case "/sendRequest_GET":
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(SERVLET_GET_RESPONSE);
        break;
      case "/setTimeout/timeout":
        // cause a timeout on the client
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(SERVLET_GET_RESPONSE);
        break;
      case "/setTimeout/noTimeout":
        // wait but not long enough to timeout
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(SERVLET_GET_RESPONSE);
        break;
      case "/user/pass":
        String auth = request.getHeader("Authorization");
        if (auth == null) {
          response.setHeader("WWW-Authenticate", "BASIC");
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
          response.setStatus(HttpServletResponse.SC_OK);
          response.getWriter().print(SERVLET_GET_RESPONSE);
        }
        break;
      default:
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        break;
    }
  }

  @Override
  protected void doHead(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    processRequest(request, response);
    response.setStatus(HttpServletResponse.SC_OK);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException {
    try {

      processRequest(request, response);

      if (request.getPathInfo().equals("/sendRequest_POST")) {
        response.getWriter().print(SERVLET_POST_RESPONSE);
        response.setStatus(HttpServletResponse.SC_OK);
      } else {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      }
    } catch (IOException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  protected void doPut(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    processRequest(request, response);

    BufferedReader reader = request.getReader();
    String content = reader.readLine();
    if (content != null && content.equals("<html><body>Put Me</body></html>")) {
      response.getWriter().print(SERVLET_PUT_RESPONSE);
      response.setStatus(HttpServletResponse.SC_OK);
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
  }
}
