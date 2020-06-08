/*
 * Copyright 2007 The GWT Project Authors
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.google.j2cl.junit.apt.J2clTestInput;
import elemental2.dom.XMLHttpRequest;
import elemental2.promise.Promise;
import org.junit.Test;

/** */
@J2clTestInput(ResponseTest.class)
public class ResponseTest extends RequestTestBase {

  private static RequestBuilder getHTTPRequestBuilder() {
    return getHTTPRequestBuilder(getTestBaseURL());
  }

  private static RequestBuilder getHTTPRequestBuilder(String testURL) {
    return new RequestBuilder(RequestBuilder.GET, testURL);
  }

  private static String getTestBaseURL() {
    return BASE_URL + "testResponse/";
  }

  private static void raiseUnexpectedException(Throwable exception) {
    fail("Unexpected exception: " + exception.toString());
  }

  /** Test method for {@link Response#getStatusCode()}. */
  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testGetStatusCode() {
    return executeTest(
        new RequestCallback() {
          @Override
          public void onError(Request request, Throwable exception) {
            fail();
          }

          @Override
          public void onResponseReceived(Request request, Response response) {
            assertEquals(200, response.getStatusCode());
          }
        });
  }

  /** Test method for {@link Response#getStatusText()}. */
  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testGetStatusText() {
    return executeTest(
        new RequestCallback() {
          @Override
          public void onError(Request request, Throwable exception) {
            if (exception instanceof RuntimeException) {

            } else {
              raiseUnexpectedException(exception);
            }
          }

          @Override
          public void onResponseReceived(Request request, Response response) {
            assertEquals("OK", response.getStatusText());
          }
        });
  }

  @Test
  public void testGetHeadersOffline() {
    ResponseImpl resp =
        new ResponseImpl(new XMLHttpRequest()) {
          @Override
          protected boolean isResponseReady() {
            return true;
          }
        };
    Header[] headers = resp.getHeaders();
    assertNotNull(headers);
    assertEquals(0, headers.length);
  }

  private Promise<Void> executeTest(RequestBuilder builder, RequestCallback callback) {
    return sendRequest(builder, null, callback);
  }

  private Promise<Void> executeTest(RequestCallback callback) {
    return executeTest(getHTTPRequestBuilder(), callback);
  }
}
