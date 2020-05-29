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
package org.gwtproject.http.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.j2cl.junit.apt.J2clTestInput;
import elemental2.dom.XMLHttpRequest;
import elemental2.promise.Promise;
import org.junit.Test;

@J2clTestInput(ResponseTest.class)
public class ResponseTest {

  public static final int REQUEST_TIMEOUT = 15000;

  private static RequestBuilder getHTTPRequestBuilder() {
    return getHTTPRequestBuilder(getTestBaseURL());
  }

  private static RequestBuilder getHTTPRequestBuilder(String testURL) {
    return new RequestBuilder(RequestBuilder.GET, testURL);
  }

  private static String getTestBaseURL() {
    return "http://localhost:9999/testResponse/";
  }

  /** Test method for {@link Response#getStatusCode()}. */
  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testGetStatusCode() {

    return new Promise<>(
        (resolve, reject) -> {
          try {
            executeTest(
                new RequestCallback() {
                  @Override
                  public void onError(Request request, Throwable exception) {
                    reject.onInvoke(exception.getMessage());
                  }

                  @Override
                  public void onResponseReceived(Request request, Response response) {
                    assertEquals(200, response.getStatusCode());
                    resolve.onInvoke((Void) null);
                  }
                });
          } catch (RequestException e) {
            reject.onInvoke(e.getMessage());
          }
        });
  }

  /** Test method for {@link Response#getStatusText()}. */
  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testGetStatusText() {

    return new Promise<>(
        (resolve, reject) -> {
          try {
            executeTest(
                new RequestCallback() {
                  @Override
                  public void onError(Request request, Throwable exception) {
                    if (exception instanceof RuntimeException) {

                    } else {
                      reject.onInvoke("Unexpected exception: " + exception.getMessage());
                    }
                  }

                  @Override
                  public void onResponseReceived(Request request, Response response) {
                    assertEquals("OK", response.getStatusText());
                    resolve.onInvoke((Void) null);
                  }
                });
          } catch (RequestException e) {
            reject.onInvoke(e.getMessage());
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

  private void executeTest(RequestBuilder builder, RequestCallback callback)
      throws RequestException {
    builder.sendRequest(null, callback);
  }

  private void executeTest(RequestCallback callback) throws RequestException {
    executeTest(getHTTPRequestBuilder(), callback);
  }
}
