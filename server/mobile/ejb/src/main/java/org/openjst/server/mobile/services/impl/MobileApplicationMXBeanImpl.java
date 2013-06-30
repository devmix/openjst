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

package org.openjst.server.mobile.services.impl;

import org.openjst.commons.protocols.auth.SecretKeys;
import org.openjst.server.commons.AbstractMBean;
import org.openjst.server.commons.model.types.LanguageCode;
import org.openjst.server.commons.model.types.RoleType;
import org.openjst.server.commons.services.PreferencesManager;
import org.openjst.server.mobile.Environment;
import org.openjst.server.mobile.I18n;
import org.openjst.server.mobile.Preferences;
import org.openjst.server.mobile.dao.AccountDAO;
import org.openjst.server.mobile.dao.UserDAO;
import org.openjst.server.mobile.model.Account;
import org.openjst.server.mobile.model.User;
import org.openjst.server.mobile.services.MobileApplicationMXBean;
import org.openjst.server.mobile.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * @author Sergey Grachev
 */
@Startup
@Singleton
public class MobileApplicationMXBeanImpl extends AbstractMBean implements MobileApplicationMXBean {

    public static final Logger LOG = LoggerFactory.getLogger(MobileApplicationMXBean.class);

    @EJB
    private AccountDAO accountDAO;

    @EJB
    private UserDAO userDAO;

    @EJB
    private PreferencesManager preferencesManager;

    @Override
    protected String getName() {
        return MobileApplicationMXBean.class.getSimpleName();
    }

    @Override
    protected String getDomain() {
        return Environment.JMX.DOMAIN;
    }

    @PostConstruct
    public void create() {
        try {
            I18n.setDefaultLocale("en", true);
            initDemoData();
        } catch (Exception ignore) {
            LOG.error("", ignore);
        }
        super.create();
    }

    private void initDemoData() {
        Account account = accountDAO.findSystemAccount();
        if (account != null) {
            return;
        }

        LOG.info("Create system account");

        account = new Account();
        account.setAuthId("system");
        account.setName("System account");
        account.setSystem(true);
        em.persist(account);

        LOG.info("Create system user");

        final User user = new User();
        user.setAccount(account);
        user.setAuthId("system");
        user.setName("System");
        user.setPasswordSalt(SecretKeys.salt(User.DEFAULT_SALT_SIZE));
        user.setPassword(UserUtils.encodePassword("system", SecretKeys.PBKDF2WithHmacSHA1, user.getPasswordSalt()));
        user.setRole(RoleType.ADMIN);
        user.setLanguage(LanguageCode.EN);
        user.setSystem(true);
        em.persist(user);

        // TODO remove
        preferencesManager.setBoolean(Preferences.UI.SCRIPTS_CACHE, false);
    }
}
