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

public class Status implements BaseType, Parcelable {

    private int mCode;
    private String mMessage;

    public Status() {
    }

    private Status(Parcel in) {
        mCode = in.readInt();
        mMessage = ParcelUtils.readStringFromParcel(in);
    }

    public static final Parcelable.Creator<Status> CREATOR =
            new Parcelable.Creator<Status>() {

                @Override
                public Status createFromParcel(Parcel in) {
                    return new Status(in);
                }

                @Override
                public Status[] newArray(int size) {
                    return new Status[size];
                }
            };

    public int getStatusCode() {
        return mCode;
    }

    public void setStatusCode(int statusCode) {
        mCode = statusCode;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mCode);
        ParcelUtils.writeStringToParcel(out, mMessage);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
