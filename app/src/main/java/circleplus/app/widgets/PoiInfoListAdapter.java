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

package circleplus.app.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.MKPoiInfo;

import java.util.ArrayList;
import java.util.List;

import circleplus.app.R;

/**
 * List adapter displaying POI info, such as title, location,
 * distance. It also provide a button to click for detail POI.
 */
public class PoiInfoListAdapter extends BaseAdapter {

    private List<MKPoiInfo> mList = null;
    private LayoutInflater mInflater = null;

    public PoiInfoListAdapter(Context context) {
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return (mList == null) ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return (mList == null) ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.poi_info_layout, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.poi_type_icon);
            holder.title = (TextView) convertView.findViewById(R.id.poi_title);
            holder.info = (TextView) convertView.findViewById(R.id.poi_info);
            holder.distance = (TextView) convertView.findViewById(R.id.poi_distance);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MKPoiInfo poiInfo = (MKPoiInfo) getItem(position);
        if (poiInfo != null) {
            holder.title.setText(poiInfo.name);
            holder.info.setText(poiInfo.address);
            holder.distance.setText("<500m");
        }
        return convertView;
    }

    public void setData(List<MKPoiInfo> list) {
        mList = list;
        notifyDataSetChanged();
    }

    public void addNewData(List<MKPoiInfo> list) {
        if (mList == null) {
            mList = new ArrayList<MKPoiInfo>(list);
        } else {
            mList.addAll(list);
        }
    }

    private static class ViewHolder {
        ImageView image;
        TextView title;
        TextView info;
        TextView distance;
    }
}
