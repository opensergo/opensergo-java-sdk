/*
 * Copyright 2022, OpenSergo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.opensergo.subscribe;

/**
 * @author Eric Zhao
 */
public class SubscribedData {

    private long version;
    private Object data;

    public SubscribedData() {}

    public SubscribedData(long version, Object data) {
        this.version = version;
        this.data = data;
    }

    public long getVersion() {
        return version;
    }

    public SubscribedData setVersion(long version) {
        this.version = version;
        return this;
    }

    public Object getData() {
        return data;
    }

    public SubscribedData setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "SubscribedData{" +
            "version='" + version + '\'' +
            ", data=" + data +
            '}';
    }
}
