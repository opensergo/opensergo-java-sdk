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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eric Zhao
 * @author xzd
 */
public class SubscribedConfigCache {

    private final ConcurrentMap<SubscribeKey, SubscribedData> dataMap = new ConcurrentHashMap<>();

    public void updateData(SubscribeKey key, Object data, long version) {
        // TODO: guarantee the latest version
        dataMap.put(key, new SubscribedData(version, data));
    }

    public SubscribedData getDataFor(SubscribeKey key) {
        if (key == null) {
            return null;
        }
        return dataMap.get(key);
    }

    public Optional<Long> getDataVersionFor(SubscribeKey key) {
        SubscribedData d = getDataFor(key);
        return Optional.ofNullable(getDataFor(key)).map(SubscribedData::getVersion);
    }

    public <T> List<T> getDataListFor(SubscribeKey key, Class<T> clazz) {
        SubscribedData d = getDataFor(key);
        if (d == null || d.getData() == null) {
            return null;
        }
        return (List<T>) d.getData();
    }
}
