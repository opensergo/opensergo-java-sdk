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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import io.opensergo.util.AssertUtils;

/**
 * @author Eric Zhao
 */
public class SubscribeRegistry {

    private ConcurrentMap<SubscribeKey, List<OpenSergoConfigSubscriber>> subscriberMap = new ConcurrentHashMap<>();

    public void registerSubscriber(SubscribeKey key, OpenSergoConfigSubscriber subscriber) {
        AssertUtils.assertNotNull(key, "subscribeKey cannot be null");
        AssertUtils.assertNotNull(subscriber, "subscriber cannot be null");
        List<OpenSergoConfigSubscriber> list = subscriberMap.computeIfAbsent(key, v -> new CopyOnWriteArrayList<>());
        list.add(subscriber);
    }

    public List<OpenSergoConfigSubscriber> getSubscribersOf(SubscribeKey key) {
        if (key == null) {
            return null;
        }
        return subscriberMap.get(key);
    }

    public boolean removeAllSubscribers(SubscribeKey key) {
        return subscriberMap.remove(key) != null;
    }
}
