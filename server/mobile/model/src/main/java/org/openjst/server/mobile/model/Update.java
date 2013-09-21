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

import org.openjst.server.commons.model.types.MobileClientOS;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
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
                        " where (:accountId is null or e.account.id = :accountId)"),

        @NamedQuery(name = Queries.Update.GET_UPDATE_TO_SENT,
                query = "select new org.openjst.server.mobile.model.dto.UpdateToSentObj(" +
                        "   e.id, e.account.id, e.os, concat(e.major,'.',e.minor,'.',e.build), e.uploadDate, e.description " +
                        ")from Update e" +
                        " where e.id = :updateId")
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
    public static final String COLUMN_OS = "os";

    @Column(name = COLUMN_OS, nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    @NotNull
    private MobileClientOS os;

    @Column(name = COLUMN_MAJOR, nullable = false)
    @Min(0)
    private int major;

    @Column(name = COLUMN_MINOR, nullable = false)
    @Min(0)
    private int minor;

    @Column(name = COLUMN_BUILD, nullable = false)
    @Min(0)
    private int build;

    @Column(name = COLUMN_UPLOAD_DATE, nullable = false)
    @Past
    @NotNull
    private Date uploadDate;

    @Column(name = COLUMN_DESCRIPTION, length = Short.MAX_VALUE)
    @Size(max = Short.MAX_VALUE)
    private String description;

    @Column(name = COLUMN_EXTERNAL_ID, nullable = false, length = 32)
    @Size(max = 32)
    @NotNull
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

    public MobileClientOS getOS() {
        return os;
    }

    public void setOS(final MobileClientOS operatingSystem) {
        this.os = operatingSystem;
    }
}
