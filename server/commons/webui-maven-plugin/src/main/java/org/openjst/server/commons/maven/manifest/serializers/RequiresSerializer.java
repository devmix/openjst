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

package org.openjst.server.commons.maven.manifest.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.openjst.server.commons.maven.manifest.yui.YUIConfigGroupModule;

import java.io.IOException;

/**
 * @author Sergey Grachev
 */
public class RequiresSerializer extends JsonSerializer<YUIConfigGroupModule.Dependency> {

    @Override
    public void serialize(final YUIConfigGroupModule.Dependency value, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException {
        if (value.isObject) {
            jgen.writeRawValue(value.name);
        } else {
            jgen.writeString(value.name);
        }
    }
}
