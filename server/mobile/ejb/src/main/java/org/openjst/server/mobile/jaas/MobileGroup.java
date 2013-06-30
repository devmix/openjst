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

package org.openjst.server.mobile.jaas;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Sergey Grachev
 */
public class MobileGroup extends MobilePrincipal implements Group {

    private final HashSet<Principal> members = new HashSet<Principal>(3);

    public MobileGroup(final String name) {
        super(name);
    }

    @Override
    public boolean addMember(final Principal user) {
        final boolean isMember = members.contains(user);
        if (!isMember) {
            members.add(user);
        }
        return !isMember;
    }

    @Override
    public boolean removeMember(final Principal user) {
        final Object exists = members.remove(user);
        return exists != null;
    }

    @Override
    public boolean isMember(final Principal member) {
        boolean isMember = members.contains(member);
        if (!isMember) {
            for (final Iterator<Principal> iterator = members.iterator(); iterator.hasNext() && !isMember; ) {
                final Principal principal = iterator.next();
                if (principal instanceof Group) {
                    isMember = ((Group) principal).isMember(member);
                }
            }
        }
        return isMember;
    }

    @Override
    public Enumeration<? extends Principal> members() {
        return Collections.enumeration(members);
    }
}
