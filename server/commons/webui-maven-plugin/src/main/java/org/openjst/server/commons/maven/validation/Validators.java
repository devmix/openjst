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

package org.openjst.server.commons.maven.validation;

import org.apache.maven.plugin.logging.Log;
import org.mozilla.javascript.ContextFactory;
import org.openjst.server.commons.maven.manifest.jaxb.ValidatorType;
import org.openjst.server.commons.maven.manifest.jaxb.ValidatorsType;
import org.openjst.server.commons.maven.resources.ResourceType;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public final class Validators {

    private final List<Validator> js = new LinkedList<Validator>();
    private final List<Validator> css = new LinkedList<Validator>();
    private final Map<Validator, ValidatorType> map = new LinkedHashMap<Validator, ValidatorType>();
    private final Log log;
    private final ContextFactory ctx;

    public Validators(final Log log) {
        this.log = log;
        this.ctx = ContextFactory.getGlobal();
    }

    public Validators initialize(final ValidatorsType validators) {
        if (validators == null) {
            return this;
        }

        log.info("");
        log.info("Initialize validators...");

        for (final ValidatorType item : validators.getValidator()) {
            final String type = item.getType();
            if (item.isSkip()) {
                log.info("  Validators " + type + " skipped");
            }

            final ResourceType resourceType = ResourceType.of(item.getFor());
            final Validator validator = resourceType.createValidator(type, ctx, null);
            switch (resourceType) {
                case JAVA_SCRIPT:
                    js.add(validator);
                    break;
                case CSS:
                    css.add(validator);
                    break;
            }

            map.put(validator, item);

            if (log.isDebugEnabled()) {
                log.debug(String.format("  tValidators '%s' created", type));
            }
        }

        return this;
    }

    public List<Validator> forJavaScript() {
        return js;
    }

    public List<Validator> forCss() {
        return css;
    }

    public boolean isAllowErrors(final Validator validator) {
        final ValidatorType v = map.get(validator);
        return v == null || !v.isFailOnError();
    }
}
