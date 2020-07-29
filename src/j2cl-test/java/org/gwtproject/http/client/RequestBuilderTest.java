/*
 * Copyright 2008 The GWT Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.gwtproject.http.client;

import static org.gwtproject.http.shared.RequestBuilderTestConstants.SERVLET_DELETE_RESPONSE;
import static org.gwtproject.http.shared.RequestBuilderTestConstants.SERVLET_GET_RESPONSE;
import static org.gwtproject.http.shared.RequestBuilderTestConstants.SERVLET_HEAD_RESPONSE;
import static org.gwtproject.http.shared.RequestBuilderTestConstants.SERVLET_POST_RESPONSE;
import static org.gwtproject.http.shared.RequestBuilderTestConstants.SERVLET_PUT_RESPONSE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.j2cl.junit.apt.J2clTestInput;
import elemental2.promise.Promise;
import org.junit.Test;

/** Test cases for the {@link RequestBuilder} class. */
@J2clTestInput(RequestBuilderTest.class)
public class RequestBuilderTest extends RequestTestBase {

  private static String getTestBaseURL() {
    return BASE_URL + "testRequestBuilder/";
  }

  /**
   * Test method for {@link RequestBuilder#RequestBuilder(String, String)}.
   *
   * <p>Test Cases:
   *
   * <ul>
   *   <li>httpMethod == null
   *   <li>httpMethod == ""
   *   <li>url == null
   *   <li>url == ""
   * </ul>
   */
  @Test
  public void testRequestBuilderStringString() throws RequestException {
    try {
      new RequestBuilder((RequestBuilder.Method) null, null);
      fail("NullPointerException should have been thrown for construction with null method.");
    } catch (NullPointerException ex) {
      // purposely ignored
    }

    try {
      new RequestBuilder(RequestBuilder.GET, null);
      fail("NullPointerException should have been thrown for construction with null URL.");
    } catch (NullPointerException ex) {
      // purposely ignored
    }

    try {
      new RequestBuilder(RequestBuilder.GET, "");
      fail("IllegalArgumentException should have been throw for construction with empty URL.");
    } catch (IllegalArgumentException ex) {
      // purposely ignored
    }
  }

  /** Test method for {@link RequestBuilder#RequestBuilder(String, String)}. */
  @Test
  public void testRequestBuilderStringString_HTTPMethodRestrictionOverride() {
    new RequestBuilder(RequestBuilder.GET, "FOO");

    class MyRequestBuilder extends RequestBuilder {
      MyRequestBuilder(String httpMethod, String url) {
        super(httpMethod, url);
      }
    }

    new MyRequestBuilder("HEAD", "FOO");
    // should reach here without any exceptions being thrown
  }

  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testSend_DELETE() throws RequestException {
    RequestBuilder builder = new RequestBuilder(RequestBuilder.DELETE, getTestBaseURL());
    return testSend(builder, SERVLET_DELETE_RESPONSE);
  }

  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testSend_GET() throws RequestException {
    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, getTestBaseURL() + "send_GET");
    return testSend(builder, SERVLET_GET_RESPONSE);
  }

  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testSend_HEAD() throws RequestException {
    RequestBuilder builder = new RequestBuilder(RequestBuilder.HEAD, getTestBaseURL());
    return testSend(builder, SERVLET_HEAD_RESPONSE);
  }

  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testSend_POST() throws RequestException {
    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.POST, getTestBaseURL() + "sendRequest_POST");
    builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
    return testSend(builder, SERVLET_POST_RESPONSE);
  }

  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testSend_PUT() throws RequestException {
    RequestBuilder builder = new RequestBuilder(RequestBuilder.PUT, getTestBaseURL());
    builder.setHeader("Content-Type", "text/html");
    builder.setRequestData("<html><body>Put Me</body></html>");
    return testSend(builder, SERVLET_PUT_RESPONSE);
  }

  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testSendRequest_DELETE() throws RequestException {
    RequestBuilder builder = new RequestBuilder(RequestBuilder.DELETE, getTestBaseURL());
    return testSendRequest(builder, null, SERVLET_DELETE_RESPONSE);
  }

  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testSendRequest_GET() throws RequestException {
    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, getTestBaseURL() + "sendRequest_GET");
    return testSendRequest(builder, null, SERVLET_GET_RESPONSE);
  }

  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testSendRequest_HEAD() throws RequestException {
    RequestBuilder builder = new RequestBuilder(RequestBuilder.HEAD, getTestBaseURL());
    return testSendRequest(builder, null, SERVLET_HEAD_RESPONSE);
  }

  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testSendRequest_POST() throws RequestException {
    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.POST, getTestBaseURL() + "sendRequest_POST");
    builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
    return testSendRequest(builder, null, SERVLET_POST_RESPONSE);
  }

  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testSendRequest_PUT() throws RequestException {
    RequestBuilder builder = new RequestBuilder(RequestBuilder.PUT, getTestBaseURL());
    builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
    return testSendRequest(builder, "<html><body>Put Me</body></html>", SERVLET_PUT_RESPONSE);
  }

  @Test
  public void testSetCallback() {
    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, getTestBaseURL());
    try {
      builder.setCallback(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
    }
  }

  @Test
  public void testSetPassword() {
    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, getTestBaseURL());
    try {
      builder.setPassword(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
    }

    try {
      builder.setPassword("");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void testSetRequestData() {
    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, getTestBaseURL());
    // Legal.
    builder.setRequestData(null);
    builder.setRequestData("");
  }

  /**
   * Test method for {@link RequestBuilder#setHeader(String, String)}.
   *
   * <p>Test Cases:
   *
   * <ul>
   *   <li>name == null
   *   <li>name == ""
   *   <li>value == null
   *   <li>value == ""
   * </ul>
   */
  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testSetRequestHeader() throws RequestException {
    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, getTestBaseURL() + "setRequestHeader");

    try {
      builder.setHeader(null, "bar");
      fail("setRequestHeader(null, \"bar\")");
    } catch (NullPointerException expected) {
    }

    try {
      builder.setHeader("", "bar");
      fail("setRequestHeader(\"\", \"bar\")");
    } catch (IllegalArgumentException expected) {
    }

    try {
      builder.setHeader("foo", null);
      fail("setRequestHeader(\"foo\", null)");
    } catch (NullPointerException expected) {
    }

    try {
      builder.setHeader("foo", "");
      fail("setRequestHeader(\"foo\", \"\")");
    } catch (IllegalArgumentException expected) {
    }

    builder = new RequestBuilder(RequestBuilder.GET, getTestBaseURL() + "setRequestHeader");
    builder.setHeader("Foo", "Bar");
    builder.setHeader("Foo", "Bar1");

    return sendRequest(
        builder,
        null,
        new RequestCallback() {
          @Override
          public void onError(Request request, Throwable exception) {
            fail(exception.getMessage());
          }

          @Override
          public void onResponseReceived(Request request, Response response) {
            assertEquals(SERVLET_GET_RESPONSE, response.getText());
            assertEquals(200, response.getStatusCode());
          }
        });
  }

  /**
   * Test method for {@link RequestBuilder#setTimeoutMillis(int)}.
   *
   * <p>Test Cases:
   *
   * <ul>
   *   <li>Timeout greater than the server's response time
   *   <li>Timeout is less than the server's response time
   * </ul>
   */
  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testSetTimeout_noTimeout() throws RequestException {
    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, getTestBaseURL() + "setTimeout/noTimeout");
    builder.setTimeoutMillis(10000);
    return sendRequest(
        builder,
        null,
        new RequestCallback() {
          @Override
          public void onError(Request request, Throwable exception) {
            fail(exception.getMessage());
          }

          @Override
          public void onResponseReceived(Request request, Response response) {
            assertEquals(SERVLET_GET_RESPONSE, response.getText());
            assertEquals(200, response.getStatusCode());
          }
        });
  }

  /**
   * Test method for {@link RequestBuilder#setTimeoutMillis(int)}.
   *
   * <p>Test Cases:
   *
   * <ul>
   *   <li>Timeout greater than the server's response time
   *   <li>Timeout is less than the server's response time
   * </ul>
   *
   * <p>XHR handling is synchronous in HtmlUnit at present (svn r5607).
   */
  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testSetTimeout_timeout() throws RequestException {
    if ("htmlunit".equals(System.getProperty("test.webdriver", "htmlunit"))) {
      // XHR handling is synchronous in HtmlUnit
      return Promise.resolve((Void) null);
    }
    RequestBuilder builder =
        new RequestBuilder(RequestBuilder.GET, getTestBaseURL() + "setTimeout/timeout");
    builder.setTimeoutMillis(2000);
    return sendRequest(
        builder,
        null,
        new RequestCallback() {
          @Override
          public void onError(Request request, Throwable exception) {}

          @Override
          public void onResponseReceived(Request request, Response response) {
            assertEquals(SERVLET_GET_RESPONSE, response.getText());
            assertEquals(200, response.getStatusCode());
            fail("Test did not timeout");
          }
        });
  }

  @Test
  public void testSetUser() {
    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, getTestBaseURL());
    try {
      builder.setUser(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException expected) {
    }

    try {
      builder.setUser("");
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
    }
  }

  /**
   * Helper method to test {@link RequestBuilder#send()}.
   *
   * @param builder the {@link RequestBuilder}
   * @param expectedResponse the expected response
   */
  private Promise<Void> testSend(RequestBuilder builder, final String expectedResponse)
      throws RequestException {
    return withCallback(
        new RequestCallback() {
          @Override
          public void onError(Request request, Throwable exception) {
            fail(exception.getMessage());
          }

          @Override
          public void onResponseReceived(Request request, Response response) {
            assertEquals(expectedResponse, response.getText());
            assertEquals(200, response.getStatusCode());
          }
        },
        cb -> {
          builder.setCallback(cb);
          builder.send();
        });
  }

  /**
   * Helper method to test {@link RequestBuilder#sendRequest(String, RequestCallback)}.
   *
   * @param builder the {@link RequestBuilder}
   * @param requestData the data to request
   * @param expectedResponse the expected response
   */
  private Promise<Void> testSendRequest(
      RequestBuilder builder, String requestData, final String expectedResponse)
      throws RequestException {
    return sendRequest(
        builder,
        requestData,
        new RequestCallback() {
          @Override
          public void onError(Request request, Throwable exception) {
            fail(exception.getMessage());
          }

          @Override
          public void onResponseReceived(Request request, Response response) {
            assertEquals(expectedResponse, response.getText());
            assertEquals(200, response.getStatusCode());
          }
        });
  }
}
