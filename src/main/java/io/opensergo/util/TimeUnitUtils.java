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
package io.opensergo.util;

import io.opensergo.proto.common.v1.TimeUnit;

/**
 * @author Eric Zhao
 */
public final class TimeUnitUtils {

    public static long convertToMillis(long t, TimeUnit fromUnit) {
        AssertUtils.assertNotNull(fromUnit, "fromUnit cannot be null");
        switch (fromUnit) {
            case DAY:
                return java.util.concurrent.TimeUnit.DAYS.toMillis(t);
            case HOUR:
                return java.util.concurrent.TimeUnit.HOURS.toMillis(t);
            case MINUTE:
                return java.util.concurrent.TimeUnit.MINUTES.toMillis(t);
            case SECOND:
                return java.util.concurrent.TimeUnit.SECONDS.toMillis(t);
            case MILLISECOND:
                return java.util.concurrent.TimeUnit.MILLISECONDS.toMillis(t);
            case UNKNOWN:
            default:
                throw new IllegalArgumentException("unknown time unit: " + fromUnit);
        }
    }

    private TimeUnitUtils() {}
}
