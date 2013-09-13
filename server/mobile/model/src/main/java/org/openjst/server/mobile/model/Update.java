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

package org.openjst.server.mobile.model;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Grachev
 */
@NamedQueries({
        @NamedQuery(name = Queries.Update.FIND_BY_ID,
                query = "select e from Update e" +
                        " where e.id = :id and (:accountId is null or e.account.id = :accountId)"),

        @NamedQuery(name = Queries.Update.GET_LIST_OF,
                query = "select e from Update e " +
                        " where (:accountId is null or e.account.id = :accountId)" +
                        " order by e.major desc, e.minor desc, e.build desc"),

        @NamedQuery(name = Queries.Update.GET_COUNT_OF,
                query = "select count(e) from Update e " +
                        " where (:accountId is null or e.account.id = :accountId)")
})
@Entity
@Table(name = Update.TABLE)
public class Update extends AbstractAccountEntity {

    public static final String TABLE = "client_update";
    public static final String COLUMN_MAJOR = "major";
    public static final String COLUMN_MINOR = "minor";
    public static final String COLUMN_BUILD = "build";
    public static final String COLUMN_UPLOAD_DATE = "upload_date";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_EXTERNAL_ID = "last_upload_id";

    @Column(name = COLUMN_MAJOR, nullable = false)
    private int major;

    @Column(name = COLUMN_MINOR, nullable = false)
    private int minor;

    @Column(name = COLUMN_BUILD, nullable = false)
    private int build;

    @Column(name = COLUMN_UPLOAD_DATE, nullable = false)
    private Date uploadDate;

    @Column(name = COLUMN_DESCRIPTION)
    private String description;

    @Column(name = COLUMN_EXTERNAL_ID, nullable = false)
    private String lastUploadId;

    public int getMajor() {
        return major;
    }

    public void setMajor(final int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(final int minor) {
        this.minor = minor;
    }

    public int getBuild() {
        return build;
    }

    public void setBuild(final int build) {
        this.build = build;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(final Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getLastUploadId() {
        return lastUploadId;
    }

    public void setLastUploadId(final String lastUploadId) {
        this.lastUploadId = lastUploadId;
    }
}
