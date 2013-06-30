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

package org.openjst.server.mobile.cdi.beans.impl;

import org.openjst.server.commons.cdi.beans.impl.AbstractGlobalSession;
import org.openjst.server.commons.model.types.RoleType;
import org.openjst.server.mobile.I18n;
import org.openjst.server.mobile.cdi.beans.MobileSession;
import org.openjst.server.mobile.model.User;
import org.openjst.server.mobile.mq.model.SessionModel;
import org.openjst.server.mobile.mq.model.UserModel;

import javax.enterprise.context.SessionScoped;
import java.util.Locale;

/**
 * @author Sergey Grachev
 */
@SessionScoped
@SuppressWarnings("UnusedDeclaration")
public class MobileSessionImpl extends AbstractGlobalSession implements MobileSession {

    private static final long serialVersionUID = -4401358883640148661L;

    private Locale locale = null;
    private SessionModel session = SessionModel.GUEST;
    private RoleType role = RoleType.UNKNOWN;
    private long userId = -1;
    private long accountId = -1;

    @Override
    public void initialization(final User user) {
        this.session = SessionModel.newInstance(UserModel.USER_TO_MODEL.map(user));
        this.role = user.getRole();
        this.locale = I18n.getLocale(user.getLanguage().name());
        this.userId = user.getId();
        this.accountId = user.getAccount().getId();
    }

    @Override
    public SessionModel getSession() {
        return session;
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public long getAccountId() {
        return accountId;
    }

    @Override
    public boolean isAuthorized() {
        return !SessionModel.GUEST.equals(session);
    }

    @Override
    public boolean isAdministrator() {
        return RoleType.ADMIN.equals(role);
    }

    @Override
    public Locale getLocale() {
        return locale;
    }
}
