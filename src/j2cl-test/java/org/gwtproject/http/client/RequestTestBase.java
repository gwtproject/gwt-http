/*
 * Copyright 2009 The GWT Project Authors
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

import elemental2.promise.Promise;
import elemental2.promise.Promise.PromiseExecutorCallbackFn.RejectCallbackFn;
import elemental2.promise.Promise.PromiseExecutorCallbackFn.ResolveCallbackFn;

/** Base class for tests that send an http request. */
public abstract class RequestTestBase {
  protected static final String BASE_URL =
      System.getProperty("test.baseUrl", "http://localhost:8080/");

  /** The timeout for request tests. */
  protected static final int REQUEST_TIMEOUT = 15000;

  @FunctionalInterface
  protected interface Action {
    void accept(RequestCallback callback) throws Exception;
  }

  protected static Promise<Void> sendRequest(
      RequestBuilder builder, String requestData, RequestCallback callback) {
    return withCallback(callback, cb -> builder.sendRequest(requestData, cb));
  }

  protected static Promise<Void> withCallback(RequestCallback callback, Action action) {
    return new Promise<>(
        (resolve, reject) -> {
          try {
            action.accept(wrapCallback(callback, resolve, reject));
          } catch (Throwable throwable) {
            reject.onInvoke(throwable);
          }
        });
  }

  protected static RequestCallback wrapCallback(
      RequestCallback callback, ResolveCallbackFn<Void> resolve, RejectCallbackFn reject) {
    return new RequestCallback() {
      @Override
      public void onResponseReceived(Request request, Response response) {
        try {
          callback.onResponseReceived(request, response);
        } catch (Throwable throwable) {
          reject.onInvoke(throwable);
          return;
        }
        resolve.onInvoke((Void) null);
      }

      @Override
      public void onError(Request request, Throwable exception) {
        try {
          callback.onError(request, exception);
        } catch (Throwable throwable) {
          reject.onInvoke(throwable);
          return;
        }
        resolve.onInvoke((Void) null);
      }
    };
  }
}
