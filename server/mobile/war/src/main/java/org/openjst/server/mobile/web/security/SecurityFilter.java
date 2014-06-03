/*
 * Copyright (C) 2014 OpenJST Project
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

package org.openjst.server.mobile.web.security;

import org.openjst.commons.dto.tuples.Pair;
import org.openjst.server.mobile.dao.UserDAO;
import org.openjst.server.mobile.session.MobileSession;
import org.openjst.server.mobile.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;

/**
 * @author Sergey Grachev
 */
@WebFilter("/*")
public class SecurityFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private MobileSession session;

    @EJB
    private UserDAO userDAO;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final Principal principal = ((HttpServletRequest) request).getUserPrincipal();
        // restore session data from WildFly cache
        if (principal != null && !session.isInitialized()) {
            final String name = principal.getName();
            if (name != null && name.contains("@")) {
                try {
                    final Pair<String, String> id = UserUtils.parseAuthId(name);
                    log.debug("Restore session for {}", name);
                    session.initialization(userDAO.findByAccountAndName(id.first(), id.second()));
                } catch (final ParseException ignore) {
                    log.warn("Incorrect principal name {}", name);
                }
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
