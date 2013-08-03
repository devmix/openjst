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

package org.openjst.server.mobile.dao;

import org.jetbrains.annotations.Nullable;
import org.openjst.server.commons.model.types.ProtocolType;
import org.openjst.server.commons.mq.ModelQuery;
import org.openjst.server.commons.mq.queries.VoidQuery;
import org.openjst.server.mobile.model.Account;
import org.openjst.server.mobile.model.dto.AccountAuthenticationObj;
import org.openjst.server.mobile.model.dto.AccountConnectionObj;
import org.openjst.server.mobile.mq.model.AccountModel;
import org.openjst.server.mobile.mq.queries.AccountQuery;

import java.util.List;

import static org.openjst.server.commons.mq.queries.VoidQuery.*;

/**
 * @author Sergey Grachev
 */
public interface AccountDAO {

    @Nullable
    Account findById(long id);

    @Nullable
    Account findByAuthId(String authId);

    @Nullable
    Account findSystemAccount();

    @Nullable
    AccountAuthenticationObj findAccountByApiKey(String secretKey);

    List<Account> getListOf(ModelQuery<VoidQuery.Filter, AccountQuery.Order, VoidQuery.Search> query);

    long getCountOf(ModelQuery<VoidQuery.Filter, AccountQuery.Order, VoidQuery.Search> query);

    Account create(AccountModel model);

    Account update(Account account, AccountModel model);

    void delete(Long id);

    void setOnlineStatus(Long accountId, ProtocolType protocolType, String remoteHost);

    void setOfflineStatus(Long accountId);

    long getOnlineCountOf(ModelQuery<Filter, Order, Search> query);

    List<AccountConnectionObj> getOnlineListOf(ModelQuery<Filter, Order, Search> query);
}
