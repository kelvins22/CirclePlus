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

import circleplus.app.types.Checkin;

public class CheckinParser extends AbstractParser<Checkin> {

    @Override
    public Checkin parse(JSONObject json) throws JSONException {

        Checkin obj = new Checkin();
        if (json.has("created")) {
            obj.setCreated(json.getString("created"));
        }
        if (json.has("distance")) {
            obj.setDistance(json.getString("distance"));
        }
        if (json.has("id")) {
            obj.setId(json.getString("id"));
        }
        if (json.has("shout")) {
            obj.setShout(json.getString("shout"));
        }
        if (json.has("user")) {
            obj.setUser(new UserParser().parse(json.getJSONObject("user")));
        }

        return obj;
    }
}
