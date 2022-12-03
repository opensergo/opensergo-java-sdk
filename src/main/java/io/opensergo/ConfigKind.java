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

/**
 * @author Eric Zhao
 */
public enum ConfigKind {

    /**
     * FaultToleranceRule
     */
    FAULT_TOLERANCE_RULE("fault-tolerance.opensergo.io/v1alpha1/FaultToleranceRule", "FaultToleranceRule"),
    RATE_LIMIT_STRATEGY("fault-tolerance.opensergo.io/v1alpha1/RateLimitStrategy", "RateLimitStrategy"),
    THROTTLING_STRATEGY("fault-tolerance.opensergo.io/v1alpha1/ThrottlingStrategy", "ThrottlingStrategy"),
    CONCURRENCY_LIMIT_STRATEGY("fault-tolerance.opensergo.io/v1alpha1/ConcurrencyLimitStrategy",
            "ConcurrencyLimitStrategy"),
    CIRCUIT_BREAKER_STRATEGY("fault-tolerance.opensergo.io/v1alpha1/CircuitBreakerStrategy", "CircuitBreakerStrategy"),

    VIRTUAL_SERVICE_STRATEGY("networking.istio.io/v1beta1/VirtualService", "VirtualService");

    private final String kindName;
    private final String simpleKindName;

    ConfigKind(String kindName, String simpleKindName) {
        this.kindName = kindName;
        this.simpleKindName = simpleKindName;
    }

    public String getKindName() {
        return kindName;
    }

    public String getSimpleKindName() {
        return simpleKindName;
    }
}
