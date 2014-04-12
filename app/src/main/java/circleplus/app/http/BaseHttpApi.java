/*
* Copyright 2013 The Android Open Source Project
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

package circleplus.app.http;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import circleplus.app.parser.json.Parser;
import circleplus.app.types.BaseType;

public class BaseHttpApi extends AbstractHttpApi {

    public BaseHttpApi() {
        super(null);
    }

    @Override
    public BaseType doHttpRequest(URL url, Parser<? extends BaseType> parser)
            throws IOException, Exception {
        return executeHttpRequest(url, REQUEST_METHOD_GET, null, parser);
    }

    @Override
    public BaseType doHttpJsonPost(URL url, JSONObject jsonObject,
            Parser<? extends BaseType> parser) throws IOException, Exception {
        return executeHttpRequest(url, REQUEST_METHOD_POST, jsonObject, parser);
    }

    @Override
    public BaseType doHttpPost(URL url, Parser<? extends BaseType> parser)
            throws IOException, Exception {
        return executeHttpRequest(url, REQUEST_METHOD_POST, null, parser);
    }
}
