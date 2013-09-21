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

package org.openjst.server.commons.session.impl;

import org.openjst.server.commons.session.GlobalSession;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Locale;

/**
 * @author Sergey Grachev
 */
@SessionScoped
public abstract class AbstractGlobalSession implements GlobalSession, Serializable {

    private static final long serialVersionUID = 7092663331297050548L;

    @Override
    public boolean isAuthorized() {
        return false;
    }

    @Override
    public boolean isAdministrator() {
        return false;
    }

    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }
}
