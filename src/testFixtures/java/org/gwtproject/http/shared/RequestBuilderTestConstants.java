/*
 * Copyright © ${year} ${name}
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
package org.gwtproject.http.shared;

public interface RequestBuilderTestConstants {
  String SERVLET_DELETE_RESPONSE = "delete";
  String SERVLET_GET_RESPONSE = "get";
  String SERVLET_POST_RESPONSE = "post";
  // W3C's XMLHttpRequest requires it be the empty string
  String SERVLET_HEAD_RESPONSE = "";
  String SERVLET_PUT_RESPONSE = "put";
}
