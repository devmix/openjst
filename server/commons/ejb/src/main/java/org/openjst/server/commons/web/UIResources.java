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

package org.openjst.server.commons.web;

import org.openjst.server.commons.model.types.LanguageCode;
import org.openjst.server.commons.model.types.RoleType;
import org.openjst.server.commons.web.qualifiers.UIEnum;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Arrays;
import java.util.List;

/**
 * @author Sergey Grachev
 */
public final class UIResources {

    @Produces
    @ApplicationScoped
    @UIEnum
    public static final List<Class> UI_ENUMS = Arrays.asList(
            (Class) LanguageCode.class,
            (Class) RoleType.class
    );
}
