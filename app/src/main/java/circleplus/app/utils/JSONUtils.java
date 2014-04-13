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

package circleplus.app.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import circleplus.app.parser.json.Parser;
import circleplus.app.types.BaseType;

public class JSONUtils {

    private static final String TAG = "JSONUtils";
    private static final boolean D = true;

    @SuppressWarnings("unchecked")
    public static BaseType consume(Parser<? extends BaseType> parser,
            String content) throws Exception {
        if (D) Log.d(TAG, "http response: " + content);

        try {
            JSONObject json = new JSONObject(content);
            Iterator<String> it = (Iterator<String>) json.keys();
            if (it.hasNext()) {
                String key = it.next();
                // we had checked network error on http level
                // so the parser here must be instance of StatusParser
                if ("error".equals(key)) {
                    return parser.parse(json.getJSONObject(key));
                } else {
                    Object obj = json.get(key);
                    if (obj instanceof JSONArray) {
                        return parser.parse((JSONArray) obj);
                    } else {
                        return parser.parse((JSONObject) obj);
                    }
                }
            } else {
                throw new Exception("Error parsing JSON response, "
                        + "object has no single child key.");
            }
        } catch (JSONException ex) {
            throw new Exception("Error parsing JSON response: "
                    + ex.getMessage());
        }
    }
}
