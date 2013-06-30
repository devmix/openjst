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

package org.openjst.commons.rpc.formats.json;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.core.type.TypeReference;
import org.openjst.commons.utils.DateTimeUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sergey Grachev
 */
@SuppressWarnings("DuplicateThrows")
public final class JsonRpcObjectCodec extends ObjectCodec {

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readValue(final JsonParser jp, final Class<T> valueType) throws IOException {
        switch (jp.getCurrentToken()) {
            case START_OBJECT: {
                final Map result = new HashMap();
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    final String fieldName = jp.getCurrentName();
                    jp.nextToken(); // move to value
                    result.put(fieldName, jp.readValueAs(Object.class));
                }
                return (T) result;
            }
            case START_ARRAY: {
                final List<Object> result = new LinkedList<Object>();
                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    switch (jp.getCurrentToken()) {
                        case START_OBJECT:
                            result.add(jp.readValueAs(Map.class));
                            break;
                        case START_ARRAY:
                            result.add(jp.readValueAs(Object[].class));
                            break;
                        default:
                            result.add(jp.readValueAs(Object.class));
                    }
                }
                return (T) result.toArray();
            }
            case VALUE_STRING: {
                return (T) jp.getText();
            }
            case VALUE_NUMBER_INT:
            case VALUE_NUMBER_FLOAT: {
                return (T) jp.getNumberValue();
            }
            case VALUE_TRUE:
            case VALUE_FALSE: {
                return (T) (jp.getBooleanValue() ? Boolean.TRUE : Boolean.FALSE);
            }
            case VALUE_NULL: {
                return null;
            }
        }

        throw new JsonParseException(jp.getCurrentToken().toString(), jp.getCurrentLocation());
    }

    @Override
    public <T> T readValue(final JsonParser jp, final TypeReference<?> valueTypeRef) throws JsonProcessingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T readValue(final JsonParser jp, final ResolvedType valueType) throws JsonProcessingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends TreeNode> T readTree(final JsonParser jp) throws JsonProcessingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Iterator<T> readValues(final JsonParser jp, final Class<T> valueType) throws JsonProcessingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Iterator<T> readValues(final JsonParser jp, final TypeReference<?> valueTypeRef) throws JsonProcessingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Iterator<T> readValues(final JsonParser jp, final ResolvedType valueType) throws JsonProcessingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeValue(final JsonGenerator jgen, final Object value) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
        if (value instanceof Object[]) {
            jgen.writeStartArray();
            for (final Object object : (Object[]) value) {
                jgen.writeObject(object);
            }
            jgen.writeEndArray();
            return;
        }
        if (value instanceof List) {
            jgen.writeStartArray();
            for (final Object object : (List) value) {
                jgen.writeObject(object);
            }
            jgen.writeEndArray();
            return;
        }
        if (value instanceof Map) {
            jgen.writeStartObject();
            for (final Object object : ((Map) value).entrySet()) {
                final Map.Entry entry = (Map.Entry) object;
                jgen.writeObjectField(entry.getKey().toString(), entry.getValue());
            }
            jgen.writeEndObject();
            return;
        }
        if (value == null) {
            jgen.writeNull();
            return;
        }
        if (value instanceof String) {
            jgen.writeString((String) value);
            return;
        }
        if (value instanceof Number) {
            final Number n = (Number) value;
            if (n instanceof Integer) {
                jgen.writeNumber(n.intValue());
            } else if (n instanceof Long) {
                jgen.writeNumber(n.longValue());
            } else if (n instanceof Double) {
                jgen.writeNumber(n.doubleValue());
            } else if (n instanceof Float) {
                jgen.writeNumber(n.floatValue());
            } else if (n instanceof Short) {
                jgen.writeNumber(n.shortValue());
            } else if (n instanceof Byte) {
                jgen.writeNumber(n.byteValue());
            } else if (n instanceof BigInteger) {
                jgen.writeNumber((BigInteger) n);
            } else if (n instanceof BigDecimal) {
                jgen.writeNumber((BigDecimal) n);

                // then Atomic types

            } else if (n instanceof AtomicInteger) {
                jgen.writeNumber(((AtomicInteger) n).get());
            } else if (n instanceof AtomicLong) {
                jgen.writeNumber(((AtomicLong) n).get());
            }
        } else if (value instanceof byte[]) {
            jgen.writeBinary((byte[]) value);
        } else if (value instanceof Boolean) {
            jgen.writeBoolean((Boolean) value);
        } else if (value instanceof AtomicBoolean) {
            jgen.writeBoolean(((AtomicBoolean) value).get());
        } else if (value instanceof Date) {
            jgen.writeString(DateTimeUtils.formatXmlRpcIso8601((Date) value));
        } else if (value instanceof Calendar) {
            jgen.writeString(DateTimeUtils.formatXmlRpcIso8601(((Calendar) value).getTime()));
        } else {
            final Class valueClass = value.getClass();
            final Field[] fields = valueClass.getDeclaredFields();
            jgen.writeStartObject();
            if (fields.length > 0) {
                for (final Field field : fields) {
                    if (Modifier.isTransient(field.getModifiers())) {
                        continue;
                    }

                    final boolean accessible = field.isAccessible();
                    if (!accessible) {
                        field.setAccessible(true);
                    }

                    try {
                        jgen.writeObjectField(field.getName(), field.get(value));
                    } catch (IllegalAccessException e) {
                        throw new JsonGenerationException(e);
                    } finally {
                        if (!accessible) {
                            field.setAccessible(false);
                        }
                    }
                }
            }
            jgen.writeEndObject();
        }
    }

    @Override
    public TreeNode createObjectNode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public TreeNode createArrayNode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonParser treeAsTokens(final TreeNode n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T treeToValue(final TreeNode n, final Class<T> valueType) throws JsonProcessingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonFactory getJsonFactory() {
        throw new UnsupportedOperationException();
    }
}
