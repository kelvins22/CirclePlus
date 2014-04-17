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
import android.os.Bundle;
import android.text.TextUtils;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKSearch;

import circleplus.app.CheckinActivity;

public class SearchPoiOverlay extends PoiOverlay {

    private MKSearch mSearch = null;
    private Context mContext = null;
    private String mProvince = null;

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
            Intent intent = new Intent(mContext, CheckinActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(CheckinActivity.KEY_POI_NAME, info.name);
            bundle.putString(CheckinActivity.KEY_POI_PROVINCE, mProvince);
            bundle.putString(CheckinActivity.KEY_POI_CITY, info.city);
            bundle.putString(CheckinActivity.KEY_POI_ADDRESS, info.address);
            bundle.putString(CheckinActivity.KEY_POI_UID, info.uid);
            bundle.putInt(CheckinActivity.KEY_POI_TYPE, info.ePoiType);
            bundle.putInt(CheckinActivity.KEY_POI_LAT, info.pt.getLatitudeE6());
            bundle.putInt(CheckinActivity.KEY_POI_LNG, info.pt.getLongitudeE6());
            bundle.putBoolean(CheckinActivity.KEY_POI_PANORAMA, info.isPano);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        }
        return true;
    }

    public void setExtra(Bundle bundle) {
        if (bundle != null) {
            mProvince = bundle.getString(CheckinActivity.KEY_POI_PROVINCE);
        }
    }
}
