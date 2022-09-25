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

import io.opensergo.ConfigKind;

/**
 * @author Eric Zhao
 */
public class SubscribeKey {

    private final String namespace;
    private final String app;
    private final ConfigKind kind;

    public SubscribeKey(String namespace, String app, ConfigKind kind) {
        this.namespace = namespace;
        this.app = app;
        this.kind = kind;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getApp() {
        return app;
    }

    public ConfigKind getKind() {
        return kind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}

        SubscribeKey that = (SubscribeKey) o;

        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) {return false;}
        if (app != null ? !app.equals(that.app) : that.app != null) {return false;}
        return kind == that.kind;
    }

    @Override
    public int hashCode() {
        int result = namespace != null ? namespace.hashCode() : 0;
        result = 31 * result + (app != null ? app.hashCode() : 0);
        result = 31 * result + (kind != null ? kind.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SubscribeKey{" +
            "namespace='" + namespace + '\'' +
            ", app='" + app + '\'' +
            ", kind=" + kind +
            '}';
    }
}
