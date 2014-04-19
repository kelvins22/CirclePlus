package circleplus.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import circleplus.app.http.CirclePlusApi;
import circleplus.app.types.BaseType;
import circleplus.app.types.Loc;
import circleplus.app.types.TypeArrayList;
import circleplus.app.widgets.PoiInfoListAdapter;

/**
 * Checkin business location provide by Circle Plus
 */
public class BusinessCheckinActivity extends ActionBarActivity {

    private PoiInfoListAdapter mAdapter = null;
    private ListBusinessLocTask mTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.favorite_frag);

        setProgressBarIndeterminateVisibility(false);

        ListView mListView = (ListView) findViewById(R.id.fav_place_list);
        mAdapter = new PoiInfoListAdapter(BusinessCheckinActivity.this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MKPoiInfo info = (MKPoiInfo) mAdapter.getItem(position);
                if (!TextUtils.isEmpty(info.uid)) {
                    Intent intent = new Intent(BusinessCheckinActivity.this, CheckinActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(CheckinActivity.KEY_POI_NAME, info.name);
                    bundle.putString(CheckinActivity.KEY_POI_PROVINCE, info.postCode);
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
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mTask = new ListBusinessLocTask();
        mTask.execute();
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

    private class ListBusinessLocTask extends AsyncTask<Void, Void, BaseType> {
        @Override
        protected BaseType doInBackground(Void... params) {
            BaseType result = null;
            CirclePlusApi api = new CirclePlusApi();
            try {
                result = api.getBusinessLocation();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            BusinessCheckinActivity.this.setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected void onPostExecute(final BaseType result) {
            super.onPostExecute(result);
            BusinessCheckinActivity.this.setProgressBarIndeterminateVisibility(false);
            if (result == null) {
                Toast.makeText(BusinessCheckinActivity.this, "Network error",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (result instanceof circleplus.app.types.Status) {
                Toast.makeText(BusinessCheckinActivity.this,
                        ((circleplus.app.types.Status) result).getMessage(),
                        Toast.LENGTH_LONG).show();
            } else if (result instanceof TypeArrayList) {
                new Thread() {
                    @Override
                    public void run() {
                        List<MKPoiInfo> list = new ArrayList<MKPoiInfo>();
                        List<Loc> locList = (TypeArrayList<Loc>) result;
                        for (Loc loc : locList) {
                            MKPoiInfo info = new MKPoiInfo();
                            info.isPano = false;
                            info.address = loc.getAddress();
                            info.city = loc.getCity();
                            info.uid = String.valueOf(loc.getId());
                            info.ePoiType = loc.getType();
                            info.hasCaterDetails = loc.getType() == 3;
                            info.name = loc.getName();
                            info.pt = new GeoPoint((int) loc.getLat(), (int) loc.getLng());
                            info.phoneNum = "";
                            info.postCode = loc.getProvince();
                            list.add(info);
                        }
                        mAdapter.setData(list);
                    }
                }.run(); // start();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            BusinessCheckinActivity.this.setProgressBarIndeterminateVisibility(false);
        }
    }
}
