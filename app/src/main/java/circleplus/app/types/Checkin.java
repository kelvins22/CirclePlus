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

public class Checkin implements BaseType, Parcelable {

    private String mCreated;
    private String mDistance;
    private String mId;
    private String mName;
    private String mPlace;
    private String mShout;
    private User mUser;

    public Checkin() {
    }

    private Checkin(Parcel in) {
        mCreated = ParcelUtils.readStringFromParcel(in);
        mDistance = ParcelUtils.readStringFromParcel(in);
        mId = ParcelUtils.readStringFromParcel(in);
        mName = ParcelUtils.readStringFromParcel(in);
        mPlace = ParcelUtils.readStringFromParcel(in);
        mShout = ParcelUtils.readStringFromParcel(in);

        if (in.readInt() == 1) {
            mUser = in.readParcelable(User.class.getClassLoader());
        }
    }

    public static final Parcelable.Creator<Checkin> CREATOR =
            new Parcelable.Creator<Checkin>() {

                @Override
                public Checkin createFromParcel(Parcel in) {
                    return new Checkin(in);
                }

                @Override
                public Checkin[] newArray(int size) {
                    return new Checkin[size];
                }
            };

    public String getCreated() {
        return mCreated;
    }

    public void setCreated(String created) {
        mCreated = created;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPlace() {
        return mPlace;
    }

    public void setPlace(String place) {
        mPlace = place;
    }

    public String getShout() {
        return mShout;
    }

    public void setShout(String shout) {
        mShout = shout;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        mUser = user;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        ParcelUtils.writeStringToParcel(out, mCreated);
        ParcelUtils.writeStringToParcel(out, mDistance);
        ParcelUtils.writeStringToParcel(out, mId);
        ParcelUtils.writeStringToParcel(out, mName);
        ParcelUtils.writeStringToParcel(out, mPlace);
        ParcelUtils.writeStringToParcel(out, mShout);

        if (mUser != null) {
            out.writeInt(1);
            out.writeParcelable(mUser, flags);
        } else {
            out.writeInt(0);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
