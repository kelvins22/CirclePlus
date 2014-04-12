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

public class Loc implements BaseType, Parcelable {

    private String mAddress;
    private String mCity;
    private long mId;
    private long mLat;
    private long mLng;
    private String mName;
    private String mNation;
    private String mProvince;
    private int mType;

    public Loc() {
    }

    private Loc(Parcel in) {
        mAddress = ParcelUtils.readStringFromParcel(in);
        mCity = ParcelUtils.readStringFromParcel(in);
        mId = in.readLong();
        mLat = in.readLong();
        mLng = in.readLong();
        mName = ParcelUtils.readStringFromParcel(in);
        mNation = ParcelUtils.readStringFromParcel(in);
        mProvince = ParcelUtils.readStringFromParcel(in);
        mType = in.readInt();
    }

    public static final Parcelable.Creator<Loc> CREATOR =
            new Parcelable.Creator<Loc>() {
                @Override
                public Loc createFromParcel(Parcel in) {
                    return new Loc(in);
                }

                @Override
                public Loc[] newArray(int size) {
                    return new Loc[size];
                }
            };

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getLat() {
        return mLat;
    }

    public void setLat(long lat) {
        mLat = lat;
    }

    public long getLng() {
        return mLng;
    }

    public void setLng(long lng) {
        mLng = lng;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getNation() {
        return mNation;
    }

    public void setNation(String nation) {
        mNation = nation;
    }

    public String getProvince() {
        return mProvince;
    }

    public void setProvince(String province) {
        mProvince = province;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        ParcelUtils.writeStringToParcel(out, mAddress);
        ParcelUtils.writeStringToParcel(out, mCity);
        out.writeLong(mId);
        out.writeLong(mLat);
        out.writeLong(mLng);
        ParcelUtils.writeStringToParcel(out, mName);
        ParcelUtils.writeStringToParcel(out, mNation);
        ParcelUtils.writeStringToParcel(out, mProvince);
        out.writeInt(mType);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
