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

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import circleplus.app.parser.json.Parser;
import circleplus.app.types.BaseType;
import circleplus.app.utils.JSONUtils;

public abstract class AbstractHttpApi implements HttpApi {

    protected static final String TAG = "AbstractHttpApi";
    protected static final boolean D = true;

    public static final int REQUEST_METHOD_GET = 0x1;
    public static final int REQUEST_METHOD_POST = 0x2;

    private static final String DEFAULT_CLIENT_VERSION = "circleplus.app";
    private static final String CLIENT_VERSION_HEADER = "User-Agent";
    private static final int TIMEOUT = 15 * 1000; // 60 * 1000; /* milliseconds */

    private final String mClientVersion;

    public AbstractHttpApi(String clientVersion) {
        if (clientVersion != null) {
            mClientVersion = clientVersion;
        } else {
            mClientVersion = DEFAULT_CLIENT_VERSION;
        }
    }

    @Override
    public String doHttpPost(URL url) throws IOException, Exception {
        // TODO:
        return "";
    }

    public BaseType executeHttpRequest(URL url, int method,
            Parser<? extends BaseType> parser) throws IOException, Exception {
        InputStream is = null;
        HttpURLConnection conn = null;
        try {
            conn = getHttpURLConnection(url, method);

            int response = conn.getResponseCode();
            if (D) Log.d(TAG, "Response code = " + response);

            switch (response) {
                case 200:
                    is = conn.getInputStream();
                    // TODO: calculate length
                    String content = readStream(is, 1024 * 10);
                    if (D) Log.d(TAG, content);
                    return JSONUtils.consume(parser, content);

                case 400:
                    if (D) Log.d(TAG, "Http code: 400");
                    throw new IOException("Http code: 400");

                case 404:
                    if (D) Log.d(TAG, "Http code: 404");
                    throw new IOException("Http code: 404");

                case 500:
                    if (D) Log.d(TAG, "Http code: 500");
                    throw new IOException("Http code: 500");

                default:
                    if (D) Log.d(TAG, "Default case for status code reached: " + response);
                    throw new IOException("Http code: " + response);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                is.close();
            }
        }
    }

    public static HttpURLConnection getHttpURLConnection(URL url, int requestMethod)
            throws IOException {
        if (D) Log.d(TAG, "execute method: " + requestMethod + " url: " + url.toString());

        String method;
        boolean isPost;
        switch (requestMethod) {
            case REQUEST_METHOD_GET:
                method = "GET";
                isPost = false;
                break;
            case REQUEST_METHOD_POST:
                method = "POST";
                isPost = true;
                break;
            default:
                method = "GET";
                isPost = false;
                break;
        }

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(TIMEOUT);
        conn.setConnectTimeout(TIMEOUT);
        conn.setRequestProperty("Content-Type", "text/json; charset=utf-8");
        conn.setRequestMethod(method);
        /* setDoOutput(true) equals setRequestMethod("POST") */
        conn.setDoOutput(isPost);
        conn.setChunkedStreamingMode(0);
        // Starts the query
        conn.connect();

        return conn;
    }

    public static URL createHttpUrl(String url, Map<String, String> params)
            throws IOException {
        if (D) Log.d(TAG, "url: " + url);
        String requestParams = createStringParams(params);
        return new URL(url + requestParams);
    }

    public static String createStringParams(Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('?');
        Set<String> keys = params.keySet();
        int i = 0;
        for (String key : keys) {
            String value = params.get(key);
            if (value != null) {
                if (i > 0) {
                    sb.append('&');
                }
                sb.append(key);
                sb.append('=');
                sb.append(params.get(key));
            }
            i++;
        }
        if (D) Log.d(TAG, "parse request params: " + sb.toString());
        return sb.toString();
    }

    public static String readStream(InputStream is, int len)
            throws UnsupportedEncodingException, IOException {
        Reader reader = new InputStreamReader(is, "UTF-8");
        try {
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        } finally {
            reader.close();
        }
    }
}
