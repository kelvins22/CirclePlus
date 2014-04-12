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
    private String mCreated;
    private String mEmail;
    private int mFollowerCount;
    private int mFriendCount;
    private String mGender;
    private long mId;
    private String mName;
    private String mPhone;
    private String mPhoto;

    public User() {
    }

    private User(Parcel in) {
        mCheckinCount = in.readInt();
        mCreated = ParcelUtils.readStringFromParcel(in);
        mEmail = ParcelUtils.readStringFromParcel(in);
        mFollowerCount = in.readInt();
        mFriendCount = in.readInt();
        mGender = ParcelUtils.readStringFromParcel(in);
        mId = in.readLong();
        mName = ParcelUtils.readStringFromParcel(in);
        mPhone = ParcelUtils.readStringFromParcel(in);
        mPhoto = ParcelUtils.readStringFromParcel(in);
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

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
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

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mCheckinCount);
        ParcelUtils.writeStringToParcel(out, mCreated);
        ParcelUtils.writeStringToParcel(out, mEmail);
        out.writeInt(mFollowerCount);
        out.writeInt(mFriendCount);
        ParcelUtils.writeStringToParcel(out, mGender);
        out.writeLong(mId);
        ParcelUtils.writeStringToParcel(out, mName);
        ParcelUtils.writeStringToParcel(out, mPhone);
        ParcelUtils.writeStringToParcel(out, mPhoto);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
