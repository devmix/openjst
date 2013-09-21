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

import org.openjst.server.commons.model.types.MobileClientOS;
import org.openjst.server.commons.mq.IMapping;
import org.openjst.server.commons.mq.model.AbstractEntityModel;
import org.openjst.server.mobile.model.Update;

import java.util.Date;

/**
 * @author Sergey Grachev
 */
public final class UpdateModel extends AbstractEntityModel {

    public static final IMapping<Update, UpdateModel> ENTITY_TO_MODEL = new IMapping<Update, UpdateModel>() {
        @Override
        public UpdateModel map(final Update value) {
            return new UpdateModel(
                    value.getId(),
                    value.getOS(),
                    value.getMajor() + "." + value.getMinor() + "." + value.getBuild(),
                    value.getUploadDate(),
                    value.getDescription(),
                    value.getLastUploadId());
        }
    };

    private Long accountId;
    private String uploadId;
    private MobileClientOS os;
    private String version;
    private Date uploadDate;
    private String description;

    public UpdateModel() {
    }

    public UpdateModel(final long id, final MobileClientOS os, final String version, final Date uploadDate,
                       final String description, final String uploadId) {
        this.os = os;
        this.id = id;
        this.version = version;
        this.uploadDate = uploadDate;
        this.description = description;
        this.uploadId = uploadId;
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

    public Long getAccountId() {
        return accountId;
    }

    public String getUploadId() {
        return uploadId;
    }

    public MobileClientOS getOS() {
        return os;
    }
}
