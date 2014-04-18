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

package circleplus.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import circleplus.app.types.User;

public class UserUtils {

    public static final String KEY_CHECKIN_COUNT = "user_utils_key_checkin_count";
    public static final String KEY_USER_CREATED = "key_utils_key_user_created";
    public static final String KEY_USER_EMAIL = "key_utils_key_user_email";
    public static final String KEY_FOLLOWER_COUNT = "user_utils_key_follower_count";
    public static final String KEY_FRIEND_COUNT = "user_utils_key_friend_count";
    public static final String KEY_USER_GENDER = "user_utils_key_user_gender";
    public static final String KEY_USER_ID = "user_utils_key_user_id";
    public static final String KEY_USER_NAME = "user_utils_key_user_name";
    public static final String KEY_USER_PHONE = "user_utils_key_user_phone";
    public static final String KEY_USER_PHOTO = "user_utils_key_user_photo";
    public static final String KEY_USER_TOKEN = "user_utils_key_user_token";

    public static void storeUserInfo(Context context, User user) {
        if (user == null) {
            return;
        }
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();

        // write user info
        editor.putInt(KEY_CHECKIN_COUNT, user.getCheckinCount());
        editor.putString(KEY_USER_CREATED, user.getCreated());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putInt(KEY_FOLLOWER_COUNT, user.getFollowerCount());
        editor.putInt(KEY_FRIEND_COUNT, user.getFriendCount());
        editor.putString(KEY_USER_GENDER, user.getGender());
        editor.putLong(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_PHONE, user.getPhone());
        editor.putString(KEY_USER_PHOTO, user.getPhoto());
        editor.commit();
    }

    public static void storeUserToken(Context context, String token) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_TOKEN, token);
        editor.commit();
    }

    public static void cleanUserInfo(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_CHECKIN_COUNT);
        editor.remove(KEY_USER_CREATED);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_FOLLOWER_COUNT);
        editor.remove(KEY_FRIEND_COUNT);
        editor.remove(KEY_USER_GENDER);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_PHONE);
        editor.remove(KEY_USER_PHOTO);
        editor.remove(KEY_USER_TOKEN);
        editor.commit();
    }

    /**
     * Get currently login user's id
     *
     * @return -1 if there is no login user, else the login user's id
     */
    public static long getUserId(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        if (prefs.contains(KEY_USER_ID)) {
            return prefs.getLong(KEY_USER_ID, -1L);
        } else {
            return -1L;
        }
    }

    /**
     * Get the stored user info, return default values if not exists.
     *
     * @param context context to get SharedPreferences
     * @return stored user info or default values
     */
    public static User getUserInfo(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        User user = new User();
        user.setCheckinCount(prefs.getInt(KEY_CHECKIN_COUNT, 0));
        user.setCreated(prefs.getString(KEY_USER_CREATED, ""));
        user.setEmail(prefs.getString(KEY_USER_EMAIL, ""));
        user.setFollowerCount(prefs.getInt(KEY_FOLLOWER_COUNT, 0));
        user.setFriendCount(prefs.getInt(KEY_FRIEND_COUNT, 0));
        user.setGender(prefs.getString(KEY_USER_GENDER, "Male"));
        user.setId(prefs.getLong(KEY_USER_ID, -1L));
        user.setName(prefs.getString(KEY_USER_NAME, ""));
        user.setPhone(prefs.getString(KEY_USER_PHONE, ""));
        user.setPhoto(prefs.getString(KEY_USER_PHOTO, ""));
        return user;
    }

    public static String getUserToken(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        return prefs.getString(KEY_USER_TOKEN, "");
    }
}
