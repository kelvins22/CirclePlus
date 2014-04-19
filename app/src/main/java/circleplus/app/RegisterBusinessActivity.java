package circleplus.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.IOException;

import circleplus.app.http.CirclePlusApi;
import circleplus.app.http.ResponseCode;
import circleplus.app.types.BaseType;
import circleplus.app.types.User;
import circleplus.app.utils.LocationUtils;
import circleplus.app.utils.UserUtils;

/**
 * Register business of a location
 */
public class RegisterBusinessActivity extends ActionBarActivity {

    private ImageView mLocationButton = null;
    private Button mRegisterButton = null;
    private EditText mLocationEdit = null;
    private EditText mAddressEdit = null;
    private EditText mLatEdit = null;
    private EditText mLngEdit = null;
    private EditText mBusinessEdit = null;
    private EditText mLocationTypeEdit = null;

    private String mNation = null, mProvince = null, mCity = null;
    private String mAddress = null, mLocationName = null, mBusinessName = null;
    private int mLat = 0, mLng = 0, mLocationType = 0;
    private long mUserId = 0L;

    private BDLocationListener mLocationListener = new SimpleLocationListener();
    private LocationClient mLocationClient = null;

    private RegisterBusinessTask mTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.register_business_layout);

        setResult(Activity.RESULT_CANCELED);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        option.setOpenGps(LocationUtils.OPEN_GPS);
        option.setCoorType(LocationUtils.COORDINATE_TYPE);
        option.setScanSpan(LocationUtils.SCAN_SPAN);
        option.setIsNeedAddress(LocationUtils.NEED_ADDRESS);
        mLocationClient.setLocOption(option);
        mLocationClient.start();

        mLocationButton = (ImageView) findViewById(R.id.location_button);
        mLocationEdit = (EditText) findViewById(R.id.location_name_edit);
        mAddressEdit = (EditText) findViewById(R.id.address_edit);
        mLatEdit = (EditText) findViewById(R.id.lat_edit);
        mLngEdit = (EditText) findViewById(R.id.lng_edit);
        mBusinessEdit = (EditText) findViewById(R.id.business_name_edit);
        mLocationTypeEdit = (EditText) findViewById(R.id.location_type_edit);
        mRegisterButton = (Button) findViewById(R.id.register_business_button);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mRegisterButton.setEnabled(locationEditTextFieldIsValid()
                        && addressEditTextFieldIsValid()
                        && latEditTextFieldIsValid()
                        && lngEditTextFieldIsValid()
                        && businessEditFieldIsValid()
                        && locationTypeEditFieldIsValid());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            private boolean locationEditTextFieldIsValid() {
                return !TextUtils.isEmpty(mLocationEdit.getText());
            }

            private boolean addressEditTextFieldIsValid() {
                return !TextUtils.isEmpty(mAddressEdit.getText());
            }

            private boolean latEditTextFieldIsValid() {
                return (!TextUtils.isEmpty(mLatEdit.getText()))
                        && getText(mLatEdit).length() >= 6;
            }

            private boolean lngEditTextFieldIsValid() {
                return (!TextUtils.isEmpty(mLngEdit.getText()))
                        && getText(mLngEdit).length() >= 6;
            }

            private boolean businessEditFieldIsValid() {
                return !TextUtils.isEmpty(mBusinessEdit.getText());
            }

            private boolean locationTypeEditFieldIsValid() {
                return !TextUtils.isEmpty(mLocationTypeEdit.getText());
            }
        };
        mLocationEdit.addTextChangedListener(textWatcher);
        mAddressEdit.addTextChangedListener(textWatcher);
        mLatEdit.addTextChangedListener(textWatcher);
        mLngEdit.addTextChangedListener(textWatcher);
        mBusinessEdit.addTextChangedListener(textWatcher);
        mLocationTypeEdit.addTextChangedListener(textWatcher);

        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocationClient != null && mLocationClient.isStarted()) {
                    mLocationClient.requestPoi();
                } else if (mLocationClient != null) {
                    mLocationClient.start();
                }
            }
        });

        mRegisterButton.setEnabled(false);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocationName = getText(mLocationEdit);
                mAddress = getText(mAddressEdit);
                mLat = Integer.parseInt(getText(mLatEdit));
                mLng = Integer.parseInt(getText(mLngEdit));
                mBusinessName = getText(mBusinessEdit);
                mLocationType = Integer.parseInt(getText(mLocationTypeEdit));
                mUserId = UserUtils.getUserId(RegisterBusinessActivity.this);

                mTask = new RegisterBusinessTask();
                mTask.execute();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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

    @Override
    protected void onStop() {
        super.onStop();

        if (mTask != null) {
            mTask.cancel(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLocationClient != null) {
            mLocationClient.stop();
        }
    }

    private class RegisterBusinessTask extends AsyncTask<Void, Void, BaseType> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RegisterBusinessActivity.this.setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected BaseType doInBackground(Void... params) {
            CirclePlusApi api = new CirclePlusApi();
            BaseType result = null;
            try {
                result = api.registerBusiness(mLocationName, mNation, mProvince,
                        mCity, mAddress, mLat, mLng, mLocationType, mUserId,
                        mBusinessName);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(BaseType result) {
            super.onPostExecute(result);
            RegisterBusinessActivity.this.setProgressBarIndeterminateVisibility(false);
            if (result == null) {
                Toast.makeText(RegisterBusinessActivity.this, "Network error",
                        Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_CANCELED);
                return;
            }

            if (result instanceof circleplus.app.types.Status) {
                circleplus.app.types.Status s = (circleplus.app.types.Status) result;
                Toast.makeText(RegisterBusinessActivity.this, s.getMessage(),
                        Toast.LENGTH_LONG).show();
                if (s.getStatusCode() == ResponseCode.STATUS_OK) {
                    setResult(Activity.RESULT_OK);
                    new Thread() {
                        @Override
                        public void run() {
                            User user = UserUtils.getUserInfo(RegisterBusinessActivity.this);
                            user.setIsBusiness(true);
                            UserUtils.storeUserInfo(RegisterBusinessActivity.this, user);
                            finish();
                        }
                    }.start();
                }
            } else {
                Toast.makeText(RegisterBusinessActivity.this, "Result parse error",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            RegisterBusinessActivity.this.setProgressBarIndeterminateVisibility(false);
        }
    }

    private static String getText(EditText editText) {
        Editable editable = editText.getText();
        return editable == null ? "" : editable.toString();
    }

    private class SimpleLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
        }

        @Override
        public void onReceivePoi(BDLocation bdLocation) {
            if (bdLocation != null) {
                mNation = "中国";
                mProvince = bdLocation.getProvince();
                mCity = bdLocation.getCity();
                mAddress = bdLocation.getAddrStr();
                mLocationName = bdLocation.getStreet();
                mLat = (int) (bdLocation.getLatitude() * 1e6);
                mLng = (int) (bdLocation.getLongitude() * 1e6);
                mLocationType = bdLocation.getLocType();

                mAddressEdit.setText(mAddress);
                mLocationEdit.setText(mLocationName);
                mLatEdit.setText(String.valueOf(mLat));
                mLngEdit.setText(String.valueOf(mLng));
                mLocationTypeEdit.setText(String.valueOf(mLocationType));
            }
        }
    } // end SimpleLocationListener
}
