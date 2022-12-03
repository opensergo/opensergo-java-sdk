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

import io.envoyproxy.envoy.config.route.v3.RouteConfiguration;
import io.opensergo.proto.fault_tolerance.v1.CircuitBreakerStrategy;
import io.opensergo.proto.fault_tolerance.v1.ConcurrencyLimitStrategy;
import io.opensergo.proto.fault_tolerance.v1.FaultToleranceRule;
import io.opensergo.proto.fault_tolerance.v1.RateLimitStrategy;
import io.opensergo.proto.fault_tolerance.v1.ThrottlingStrategy;
import io.opensergo.util.StringUtils;

/**
 * Metadata registry for OpenSergo config kind.
 *
 * @author Eric Zhao
 */
public final class OpenSergoConfigKindRegistry {

    private static final ConcurrentMap<String, ConfigKindMetadata> KIND_MAP;

    static {
        KIND_MAP = new ConcurrentHashMap<>();

        registerConfigKind(ConfigKind.FAULT_TOLERANCE_RULE, FaultToleranceRule.class);
        registerConfigKind(ConfigKind.RATE_LIMIT_STRATEGY, RateLimitStrategy.class);
        registerConfigKind(ConfigKind.THROTTLING_STRATEGY, ThrottlingStrategy.class);
        registerConfigKind(ConfigKind.CONCURRENCY_LIMIT_STRATEGY, ConcurrencyLimitStrategy.class);
        registerConfigKind(ConfigKind.CIRCUIT_BREAKER_STRATEGY, CircuitBreakerStrategy.class);
        registerConfigKind(ConfigKind.VIRTUAL_SERVICE_STRATEGY, RouteConfiguration.class);
    }

    public static ConfigKindMetadata getKindMetadata(ConfigKind kind) {
        if (kind == null) {
            return null;
        }
        return KIND_MAP.get(kind.getKindName());
    }

    public static ConfigKindMetadata getKindMetadata(String kindName) {
        if (StringUtils.isEmpty(kindName)) {
            return null;
        }
        return KIND_MAP.get(kindName);
    }

    private static void registerConfigKind(ConfigKind kind, Class<?> clazz) {
        KIND_MAP.put(kind.getKindName(), new ConfigKindMetadata(kind, clazz));
    }

    private OpenSergoConfigKindRegistry() {}
}
