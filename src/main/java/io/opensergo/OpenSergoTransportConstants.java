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
public final class OpenSergoTransportConstants {

    public static final int CODE_SUCCESS = 1;

    public static final int CODE_ERROR_UNKNOWN = 4000;
    public static final int CODE_ERROR_SUBSCRIBE_HANDLER_ERROR = 4007;
    public static final int CODE_ERROR_VERSION_OUTDATED = 4010;

    public static final String ACK_FLAG = "ACK";
    public static final String NACK_FLAG = "NACK";

    private OpenSergoTransportConstants() {}
}
