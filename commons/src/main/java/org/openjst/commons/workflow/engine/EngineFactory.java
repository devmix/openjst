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

package org.openjst.commons.workflow.engine;

import org.jetbrains.annotations.Nullable;
import org.openjst.commons.conversion.units.TimeUnits;
import org.openjst.commons.conversion.units.Units;
import org.openjst.commons.workflow.Engine;
import org.openjst.commons.workflow.persistence.Persistence;
import org.openjst.commons.workflow.persistence.PersistenceTypes;

/**
 * @author Sergey Grachev
 */
public final class EngineFactory {

    public static final int DEFAULT_THREADS_COUNT = 4;
    public static final int DEFAULT_KEEP_ALIVE_TIME = (int) Units.convert(2, TimeUnits.SECOND, TimeUnits.MILLISECOND);

    private EngineFactory() {
    }

    public static Engine newInstance(final int threadsCount, final int keepAliveTime, @Nullable final Persistence persistence) {
        return new GenericEngine(
                threadsCount == 0 ? DEFAULT_THREADS_COUNT : threadsCount,
                keepAliveTime == 0 ? DEFAULT_KEEP_ALIVE_TIME : keepAliveTime,
                persistence == null ? PersistenceTypes.newMemory() : persistence);
    }
}
