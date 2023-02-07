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

import java.util.concurrent.ConcurrentHashMap;

/**
 * .
 *
 * @author Jiangnan Jia
 **/
public class OpenSergoClientManager {

    private static volatile OpenSergoClientManager instance;

    /**
     * Cached all the shared OpenSergoClients.
     */
    private ConcurrentHashMap<String, OpenSergoClient> sharedOpenSergoClientCache = new ConcurrentHashMap<>();


    private OpenSergoClientManager() {

    }

    /**
     * get OpenSergoClientManager by DCL (Double Check Lock)
     * @return
     */
    public static OpenSergoClientManager get() {
        if (instance == null) {
            synchronized (OpenSergoClientManager.class) {
                if (instance == null) {
                    return new OpenSergoClientManager();
                }
            }
        }
        return instance;
    }

    private String buildSharedCacheKey(String host, int port) {
        return host + ":" + port;
    }

    /**
     * get the instance from sharedOpenSergoClientCache，
     * if there is no one， will create a new instance and return it.
     *
     * @param host endpoint of the OpenSergo Control Plane
     * @param port port of the OpenSergo Control Plane
     * @return OpenSergoClient
     */
    public OpenSergoClient getOrCreateClient(String host, int port) {
        return this.getOrCreateClient(host, port, new OpenSergoConfig());
    }

    /**
     * get the instance from sharedOpenSergoClientCache with config.
     * If instance can be found by host and port, the one will be returned, whether the config is matched or not.
     *
     * @param host endpoint of the OpenSergo Control Plane
     * @param port port of the OpenSergo Control Plane
     * @param config OpenSergoConfig
     * @return OpenSergoClient
     */
    public OpenSergoClient getOrCreateClient(String host, int port, OpenSergoConfig config) {
        String sharedOpenSergoClientKey = buildSharedCacheKey(host, port);
        OpenSergoClient openSergoClient = sharedOpenSergoClientCache.get(sharedOpenSergoClientKey);

        if (openSergoClient != null) {
            return openSergoClient;
        }

        config = config == null ? new OpenSergoConfig() : config;
        openSergoClient = new OpenSergoClient.Builder().endpoint(host, port).openSergoConfig(config).build();
        sharedOpenSergoClientCache.putIfAbsent(sharedOpenSergoClientKey, openSergoClient);
        return sharedOpenSergoClientCache.get(sharedOpenSergoClientKey);
    }

}
