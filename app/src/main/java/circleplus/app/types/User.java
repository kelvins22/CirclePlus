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

package circleplus.app.types;

import android.os.Parcel;
import android.os.Parcelable;

import circleplus.app.utils.ParcelUtils;

public class User implements BaseType, Parcelable {

    private int mCheckinCount;
    private Checkin mCheckin;
    private String mCreated;
    private String mEmail;
    private String mFirstname;
    private int mFollowerCount;
    private int mFriendCount;
    private String mGender;
    private String mHometown;
    private String mId;
    private String mLastname;
    private String mPhone;
    private String mPhoto;
//    private Settings mSettings;

    public User() {
    }

    private User(Parcel in) {
        mCheckinCount = in.readInt();
        mCreated = ParcelUtils.readStringFromParcel(in);
        mEmail = ParcelUtils.readStringFromParcel(in);
        mFirstname = ParcelUtils.readStringFromParcel(in);
        mFollowerCount = in.readInt();
        mFriendCount = in.readInt();
        mGender = ParcelUtils.readStringFromParcel(in);
        mHometown = ParcelUtils.readStringFromParcel(in);
        mId = ParcelUtils.readStringFromParcel(in);
        mLastname = ParcelUtils.readStringFromParcel(in);
        mPhone = ParcelUtils.readStringFromParcel(in);
        mPhoto = ParcelUtils.readStringFromParcel(in);

        if (in.readInt() == 1) {
            mCheckin = in.readParcelable(Checkin.class.getClassLoader());
        }

//        if (in.readInt() == 1) {
//            mSettings = in.readParcelable(Settings.class.getClassLoader());
//        }
    }

    public static final Parcelable.Creator<User> CREATOR =
            new Parcelable.Creator<User>() {

                @Override
                public User createFromParcel(Parcel in) {
                    return new User(in);
                }

                @Override
                public User[] newArray(int size) {
                    return new User[size];
                }
            };

    public Checkin getCheckin() {
        return mCheckin;
    }

    public void setCheckin(Checkin checkin) {
        mCheckin = checkin;
    }

    public int getCheckinCount() {
        return mCheckinCount;
    }

    public void setCheckinCount(int checkinCount) {
        mCheckinCount = checkinCount;
    }

    public String getCreated() {
        return mCreated;
    }

    public void setCreated(String created) {
        mCreated = created;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getFirstname() {
        return mFirstname;
    }

    public void setFirstname(String firstname) {
        mFirstname = firstname;
    }

    public int getFollowerCount() {
        return mFollowerCount;
    }

    public void setFollowerCount(int followerCount) {
        mFollowerCount = followerCount;
    }

    public int getFriendCount() {
        return mFriendCount;
    }

    public void setFriendCount(int friendCount) {
        mFriendCount = friendCount;
    }

    public String getGender() {
        return mGender;
    }

    public void setGender(String gender) {
        mGender = gender;
    }

    public String getHometown() {
        return mHometown;
    }

    public void setHometown(String hometown) {
        mHometown = hometown;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getLastname() {
        return mLastname;
    }

    public void setLastname(String lastname) {
        mLastname = lastname;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getPhoto() {
        return mPhoto;
    }

    public void setPhoto(String photo) {
        mPhoto = photo;
    }

//    public Settings getSettings() {
//        return mSettings;
//    }

//    public void setSettings(Settings settings) {
//        mSettings = settings;
//    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mCheckinCount);
        ParcelUtils.writeStringToParcel(out, mCreated);
        ParcelUtils.writeStringToParcel(out, mEmail);
        ParcelUtils.writeStringToParcel(out, mFirstname);
        out.writeInt(mFollowerCount);
        out.writeInt(mFriendCount);
        ParcelUtils.writeStringToParcel(out, mGender);
        ParcelUtils.writeStringToParcel(out, mHometown);
        ParcelUtils.writeStringToParcel(out, mId);
        ParcelUtils.writeStringToParcel(out, mLastname);
        ParcelUtils.writeStringToParcel(out, mPhone);
        ParcelUtils.writeStringToParcel(out, mPhoto);

        if (mCheckin != null) {
            out.writeInt(1);
            out.writeParcelable(mCheckin, flags);
        } else {
            out.writeInt(0);
        }

//        if (mSettings != null) {
//            out.writeInt(1);
//            out.writeParcelable(mSettings, flags);
//        } else {
//            out.writeInt(0);
//        }
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
