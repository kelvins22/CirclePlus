package circleplus.app;

import android.app.Activity;
import android.text.TextUtils;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKSearch;

public class SearchPoiOverlay extends PoiOverlay {

    private MKSearch mSearch = null;

    public SearchPoiOverlay(Activity activity, MapView mapView, MKSearch search) {
        super(activity, mapView);
        mSearch = search;
    }

    @Override
    protected boolean onTap(int i) {
        super.onTap(i);
        MKPoiInfo info = getPoi(i);
        if (info.hasCaterDetails && !TextUtils.isEmpty(info.uid)) {
            mSearch.poiDetailSearch(info.uid);
        } else if (!TextUtils.isEmpty(info.uid)) {
            // TODO: do check-in
        }
        return true;
    }
}
