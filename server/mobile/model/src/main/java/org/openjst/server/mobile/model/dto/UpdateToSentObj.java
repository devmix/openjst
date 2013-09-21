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

package org.openjst.server.mobile.model.dto;

import org.openjst.server.commons.model.types.MobileClientOS;

import java.util.Date;

/**
 * @author Sergey Grachev
 */
public final class UpdateToSentObj {

    private final Long id;
    private final Long accountId;
    private final MobileClientOS os;
    private final String version;
    private final Date uploadDate;
    private final String description;

    public UpdateToSentObj(final Long id, final Long accountId, final MobileClientOS os,
                           final String version, final Date uploadDate, final String description) {
        this.id = id;
        this.accountId = accountId;
        this.os = os;
        this.version = version;
        this.uploadDate = uploadDate;
        this.description = description;
    }

    public Long getAccountId() {
        return accountId;
    }

    public MobileClientOS getOS() {
        return os;
    }

    public String getVersion() {
        return version;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public String getDescription() {
        return description;
    }

    public Long getId() {
        return id;
    }
}
