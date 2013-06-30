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

package org.openjst.server.commons.maven.manifest.yui;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
@SuppressWarnings({"MismatchedQueryAndUpdateOfCollection", "UnusedDeclaration"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public final class YUIConfig {

    private final boolean debug;
    private final boolean combine;
    private final String comboBase;
    private final String filter;
    private final String root = "";
    private final Map<String, YUIConfigGroup> groups = new LinkedHashMap<String, YUIConfigGroup>();

    public YUIConfig(final String comboBase, final boolean debug, final boolean combine,
                     final String filter) {
        this.combine = combine;
        this.comboBase = comboBase;
        this.filter = filter;
        this.debug = debug;
    }

    public YUIConfigGroup addGroup(final String name, final String comboBase,
                                   final boolean combine, final String filter) {
        final YUIConfigGroup group = new YUIConfigGroup(comboBase, combine, filter);
        groups.put(name, group);
        return group;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isCombine() {
        return combine;
    }

    public String getComboBase() {
        return comboBase;
    }

    public String getFilter() {
        return filter;
    }

    public Map<String, YUIConfigGroup> getGroups() {
        return groups;
    }

    @Override
    public String toString() {
        return "YUIConfig{" +
                "debug=" + debug +
                ", combine=" + combine +
                ", comboBase='" + comboBase + '\'' +
                ", filter='" + filter + '\'' +
                ", groups=" + groups +
                '}';
    }

    public String getRoot() {
        return root;
    }
}
