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

import java.util.ArrayList;
import java.util.Collection;

public class TypeArrayList<T extends BaseType> extends ArrayList<T>
        implements BaseType {

    private static final long serialVersionUID = 1L;

    private String mType = null;

    public TypeArrayList() {
        super();
    }

    public TypeArrayList(Collection<T> collection) {
        super(collection);
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }
}
