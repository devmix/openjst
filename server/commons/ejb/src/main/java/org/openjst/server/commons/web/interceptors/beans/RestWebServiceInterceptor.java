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

package org.openjst.server.commons.web.interceptors.beans;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openjst.server.commons.mq.access.ModelAccessRestriction;
import org.openjst.server.commons.mq.mapping.ExceptionMapping;
import org.openjst.server.commons.mq.results.QuerySingleResult;
import org.openjst.server.commons.mq.results.Result;
import org.openjst.server.commons.session.GlobalSession;
import org.openjst.server.commons.web.interceptors.UIService;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.PersistenceException;
import java.io.Serializable;

/**
 * @author Sergey Grachev
 */
@Interceptor
@UIService
public final class RestWebServiceInterceptor implements Serializable {

    private static final long serialVersionUID = -6574904678636553620L;

    @Inject
    private GlobalSession globalSession;

    @AroundInvoke
    private Object aroundInvoke(final InvocationContext ctx) throws Exception {
        checkModelAccessRestriction(ctx);

        try {
            return ctx.proceed();
        } catch (Exception e) {
            if (QuerySingleResult.class.isAssignableFrom(ctx.getMethod().getReturnType())) {
                final ExceptionMapping mapping = ctx.getMethod().getAnnotation(ExceptionMapping.class);

                final int i = ExceptionUtils.indexOfThrowable(e, PersistenceException.class);
                if (i > -1) {
                    final String message = ExceptionUtils.getThrowables(e)[i].getMessage().toLowerCase();
                    if (message.contains("constraintviolationexception")) {
                        if (message.contains("unique") && mapping != null && mapping.unique()) {
                            return Result.notUnique(mapping.uniqueFields());
                        }
                        return Result.notUnique();
                    }
                }
            }
            throw e;
        }
    }

    private void checkModelAccessRestriction(final InvocationContext ctx) throws IllegalAccessException {
        final ModelAccessRestriction classAccess = ctx.getTarget().getClass().getAnnotation(ModelAccessRestriction.class);
        final ModelAccessRestriction methodAccess = ctx.getMethod().getAnnotation(ModelAccessRestriction.class);

        // check authorization

        boolean permission = methodAccess == null
                ? (classAccess != null && classAccess.requireAuthorized()) : methodAccess.requireAuthorized();

        if (permission && !globalSession.isAuthorized()) {
            throw new IllegalAccessException("Not authorized");
        }

        // check administrator or not

        permission = methodAccess == null
                ? (classAccess != null && classAccess.requireAdministrator()) : methodAccess.requireAdministrator();

        if (permission && !globalSession.isAdministrator()) {
            throw new IllegalAccessException("Only for administrators");
        }
    }
}
