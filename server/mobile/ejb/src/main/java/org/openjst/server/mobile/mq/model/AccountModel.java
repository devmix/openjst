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

import org.openjst.server.commons.mq.IMapping;
import org.openjst.server.commons.mq.model.AbstractEntityModel;
import org.openjst.server.mobile.model.Account;

/**
 * @author Sergey Grachev
 */
public final class AccountModel extends AbstractEntityModel {

    public static final IMapping<Account, AccountModel> ACCOUNT_TO_MODEL = new IMapping<Account, AccountModel>() {
        @Override
        public AccountModel map(final Account value) {
            return new AccountModel(value.getId(), value.getAuthId(), value.getName(), value.getApiKey());
        }
    };

    private String apiKey;
    private String authId;
    private String name;

    @SuppressWarnings("UnusedDeclaration")
    public AccountModel() {
    }

    private AccountModel(final long id, final String authId, final String name, final String apiKey) {
        this.apiKey = apiKey;
        this.id = id;
        this.authId = authId;
        this.name = name;
    }

    public String getAuthId() {
        return authId;
    }

    public String getName() {
        return name;
    }

    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String toString() {
        return "AccountModel{" +
                "id=" + id +
                ", authId='" + authId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
