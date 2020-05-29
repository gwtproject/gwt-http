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

import elemental2.dom.XMLHttpRequest;
import java.util.HashMap;
import java.util.Map;
import jsinterop.base.Js;

/** Builder for constructing {@link Request} objects. */
public class RequestBuilder {
  /** HTTP request method constants. */
  public static final class Method {
    private final String name;

    private Method(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  /** Specifies that the HTTP DELETE method should be used. */
  public static final Method DELETE = new Method("DELETE");

  /** Specifies that the HTTP GET method should be used. */
  public static final Method GET = new Method("GET");

  /** Specifies that the HTTP HEAD method should be used. */
  public static final Method HEAD = new Method("HEAD");

  /** Specifies that the HTTP POST method should be used. */
  public static final Method POST = new Method("POST");

  /** Specifies that the HTTP PUT method should be used. */
  public static final Method PUT = new Method("PUT");

  /** The callback to call when the request completes. */
  private RequestCallback callback;

  /**
   * Map of header name to value that will be added to the JavaScript XmlHttpRequest object before
   * sending a request.
   */
  private Map<String, String> headers;

  /** HTTP method to use when opening a JavaScript XmlHttpRequest object. */
  private final String httpMethod;

  /** Whether to include credentials for a Cross Origin Request. */
  private boolean includeCredentials;

  /** Password to use when opening a JavaScript XmlHttpRequest object. */
  private String password;

  /** Request data to use when sending a JavaScript XmlHttpRequest object. */
  private String requestData;

  /** Timeout in milliseconds before the request timeouts and fails. */
  private int timeoutMillis;

  /** URL to use when opening a JavaScript XmlHttpRequest object. */
  private final String url;

  /** User to use when opening a JavaScript XmlHttpRequest object. */
  private String user;

  /**
   * Creates a builder using the parameters for configuration.
   *
   * @param httpMethod HTTP method to use for the request
   * @param url URL that has already has already been encoded. Please see {@link
   *     URL#encode(String)}, {@link URL#encodePathSegment(String)} and {@link
   *     URL#encodeQueryString(String)} for how to do this.
   * @throws IllegalArgumentException if the httpMethod or URL are empty
   * @throws NullPointerException if the httpMethod or the URL are null
   */
  public RequestBuilder(Method httpMethod, String url) {
    this((httpMethod == null) ? null : httpMethod.toString(), url);
  }

  /**
   * Creates a builder using the parameters values for configuration.
   *
   * @param httpMethod HTTP method to use for the request
   * @param url URL that has already has already been URL encoded. Please see {@link
   *     URL#encode(String)} and {@link URL#encodePathSegment(String)} and {@link
   *     URL#encodeQueryString(String)} for how to do this.
   * @throws IllegalArgumentException if the httpMethod or URL are empty
   * @throws NullPointerException if the httpMethod or the URL are null
   */
  protected RequestBuilder(String httpMethod, String url) {

    StringValidator.throwIfEmptyOrNull("httpMethod", httpMethod);
    StringValidator.throwIfEmptyOrNull("url", url);

    this.httpMethod = httpMethod;
    this.url = url;
  }

  /**
   * Returns the callback previously set by {@link #setCallback(RequestCallback)}, or <code>null
   * </code> if no callback was set.
   */
  public RequestCallback getCallback() {
    return callback;
  }

  /**
   * Returns the value of a header previous set by {@link #setHeader(String, String)}, or <code>null
   * </code> if no such header was set.
   *
   * @param header the name of the header
   */
  public String getHeader(String header) {
    if (headers == null) {
      return null;
    }
    return headers.get(header);
  }

  /** Returns the HTTP method specified in the constructor. */
  public String getHTTPMethod() {
    return httpMethod;
  }

  /**
   * Returns the password previously set by {@link #setPassword(String)}, or <code>null</code> if no
   * password was set.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Returns the requestData previously set by {@link #setRequestData(String)}, or <code>null</code>
   * if no requestData was set.
   */
  public String getRequestData() {
    return requestData;
  }

  /**
   * Returns the timeoutMillis previously set by {@link #setTimeoutMillis(int)}, or <code>0</code>
   * if no timeoutMillis was set.
   */
  public int getTimeoutMillis() {
    return timeoutMillis;
  }

  /** Returns the HTTP URL specified in the constructor. */
  public String getUrl() {
    return url;
  }

  /**
   * Returns the user previously set by {@link #setUser(String)}, or <code>null</code> if no user
   * was set.
   */
  public String getUser() {
    return user;
  }

  /**
   * Sends an HTTP request based on the current builder configuration. If no request headers have
   * been set, the header "Content-Type" will be used with a value of "text/plain; charset=utf-8".
   * You must call {@link #setRequestData(String)} and {@link #setCallback(RequestCallback)} before
   * calling this method.
   *
   * @return a {@link Request} object that can be used to track the request
   * @throws RequestException if the call fails to initiate
   * @throws NullPointerException if a request callback has not been set
   */
  public Request send() throws RequestException {
    StringValidator.throwIfNull("callback", callback);
    return doSend(requestData, callback);
  }

  /**
   * Sends an HTTP request based on the current builder configuration with the specified data and
   * callback. If no request headers have been set, the header "Content-Type" will be used with a
   * value of "text/plain; charset=utf-8". This method does not cache <code>requestData</code> or
   * <code>callback</code>.
   *
   * @param requestData the data to send as part of the request
   * @param callback the response handler to be notified when the request fails or completes
   * @return a {@link Request} object that can be used to track the request
   * @throws NullPointerException if <code>callback</code> <code>null</code>
   */
  public Request sendRequest(String requestData, RequestCallback callback) throws RequestException {
    StringValidator.throwIfNull("callback", callback);
    return doSend(requestData, callback);
  }

  /**
   * Sets the response handler for this request. This method <b>must</b> be called before calling
   * {@link #send()}.
   *
   * @param callback the response handler to be notified when the request fails or completes
   * @throws NullPointerException if <code>callback</code> is <code>null</code>
   */
  public void setCallback(RequestCallback callback) {
    StringValidator.throwIfNull("callback", callback);

    this.callback = callback;
  }

  /**
   * Sets a request header with the given name and value. If a header with the specified name has
   * already been set then the new value overwrites the current value.
   *
   * @param header the name of the header
   * @param value the value of the header
   * @throws NullPointerException if header or value are null
   * @throws IllegalArgumentException if header or value are the empty string
   */
  public void setHeader(String header, String value) {
    StringValidator.throwIfEmptyOrNull("header", header);
    StringValidator.throwIfEmptyOrNull("value", value);

    if (headers == null) {
      headers = new HashMap<>();
    }

    headers.put(header, value);
  }

  /**
   * Sets the password to use in the request URL. This is ignored if there is no user specified.
   *
   * @param password password to use in the request URL
   * @throws IllegalArgumentException if the password is empty
   * @throws NullPointerException if the password is null
   */
  public void setPassword(String password) {
    StringValidator.throwIfEmptyOrNull("password", password);

    this.password = password;
  }

  /**
   * Sets the data to send as part of this request. This method <b>must</b> be called before calling
   * {@link #send()}.
   *
   * @param requestData the data to send as part of the request
   */
  public void setRequestData(String requestData) {
    this.requestData = requestData;
  }

  /**
   * Sets the number of milliseconds to wait for a request to complete. Should the request timeout,
   * the {@link RequestCallback#onError(Request, Throwable)} method will be called on the callback
   * instance given to the {@link RequestBuilder#sendRequest(String, RequestCallback)} method. The
   * callback method will receive an instance of the {@link RequestTimeoutException} class as its
   * {@link Throwable} argument.
   *
   * @param timeoutMillis number of milliseconds to wait before canceling the request, a value of
   *     zero disables timeouts
   * @throws IllegalArgumentException if the timeout value is negative
   */
  public void setTimeoutMillis(int timeoutMillis) {
    if (timeoutMillis < 0) {
      throw new IllegalArgumentException("Timeouts cannot be negative");
    }

    this.timeoutMillis = timeoutMillis;
  }

  /**
   * Sets the user name that will be used in the request URL.
   *
   * @param user user name to use
   * @throws IllegalArgumentException if the user is empty
   * @throws NullPointerException if the user is null
   */
  public void setUser(String user) {
    StringValidator.throwIfEmptyOrNull("user", user);

    this.user = user;
  }

  /**
   * Sets whether the cross origin request will include credentials.
   *
   * @param includeCredentials whether to include credentials in XHR
   */
  public void setIncludeCredentials(boolean includeCredentials) {
    this.includeCredentials = includeCredentials;
  }

  /**
   * Sends an HTTP request based on the current builder configuration. If no request headers have
   * been set, the header "Content-Type" will be used with a value of "text/plain; charset=utf-8".
   *
   * @return a {@link Request} object that can be used to track the request
   * @throws RequestException if the call fails to initiate
   * @throws NullPointerException if request data has not been set
   * @throws NullPointerException if a request callback has not been set
   */
  private Request doSend(String requestData, final RequestCallback callback)
      throws RequestException {
    XMLHttpRequest xmlHttpRequest = new XMLHttpRequest();

    if (user != null && password != null) {
      xmlHttpRequest.open(httpMethod, url, true, user, password);
    } else if (user != null) {
      xmlHttpRequest.open(httpMethod, url, true, user);
    } else {
      xmlHttpRequest.open(httpMethod, url, true);
    }

    setHeaders(xmlHttpRequest);
    if (includeCredentials) {
      xmlHttpRequest.withCredentials = true;
    }

    final Request request = new Request(xmlHttpRequest, timeoutMillis, callback);

    // Must set the onreadystatechange handler before calling send().
    xmlHttpRequest.onreadystatechange =
        evt -> {
          if (xmlHttpRequest.readyState == XMLHttpRequest.DONE) {
            // XXX: this clearOnReadyStateChange() was in
            // com.google.gwt.http.client.Request, do we really need it (and equivalent) here?
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
            //    xhr.clearOnReadyStateChange();
            request.fireOnResponseReceived(callback);
          }
          return Js.undefined();
        };

    try {
      xmlHttpRequest.send(requestData);
    } catch (Throwable e) {
      throw new RequestException(e.getMessage());
    }

    return request;
  }

  /*
   * Internal method that actually sets our cached headers on the underlying
   * JavaScript XmlHttpRequest object. If there are no headers set, then we set
   * the "Content-Type" to "text/plain; charset=utf-8". This is really lining us
   * up for integration with RPC.
   */
  private void setHeaders(XMLHttpRequest xmlHttpRequest) throws RequestException {
    if (headers != null && headers.size() > 0) {
      for (Map.Entry<String, String> header : headers.entrySet()) {
        try {
          xmlHttpRequest.setRequestHeader(header.getKey(), header.getValue());
        } catch (Throwable e) {
          throw new RequestException(e.getMessage());
        }
      }
    } else {
      xmlHttpRequest.setRequestHeader("Content-Type", "text/plain; charset=utf-8");
    }
  }
}
