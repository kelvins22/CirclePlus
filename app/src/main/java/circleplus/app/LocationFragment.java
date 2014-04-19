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

package circleplus.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionInfo;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import java.util.List;

import circleplus.app.utils.LocationUtils;
import circleplus.app.widgets.CheckinMapView;
import circleplus.app.widgets.PoiInfoListAdapter;
import circleplus.app.widgets.SearchPoiOverlay;

public class LocationFragment extends Fragment implements
        View.OnClickListener {

    // Debug
    private static final boolean D = true;
    private static final String TAG = "LocationFragment";

    /*
     * Button type transfer:
     * Default: locate
     * locate (-- located -->) follow
     * follow (-- click -->) compass
     * compass (-- click -->) follow
     * --------------------------------------------
     * LocationMode
     * locate -> normal
     * follow -> following
     * compass -> compass
     */
    private enum ButtonType {
        LOC, COMPASS, FOLLOW
    }

    private ButtonType mCurBtnType;

    private Button mGetLocationButton = null;

    // Member fields for searching
    private Button mSearchButton = null;
    private AutoCompleteTextView mSearchText = null;
    private MKSearch mSearch = null;
    private FragSearchListener mFragSearchListener = null;
    private ArrayAdapter<String> mSuggestAdapter = null;
    private String mCurrentCity = null;
    private boolean mHasLocationData = false;

    // Member fields for locating
    private LocationClient mLocationClient = null;
    private LocationData mLocationData = null;
    private String mProvince = null;
    private BDLocationListener mLocationListener = new FragLocationListener();

    // Location overlay -- blue point
    private FragLocationOverlay mLocationOverlay = null;

    // Map view
    private CheckinMapView mMapView = null;
    private MapController mMapController = null;

    private CheckinPopupContentView mContentView = null;
    private PopupWindow mPopupWindow = null;

    // Handle locating requests
    boolean isRequest = false;
    boolean isFirstLoc = true;

// ------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CirclePlusApp app = (CirclePlusApp) (getActivity().getApplicationContext());
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(getActivity().getApplicationContext());
            // initialize BMapManager
            app.mBMapManager.init(CirclePlusApp.KEY_STR, new CirclePlusApp.
                    AppGeneralListener());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.location_frag, container, false);

        mGetLocationButton = (Button) v.findViewById(R.id.get_location_button);
        mSearchText = (AutoCompleteTextView) v.findViewById(R.id.search_loc_text);
        mSearchButton = (Button) v.findViewById(R.id.search_button);
        mMapView = (CheckinMapView) v.findViewById(R.id.map_view);

        mMapController = mMapView.getController();
        mLocationOverlay = new FragLocationOverlay(mMapView);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mMapView.onRestoreInstanceState(savedInstanceState);
        }

        // Register click listeners
        mSearchButton.setOnClickListener(this);

        mGetLocationButton.setOnClickListener(this);
        mGetLocationButton.setText("Locate");
        mCurBtnType = ButtonType.LOC;

        mMapController.setZoom(LocationUtils.DEFAULT_ZOOM);
        mMapController.enableClick(LocationUtils.ENABLE_MAP_CLICK);
        mMapView.setBuiltInZoomControls(LocationUtils.ENABLE_BUILT_IN_ZOOM_CONTROLS);

        mMapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        // Transfer from FOLLOW to LOCATE (NORMAL) when moving
                        if (mCurBtnType == ButtonType.FOLLOW) {
                            mLocationOverlay.setLocationMode(
                                    MyLocationOverlay.LocationMode.NORMAL);
                        }
                        mGetLocationButton.setText("Locate");
                        mCurBtnType = ButtonType.LOC;
                        break;
                }
                return false;
            }
        });

        // Initialize locating
        mHasLocationData = false;
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationData = new LocationData();
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setOpenGps(LocationUtils.OPEN_GPS);
        option.setCoorType(LocationUtils.COORDINATE_TYPE);
        option.setScanSpan(LocationUtils.SCAN_SPAN);
        option.setIsNeedAddress(LocationUtils.NEED_ADDRESS);
        mLocationClient.setLocOption(option);
        mLocationClient.start();

        // Initialize location overlay
        mLocationOverlay.setLocationMode(MyLocationOverlay.LocationMode.NORMAL);
        mLocationOverlay.setData(mLocationData);
        // Add location overlay to map view
        mMapView.getOverlays().add(mLocationOverlay);
        mLocationOverlay.enableCompass();

        // Refresh on setting location data
        mMapView.refresh();

        // Initialize search
        mSearch = new MKSearch();
        CirclePlusApp app = (CirclePlusApp) getActivity().getApplicationContext();
        mFragSearchListener = new FragSearchListener();
        mSearch.init(app.mBMapManager, mFragSearchListener);

        mSuggestAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line);
        mSearchText.setAdapter(mSuggestAdapter);
        mSearchText.addTextChangedListener(new SearchTextWatcher());
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        if (mSearch != null) {
            mSearch.destory();
        }
        mMapView.destroy();
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        if (v == mGetLocationButton) {
            switch (mCurBtnType) {
                case LOC:
                    requestLocClick();
                    break;
                case COMPASS:
                    mLocationOverlay.setLocationMode(
                            MyLocationOverlay.LocationMode.FOLLOWING);
                    mGetLocationButton.setText("Follow");
                    mCurBtnType = ButtonType.FOLLOW;
                    break;
                case FOLLOW:
                    mLocationOverlay.setLocationMode(
                            MyLocationOverlay.LocationMode.COMPASS);
                    mGetLocationButton.setText("Compass");
                    mCurBtnType = ButtonType.COMPASS;
                    break;
                default:
                    break;
            }
//      // Manual click for searching
        } else if (v == mSearchButton) {
            /* Search nearby */
            Editable text = mSearchText.getText();
            String poiKey = text == null ? "" : text.toString();
            /*
             * Search order:
             * (1) Search nearby when we have location data (lat lng)
             * (2) Search in city when we have city info
             */
            mFragSearchListener.setRequestCode(FragSearchListener.REQ_CODE_SEARCH);
            mSearch.setPoiPageCapacity(20);
            if (mHasLocationData && !TextUtils.isEmpty(poiKey)) {
                mSearch.poiSearchNearBy(
                        poiKey,
                        new GeoPoint((int) (mLocationData.latitude * 1e6),
                                (int) (mLocationData.longitude * 1e6)),
                        LocationUtils.POI_DISTANCE
                );
            } else if (!TextUtils.isEmpty(mCurrentCity) && !TextUtils.isEmpty(poiKey)) {
                mSearch.poiSearchInCity(poiKey, mCurrentCity);
            } else if (!TextUtils.isEmpty(poiKey)) {
                Toast.makeText(getActivity(), "Can not get poi info",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Can not get location or poi info",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Manual click to send a request.
     */
    void requestLocClick() {
        isRequest = true;
        mHasLocationData = false;
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.requestLocation();
            Toast.makeText(getActivity(), "Locating...", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Location Client is not started...");
        }
    }

    /*
    static Bitmap getBitmapFromView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = view.getDrawingCache(true);
        return bitmap;
    }
    */

    /**
     * Custom location listener implements BDLocationListener
     */
    private class FragLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) {
                return;
            }

            mHasLocationData = true;
            mLocationData.latitude = location.getLatitude();
            mLocationData.longitude = location.getLongitude();
            mLocationData.accuracy = location.getRadius();
            mLocationData.direction = location.getDirection();
            mLocationData.satellitesNum = location.getSatelliteNumber();
            mLocationData.speed = location.getSpeed();

            mProvince = location.getProvince();
            mCurrentCity = location.getCity();

            /*
             * Everything relative to poi not lat lng,
             * use Baidu map api rather than Baidu location api
             */
            // Geocode address info with lat lng data
            mSearch.reverseGeocode(new GeoPoint(
                    (int) (mLocationData.latitude * 1e6),
                    (int) (mLocationData.longitude * 1e6)));

            // Update location data
            mLocationOverlay.setData(mLocationData);
            // Refresh map view
            mMapView.refresh();

            // Move to locating point if triggered by click or first locating
            if (isRequest || isFirstLoc) {
                if (D) Log.d(TAG, "receive location, animate to it");

                mMapController.animateTo(new GeoPoint(
                        (int) (mLocationData.latitude * 1e6),
                        (int) (mLocationData.longitude * 1e6)));
                isRequest = false;

                // First time: locate automatically --> follow
                // isRequest is triggered by clicking "locate"
                mLocationOverlay.setLocationMode(
                        MyLocationOverlay.LocationMode.FOLLOWING);
                mMapView.getOverlays().remove(mLocationOverlay);
                mMapView.getOverlays().add(mLocationOverlay);
                mMapView.refresh();
                mGetLocationButton.setText("Follow");
                mCurBtnType = ButtonType.FOLLOW;
            }
            // Finish first locating
            isFirstLoc = false;
        }

        @Override
        public void onReceivePoi(BDLocation poiLocation) {
            // Since we don't request poi, so omitted
        }
    }

    private void handleSearchPoiResult(MKPoiResult mkPoiResult) {
        // Move cursor to the first poi point center
        if (mkPoiResult.getCurrentNumPois() > 0) {
            // Display all the poi points
            SearchPoiOverlay overlay = new SearchPoiOverlay(getActivity(),
                    mMapView, mSearch);
            overlay.setData(mkPoiResult.getAllPoi());
            Bundle bundle = new Bundle();
            bundle.putString(CheckinActivity.KEY_POI_PROVINCE, mProvince);
            overlay.setExtra(bundle);
            // Clear old overlays and add new
            mMapView.getOverlays().clear();
            mMapView.getOverlays().add(overlay);
            mMapView.refresh();

            // Move to the first not null info
            List<MKPoiInfo> infoList = mkPoiResult.getAllPoi();
            for (MKPoiInfo info : infoList) {
                // info.pt is null when info.ePoiType == 2 or 4
                if (info.pt != null) {
                    mMapView.getController().animateTo(info.pt);
                    break;
                }
            }
//      // The typed poi is found in other cities rather than this
        } else if (mkPoiResult.getCityListNum() > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Found in ");
            for (int i = 0, sz = mkPoiResult.getCityListNum(); i < sz; i++) {
                sb.append(mkPoiResult.getCityListInfo(i).city);
                sb.append("  ");
            }
            // TODO: make it more elegant
            Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private class FragSearchListener implements MKSearchListener {

        static final int REQ_CODE_LIST = 0x1;
        static final int REQ_CODE_SEARCH = 0x2;

        private int requestCode = 0;

        FragSearchListener() {
            requestCode = REQ_CODE_SEARCH;
        }

        public void setRequestCode(int requestCode) {
            this.requestCode = requestCode;
        }

        @Override
        public void onGetPoiResult(MKPoiResult mkPoiResult, int type, int error) {
            if (getActivity() != null) {
                if (error != 0 || mkPoiResult == null) {
                    Toast.makeText(getActivity(), "Nothing found...",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (requestCode == REQ_CODE_SEARCH) {
                        handleSearchPoiResult(mkPoiResult);
                    } else if (requestCode == REQ_CODE_LIST) {
                        handleListPoiResult(mkPoiResult);
                    }
                } // end else
            } // end if (getActivity() != null)
        }

        @Override
        public void onGetTransitRouteResult(MKTransitRouteResult mkTransitRouteResult, int i) {
        }

        @Override
        public void onGetDrivingRouteResult(MKDrivingRouteResult mkDrivingRouteResult, int i) {
        }

        @Override
        public void onGetWalkingRouteResult(MKWalkingRouteResult mkWalkingRouteResult, int i) {
        }

        @Override
        public void onGetAddrResult(MKAddrInfo mkAddrInfo, int error) {
            if (getActivity() != null) {
                if (error != 0 || mkAddrInfo == null) {
                    Toast.makeText(getActivity(), "Get address failed...",
                            Toast.LENGTH_SHORT).show();
                } else {
                    mCurrentCity = mkAddrInfo.addressComponents.city;
                }
            }
        }

        @Override
        public void onGetBusDetailResult(MKBusLineResult mkBusLineResult, int i) {
        }

        @Override
        public void onGetSuggestionResult(MKSuggestionResult mkSuggestionResult, int i) {
            // Add all suggestions into suggestion adapter
            if (getActivity() != null && mkSuggestionResult != null
                    && mkSuggestionResult.getAllSuggestions() != null) {
                mSuggestAdapter.clear();
                List<MKSuggestionInfo> sugList = mkSuggestionResult.getAllSuggestions();
                for (MKSuggestionInfo info : sugList) {
                    if (info.key != null) {
                        mSuggestAdapter.add(info.key);
                    }
                }
                mSuggestAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onGetPoiDetailSearchResult(int type, int error) {
            // Do nothing here
            /*
             * MKSearch.poiDetailSearch() will enter here,
             * and it will go to PlaceCaterActivity
             */
        }

        @Override
        public void onGetShareUrlResult(MKShareUrlResult mkShareUrlResult, int type, int error) {
            // TODO: add share function later
            /*
            if (error == 0) {
                if (type == MKEvent.MKEVENT_POIDETAILSHAREURL) {

                } else if (type == MKEvent.MKEVENT_POIRGCSHAREURL) {

                }
            }
            */
        } // end onGetShareUrlResult()
    }

    private class SearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() >= 0 && !TextUtils.isEmpty(mCurrentCity)) {
                mSearch.suggestionSearch(s.toString(), mCurrentCity);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    /**
     * LocationOverlay -- Blue point
     */
    private class FragLocationOverlay extends MyLocationOverlay {

        public FragLocationOverlay(MapView mapView) {
            super(mapView);
        }

        @Override
        protected boolean dispatchTap() {
            LocationData locationData = getMyLocation();
            mFragSearchListener.setRequestCode(FragSearchListener.REQ_CODE_LIST);
            mSearch.setPoiPageCapacity(50);
            mSearch.poiSearchNearBy("美食,娱乐,旅游",
                    new GeoPoint((int) (locationData.latitude * 1e6),
                            (int) (locationData.longitude * 1e6)),
                    LocationUtils.POI_DISTANCE / 2
            );

            Resources res = getActivity().getResources();
            DisplayMetrics metrics = res.getDisplayMetrics();
            int width = metrics.widthPixels * 4 / 5;
            int height = metrics.heightPixels * 4 / 5;

            mPopupWindow = new PopupWindow();
            mContentView = new CheckinPopupContentView(getActivity());
            mContentView.progressBar.setVisibility(View.VISIBLE);
            mPopupWindow.setWidth(width);
            mPopupWindow.setHeight(height);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(res.getDrawable(
                    R.drawable.transparent_dark_holo));
            mPopupWindow.setContentView(mContentView.view);
            mPopupWindow.setAnimationStyle(R.style.popup_window_anim_style);
            mPopupWindow.showAtLocation(getActivity().getWindow().getDecorView(),
                    Gravity.CENTER, 0, 0);
            return true;
        }
    }

    private class CheckinPopupContentView {

        View view = null;
        View headerView = null;
        ProgressBar progressBar = null;
        ListView listView = null;
        PoiInfoListAdapter adapter = null;

        public CheckinPopupContentView(Context context) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            headerView = inflater.inflate(R.layout.business_header_layout, null);
            view = inflater.inflate(R.layout.checkin_popup_layout, null);
            progressBar = (ProgressBar) view.findViewById(R.id.action_bar_progress);
            listView = (ListView) view.findViewById(R.id.popup_content_list_view);
            progressBar.setIndeterminate(true);
            adapter = new PoiInfoListAdapter(context);
            listView.addHeaderView(headerView);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (view == headerView) {
                        Intent intent = new Intent(getActivity(), BusinessCheckinActivity.class);
                        startActivity(intent);
                    } else {
                        MKPoiInfo info = (MKPoiInfo) adapter.getItem(position);
                        if (!TextUtils.isEmpty(info.uid)) {
                            Intent intent = new Intent(getActivity(), CheckinActivity.class);
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
                            startActivity(intent);
                        }
                    }
                } // end onItemClick()
            });
        }
    }

    private void handleListPoiResult(MKPoiResult mkPoiResult) {
        if (mkPoiResult.getCurrentNumPois() > 0 && mContentView != null) {
            List<MKPoiInfo> poiInfoList = mkPoiResult.getAllPoi();
            mContentView.progressBar.setVisibility(View.INVISIBLE);
            mContentView.adapter.setData(poiInfoList);
        }
    }
}
