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

import elemental2.dom.DomGlobal;
import elemental2.dom.XMLHttpRequest;

/**
 * An HTTP request that is waiting for a response. Requests can be queried for their pending status
 * or they can be canceled.
 */
public class Request {

  /**
   * Creates a {@link Response} instance for the given JavaScript XmlHttpRequest object.
   *
   * @param xmlHttpRequest xmlHttpRequest object for which we need a response
   * @return a {@link Response} object instance
   */
  private static Response createResponse(final XMLHttpRequest xmlHttpRequest) {
    return new ResponseImpl(xmlHttpRequest);
  }

  /** The number of milliseconds to wait for this HTTP request to complete. */
  private final int timeoutMillis;

  /** ID of the timer used to force HTTPRequest timeouts. Only meaningful if timeoutMillis > 0. */
  private final double timerId;

  /**
   * JavaScript XmlHttpRequest object that this Java class wraps. This field is not final because we
   * transfer ownership of it to the HTTPResponse object and set this field to null.
   */
  private XMLHttpRequest xmlHttpRequest;

  /**
   * Constructs an instance of the Request object.
   *
   * @param xmlHttpRequest JavaScript XmlHttpRequest object instance
   * @param timeoutMillis number of milliseconds to wait for a response
   * @param callback callback interface to use for notification
   * @throws IllegalArgumentException if timeoutMillis &lt; 0
   * @throws NullPointerException if xmlHttpRequest, or callback are null
   */
  Request(XMLHttpRequest xmlHttpRequest, int timeoutMillis, RequestCallback callback) {
    if (xmlHttpRequest == null) {
      throw new NullPointerException();
    }

    if (callback == null) {
      throw new NullPointerException();
    }

    if (timeoutMillis < 0) {
      throw new IllegalArgumentException();
    }

    this.timeoutMillis = timeoutMillis;
    this.xmlHttpRequest = xmlHttpRequest;

    if (timeoutMillis > 0) {
      timerId = DomGlobal.setTimeout(args -> fireOnTimeout(callback), timeoutMillis);
    } else {
      timerId = 0;
    }
  }

  /**
   * Cancels a pending request. If the request has already been canceled or if it has timed out no
   * action is taken.
   */
  public void cancel() {
    if (xmlHttpRequest == null) {
      return;
    }

    cancelTimer();

    /*
     * There is a strange race condition that occurs on Mozilla when you cancel
     * a request while the response is coming in. It appears that in some cases
     * the onreadystatechange handler is still called after the handler function
     * has been deleted and during the call to XmlHttpRequest.abort(). So we
     * null the xmlHttpRequest here and that will prevent the
     * fireOnResponseReceived method from calling the callback function.
     *
     * Setting the onreadystatechange handler to null gives us the correct
     * behavior in Mozilla but crashes IE. That is why we have chosen to fixed
     * this in Java by nulling out our reference to the XmlHttpRequest object.
     */
    final XMLHttpRequest xhr = xmlHttpRequest;
    xmlHttpRequest = null;

    // XXX: this clearOnReadyStateChange() was in com.google.gwt.http.client.Request, do we really
    // need it (and equivalent) here?
    // com.google.gwt.xhr.client.XMLHttpRequest has this note:
    /*
     * NOTE: Testing discovered that for some bizarre reason, on Mozilla, the
     * JavaScript <code>XmlHttpRequest.onreadystatechange</code> handler
     * function maybe still be called after it is deleted. The theory is that the
     * callback is cached somewhere. Setting it to null or an empty function does
     * seem to work properly, though.
     *
     * On IE, setting onreadystatechange to null (as opposed to an empty function)
     * sometimes throws an exception.
     *
     * End result: *always* set onreadystatechange to an empty function (never to
     * null).
     */
    // xhr.clearOnReadyStateChange();
    xhr.abort();
  }

  /**
   * Returns true if this request is waiting for a response.
   *
   * @return true if this request is waiting for a response
   */
  public boolean isPending() {
    if (xmlHttpRequest == null) {
      return false;
    }

    double readyState = xmlHttpRequest.readyState;

    /*
     * Because we are doing asynchronous requests it is possible that we can
     * call XmlHttpRequest.send and still have the XmlHttpRequest.getReadyState
     * method return the state as XmlHttpRequest.OPEN. That is why we include
     * open although it is nottechnically true since open implies that the
     * request has not been sent.
     */
    return readyState == XMLHttpRequest.OPENED
        || readyState == XMLHttpRequest.HEADERS_RECEIVED
        || readyState == XMLHttpRequest.LOADING;
  }

  /*
   * Method called when the JavaScript XmlHttpRequest object's readyState
   * reaches 4 (LOADED).
   */
  void fireOnResponseReceived(RequestCallback callback) {
    if (xmlHttpRequest == null) {
      // the request has timed out at this point
      return;
    }

    cancelTimer();

    /*
     * We cannot use cancel here because it would clear the contents of the
     * JavaScript XmlHttpRequest object so we manually null out our reference to
     * the JavaScriptObject
     */
    final XMLHttpRequest xhr = xmlHttpRequest;
    xmlHttpRequest = null;

    Response response = createResponse(xhr);
    callback.onResponseReceived(this, response);
  }

  /** Stops the current HTTPRequest timer if there is one. */
  private void cancelTimer() {
    if (timeoutMillis > 0) {
      DomGlobal.clearTimeout(timerId);
    }
  }

  /*
   * Method called when this request times out.
   */
  private void fireOnTimeout(RequestCallback callback) {
    if (xmlHttpRequest == null) {
      // the request has been received at this point
      return;
    }

    cancel();

    callback.onError(this, new RequestTimeoutException(this, timeoutMillis));
  }
}
