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
import org.openjst.server.commons.mq.IMapping;
import org.openjst.server.commons.mq.model.AbstractEntityModel;
import org.openjst.server.mobile.model.User;

/**
 * @author Sergey Grachev
 */
@SuppressWarnings("UnusedDeclaration")
public final class UserModel extends AbstractEntityModel {

    public static final IMapping<User, UserModel> USER_TO_MODEL = new IMapping<User, UserModel>() {
        @Override
        public UserModel map(final User value) {
            return new UserModel(
                    value.getId(), value.getAuthId(), value.getName(), value.getRole(), value.getLanguage(),
                    AccountModel.ACCOUNT_TO_MODEL.map(value.getAccount()));
        }
    };

    private String authId;
    private String name;
    private String password;
    private RoleType role;
    private AccountModel account;
    private LanguageCode language;

    public UserModel() {
    }

    private UserModel(final Long id, final String authId, final String name, final RoleType role, final LanguageCode language,
                      final AccountModel account) {
        this.id = id;
        this.authId = authId;
        this.name = name;
        this.role = role;
        this.language = language;
        this.account = account;
    }

    public static UserModel newInstance(final Long id, final String authId, final String name, final RoleType role,
                                        final LanguageCode language, final AccountModel account) {
        return new UserModel(id, authId, name, role, language, account);
    }

    public String getAuthId() {
        return authId;
    }

    public String getName() {
        return name;
    }

    public RoleType getRole() {
        return role;
    }

    public AccountModel getAccount() {
        return account;
    }

    public LanguageCode getLanguage() {
        return language;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "authId='" + authId + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", account=" + account +
                ", language=" + language +
                '}';
    }
}
