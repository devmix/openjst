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

/**
 * @author Sergey Grachev
 */
public abstract class AbstractColumn implements Column {

    protected final Table table;
    protected final String name;
    protected final String type;
    protected final String modifiers;

    public AbstractColumn(final Table table, final String name, final String type, final String modifiers) {
        this.table = table;
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String modifiers() {
        return modifiers;
    }

    @Override
    public Table table() {
        return table;
    }

    @Override
    public String ddlCreate() {
        return name() + " " + type() + " " + modifiers();
    }

    @Override
    public String toString() {
        return ddlCreate();
    }
}
