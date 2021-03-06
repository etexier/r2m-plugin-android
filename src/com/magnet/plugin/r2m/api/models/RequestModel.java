/*
 * Copyright (c) 2014 Magnet Systems, Inc.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.magnet.plugin.r2m.api.models;

import com.intellij.util.net.HTTPMethod;
import com.magnet.plugin.r2m.models.Method;

import java.util.ArrayList;
import java.util.List;

/**
 * Request model
 */
public class RequestModel {


    private final HTTPMethod httpMethod;
    private final String methodName;

    private String testUrl;
    private String templateUrl;
    private String request;
    private String response;
    private List<RequestHeaderModel> headers = new ArrayList<RequestHeaderModel>();

    public RequestModel(Method method) {
        httpMethod = method.getHttpMethod();
        methodName = method.getMethodName();
        testUrl = method.getTestUrl();
        templateUrl = method.getTemplateUrl();
        request = method.getPayload();
        headers = method.getHeaders();
    }

    public String getMethodName() {
        return methodName;
    }

    public String getTestUrl() {
        return testUrl.replaceAll(" ", "%20");
    }

    public void setTestUrl(String testUrl) {
        this.testUrl = testUrl;
    }

    public String getTemplateUrl() {
        return templateUrl;
    }

    public void setTemplateUrl(String templateUrl) {
        this.templateUrl = templateUrl;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<RequestHeaderModel> getHeaders() {
        return headers;
    }

    public void setHeaders(List<RequestHeaderModel> headers) {
        this.headers = headers;
    }

    public HTTPMethod getHttpMethod() {
        return httpMethod;
    }

}
