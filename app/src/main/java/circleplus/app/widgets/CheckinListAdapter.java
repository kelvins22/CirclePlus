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
import android.widget.ImageView;
import android.widget.TextView;

import circleplus.app.R;
import circleplus.app.types.Checkin;

public class CheckinListAdapter extends BaseListAdapter {

    private LayoutInflater mInflater;

    public CheckinListAdapter(Context context) {
        super(context);

        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.fav_item_layout, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.fav_place_icon);
            holder.time = (TextView) convertView.findViewById(R.id.fav_place_time);
            holder.name = (TextView) convertView.findViewById(R.id.fav_place_name);
            holder.detail = (TextView) convertView.findViewById(R.id.fav_place_detail);
            holder.text = (TextView) convertView.findViewById(R.id.fav_place_text);
            holder.score = (TextView) convertView.findViewById(R.id.fav_place_score);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Checkin checkin = (Checkin) getItem(position);
        holder.time.setText(checkin.getCreated());
        holder.name.setText(checkin.getName());
        holder.detail.setText(checkin.getLoc().getAddress());
        holder.text.setText(checkin.getShout());
        holder.score.setText("Score: " + checkin.getScore());

        return convertView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView time;
        TextView name;
        TextView detail;
        TextView text;
        TextView score;
    }
}
