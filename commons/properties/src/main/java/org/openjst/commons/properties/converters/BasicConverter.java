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

package org.openjst.commons.properties.converters;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.openjst.commons.properties.exceptions.PropertyConversionException;

import javax.annotation.Nullable;
import java.util.Locale;

import static org.openjst.commons.properties.Property.Converter;
import static org.openjst.commons.properties.Property.Type;

/**
 * @author Sergey Grachev
 */
final class BasicConverter implements Converter {

    private static final DateTimeFormatter F_TIME_WITH_MILLI = DateTimeFormat.forPattern("HH:mm:ss:SSS")
            .withLocale(Locale.ROOT);
    private static final DateTimeFormatter F_TIME_WITH_SECONDS = DateTimeFormat.forPattern("HH:mm:ss")
            .withLocale(Locale.ROOT);
    private static final DateTimeFormatter F_TIME = DateTimeFormat.forPattern("HH:mm")
            .withLocale(Locale.ROOT);
    private static final DateTimeFormatter P_ANY_TIME = new DateTimeFormatterBuilder()
            .appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2)
            .appendOptional(DateTimeFormat.forPattern(":ss").getParser())
            .appendOptional(DateTimeFormat.forPattern(":SSS").getParser())
            .toFormatter();

    @Nullable
    @Override
    public Object asOf(final Type type, @Nullable final Object value) {
        if (value == null) {
            return null;
        }

        switch (type) {
            case BOOLEAN: {
                if (value instanceof Boolean) {
                    return value;
                } else if (value instanceof Number) {
                    return asBoolean(Type.BYTE, ((Number) value).byteValue());
                } else if (value instanceof String) {
                    return asBoolean(Type.STRING, value);
                } else if (value instanceof Character) {
                    return asBoolean(Type.CHAR, value);
                }
                break;
            }
            case BYTE: {
                if (value instanceof Byte) {
                    return value;
                } else if (value instanceof Boolean) {
                    return asByte(Type.BOOLEAN, value);
                } else if (value instanceof Number) {
                    return ((Number) value).byteValue();
                } else if (value instanceof String) {
                    return asByte(Type.STRING, value);
                } else if (value instanceof Character) {
                    return asByte(Type.CHAR, value);
                }
            }
            case SHORT: {
                if (value instanceof Short) {
                    return value;
                } else if (value instanceof Boolean) {
                    return asShort(Type.BOOLEAN, value);
                } else if (value instanceof Number) {
                    return ((Number) value).shortValue();
                } else if (value instanceof String) {
                    return asShort(Type.STRING, value);
                } else if (value instanceof Character) {
                    return asShort(Type.CHAR, value);
                }
            }
            case INT: {
                if (value instanceof Integer) {
                    return value;
                } else if (value instanceof Boolean) {
                    return asInt(Type.BOOLEAN, value);
                } else if (value instanceof Number) {
                    return ((Number) value).intValue();
                } else if (value instanceof String) {
                    return asInt(Type.STRING, value);
                } else if (value instanceof Character) {
                    return asInt(Type.CHAR, value);
                }
            }
            case LONG: {
                if (value instanceof Long) {
                    return value;
                } else if (value instanceof Boolean) {
                    return asLong(Type.BOOLEAN, value);
                } else if (value instanceof Number) {
                    return ((Number) value).longValue();
                } else if (value instanceof String) {
                    return asLong(Type.STRING, value);
                } else if (value instanceof Character) {
                    return asLong(Type.CHAR, value);
                }
            }
            case FLOAT: {
                if (value instanceof Float) {
                    return value;
                } else if (value instanceof Boolean) {
                    return asFloat(Type.BOOLEAN, value);
                } else if (value instanceof Number) {
                    return ((Number) value).floatValue();
                } else if (value instanceof String) {
                    return asFloat(Type.STRING, value);
                } else if (value instanceof Character) {
                    return asFloat(Type.CHAR, value);
                }
            }
            case DOUBLE: {
                if (value instanceof Double) {
                    return value;
                } else if (value instanceof Boolean) {
                    return asDouble(Type.BOOLEAN, value);
                } else if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                } else if (value instanceof String) {
                    return asDouble(Type.STRING, value);
                } else if (value instanceof Character) {
                    return asDouble(Type.CHAR, value);
                }
            }
            case CHAR: {
                if (value instanceof Character) {
                    return value;
                } else if (value instanceof Boolean) {
                    return asChar(Type.BOOLEAN, value);
                } else if (value instanceof Byte) {
                    return asChar(Type.BYTE, ((Number) value).byteValue());
                } else if (value instanceof Short) {
                    return asChar(Type.SHORT, ((Number) value).shortValue());
                } else if (value instanceof Number) {
                    return asChar(Type.INT, ((Number) value).intValue());
                } else if (value instanceof String) {
                    return asChar(Type.STRING, value);
                }
            }
            case STRING: {
                if (value instanceof String) {
                    return value;
                } else if (value instanceof LocalTime) {
                    return asString(Type.TIME, value);
                } else {
                    return String.valueOf(value);
                }
            }
            case TIME: {
                if (value instanceof LocalTime) {
                    return value;
                } else if (value instanceof Number) {
                    return asTime(Type.LONG, ((Number) value).longValue());
                } else if (value instanceof String) {
                    return asTime(Type.STRING, value);
                }
            }
        }

        throw new PropertyConversionException(value.getClass(), type);
    }

    @Override
    public boolean asBoolean(final Type type, @Nullable final Object value) {
        if (value == null) {
            return false;
        }
        switch (type) {
            case BOOLEAN:
                return (Boolean) value;
            case BYTE:
                return ((Number) value).byteValue() != 0;
            case SHORT:
                return ((Number) value).shortValue() != 0;
            case INT:
                return ((Number) value).intValue() != 0;
            case LONG:
                return ((Number) value).longValue() != 0;
            case FLOAT:
                return ((Number) value).floatValue() != 0;
            case DOUBLE:
                return ((Number) value).doubleValue() != 0;
            case CHAR: {
                final Character c = (Character) value;
                return c == 'Y' || c == 'y';
            }
            case STRING:
                return Boolean.parseBoolean((String) value);
        }
        throw new PropertyConversionException(type, Type.BOOLEAN);
    }

    @Override
    public byte asByte(final Type type, @Nullable final Object value) {
        if (value == null) {
            return 0;
        }
        switch (type) {
            case BOOLEAN:
                return (byte) (Boolean.TRUE.equals(value) ? 1 : 0);
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                return ((Number) value).byteValue();
            case BYTE:
                return (Byte) value;
            case CHAR:
                return (byte) ((Character) value).charValue();
            case STRING:
                return Byte.parseByte((String) value);
        }
        throw new PropertyConversionException(type, Type.BYTE);
    }

    @Override
    public short asShort(final Type type, @Nullable final Object value) {
        if (value == null) {
            return 0;
        }
        switch (type) {
            case BOOLEAN:
                return (byte) (Boolean.TRUE.equals(value) ? 1 : 0);
            case BYTE:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                return ((Number) value).shortValue();
            case SHORT:
                return (Short) value;
            case CHAR:
                return (short) ((Character) value).charValue();
            case STRING:
                return Short.parseShort((String) value);
        }
        throw new PropertyConversionException(type, Type.SHORT);
    }

    @Override
    public int asInt(final Type type, @Nullable final Object value) {
        if (value == null) {
            return 0;
        }
        switch (type) {
            case BOOLEAN:
                return (byte) (Boolean.TRUE.equals(value) ? 1 : 0);
            case BYTE:
            case SHORT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                return ((Number) value).intValue();
            case INT:
                return (Integer) value;
            case CHAR:
                return (Character) value;
            case STRING:
                return Integer.parseInt((String) value);
            case TIME:
                return ((LocalTime) value).getMillisOfDay();
        }
        throw new PropertyConversionException(type, Type.INT);
    }

    @Override
    public long asLong(final Type type, @Nullable final Object value) {
        if (value == null) {
            return 0;
        }
        switch (type) {
            case BOOLEAN:
                return (byte) (Boolean.TRUE.equals(value) ? 1 : 0);
            case BYTE:
            case SHORT:
            case INT:
            case FLOAT:
            case DOUBLE:
                return ((Number) value).longValue();
            case LONG:
                return (Long) value;
            case CHAR:
                return (Character) value;
            case STRING:
                return Long.parseLong((String) value);
            case TIME:
                return ((LocalTime) value).getMillisOfDay();
        }
        throw new PropertyConversionException(type, Type.LONG);
    }

    @Override
    public float asFloat(final Type type, @Nullable final Object value) {
        if (value == null) {
            return 0;
        }
        switch (type) {
            case BOOLEAN:
                return (byte) (Boolean.TRUE.equals(value) ? 1 : 0);
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case DOUBLE:
                return ((Number) value).floatValue();
            case FLOAT:
                return (Float) value;
            case CHAR:
                return (Character) value;
            case STRING:
                return Float.parseFloat((String) value);
            case TIME:
                return ((LocalTime) value).getMillisOfDay();
        }
        throw new PropertyConversionException(type, Type.FLOAT);
    }

    @Override
    public double asDouble(final Type type, @Nullable final Object value) {
        if (value == null) {
            return 0;
        }
        switch (type) {
            case BOOLEAN:
                return (byte) (Boolean.TRUE.equals(value) ? 1 : 0);
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
                return ((Number) value).doubleValue();
            case DOUBLE:
                return (Double) value;
            case CHAR:
                return (Character) value;
            case STRING:
                return Double.parseDouble((String) value);
            case TIME:
                return ((LocalTime) value).getMillisOfDay();
        }
        throw new PropertyConversionException(type, Type.DOUBLE);
    }

    @Override
    public char asChar(final Type type, @Nullable final Object value) {
        if (value == null) {
            return 0;
        }
        switch (type) {
            case BOOLEAN:
                return Boolean.TRUE.equals(value) ? 'Y' : 'N';
            case BYTE:
                return (char) ((Number) value).byteValue();
            case SHORT:
                return (char) ((Number) value).shortValue();
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                return (char) ((Number) value).intValue();
            case CHAR:
                return (Character) value;
            case STRING: {
                final String s = (String) value;
                return s.length() > 0 ? s.charAt(0) : 0;
            }
        }
        throw new PropertyConversionException(type, Type.CHAR);
    }

    @Nullable
    @Override
    public String asString(final Type type, @Nullable final Object value) {
        if (value == null) {
            return null;
        }
        switch (type) {
            case STRING:
                return (String) value;
            case TIME: {
                final LocalTime time = ((LocalTime) value);
                final DateTimeFormatter format;
                if (time.getMillisOfSecond() != 0) {
                    format = F_TIME_WITH_MILLI;
                } else if (time.getSecondOfMinute() != 0) {
                    format = F_TIME_WITH_SECONDS;
                } else {
                    format = F_TIME;
                }
                return time.toString(format);
            }
        }
        return String.valueOf(value);
    }

    @Nullable
    @Override
    public LocalTime asTime(final Type type, @Nullable final Object value) {
        if (value == null) {
            return null;
        }
        switch (type) {
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
                return LocalTime.fromMillisOfDay(((Number) value).longValue());
            case STRING:
                return P_ANY_TIME.parseLocalTime((String) value);
            case TIME:
                return (LocalTime) value;
        }
        throw new PropertyConversionException(type, Type.TIME);
    }
}
