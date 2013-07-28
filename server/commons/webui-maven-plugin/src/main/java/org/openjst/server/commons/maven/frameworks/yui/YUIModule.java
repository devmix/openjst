/*
 * Copyright (C) 2013 OpenJST Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openjst.server.commons.maven.frameworks.yui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jetbrains.annotations.Nullable;
import org.openjst.server.commons.maven.frameworks.Module;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
@SuppressWarnings("UnusedDeclaration")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
final class YUIModule implements Module {

    private final String name;
    private final String path;
    private final String type;
    private final Set<YUIDependency> requires = new LinkedHashSet<YUIDependency>();
    private final Set<YUIDependency> immutable = Collections.unmodifiableSet(requires);

    public YUIModule(final String name, final String path, final String type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    @Nullable
    @Override
    @JsonSerialize(contentUsing = RequiresSerializer.class, include = JsonSerialize.Inclusion.NON_NULL)
    @JsonProperty("requires")
    public Set<YUIDependency> getDependencies() {
        return requires.isEmpty() ? null : immutable;
    }

    @JsonIgnore
    public YUIModule addRequires(final YUIDependency module) {
        requires.add(module);
        return this;
    }

    @JsonIgnore
    public YUIModule addRequires(final Set<YUIDependency> modules) {
        requires.addAll(modules);
        return this;
    }

    @JsonIgnore
    public boolean isJavaScript() {
        return YUI.TYPE_JS.equals(type);
    }

    @Override
    public String toString() {
        return "YUIModule{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", type='" + type + '\'' +
                ", requires=" + requires +
                '}';
    }

    static final class RequiresSerializer extends JsonSerializer<YUIDependency> {

        @Override
        public void serialize(final YUIDependency value, final JsonGenerator jgen, final SerializerProvider provider)
                throws IOException {
            if (value.isObject()) {
                jgen.writeRawValue(value.getName());
            } else {
                jgen.writeString(value.getName());
            }
        }
    }
}
