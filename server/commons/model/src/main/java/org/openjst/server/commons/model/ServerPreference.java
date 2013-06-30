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

package org.openjst.server.commons.model;

import org.hibernate.validator.constraints.NotEmpty;
import org.openjst.server.commons.model.types.PreferenceType;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Sergey Grachev
 */
@NamedQueries({
        @NamedQuery(name = CommonsQueries.ServerProperty.GET_ALL,
                query = "select e from ServerPreference e"),

        @NamedQuery(name = CommonsQueries.ServerProperty.FIND_VALUE_OF_PREFERENCE,
                query = "select e.value from ServerPreference e where e.name = :name"),

        @NamedQuery(name = CommonsQueries.ServerProperty.FIND_VALUES,
                query = "select e.name, e.value from ServerPreference e where e.name in (:names)")
})
@Entity
@Table(name = ServerPreference.TABLE)
public class ServerPreference {

    public static final String TABLE = "server_preference";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_VALUE = "value";
    public static final String COLUMN_TYPE = "type";

    @NotEmpty
    @Size(min = 1, max = 255)
    @Pattern(regexp = "[A-Za-z0-9\\-_\\.]*")
    @Id
    @Column(name = COLUMN_NAME, unique = true, length = 255, nullable = false)
    private String name;

    @Size(min = 1, max = 0xffff)
    @Column(name = COLUMN_VALUE, length = 255, nullable = true)
    private String value;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = COLUMN_TYPE, length = 255, nullable = false)
    private PreferenceType type;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public PreferenceType getType() {
        return type;
    }

    public void setType(final PreferenceType type) {
        this.type = type;
    }
}
