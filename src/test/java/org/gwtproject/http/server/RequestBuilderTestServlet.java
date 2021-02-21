/*
 * Copyright 2008 The GWT Project Authors
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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gwtproject.http.client.RequestBuilderTest;
import org.gwtproject.http.shared.RequestBuilderTestConstants;

/** Servlet component of the {@link RequestBuilderTest}. */
@SuppressWarnings("serial")
@WebServlet("/testRequestBuilder/*")
public class RequestBuilderTestServlet extends HttpServlet {

  @Override
  protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
    try {
      response.setStatus(HttpServletResponse.SC_OK);
      response.getWriter().print(RequestBuilderTestConstants.SERVLET_DELETE_RESPONSE);
    } catch (IOException e) {
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String pathInfo = request.getPathInfo();
    switch (pathInfo) {
      case "/setRequestHeader":
        String value = request.getHeader("Foo");
        if (value.equals("Bar1")) {
          response.setStatus(HttpServletResponse.SC_OK);
          response.getWriter().print(RequestBuilderTestConstants.SERVLET_GET_RESPONSE);
        } else {
          response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        break;
      case "/send_GET":
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(RequestBuilderTestConstants.SERVLET_GET_RESPONSE);
        break;
      case "/sendRequest_GET":
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(RequestBuilderTestConstants.SERVLET_GET_RESPONSE);
        break;
      case "/setTimeout/timeout":
        // cause a timeout on the client
        try {
          Thread.sleep(5000);
        } catch (InterruptedException e) {
          throw new AssertionError(e);
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(RequestBuilderTestConstants.SERVLET_GET_RESPONSE);
        break;
      case "/setTimeout/noTimeout":
        // wait but not long enough to timeout
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new AssertionError(e);
        }
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(RequestBuilderTestConstants.SERVLET_GET_RESPONSE);
        break;
      case "/user/pass":
        String auth = request.getHeader("Authorization");
        if (auth == null) {
          response.setHeader("WWW-Authenticate", "BASIC");
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
          response.setStatus(HttpServletResponse.SC_OK);
          response.getWriter().print(RequestBuilderTestConstants.SERVLET_GET_RESPONSE);
        }
        break;
      default:
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        break;
    }
  }

  @Override
  protected void doHead(HttpServletRequest request, HttpServletResponse response) {
    response.setStatus(HttpServletResponse.SC_OK);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) {
    try {
      if (request.getPathInfo().equals("/sendRequest_POST")) {
        response.getWriter().print(RequestBuilderTestConstants.SERVLET_POST_RESPONSE);
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
      throws IOException {
    BufferedReader reader = request.getReader();
    String content = reader.readLine();
    if (content != null && content.equals("<html><body>Put Me</body></html>")) {
      response.getWriter().print(RequestBuilderTestConstants.SERVLET_PUT_RESPONSE);
      response.setStatus(HttpServletResponse.SC_OK);
    } else {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
  }
}
