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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import circleplus.app.types.User;
import circleplus.app.utils.UserUtils;

public class UserInfoFragment extends Fragment {

    private static final int LOGIN_REQ_CODE = 0x1;

    private TextView mUsernameText, mCreatedText, mGenderText;
    private TextView mEmailText, mPhoneText;
    private TextView mCheckinText, mFollowerText, mFriendText;
    private RelativeLayout mInfoFrame;
    private FrameLayout mEmptyFrame;

    private User mUser = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // in order to add logout menu item
        setHasOptionsMenu(true);

        if (UserUtils.getUserId(getActivity()) == -1) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQ_CODE);
        } else {
            mUser = UserUtils.getUserInfo(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_info_layout, container, false);
        mEmptyFrame = (FrameLayout) view.findViewById(R.id.empty_frame);
        mInfoFrame = (RelativeLayout) view.findViewById(R.id.info_frame);
        mUsernameText = (TextView) mInfoFrame.findViewById(R.id.info_username);
        mCreatedText = (TextView) mInfoFrame.findViewById(R.id.info_created);
        mGenderText = (TextView) mInfoFrame.findViewById(R.id.info_gender);
        mEmailText = (TextView) mInfoFrame.findViewById(R.id.info_email);
        mPhoneText = (TextView) mInfoFrame.findViewById(R.id.info_phone);
        mCheckinText = (TextView) mInfoFrame.findViewById(R.id.info_checkin_count);
        mFollowerText = (TextView) mInfoFrame.findViewById(R.id.info_follower_count);
        mFriendText = (TextView) mInfoFrame.findViewById(R.id.info_friend_count);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        refreshViewInfo();
        // TODO: add checkin, follower, friend Activity jump
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED || getActivity() == null) {
            mEmptyFrame.setVisibility(View.VISIBLE);
            mInfoFrame.setVisibility(View.GONE);
            return;
        }
        if (requestCode == LOGIN_REQ_CODE) {
            mUser = UserUtils.getUserInfo(getActivity());
            refreshViewInfo();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.user_info_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                UserUtils.cleanUserInfo(getActivity());
                mUser = null;
                refreshViewInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshViewInfo() {
        if (mUser == null) {
            mEmptyFrame.setVisibility(View.VISIBLE);
            mInfoFrame.setVisibility(View.GONE);
            return;
        }
        mEmptyFrame.setVisibility(View.GONE);
        mEmptyFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQ_CODE);
            }
        });

        mInfoFrame.setVisibility(View.VISIBLE);
        mUsernameText.setText(getString(R.string.username)
                + " " + mUser.getName());
        mCreatedText.setText(getString(R.string.join)
                + " " + mUser.getCreated());
        mGenderText.setText(getString(R.string.gender)
                + " " + mUser.getGender());
        mEmailText.setText(getString(R.string.email)
                + " " + mUser.getEmail());
        mPhoneText.setText(getString(R.string.phone)
                + " " + mUser.getPhone());
        mCheckinText.setText(getString(R.string.checkin_count_title)
                + "\n     " + mUser.getCheckinCount());
        mFollowerText.setText(getString(R.string.follower_count_title)
                + "\n     " + mUser.getFollowerCount());
        mFriendText.setText(getString(R.string.friend_count_title)
                + "\n     " + mUser.getFriendCount());
    }
}
