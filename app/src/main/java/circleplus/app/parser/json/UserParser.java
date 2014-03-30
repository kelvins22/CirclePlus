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

import org.json.JSONException;
import org.json.JSONObject;

import circleplus.app.types.User;

public class UserParser extends AbstractParser<User> {

    @Override
    public User parse(JSONObject json) throws JSONException {
        User obj = new User();
        if (json.has("checkin")) {
            obj.setCheckin(new CheckinParser().parse(json.getJSONObject("checkin")));
        }
        if (json.has("checkinCount")) {
            obj.setCheckinCount(json.getInt("checkinCount"));
        }
        if (json.has("created")) {
            obj.setCreated(json.getString("created"));
        }
        if (json.has("email")) {
            obj.setEmail(json.getString("email"));
        }
        if (json.has("firstname")) {
            obj.setFirstname(json.getString("firstname"));
        }
        if (json.has("followerCount")) {
            obj.setFollowerCount(json.getInt("followerCount"));
        }
        if (json.has("friendCount")) {
            obj.setFriendCount(json.getInt("friendCount"));
        }
        if (json.has("gender")) {
            obj.setGender(json.getString("gender"));
        }
        if (json.has("hometown")) {
            obj.setHometown(json.getString("hometown"));
        }
        if (json.has("id")) {
            obj.setId(json.getString("id"));
        }
        if (json.has("lastname")) {
            obj.setLastname(json.getString("lastname"));
        }
        if (json.has("phone")) {
            obj.setPhone(json.getString("phone"));
        }
        if (json.has("photo")) {
            obj.setPhoto(json.getString("photo"));
        }
//        if (json.has("settings")) {
//            obj.setSettings(new SettingsParser().parse(json.getJSONObject("settings"));
//        }

        return obj;
    }
}
