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

package org.openjst.commons.database.model;

import org.openjst.commons.dto.constants.Constants;

import java.util.List;

/**
 * @author Sergey Grachev
 */
public final class QueryWhere {

    private final Operator operator;
    private final String key;
    private final Object value;
    private final Group group;
    private final QueryWhere[] arguments;

    public QueryWhere(final Group group, final Operator operator, final String key, final Object value) {
        this.group = group;
        this.operator = operator;
        this.arguments = null;
        this.key = key;
        this.value = value;
    }

    public QueryWhere(final Group group, final QueryWhere... arguments) {
        this.group = group;
        this.operator = null;
        this.arguments = arguments;
        this.key = null;
        this.value = null;
    }

    public static QueryWhere eq(final String key, final Object value) {
        return new QueryWhere(Group.NONE, Operator.EQ, key, value);
    }

    public static QueryWhere and(final QueryWhere... arguments) {
        return new QueryWhere(Group.AND, arguments);
    }

    public static QueryWhere or(final QueryWhere... arguments) {
        return new QueryWhere(Group.OR, arguments);
    }

    public String getOperator() {
        return operator != null ? operator.asString() : Constants.stringsEmpty();
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public String getGroup() {
        return group != null ? group.asString() : Constants.stringsEmpty();
    }

    public boolean isGroup() {
        return !Group.NONE.equals(group);
    }

    public QueryWhere[] getArguments() {
        return arguments;
    }

    public void toSql(final StringBuilder sb, final List<Object> parameters) {
        if (!isGroup()) {
            sb.append(key).append(operator.asString()).append("?");
            if (parameters != null) {
                parameters.add(value);
            }
            return;
        }
        sb.append("(");
        boolean first = true;
        for (final QueryWhere item : arguments) {
            if (!first) {
                sb.append(group.asString());
            } else {
                first = false;
            }
            item.toSql(sb, parameters);
        }
        sb.append(")");
    }

    private enum Operator {
        EQ("="),
        NOT_EQ("<>"),
        LT("<"),
        LT_EQ("<="),
        GT(">"),
        GT_EQ(">="),
        LIKE("LIKE"),
        IN("IN"),
        NOT_IN("NOT IN");

        private final String stringValue;

        Operator(final String stringValue) {
            this.stringValue = stringValue;
        }

        public String asString() {
            return stringValue;
        }
    }

    private enum Group {
        NONE(""),
        OR(" OR "),
        AND(" AND ");

        private final String stringValue;

        Group(final String stringValue) {
            this.stringValue = stringValue;
        }

        public String asString() {
            return stringValue;
        }
    }
}
