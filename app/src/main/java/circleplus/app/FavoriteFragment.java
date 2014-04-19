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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

import circleplus.app.http.CirclePlusApi;
import circleplus.app.types.BaseType;
import circleplus.app.types.Checkin;
import circleplus.app.types.TypeArrayList;
import circleplus.app.utils.UserUtils;
import circleplus.app.widgets.CheckinListAdapter;

/**
 * Display favorite places
 */
public class FavoriteFragment extends Fragment {

    private ListView mListView = null;
    private CheckinListAdapter mAdapter = null;
    private ListFavoriteTask mTask = null;
    private String mToken = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.favorite_frag, container, false);
        mListView = (ListView) v.findViewById(R.id.fav_place_list);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new CheckinListAdapter(getActivity());
        mListView.setAdapter(mAdapter);

        long id = UserUtils.getUserId(getActivity());
        mToken = UserUtils.getUserToken(getActivity());
        mTask = new ListFavoriteTask();
        mTask.execute(id);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mTask != null) {
            mTask.cancel(true);
        }
    }

    private class ListFavoriteTask extends AsyncTask<Long, Void, BaseType> {
        @Override
        protected BaseType doInBackground(Long... params) {
            long id = params[0];
            BaseType result = null;
            CirclePlusApi api = new CirclePlusApi();
            try {
                result = api.getFavorites(id, mToken);
                mToken = null;
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
            if (result == null) {
                Toast.makeText(getActivity(), "Network error",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (result instanceof circleplus.app.types.Status) {
                Toast.makeText(getActivity(),
                        ((circleplus.app.types.Status) result).getMessage(),
                        Toast.LENGTH_LONG).show();
            } else if (result instanceof TypeArrayList) {
                mAdapter.setList((TypeArrayList<Checkin>) result);
            }
        }
    } // end ListFavoriteTask
}
