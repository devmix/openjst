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

package org.openjst.server.mobile.mq.model;

import org.openjst.server.commons.model.types.LanguageCode;
import org.openjst.server.commons.model.types.RoleType;

/**
 * @author Sergey Grachev
 */
public final class SessionModel {

    public static final SessionModel GUEST = new SessionModel(UserModel.newInstance(
            -1L, null, "guest", RoleType.USER, LanguageCode.EN, null));

    private final UserModel user;

    private SessionModel(final UserModel user) {
        this.user = user;
    }

    public static SessionModel newInstance(final UserModel user) {
        return new SessionModel(user);
    }

    public UserModel getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "SessionModel{" +
                "user=" + user +
                '}';
    }
}
