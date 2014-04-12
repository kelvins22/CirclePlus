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

import circleplus.app.types.Loc;

public class LocParser extends AbstractParser<Loc> {

    @Override
    public Loc parse(JSONObject json) throws JSONException {
        Loc obj = new Loc();
        if (json.has("address")) {
            obj.setAddress(json.getString("address"));
        }
        if (json.has("city")) {
            obj.setCity(json.getString("city"));
        }
        if (json.has("id")) {
            obj.setId(json.getLong("id"));
        }
        if (json.has("lat")) {
            obj.setLat(json.getLong("lat"));
        }
        if (json.has("lng")) {
            obj.setLng(json.getLong("lng"));
        }
        if (json.has("name")) {
            obj.setName(json.getString("name"));
        }
        if (json.has("nation")) {
            obj.setNation(json.getString("nation"));
        }
        if (json.has("province")) {
            obj.setProvince(json.getString("province"));
        }
        if (json.has("type")) {
            obj.setType(json.getInt("type"));
        }
        return obj;
    }
}
