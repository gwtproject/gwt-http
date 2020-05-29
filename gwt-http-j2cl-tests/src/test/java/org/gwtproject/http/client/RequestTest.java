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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.j2cl.junit.apt.J2clTestInput;
import elemental2.dom.XMLHttpRequest;
import elemental2.promise.Promise;
import org.junit.Test;

/** TODO: document me. */
@J2clTestInput(RequestTest.class)
public class RequestTest {

  /** The timeout for request tests. */
  protected static final int REQUEST_TIMEOUT = 15000;

  private static String getTestBaseURL() {
    return "http://localhost:9999/testRequest/";
  }

  /** Test method for {@link Request#cancel()}. */
  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testCancel() {

    return new Promise<>(
        (resolve, reject) -> {
          RequestBuilder builder =
              new RequestBuilder(RequestBuilder.GET, getTestBaseURL() + "/cancel");
          try {
            Request request =
                builder.sendRequest(
                    null,
                    new RequestCallback() {
                      @Override
                      public void onResponseReceived(Request request, Response response) {
                        reject.onInvoke("Request was canceled - no response should be received");
                      }

                      @Override
                      public void onError(Request request, Throwable exception) {
                        reject.onInvoke("Request was canceled - no timeout should occur");
                      }
                    });

            assertTrue(request.isPending());
            request.cancel();
            assertFalse(request.isPending());

            resolve.onInvoke((Void) null);
          } catch (RequestException e) {
            reject.onInvoke(e.getMessage());
          }
        });
  }

  /** Test method for {@link Request#Request(XMLHttpRequest, int, RequestCallback)}. */
  @Test()
  public void testRequest() {
    RequestCallback callback =
        new RequestCallback() {
          @Override
          public void onResponseReceived(Request request, Response response) {}

          @Override
          public void onError(Request request, Throwable exception) {}
        };

    try {
      new Request(null, 0, callback);
      fail();
    } catch (NullPointerException ex) {
      // Success (The Request ctor explicitly throws an NPE).
    }

    try {
      new Request(new XMLHttpRequest(), -1, callback);
      fail();
    } catch (IllegalArgumentException ex) {
      // Success.
    }

    try {
      new Request(new XMLHttpRequest(), -1, null);
      fail();
    } catch (NullPointerException ex) {
      // Success (The Request ctor explicitly throws an NPE).
    }

    try {
      new Request(new XMLHttpRequest(), 0, callback);
    } catch (Throwable ex) {
      fail(ex.getMessage());
    }
  }

  /** Test method for {@link Request#isPending()}. */
  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Void> testIsPending() {

    return new Promise<>(
        (resolve, reject) -> {
          RequestBuilder builder =
              new RequestBuilder(RequestBuilder.GET, getTestBaseURL() + "isPending");
          try {
            Request request =
                builder.sendRequest(
                    null,
                    new RequestCallback() {
                      @Override
                      public void onResponseReceived(Request request, Response response) {
                        resolve.onInvoke((Void) null);
                      }

                      @Override
                      public void onError(Request request, Throwable exception) {
                        reject.onInvoke(exception.getMessage());
                      }
                    });

            assertTrue(request.isPending());
            resolve.onInvoke((Void) null);
          } catch (RequestException e) {
            reject.onInvoke(e.getMessage());
          }
        });
  }

  /*
   * Checks that the status code is correct when receiving a 204-No-Content. This needs special
   * handling in IE6-9. See http://code.google.com/p/google-web-toolkit/issues/detail?id=5031
   */
  @Test(timeout = REQUEST_TIMEOUT)
  public Promise<Object> test204NoContent() {

    return new Promise<>(
        (resolve, reject) -> {
          RequestBuilder builder =
              new RequestBuilder(RequestBuilder.GET, getTestBaseURL() + "204NoContent");
          try {
            builder.sendRequest(
                null,
                new RequestCallback() {

                  @Override
                  public void onResponseReceived(Request request, Response response) {
                    assertEquals(204, response.getStatusCode());
                    resolve.onInvoke((Void) null);
                  }

                  @Override
                  public void onError(Request request, Throwable exception) {
                    reject.onInvoke(exception.getMessage());
                  }
                });
          } catch (RequestException e) {
            reject.onInvoke(e.getMessage());
          }
        });
  }
}
