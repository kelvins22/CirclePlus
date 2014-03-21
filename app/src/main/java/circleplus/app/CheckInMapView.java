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
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PopupOverlay;

/**
 * MapView with check-in popup overlay.
 */
public class CheckInMapView extends MapView {

    private PopupOverlay mPopup;

    public CheckInMapView(Context context) {
        super(context);
    }

    public CheckInMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckInMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!super.onTouchEvent(event)) {
            // Hide popup overlay
            if (mPopup != null && event.getAction() == MotionEvent.ACTION_UP) {
                mPopup.hidePop();
            }
        }
        return true;
    }

    public void setPopup(PopupOverlay popup) {
        mPopup = popup;
    }
}
