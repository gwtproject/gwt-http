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
import static org.junit.Assert.fail;

import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Test;

/** Tests for the URL utility class. */
@J2clTestInput(URLTest.class)
public class URLTest {

  private final String DECODED_URL = "http://www.foo \u00E9+bar.com/1_!~*'();/?@&=+$,#";
  private final String DECODED_URL_COMPONENT = "-_.!~*'():/#?@ \u00E9+";
  private final String ENCODED_URL = "http://www.foo%20%C3%A9+bar.com/1_!~*'();/?@&=+$,#";
  private final String ENCODED_URL_COMPONENT = "-_.!~*'()%3A%2F%23%3F%40%20%C3%A9%2B";
  private final String ENCODED_URL_COMPONENT_QS = "-_.!~*'()%3A%2F%23%3F%40+%C3%A9%2B";

  /** Test method for {@link URL#decode(String)}. */
  @Test
  public void testDecode() {
    try {
      URL.decode(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException ex) {
      // expected exception was thrown
    }

    assertEquals("", URL.decode(""));
    assertEquals(" ", URL.decode(" "));

    String actualURL = URL.decode(ENCODED_URL);
    assertEquals(DECODED_URL, actualURL);
  }

  /** Test method for {@link URL#decodePathSegment(String)}. */
  @Test
  public void testDecodePathSegment() {
    try {
      URL.decodePathSegment(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException ex) {
      // expected exception was thrown
    }

    assertEquals("", URL.decodePathSegment(""));
    assertEquals(" ", URL.decodePathSegment(" "));
    assertEquals("+", URL.decodePathSegment("+"));
    assertEquals(" ", URL.decodePathSegment("%20"));

    String actualURLComponent = URL.decodePathSegment(ENCODED_URL_COMPONENT);
    assertEquals(DECODED_URL_COMPONENT, actualURLComponent);
  }

  /** Test method for {@link URL#decodeQueryString(String)}. */
  @Test
  public void testDecodeQueryString() {
    try {
      URL.decodeQueryString(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException ex) {
      // expected exception was thrown
    }

    try {
      // Malformed URI sequence
      URL.decodeQueryString("%E4");
      fail("Expected JavaScriptException");
    } catch (Exception ignored) {
      // expected exception was thrown
      assertEquals("java.lang.JsException", ignored.getClass().getName());
    }

    assertEquals("", URL.decodeQueryString(""));
    assertEquals(" ", URL.decodeQueryString(" "));
    assertEquals(" ", URL.decodeQueryString("+"));
    assertEquals(" ", URL.decodeQueryString("%20"));

    String actualURLComponent = URL.decodeQueryString(ENCODED_URL_COMPONENT);
    assertEquals(DECODED_URL_COMPONENT, actualURLComponent);

    actualURLComponent = URL.decodeQueryString(ENCODED_URL_COMPONENT_QS);
    assertEquals(DECODED_URL_COMPONENT, actualURLComponent);
  }

  /** Test method for {@link URL#encode(String)}. */
  @Test
  public void testEncode() {
    try {
      URL.encode(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException ex) {
      // expected exception was thrown
    }

    assertEquals("", URL.encode(""));
    assertEquals("%20", URL.encode(" "));

    String actualURL = URL.encode(DECODED_URL);
    assertEquals(ENCODED_URL, actualURL);
  }

  /** Test method for {@link URL#encodePathSegment(String)}. */
  @Test
  public void testEncodePathSegment() {
    try {
      URL.encodePathSegment(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException ex) {
      // expected exception was thrown
    }

    assertEquals("", URL.encodePathSegment(""));
    assertEquals("%20", URL.encodePathSegment(" "));

    String actualURLComponent = URL.encodePathSegment(DECODED_URL_COMPONENT);
    assertEquals(ENCODED_URL_COMPONENT, actualURLComponent);
  }

  /** Test method for {@link URL#encodeQueryString(String)}. */
  @Test
  public void testEncodeQueryString() {
    try {
      URL.encodeQueryString(null);
      fail("Expected NullPointerException");
    } catch (NullPointerException ex) {
      // expected exception was thrown
    }

    assertEquals("", URL.encodeQueryString(""));
    assertEquals("+", URL.encodeQueryString(" "));

    String actualURLComponent = URL.encodeQueryString(DECODED_URL_COMPONENT);
    assertEquals(ENCODED_URL_COMPONENT_QS, actualURLComponent);
  }
}
