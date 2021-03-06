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

package org.openjst.client.android.dao;

import org.openjst.commons.dto.ApplicationVersion;

import javax.annotation.Nullable;

/**
 * @author Sergey Grachev
 */
public interface VersionDAO {

    void add(ApplicationVersion version);

    void update(ApplicationVersion version);

    void remove(ApplicationVersion version);

    @Nullable
    Long findVersionId(int major, int minor, int build);

    ApplicationVersion getLatestVersion();
}
