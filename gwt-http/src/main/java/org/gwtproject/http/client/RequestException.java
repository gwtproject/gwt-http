/*
 * Copyright 2006 The GWT Project Authors
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

/** RequestException is the superclass for the HTTP request related exceptions. */
@SuppressWarnings("serial")
public class RequestException extends Exception {

  public RequestException() {
    super();
  }

  public RequestException(String message) {
    super(message);
  }

  public RequestException(Throwable cause) {
    super(cause);
  }

  public RequestException(String message, Throwable cause) {
    super(message, cause);
  }
}
