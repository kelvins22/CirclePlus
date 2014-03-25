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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class LocationFragment extends Fragment implements
        View.OnClickListener {

    // Debug
    private static final boolean D = false; //true;
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

    /*
    // Member fields for seaching
    private Button mSearchButton = null;
    private AutoCompleteTextView mSearchText = null;
    private MKSearch mSearch = null;
    private ArrayAdapter<String> mSuggestAdapter = null;
    private int mLoadIndex = 0;
    private String mCurrentCity = null;
    */

    // Member fields for locating
    private LocationClient mLocationClient = null;
    private LocationData mLocationData = null;
    private BDLocationListener mLocationListener = new FragLocationListener();

    // Location overlay -- blue point
    private FragLocationOverlay mLocationOverlay = null;
//    private PopupOverlay mPopupOverlay = null;
//    private TextView mPopupText = null;

    // Map view
    private CheckInMapView mMapView = null;
    private MapController mMapController = null;

    // Handle locating requests
    boolean isRequest = false;
    boolean isFirstLoc = true;

// ---

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
        /*
        mSearchText = (AutoCompleteTextView) v.findViewById(R.id.search_loc_text);
        mSearchButton = (Button) v.findViewById(R.id.search_button);
        */
        mMapView = (CheckInMapView) v.findViewById(R.id.map_view);

        mMapController = mMapView.getController();
        mLocationOverlay = new FragLocationOverlay(mMapView);
        createPopupOverlay(inflater);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mMapView.onRestoreInstanceState(savedInstanceState);
        }

        // Register click listeners
        /*
        mSearchButton.setOnClickListener(this);
        */

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
        mLocationClient = new LocationClient(getActivity());
        mLocationData = new LocationData();
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setOpenGps(LocationUtils.OPEN_GPS);
        option.setCoorType(LocationUtils.COORDINATE_TYPE);
        option.setScanSpan(LocationUtils.SCAN_SPAN);
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

        /*
        // Initialize search
        mSearch = new MKSearch();
        CirclePlusApp app = (CirclePlusApp) getActivity().getApplicationContext();
        mSearch.init(app.mBMapManager, new FragSearchListener());

        mSuggestAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line);
        mSearchText.setAdapter(mSuggestAdapter);
        mSearchText.addTextChangedListener(new SearchTextWatcher());
        */
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
        /*
        if (mSearch != null) {
            mSearch.destory();
        }
        */
        mMapView.destroy();
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
            /*
        } else if (v == mSearchButton) {
            Editable text = mSearchText.getText();
            String poiKey = text == null ? "" : text.toString();
            if (!TextUtils.isEmpty(mCurrentCity) && !TextUtils.isEmpty(poiKey)) {
                mSearch.poiSearchInCity(mCurrentCity, poiKey);
            } else if (!TextUtils.isEmpty(poiKey)) {
                Toast.makeText(getActivity(), "Can not get city info",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Can not get city info or poi info",
                        Toast.LENGTH_SHORT).show();
            }
            */
        }
    }

    private void createPopupOverlay(LayoutInflater inflater) {
        /*
        View viewCache = inflater.inflate(R.layout.custom_text_view, null);
        mPopupText = (TextView) viewCache.findViewById(R.id.text_cache);

        PopupClickListener popupListener = new PopupClickListener() {
            @Override
            public void onClickedPopup(int index) {
                Log.v(TAG, "Click popup overlay, index = " + index);
                Toast.makeText(getActivity(), "A popup window to check in like foursquare",
                        Toast.LENGTH_LONG).show();
            }
        };
        mPopupOverlay = new PopupOverlay(mMapView, popupListener);
        mMapView.setPopup(mPopupOverlay);
        */
    }

    /**
     * Manual click to send a request.
     */
    void requestLocClick() {
        isRequest = true;
        mLocationClient.requestLocation();
        Toast.makeText(getActivity(), "Locating...", Toast.LENGTH_SHORT).show();
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

            mLocationData.latitude = location.getLatitude();
            mLocationData.longitude = location.getLongitude();
            mLocationData.accuracy = location.getRadius();
            mLocationData.direction = location.getDirection();
            mLocationData.satellitesNum = location.getSatelliteNumber();
            mLocationData.speed = location.getSpeed();

            // Update location data
            mLocationOverlay.setData(mLocationData);
            // Refresh map view
            mMapView.refresh();

            // Move to locating point if triggered by click or first locating
            if (isRequest || isFirstLoc) {
                if (D) Log.d(TAG, "receive location, animate to it");

                mMapController.animateTo(new GeoPoint((int) (mLocationData.latitude * 1e6),
                        (int) (mLocationData.longitude * 1e6)));
                isRequest = false;

                // First time: locate automatically --> follow
                // isRequest is triggered by clicking "locate"
                mLocationOverlay.setLocationMode(
                        MyLocationOverlay.LocationMode.FOLLOWING);
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

    /*
    private class FragSearchListener implements MKSearchListener {

        @Override
        public void onGetPoiResult(MKPoiResult mkPoiResult, int type, int error) {
            if (getActivity() != null) {
                if (error != 0 || mkPoiResult == null) {
                    Toast.makeText(getActivity(), "Nothing found...",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Move cursor to the first poi point center
                    if (mkPoiResult.getCurrentNumPois() > 0) {
                        // Display all the poi points
                        SearchPoiOverlay overlay = new SearchPoiOverlay(getActivity(),
                                mMapView, mSearch);
                        overlay.setData(mkPoiResult.getAllPoi());
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
//                  // The typed poi is found in other cities rather than this
                    } else if (mkPoiResult.getCityListNum() > 0) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Found in ");
                        for (int i = 0, sz = mkPoiResult.getCityListNum(); i < sz; i++) {
                            sb.append(mkPoiResult.getCityListInfo(i).city);
                            sb.append("  ");
                        }
                        Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_LONG).show();
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
        public void onGetAddrResult(MKAddrInfo mkAddrInfo, int i) {
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
            // error == 0 indicates success
            if (getActivity() != null) {
                if (error != 0) {
                    Toast.makeText(getActivity(), "Nothing found...",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (D) {
                        Toast.makeText(getActivity(), "Check detail info.",
                                Toast.LENGTH_SHORT).show();
                    }
                    // TODO: Show a detail info popup window
                }
            }
        }

        @Override
        public void onGetShareUrlResult(MKShareUrlResult mkShareUrlResult, int type, int error) {
            // TODO: Add share function
        }
    }

    private class SearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() >= 0) {

            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
    */

    /**
     * LocationOverlay -- Blue point
     */
    private class FragLocationOverlay extends MyLocationOverlay {

        public FragLocationOverlay(MapView mapView) {
            super(mapView);
        }

        @Override
        protected boolean dispatchTap() {
            /*
            // Handle tap event, show the bubble
            StringBuilder sb = new StringBuilder();
            if (D) {
                mPopupText.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
                sb.append("My Location = (");
                sb.append(mLocationData.latitude);
                sb.append(", ");
                sb.append(mLocationData.longitude);
                sb.append(")");
            } else {
                mPopupText.setBackgroundResource(0);
                mPopupText.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, 0, R.drawable.location_popup);
                int pad = (int) (getResources().getDisplayMetrics().density * 3 + 0.5f);
                mPopupText.setCompoundDrawablePadding(pad);
                sb.append("Check-in");
            }
            mPopupText.setText(sb.toString());
            mPopupOverlay.showPopup(
                    getBitmapFromView(mPopupText),  // drawable
                    new GeoPoint((int) (mLocationData.latitude * 1e6),  // geo point x
                            (int) (mLocationData.longitude * 1e6)),     // geo point y
                    8   // radius
            );
            return true;
            */
            return false;
        }
    }

}
