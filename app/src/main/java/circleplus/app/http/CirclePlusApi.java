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
import circleplus.app.parser.json.UserParser;
import circleplus.app.types.BaseType;
import circleplus.app.types.Status;

public class CirclePlusApi {

    private static final String CIRCLE_PLUS_URL = "http://192.168.1.115:9000/";

    private static final String REGISTER_URL = CIRCLE_PLUS_URL + "register";
    private static final String LOGIN_URL = CIRCLE_PLUS_URL + "login";
    private static final String LIST_FAVORITE_URL = CIRCLE_PLUS_URL + "listFavorites";
    private static final String CHECKIN_URL = CIRCLE_PLUS_URL + "checkin";
    private static final String REGISTER_BUSINESS_URL = CIRCLE_PLUS_URL + "registerBusiness";
    private static final String LIST_CHECKIN_HISTORY_URL = CIRCLE_PLUS_URL + "listCheckinHistory";
    private static final String GRANT_SCORE_URL = CIRCLE_PLUS_URL + "grantScore";

    private AbstractHttpApi mHttpApi = null;

    public CirclePlusApi() {
        mHttpApi = new BaseHttpApi();
    }

    public Status register(String username, String password, String email,
            String phone, String gender) throws IOException, Exception {
        JSONObject json = new JSONObject();
        json.put("name", username);
        json.put("password", password);
        json.put("email", email);
        json.put("phone", phone);
        json.put("gender", gender);
        URL url = new URL(REGISTER_URL);
        return (Status) (mHttpApi.doHttpJsonPost(url, json, new StatusParser()));
    }

    public BaseType registerBusiness(String locName, String nation, String province,
            String city, String address, int lat, int lng, int type, long userId,
            String businessName) throws IOException, Exception {
        JSONObject json = new JSONObject();
        // location
        json.put("locName", locName);
        json.put("lat", (long) lat);
        json.put("lng", (long) lng);
        json.put("nation", nation);
        json.put("province", province);
        json.put("city", city);
        json.put("address", address);
        json.put("type", type);
        // register business
        json.put("businessName", businessName);
        json.put("userId", userId);
        // request
        URL url = new URL(REGISTER_BUSINESS_URL);
        return mHttpApi.doHttpJsonPost(url, json, new StatusParser());
    }

    public BaseType login(String username, String password)
            throws IOException, Exception {
        JSONObject json = new JSONObject();
        json.put("name", username);
        json.put("password", password);
        URL url = new URL(LOGIN_URL);
        return mHttpApi.doHttpJsonPost(url, json, new UserParser());
    }

    public BaseType checkin(String name, String nation, String province,
            String city, String address, String shout, int lat, int lng,
            int type, long userId) throws IOException, Exception {
        JSONObject json = new JSONObject();
        // location
        json.put("locName", name);
        json.put("lat", (long) lat);
        json.put("lng", (long) lng);
        json.put("nation", nation);
        json.put("province", province);
        json.put("city", city);
        json.put("address", address);
        json.put("type", type);
        // check-in
        json.put("checkinName", name);
        json.put("shout", shout);
        json.put("userId", userId);
        // request
        URL url = new URL(CHECKIN_URL);
        return mHttpApi.doHttpJsonPost(url, json, new StatusParser());
    }

    public BaseType getFavorites(long id, String token)
            throws IOException, Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", String.valueOf(id));
        params.put("token", token);
        URL url = AbstractHttpApi.createHttpUrl(LIST_FAVORITE_URL, params);
        return mHttpApi.doHttpRequest(url, new ArrayParser(new CheckinParser()));
    }

    public BaseType getCheckinHistory(long id, String token)
            throws IOException, Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", String.valueOf(id));
        params.put("token", token);
        URL url = AbstractHttpApi.createHttpUrl(LIST_CHECKIN_HISTORY_URL, params);
        return mHttpApi.doHttpRequest(url, new ArrayParser(new CheckinParser()));
    }

    public BaseType grantScore(long checkinId, int score)
            throws IOException, Exception {
        JSONObject json = new JSONObject();
        json.put("checkinId", checkinId);
        json.put("score", score);
        URL url = new URL(GRANT_SCORE_URL);
        return mHttpApi.doHttpJsonPost(url, json, new StatusParser());
    }
}
