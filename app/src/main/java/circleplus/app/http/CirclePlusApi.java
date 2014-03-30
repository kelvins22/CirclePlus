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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import circleplus.app.parser.json.ArrayParser;
import circleplus.app.parser.json.CheckinParser;
import circleplus.app.types.Checkin;
import circleplus.app.types.TypeArrayList;

public class CirclePlusApi {

    static final String AUTH_KEY = "your_key";

    static final String GEOTABLE_URL = "http://api.map.baidu.com/geodata/v3/geotable/";
    static final String COLUMN_URL = "http://api.map.baidu.com/geodata/v3/column/";

    static final String LIST_GEOTABLE_URL = GEOTABLE_URL + "list";
    static final String DETAIL_GEOTABLE_URL = GEOTABLE_URL + "detail";
    static final String UPDATE_GEOTABLE_URL = GEOTABLE_URL + "update";

    static final String LIST_COLUMN_URL = COLUMN_URL + "list";
    static final String DETAIL_COLUMN_URL = COLUMN_URL + "detail";
    static final String UPDATE_COLUMN_URL = COLUMN_URL + "update";

    private AbstractHttpApi mHttpApi = null;

    public CirclePlusApi() {
        mHttpApi = new BaseHttpApi();
    }

    public void listGeotable() {
    }

    public TypeArrayList<Checkin> getFavorites() throws IOException, Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ak", AUTH_KEY);
        URL url = AbstractHttpApi.createHttpUrl(LIST_GEOTABLE_URL, params);
        return (TypeArrayList<Checkin>)
                (mHttpApi.doHttpRequest(url, new ArrayParser(new CheckinParser())));
    }
}
