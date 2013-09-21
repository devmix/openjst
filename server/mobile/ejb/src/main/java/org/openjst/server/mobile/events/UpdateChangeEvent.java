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

package org.openjst.server.mobile.events;

import java.io.Serializable;

/**
 * @author Sergey Grachev
 */
public final class UpdateChangeEvent implements Serializable {

    private static final long serialVersionUID = -7615261544562508234L;

    private final Action action;
    private final Long updateId;

    public UpdateChangeEvent(final Action action, final Long updateId) {

        this.action = action;
        this.updateId = updateId;
    }

    public Action getAction() {
        return action;
    }

    public Long getUpdateId() {
        return updateId;
    }

    public boolean isRemove() {
        return Action.REMOVE.equals(action);
    }

    public static enum Action {
        ADD,
        UPDATE,
        REMOVE
    }
}
