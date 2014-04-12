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
import java.util.HashMap;
import java.util.Map;

import circleplus.app.parser.json.ArrayParser;
import circleplus.app.parser.json.CheckinParser;
import circleplus.app.parser.json.StatusParser;
import circleplus.app.types.Checkin;
import circleplus.app.types.Status;
import circleplus.app.types.TypeArrayList;

public class CirclePlusApi {

    private static final String CIRCLE_PLUS_URL = "http://192.168.1.115:9000/";

    private static final String REGISTER_URL = CIRCLE_PLUS_URL + "register";
    private static final String LOGIN_URL = CIRCLE_PLUS_URL + "login";
    private static final String LIST_FAVORITE_URL = CIRCLE_PLUS_URL + "listFavourite";
    private static final String CHECKIN_URL = CIRCLE_PLUS_URL + "checkin";

    private AbstractHttpApi mHttpApi = null;

    public CirclePlusApi() {
        mHttpApi = new BaseHttpApi();
    }

    public Status register(String username, String password, String email,
            String phone, String gender) throws IOException, Exception {
        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("password", password);
        json.put("email", email);
        json.put("phone", phone);
        json.put("gender", gender);
        URL url = new URL(REGISTER_URL);
        return (Status) (mHttpApi.doHttpJsonPost(url, json, new StatusParser()));
    }

    public TypeArrayList<Checkin> getFavorites() throws IOException, Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user", "kelei");
        URL url = AbstractHttpApi.createHttpUrl(LIST_FAVORITE_URL, params);
        return (TypeArrayList<Checkin>)
                (mHttpApi.doHttpRequest(url, new ArrayParser(new CheckinParser())));
    }
}
