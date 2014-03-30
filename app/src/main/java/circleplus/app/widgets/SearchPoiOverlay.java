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

package circleplus.app.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKSearch;

public class SearchPoiOverlay extends PoiOverlay {

    private MKSearch mSearch = null;
    private Context mContext = null;

    public SearchPoiOverlay(Activity activity, MapView mapView, MKSearch search) {
        super(activity, mapView);
        mContext = activity;
        mSearch = search;
    }

    @Override
    protected boolean onTap(int i) {
        super.onTap(i);
        MKPoiInfo info = getPoi(i);
//        /* It will open PlaceCaterActivity provided by Baidu SDK */
//        if (info.hasCaterDetails && !TextUtils.isEmpty(info.uid)) {
//            mSearch.poiDetailSearch(info.uid);
//        }
        if (!TextUtils.isEmpty(info.uid)) {
            Intent intent = new Intent();
            // TODO: go to check in activity
            mContext.startActivity(intent);
        }
        return true;
    }
}
