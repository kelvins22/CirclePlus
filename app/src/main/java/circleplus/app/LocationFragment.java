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

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class LocationFragment extends Fragment implements
        View.OnClickListener {

    // Debug
    private static final boolean D = true;
    private static final String TAG = "LocationFragment";

    private enum ButtonType {
        LOC, COMPASS, FOLLOW
    }

    private ButtonType mCurBtnType;

    private Button mGetLocationButton = null;
    private TextView mDisplayArea = null;

    // Member fields for locating
    private LocationClient mLocationClient = null;
    private LocationData mLocationData = null;
    private BDLocationListener mLocationListener = new FragLocationListener();

    // Location overlay
    private FragLocationOverlay mLocationOverlay = null;

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
        mDisplayArea = (TextView) v.findViewById(R.id.display_area);
        mMapView = (CheckInMapView) v.findViewById(R.id.map_view);

        mGetLocationButton.setOnClickListener(this);
        mMapController = mMapView.getController();
        mMapController.setZoom(LocationUtils.DEFAULT_ZOOM);
        mMapController.enableClick(LocationUtils.ENABLE_MAP_CLICK);
        mMapView.setBuiltInZoomControls(LocationUtils.ENABLE_BUILT_IN_ZOOM_CONTROLS);

        mLocationOverlay = new FragLocationOverlay(mMapView);
        // create popup overlay
        createPopupOverlay(inflater, mMapView, mLocationOverlay);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mMapView.onRestoreInstanceState(savedInstanceState);
        }

        // Initialize locating
        mLocationClient = new LocationClient(getActivity());
        mLocationData = new LocationData();
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(LocationUtils.OPEN_GPS);
        option.setCoorType(LocationUtils.COORDINATE_TYPE);
        option.setScanSpan(LocationUtils.SCAN_SPAN);
        mLocationClient.setLocOption(option);
        mLocationClient.start();

        // Initialize location overlay
        mLocationOverlay.setData(mLocationData);
        // Add location overlay to map view
        mMapView.getOverlays().add(mLocationOverlay);
        mLocationOverlay.enableCompass();

        // Refresh on setting location data
        mMapView.refresh();
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
        mMapView.destroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mGetLocationButton)) {
            switch (mCurBtnType) {
                case LOC:
                    requestLocClick();
                    break;
                case COMPASS:
                    mLocationOverlay.setLocationMode(
                            MyLocationOverlay.LocationMode.NORMAL);
                    mGetLocationButton.setText("Locate");
                    mCurBtnType = ButtonType.LOC;
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
        }
    }

    private void createPopupOverlay(LayoutInflater inflater,
                                    CheckInMapView mapView, FragLocationOverlay locationOverlay) {
        View viewCache = inflater.inflate(R.layout.custom_text_view, null);
        TextView popupText = (TextView) viewCache.findViewById(R.id.text_cache);

        PopupClickListener popupListener = new PopupClickListener() {
            @Override
            public void onClickedPopup(int index) {
                Log.v(TAG, "Click popup overlay, index = " + index);
            }
        };
        PopupOverlay popup = new PopupOverlay(mapView, popupListener);
        locationOverlay.setPopupText(popupText);
    }

    /**
     * Manual click to send a request.
     */
    void requestLocClick() {
        isRequest = true;
        mLocationClient.requestLocation();
        Toast.makeText(getActivity(), "Locating...", Toast.LENGTH_SHORT).show();
    }

    /**
     * Change location overlay icon
     *
     * @param marker Marker drawable for overlay, null indicates using default
     */
    void modifyLocationOverlayIcon(Drawable marker) {
        mLocationOverlay.setMarker(marker);
        mMapView.refresh();
    }

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
            mLocationData.direction = location.getDerect();

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
            if (poiLocation == null) {
                return;
            }
        }
    }

    /**
     * LocationOverlay
     */
    private static class FragLocationOverlay extends MyLocationOverlay {

        private TextView mPopupText = null;

        public FragLocationOverlay(MapView mapView) {
            super(mapView);
        }

        @Override
        protected boolean dispatchTap() {
            // Handle tap event, show the bubble
            if (mPopupText == null) {
                throw new IllegalStateException("Must call setPopupText()" +
                        " after create FragLocationOverlay");
            }
            return true;
        }

        public void setPopupText(TextView popupText) {
            mPopupText = popupText;
        }
    }

}
