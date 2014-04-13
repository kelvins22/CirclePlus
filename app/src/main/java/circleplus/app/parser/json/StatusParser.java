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

import circleplus.app.types.Status;

public class StatusParser extends AbstractParser<Status> {

    @Override
    public Status parse(JSONObject json) throws JSONException {
        Status obj = new Status();
        if (json.has("status")) {
            obj.setStatusCode(json.getInt("status"));
        }
        if (json.has("message")) {
            obj.setMessage(json.getString("message"));
        }
        return obj;
    }
}
