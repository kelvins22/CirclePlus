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

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

public class CirclePlusApp extends Application {
    private static CirclePlusApp sInstance = null;
    private boolean mKeyRight = true;
    BMapManager mBMapManager = null;

    public static final String KEY_STR = "your_key";

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initEngineManager(this);
    }

    /**
     * Initialize map manager.
     *
     * @param context The application context for map manager
     */
    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }
        if (!mBMapManager.init(KEY_STR, new AppGeneralListener())) {
            Toast.makeText(CirclePlusApp.getInstance().getApplicationContext(),
                    "BMapManager initialize failed!", Toast.LENGTH_LONG).show();
        }
    }

    public static CirclePlusApp getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("getInstance() called before created");
        }
        return sInstance;
    }

    public boolean isKeyRight() {
        return mKeyRight;
    }

    // AppGeneralListener for handling general error,
    // such as network, authority exceptions
    static class AppGeneralListener implements MKGeneralListener {

        @Override
        public void onGetNetworkState(int error) {
            if (error == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(CirclePlusApp.getInstance().getApplicationContext(),
                        "Network connection error", Toast.LENGTH_LONG).show();
            } else if (error == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(CirclePlusApp.getInstance().getApplicationContext(),
                        "Enter correct query statement", Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int error) {
            // A non-zero value indicates authentication failed
            if (error != 0) {
                Toast.makeText(CirclePlusApp.getInstance().getApplicationContext(),
                        "Please enter correct key and check your network state. Error code: " + error,
                        Toast.LENGTH_LONG).show();
                CirclePlusApp.getInstance().mKeyRight = false;
            } else {
                Toast.makeText(CirclePlusApp.getInstance().getApplicationContext(),
                        "Authentication success", Toast.LENGTH_LONG).show();
                CirclePlusApp.getInstance().mKeyRight = true;
            }
        }
    }
}
