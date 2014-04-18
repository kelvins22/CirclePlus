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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import circleplus.app.http.CirclePlusApi;
import circleplus.app.http.ResponseCode;
import circleplus.app.types.BaseType;
import circleplus.app.utils.UserUtils;

public class CheckinActivity extends ActionBarActivity {

    public static final String KEY_POI_NAME = "key_poi_name";
    public static final String KEY_POI_PROVINCE = "key_poi_province";
    public static final String KEY_POI_CITY = "key_poi_city";
    public static final String KEY_POI_ADDRESS = "key_poi_address";
    public static final String KEY_POI_LAT = "key_poi_lat";
    public static final String KEY_POI_LNG = "key_poi_lng";
    public static final String KEY_POI_PANORAMA = "key_poi_panorama";
    public static final String KEY_POI_TYPE = "key_poi_type";
    public static final String KEY_POI_UID = "key_poi_uid";

    private String mUid, mName, mProvince, mCity, mAddress;
    private int mLat, mLng, mType;
    private boolean mIsPanorama;

    private ImageView mAlbumImage;
    private TextView mTitleText, mInfoText, mHistoryText;
    private EditText mShoutInput;
    private Button mCheckinButton;

    private CheckinTask mTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.checkin_layout);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mUid = bundle.getString(KEY_POI_UID);
            mName = bundle.getString(KEY_POI_NAME);
            mProvince = bundle.getString(KEY_POI_PROVINCE);
            mCity = bundle.getString(KEY_POI_CITY);
            mAddress = bundle.getString(KEY_POI_ADDRESS);
            mLat = bundle.getInt(KEY_POI_LAT);
            mLng = bundle.getInt(KEY_POI_LNG);
            mType = bundle.getInt(KEY_POI_TYPE);
            mIsPanorama = bundle.getBoolean(KEY_POI_PANORAMA);
        } else {
            mUid = mName = mProvince = mCity = mAddress = "";
            mLat = mLng = mType = 0;
            mIsPanorama = false;
        }

        mAlbumImage = (ImageView) findViewById(R.id.album_image_view);
        mTitleText = (TextView) findViewById(R.id.checkin_title);
        mInfoText = (TextView) findViewById(R.id.checkin_info);
        mHistoryText = (TextView) findViewById(R.id.checkin_history);
        mShoutInput = (EditText) findViewById(R.id.shout_input);
        mCheckinButton = (Button) findViewById(R.id.checkin_button);

        mTitleText.setText(mName);
        mInfoText.setText(mCity + " " + mAddress);
        mHistoryText.setText("0 people have check-in here.");
        mCheckinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShoutInput.getText() != null
                        && mShoutInput.getText().toString().length() > 0) {
                    mTask = new CheckinTask();
                    String shout = mShoutInput.getText().toString();
                    mTask.execute(shout);
                } else {
                    Toast.makeText(CheckinActivity.this, "No input error",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTask != null) {
            mTask.cancel(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class CheckinTask extends AsyncTask<String, Void, BaseType> {
        @Override
        protected BaseType doInBackground(String... params) {
            String shout = params[0];
            BaseType result = null;
            long uid = UserUtils.getUserId(CheckinActivity.this);
            if (uid == -1) {
                result = new circleplus.app.types.Status();
                ((circleplus.app.types.Status) result)
                        .setStatusCode(ResponseCode.STATUS_CHECK_IN_ERROR);
                ((circleplus.app.types.Status) result)
                        .setMessage("You have NOT login!");
            } else {
                CirclePlusApi api = new CirclePlusApi();
                if (TextUtils.isEmpty(mProvince)) {
                    mProvince = "浙江";
                }
                try {
                    result = api.checkin(mName, "中国", mProvince, mCity, mAddress,
                            shout, mLat, mLng, mType, uid);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CheckinActivity.this.setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onPostExecute(BaseType status) {
            super.onPostExecute(status);
            CheckinActivity.this.setProgressBarIndeterminateVisibility(false);
            if (status == null) {
                Toast.makeText(CheckinActivity.this, "Network error",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (status instanceof circleplus.app.types.Status) {
                Toast.makeText(CheckinActivity.this,
                        ((circleplus.app.types.Status) status).getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            CheckinActivity.this.setProgressBarIndeterminateVisibility(false);
        }
    }
}
