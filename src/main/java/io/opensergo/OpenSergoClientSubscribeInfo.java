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
package io.opensergo;

import com.google.common.collect.Lists;
import io.opensergo.subscribe.OpenSergoConfigSubscriber;
import io.opensergo.subscribe.SubscribeKey;

import java.util.List;

/**
 * @author Jiangnan Jia
 **/
public class OpenSergoClientSubscribeInfo {

    private SubscribeKey subscribeKey;
    private List<OpenSergoConfigSubscriber> subscriberList;

    public OpenSergoClientSubscribeInfo(SubscribeKey subscribeKey) {
        this.subscribeKey = subscribeKey;
        this.subscriberList = Lists.newArrayList();
    }
    public OpenSergoClientSubscribeInfo(SubscribeKey subscribeKey, List<OpenSergoConfigSubscriber> subscriberList) {
        this.subscribeKey = subscribeKey;
        this.subscriberList = subscriberList;
    }

    public OpenSergoClientSubscribeInfo addSubscriber(OpenSergoConfigSubscriber subscriber) {
        // TODO distinct the same OpenSergoConfigSubscriber
        this.subscriberList.add(subscriber);
        return this;
    }

    public SubscribeKey getSubscribeKey() {
        return subscribeKey;
    }

    public List<OpenSergoConfigSubscriber> getSubscriberList() {
        return subscriberList;
    }

    @Override
    public String toString() {
        return "OpensergoClientSubscribeInfo{" +
                "subscribeKey=" + subscribeKey +
                ", subscriberList=" + subscriberList +
                '}';
    }
}
