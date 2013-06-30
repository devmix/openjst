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

package org.openjst.commons.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Sergey Grachev
 */
public final class DateTimeUtils {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmssSSSZ");
    private static final DateFormat DATE_XML_RPC_ISO8601 = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss");

    private DateTimeUtils() {
        throw new UnsupportedOperationException();
    }

    public static Date parseTimestamp(final String s) throws ParseException {
        try {
            return new Date(Long.valueOf(s));
        } catch (NumberFormatException e) {
            return DATE_FORMAT.parse(s);
        }
    }

    public static String formatTimestamp(final Date date) {
        return DATE_FORMAT.format(date);
    }

    public static Date parseXmlRpcIso8601(final String date) throws ParseException {
        return DATE_XML_RPC_ISO8601.parse(date);
    }

    public static String formatXmlRpcIso8601(final Date date) {
        return DATE_XML_RPC_ISO8601.format(date);
    }
}
