/*
 * Copyright 2007 The GWT Project Authors
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
package org.gwtproject.http;

import org.gwtproject.http.client.RequestBuilderTest;
import org.gwtproject.http.client.RequestTest;
import org.gwtproject.http.client.ResponseTest;
import org.gwtproject.http.client.URLTest;
import org.gwtproject.http.client.UrlBuilderTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/** Test for suite for the org.gwtproject.http module */
@RunWith(Suite.class)
@Suite.SuiteClasses({
  URLTest.class,
  RequestBuilderTest.class,
  RequestTest.class,
  ResponseTest.class,
  UrlBuilderTest.class
})
public class HTTPSuite {}
