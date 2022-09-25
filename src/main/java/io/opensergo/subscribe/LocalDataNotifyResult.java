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

import io.opensergo.OpenSergoTransportConstants;

/**
 * @author Eric Zhao
 */
public class LocalDataNotifyResult {
    private Integer code;
    private List<Object> decodedData;
    private List<Throwable> notifyErrors;

    public static LocalDataNotifyResult withSuccess(List<Object> decodedData) {
        return new LocalDataNotifyResult().setCode(OpenSergoTransportConstants.CODE_SUCCESS)
            .setDecodedData(decodedData);
    }

    public Integer getCode() {
        return code;
    }

    public LocalDataNotifyResult setCode(Integer code) {
        this.code = code;
        return this;
    }

    public List<Object> getDecodedData() {
        return decodedData;
    }

    public LocalDataNotifyResult setDecodedData(List<Object> decodedData) {
        this.decodedData = decodedData;
        return this;
    }

    public List<Throwable> getNotifyErrors() {
        return notifyErrors;
    }

    public LocalDataNotifyResult setNotifyErrors(List<Throwable> notifyErrors) {
        this.notifyErrors = notifyErrors;
        return this;
    }

    @Override
    public String toString() {
        return "LocalDataNotifyResult{" +
            "code=" + code +
            ", decodedData=" + decodedData +
            ", notifyErrors=" + notifyErrors +
            '}';
    }
}
