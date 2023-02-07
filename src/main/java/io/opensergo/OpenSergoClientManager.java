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
import java.util.concurrent.ConcurrentMap;

/**
 * @author Jiangnan Jia
 */
public class OpenSergoClientManager {

    private static volatile OpenSergoClientManager instance;

    /**
     * Cached all the shared OpenSergoClients.
     */
    private final ConcurrentMap<String, OpenSergoClient> sharedClientCache = new ConcurrentHashMap<>();

    private OpenSergoClientManager() {}

    /**
     * Get OpenSergoClientManager by DCL (Double Check Lock).
     *
     * @return the OpenSergoClientManager singleton
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
     * Get the instance from sharedOpenSergoClientCache.
     * If there is no one, the manager will create a new client instance and return it.
     *
     * @param host endpoint of the OpenSergo Control Plane
     * @param port port of the OpenSergo Control Plane
     * @return OpenSergoClient
     */
    public OpenSergoClient getOrCreateClient(String host, int port) {
        return this.getOrCreateClient(host, port, new OpenSergoClientConfig());
    }

    /**
     * Get the instance from sharedOpenSergoClientCache with config.
     * If instance can be found by host and port, the one will be returned, whether the config is matched or not.
     *
     * @param host   endpoint of the OpenSergo Control Plane
     * @param port   port of the OpenSergo Control Plane
     * @param config client config
     * @return OpenSergoClient
     */
    public OpenSergoClient getOrCreateClient(String host, int port, OpenSergoClientConfig config) {
        String sharedOpenSergoClientKey = buildSharedCacheKey(host, port);
        synchronized (this) {
            OpenSergoClient openSergoClient = sharedClientCache.get(sharedOpenSergoClientKey);
            if (openSergoClient != null) {
                return openSergoClient;
            }

            if (config == null) {
                config = new OpenSergoClientConfig();
            }
            openSergoClient = new OpenSergoClient.Builder().endpoint(host, port)
                .openSergoConfig(config).build();
            sharedClientCache.putIfAbsent(sharedOpenSergoClientKey, openSergoClient);
            return sharedClientCache.get(sharedOpenSergoClientKey);
        }
    }

}
