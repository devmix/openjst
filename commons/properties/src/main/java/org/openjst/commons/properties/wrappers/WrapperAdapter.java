/*
 * Copyright (C) 2013-2014 OpenJST Project
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

package org.openjst.commons.properties.wrappers;

import org.openjst.commons.properties.Caches;
import org.openjst.commons.properties.Property;
import org.openjst.commons.properties.storages.annotations.Levels;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * @author Sergey Grachev
 */
public abstract class WrapperAdapter implements Property.Wrapper {

    @Override
    public String key() {
        return getClass().toString();
    }

    @Override
    public Type type() {
        return Type.STRING;
    }

    @Nullable
    @Override
    public String nullAs() {
        return null;
    }

    @Nullable
    @Override
    public Levels levels() {
        return Caches.class.getAnnotation(Levels.class);
    }

    @Nullable
    @Override
    public Annotation[] restrictions() {
        return null;
    }
}
