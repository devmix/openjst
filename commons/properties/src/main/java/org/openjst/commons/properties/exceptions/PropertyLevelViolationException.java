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

package org.openjst.commons.properties.exceptions;

import org.openjst.commons.properties.annotations.Levels;

/**
 * @author Sergey Grachev
 */
public final class PropertyLevelViolationException extends PropertyException {

    private static final long serialVersionUID = -7731349290779111642L;

    private final Levels levels;
    private final int level;

    public PropertyLevelViolationException(final Levels levels, final int level) {
        this.levels = levels;
        this.level = level;
    }

    public Levels getLevels() {
        return levels;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return "PropertyLevelViolationException{" +
                "levels=" + levels +
                ", level=" + level +
                '}';
    }
}
