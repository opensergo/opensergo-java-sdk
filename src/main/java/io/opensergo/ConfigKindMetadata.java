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

import io.opensergo.util.AssertUtils;

/**
 * @author Eric Zhao
 */
public class ConfigKindMetadata {

    private final String kindName;
    private final ConfigKind kind;
    private final Class<?> kindClass;

    public ConfigKindMetadata(ConfigKind kind, Class<?> kindClass) {
        AssertUtils.assertNotNull(kind, "kind cannot be null");
        this.kindName = kind.getKindName();
        this.kind = kind;
        this.kindClass = kindClass;
    }

    public String getKindName() {
        return kindName;
    }

    public ConfigKind getKind() {
        return kind;
    }

    public Class<?> getKindClass() {
        return kindClass;
    }

    @Override
    public String toString() {
        return "ConfigKindMetadata{" +
            "kindName='" + kindName + '\'' +
            ", kind=" + kind +
            ", kindClass=" + kindClass +
            '}';
    }
}
