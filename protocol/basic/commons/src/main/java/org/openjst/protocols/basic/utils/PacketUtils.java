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

package org.openjst.protocols.basic.utils;

import org.openjst.commons.io.buffer.ArrayDataInputBuffer;
import org.openjst.commons.io.buffer.ArrayDataOutputBuffer;
import org.openjst.commons.io.buffer.DataBufferException;
import org.openjst.protocols.basic.pdu.beans.Parameter;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public final class PacketUtils {
    private PacketUtils() {
    }

    public static void writeParameters(final ArrayDataOutputBuffer out, @Nullable final Set<Parameter> parameters) throws DataBufferException {
        if (parameters == null || parameters.isEmpty()) {
            out.writeVLQInt32(0);
            return;
        }

        out.writeVLQInt32(parameters.size());

        for (final Parameter parameter : parameters) {
            final Object key = parameter.getKey();
            final Object value = parameter.getValue();
            if (key != null || value != null) {
                writeObject(out, key);
                writeObject(out, value);
            }
        }
    }

    @Nullable
    public static Set<Parameter> readParameters(final ArrayDataInputBuffer in) throws DataBufferException {
        int count = in.readVLQInt32();
        if (count <= 0) {
            return null;
        }

        final Set<Parameter> parameters = new HashSet<Parameter>(count);
        while (count-- > 0) {
            final Object key = readObject(in);
            final Object value = readObject(in);
            parameters.add(Parameter.newParameter(key, value));
        }

        return parameters;
    }

    private static void writeObject(final ArrayDataOutputBuffer out, final Object value) throws DataBufferException {
        final Parameter.Type type = Parameter.Type.valueOfObject(value);
        switch (type) {
            case INT8:
                out.writeInt8((byte) type.getTag());
                out.writeVLQInt32((Byte) value);
                break;
            case INT16:
                out.writeInt8((byte) type.getTag());
                out.writeVLQInt32((Short) value);
                break;
            case INT32:
                out.writeInt8((byte) type.getTag());
                out.writeVLQInt32((Integer) value);
                break;
            case INT64:
                out.writeInt8((byte) type.getTag());
                out.writeVLQInt64((Long) value);
                break;
            case FLOAT32:
                out.writeInt8((byte) type.getTag());
                out.writeVLQFloat32((Float) value);
                break;
            case FLOAT64:
                out.writeInt8((byte) type.getTag());
                out.writeVLQFloat64((Double) value);
                break;
            case BOOLEAN:
                out.writeInt8((byte) type.getTag());
                out.writeInt8((byte) (Boolean.TRUE.equals(value) ? 1 : 0));
                break;
            case STRING:
                out.writeInt8((byte) type.getTag());
                out.writeUtf8((String) value);
                break;
        }
    }

    @Nullable
    private static Object readObject(final ArrayDataInputBuffer in) throws DataBufferException {
        final Parameter.Type tag = Parameter.Type.valueOfTag(in.readInt8());
        switch (tag) {
            case INT8:
                return (byte) in.readVLQInt32();
            case INT16:
                return (short) in.readVLQInt32();
            case INT32:
                return (int) in.readVLQInt32();
            case INT64:
                return (long) in.readVLQInt32();
            case FLOAT32:
                return (float) in.readVLQFloat32();
            case FLOAT64:
                return (double) in.readVLQFloat64();
            case BOOLEAN:
                return in.readInt8() == 0 ? Boolean.FALSE : Boolean.TRUE;
            case STRING:
                return in.readUtf8();
        }
        return null;
    }
}
