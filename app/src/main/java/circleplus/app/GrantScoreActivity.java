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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import circleplus.app.http.CirclePlusApi;
import circleplus.app.types.BaseType;
import circleplus.app.types.Checkin;
import circleplus.app.types.User;

public class GrantScoreActivity extends ActionBarActivity {

    private static final boolean D = true;
    private static final String TAG = "GrantScoreActivity";

    public static final String KEY_CHECK_IN_OBJECT = "key_check_in_object";

    private int mScore = 0;
    private long mCheckinId = -1L;
    private GrantScoreTask mTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.grant_score_layout);

        setProgressBarIndeterminateVisibility(false);

        Checkin mCheckin = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(KEY_CHECK_IN_OBJECT)) {
            mCheckin = bundle.getParcelable(KEY_CHECK_IN_OBJECT);
        }

        TextView mNameText = (TextView) findViewById(R.id.checkin_name);
        TextView mTimeText = (TextView) findViewById(R.id.checkin_time);
        TextView mUserText = (TextView) findViewById(R.id.checkin_user);
        TextView mShoutText = (TextView) findViewById(R.id.checkin_shout);
        final SeekBar mScoreBar = (SeekBar) findViewById(R.id.checkin_score_picker);
        Button mGrantButton = (Button) findViewById(R.id.grant_score_button);

        if (mCheckin != null) {
            mCheckinId = mCheckin.getId();
            mNameText.setText(mCheckin.getName());
            mTimeText.setText(mCheckin.getCreated());
            User user = mCheckin.getUser();
            mUserText.setText(user == null ? "" : user.getName());
            mShoutText.setText(mCheckin.getShout());

            mScoreBar.setIndeterminate(false);
            mScoreBar.setMax(10);
            mScoreBar.setProgress(1);
            mScoreBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mScore = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });

            mGrantButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (D) {
                        Log.d(TAG, "check-in ID = " + mCheckinId + " score = " + mScore);
                    }
                    mTask = new GrantScoreTask();
                    mTask.execute();
                }
            });
        } else {
            Toast.makeText(GrantScoreActivity.this, "Not check-in data",
                    Toast.LENGTH_SHORT).show();
        }

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

    private class GrantScoreTask extends AsyncTask<Void, Void, BaseType> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GrantScoreActivity.this.setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            GrantScoreActivity.this.setProgressBarIndeterminateVisibility(false);
        }

        @Override
        protected void onPostExecute(BaseType result) {
            super.onPostExecute(result);
            GrantScoreActivity.this.setProgressBarIndeterminateVisibility(false);
            if (result == null) {
                Toast.makeText(GrantScoreActivity.this, "Network error",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (result instanceof circleplus.app.types.Status) {
                Toast.makeText(GrantScoreActivity.this,
                        ((circleplus.app.types.Status) result).getMessage(),
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(GrantScoreActivity.this, "Result parse error",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected BaseType doInBackground(Void... params) {
            CirclePlusApi api = new CirclePlusApi();
            BaseType result = null;
            try {
                result = api.grantScore(mCheckinId, mScore);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return result;
        }
    }
}
