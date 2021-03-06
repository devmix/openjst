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

package org.openjst.commons.rpc.objects;

import org.openjst.commons.dto.constants.Constants;
import org.openjst.commons.rpc.RPCParameters;

/**
 * @author Sergey Grachev
 */
final class EmptyParameters implements RPCParameters {

    public Class[] getTypes() {
        return Constants.arraysEmptyClasses();
    }

    public Object[] getValues() {
        return Constants.arraysEmptyObjects();
    }

    public RPCParameters add(final Object value) {
        return this;
    }

    public RPCParameters addAll(final Object[] objects) {
        return this;
    }

    public boolean isEmpty() {
        return true;
    }
}
