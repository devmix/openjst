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

package org.openjst.server.commons.maven.manifest.yui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.openjst.server.commons.maven.manifest.YUI;
import org.openjst.server.commons.maven.manifest.serializers.RequiresSerializer;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
@SuppressWarnings("UnusedDeclaration")
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public final class YUIConfigGroupModule {

    private final String name;
    private final String path;
    private final String type;
    private final Set<Dependency> requires = new LinkedHashSet<Dependency>();

    public YUIConfigGroupModule(final String name, final String path, final String type) {
        this.name = name;
        this.path = path;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    @JsonSerialize(contentUsing = RequiresSerializer.class, include = JsonSerialize.Inclusion.NON_NULL)
    public Set<Dependency> getRequires() {
        return requires.isEmpty() ? null : requires;
    }

    @JsonIgnore
    public YUIConfigGroupModule addRequires(final Dependency module) {
        requires.add(module);
        return this;
    }

    @JsonIgnore
    public YUIConfigGroupModule addRequires(final Set<Dependency> modules) {
        requires.addAll(modules);
        return this;
    }

    @JsonIgnore
    public boolean isJavaScript() {
        return YUI.TYPE_JS.equals(type);
    }

    @Override
    public String toString() {
        return "YUIConfigGroupModule{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", type='" + type + '\'' +
                ", requires=" + requires +
                '}';
    }

    public static final class Dependency {
        public final boolean isObject;
        public final String name;

        public Dependency(final String name, final boolean object) {
            this.name = name;
            this.isObject = object;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Dependency that = (Dependency) o;

            return isObject == that.isObject && !(name != null ? !name.equals(that.name) : that.name != null);
        }

        @Override
        public int hashCode() {
            int result = (isObject ? 1 : 0);
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }
    }
}
