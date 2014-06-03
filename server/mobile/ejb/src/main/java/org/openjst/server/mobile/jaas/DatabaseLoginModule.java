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

import org.jboss.resteasy.plugins.server.embedded.SimplePrincipal;
import org.openjst.commons.dto.constants.Constants;
import org.openjst.commons.dto.tuples.Pair;
import org.openjst.commons.security.auth.SecretKey;
import org.openjst.server.commons.model.types.RoleType;
import org.openjst.server.commons.utils.EjbLocator;
import org.openjst.server.mobile.dao.UserDAO;
import org.openjst.server.mobile.model.User;
import org.openjst.server.mobile.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;
import java.text.ParseException;
import java.util.Map;
import java.util.Set;

/**
 * @author Sergey Grachev
 */
public final class DatabaseLoginModule implements LoginModule {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseLoginModule.class);

    private static final String ERROR_NO_CALLBACK_HANDLER = "Error: no CallbackHandler available to collect authentication information";
    private static final String ERROR_NO_AUTHENTICATION_INFORMATION = "Error: %s not available to garner authentication information from the user";
    private static final String ERROR_INCORRECT_FORMAT_OF_USER_NAME = "Error: incorrect format of user name";
    private static final String ERROR_PASSWORD_INCORRECT = "Password incorrect";
    private static final String ERROR_NO_USER = "No user with name %s";
    private static final String ERROR_USER_DAO_NOT_FOUND = "UserDAO not found";
    private static final String ERROR_MOBILE_SESSION_NOT_FOUND = "MobileSession not found";
//    private static final String ERROR_USER_WITH_ID_D_NOT_FOUND = "User with ID %d not found";
    private static final String CALLBACK_NAME = "OJST ID: ";
    private static final String CALLBACK_PASSWORD = "Password: ";

    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, Object> sharedState;

    private Identification id;
    private MobilePrincipal principal;
    private boolean loginSucceeded;
    private boolean commitSucceeded;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState,
                           final Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = (Map<String, Object>) sharedState;
        this.id = new Identification();
    }

    @Override
    public boolean login() throws LoginException {
        if (callbackHandler == null) {
            throw new LoginException(ERROR_NO_CALLBACK_HANDLER);
        }

        final Callback[] callbacks = new Callback[]{
                new NameCallback(CALLBACK_NAME),
                new PasswordCallback(CALLBACK_PASSWORD, false)};

        final Pair<String, String> authId;
        final String name;
        final String password;
        try {
            callbackHandler.handle(callbacks);
            name = ((NameCallback) callbacks[0]).getName();
            authId = UserUtils.parseAuthId(name);
            final char[] passwordChars = ((PasswordCallback) callbacks[1]).getPassword();
            password = passwordChars == null ? Constants.stringsEmpty() : new String(passwordChars);
            ((PasswordCallback) callbacks[1]).clearPassword();
        } catch (final IOException ioe) {
            throw new LoginException(ioe.toString());
        } catch (final UnsupportedCallbackException uce) {
            throw new LoginException(String.format(ERROR_NO_AUTHENTICATION_INFORMATION, uce.getCallback().toString()));
        } catch (final ParseException e) {
            throw new LoginException(ERROR_INCORRECT_FORMAT_OF_USER_NAME);
        }

        final UserDAO userDAO = getUserDAO();

        final User.AuthorizationData data = userDAO.findAuthorizationDataOf(authId.second(), authId.first());
        if (data == null) {
            throw new FailedLoginException(String.format(ERROR_NO_USER, authId.first()));
        }

        final SecretKey correctPassword = UserUtils.parseSecretKey(data.password);
        final SecretKey encodedPassword = correctPassword.getType().encode(password, data.passwordSalt);
        if (!encodedPassword.equals(correctPassword)) {
            throw new FailedLoginException(ERROR_PASSWORD_INCORRECT);
        }

        id.userId = data.userId;
        id.user = data.user;
        id.account = data.account;
        id.role = data.role;

        sharedState.put("javax.security.auth.login.name", name);
        sharedState.put("javax.security.auth.login.password", password);

        this.loginSucceeded = true;

        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        if (!loginSucceeded) {
            return false;
        }

//        final UserDAO userDAO = getUserDAO();
//
//        final User user = userDAO.findById(id.userId);
//        if (user == null) {
//            throw new LoginException(String.format(ERROR_USER_WITH_ID_D_NOT_FOUND, id.userId));
//        }

        final RoleType role = id.role;
        final Group roles = new MobileGroup("Roles");
        if (role != null) {
            roles.addMember(new SimplePrincipal(role.name()));
        }

        principal = new MobilePrincipal(id.user, id.account, id.user, id.userId);

        addPrincipal(subject, principal);
        addPrincipal(subject, roles);

//        final MobileSession session = getMobileSession();
//
//        session.initialization(user);

        LOG.trace("User authorized {}", id);

        commitSucceeded = true;
        id.clear();

        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        if (!loginSucceeded) {
            return false;
        }

        if (!commitSucceeded) {
            loginSucceeded = false;
            principal = null;
            id.clear();
        } else {
            logout();
        }

        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        removePrincipal(subject, principal);

        loginSucceeded = commitSucceeded;
        principal = null;
        id.clear();

        sharedState.put("javax.security.auth.login.name", null);
        sharedState.put("javax.security.auth.login.password", null);

        return true;
    }

    @SuppressWarnings("unchecked")
    private static void addPrincipal(final Subject subject, final Principal principal) {
        final Set<Principal> principals = subject.getPrincipals();
        java.security.AccessController.doPrivileged((java.security.PrivilegedAction) () -> {
            principals.add(principal);
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    private static void removePrincipal(final Subject subject, final Principal principal) {
        java.security.AccessController.doPrivileged((java.security.PrivilegedAction) () -> {
            subject.getPrincipals().remove(principal);
            return null;
        });
    }

    private static UserDAO getUserDAO() throws LoginException {
        final UserDAO userDAO = EjbLocator.lookupGlobal(UserDAO.class);
        if (userDAO == null) {
            throw new LoginException(ERROR_USER_DAO_NOT_FOUND);
        }
        return userDAO;
    }

//    private static MobileSession getMobileSession() throws LoginException {
//        final MobileSession ctx = CdiLocator.lookup(MobileSession.class);
//        if (ctx == null) {
//            throw new LoginException(ERROR_MOBILE_SESSION_NOT_FOUND);
//        }
//        return ctx;
//    }

    private static final class Identification {
        private Long userId;
        private String user;
        private String account;
        public RoleType role;

        public void clear() {
            userId = null;
            user = account = null;
        }

        @Override
        public String toString() {
            return "Identification{" +
                    "userId=" + userId +
                    ", user='" + user + '\'' +
                    ", account='" + account + '\'' +
                    ", role=" + role +
                    '}';
        }
    }
}
