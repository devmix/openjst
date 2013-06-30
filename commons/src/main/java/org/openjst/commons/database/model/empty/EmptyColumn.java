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

package org.openjst.commons.database.model.empty;

import org.openjst.commons.database.model.Column;
import org.openjst.commons.database.model.Table;
import org.openjst.commons.utils.Constants;

/**
 * @author Sergey Grachev
 */
public final class EmptyColumn implements Column {

    private static final Column INSTANCE = new EmptyColumn();

    public static Column getInstance() {
        return INSTANCE;
    }

    @Override
    public String name() {
        return Constants.stringsEmpty();
    }

    @Override
    public String type() {
        return Constants.stringsEmpty();
    }

    @Override
    public String modifiers() {
        return Constants.stringsEmpty();
    }

    @Override
    public Table table() {
        return EmptyTable.getInstance();
    }

    @Override
    public String ddlCreate() {
        return Constants.stringsEmpty();
    }
}
