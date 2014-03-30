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

package circleplus.app.parser.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import circleplus.app.types.BaseType;
import circleplus.app.types.TypeArrayList;

public class ArrayParser extends AbstractParser<TypeArrayList> {

    private Parser<? extends BaseType> mSubParser;

    public ArrayParser(Parser<? extends BaseType> subParser) {
        mSubParser = subParser;
    }

    public TypeArrayList<BaseType> parse(JSONObject json) throws JSONException {
        TypeArrayList<BaseType> list = new TypeArrayList<BaseType>();
        Iterator<String> it = (Iterator<String>) json.keys();
        while (it.hasNext()) {
            String key = it.next();
            if ("type".equals(key)) {
                list.setType(json.getString(key));
            } else {
                Object obj = json.get(key);
                if (obj instanceof JSONArray) {
                    parse(list, (JSONArray) obj);
                } else {
                    throw new JSONException("Could not parse JSONObject");
                }
            }
        }
        return list;
    }

    @Override
    public TypeArrayList parse(JSONArray array) throws JSONException {
        TypeArrayList<BaseType> list = new TypeArrayList<BaseType>();
        parse(list, array);
        return list;
    }

    /*
     * Parse the array elements and add them to the list.
     * Recursively parse the JSONArray by using a sub-parser.
     */
    private void parse(TypeArrayList<BaseType> list, JSONArray array)
            throws JSONException {
        for (int i = 0, sz = array.length(); i < sz; i++) {
            Object e = array.get(i);
            BaseType item = null;
            if (e instanceof JSONObject) {
                item = mSubParser.parse((JSONObject) e);
            } else {
                item = mSubParser.parse((JSONArray) e);
            }
            list.add(item);
        }
    }
}
