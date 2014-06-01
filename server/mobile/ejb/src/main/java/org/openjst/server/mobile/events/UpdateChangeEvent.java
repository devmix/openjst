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

package org.openjst.server.mobile.events;

import org.openjst.server.commons.model.types.MobileClientOS;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Sergey Grachev
 */
public final class UpdateChangeEvent implements Serializable {

    private static final long serialVersionUID = -7615261544562508234L;

    private final Action action;
    private final MobileClientOS os;
    private final Date uploadDate;
    private final int major;
    private final int minor;
    private final int build;
    private final String description;
    private final Long accountId;
    private final Long updateId;

    public UpdateChangeEvent(final Action action, final Long accountId, final Long updateId, final MobileClientOS os,
                             final Date uploadDate, final int major, final int minor, final int build, final String description) {

        this.action = action;
        this.accountId = accountId;
        this.updateId = updateId;
        this.os = os;
        this.uploadDate = uploadDate;
        this.major = major;
        this.minor = minor;
        this.build = build;
        this.description = description;
    }

    public Action getAction() {
        return action;
    }

    public Long getUpdateId() {
        return updateId;
    }

    public boolean isRemove() {
        return Action.REMOVE.equals(action);
    }

    public MobileClientOS getOS() {
        return os;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getBuild() {
        return build;
    }

    public String getDescription() {
        return description;
    }

    public Long getAccountId() {
        return accountId;
    }

    public static enum Action {
        ADD,
        UPDATE,
        REMOVE
    }
}
