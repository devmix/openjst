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

package org.openjst.server.commons.maven.utils;

import org.codehaus.plexus.util.FileUtils;
import org.openjst.server.commons.maven.manifest.jaxb.ManifestType;
import org.openjst.server.commons.maven.manifest.jaxb.ObjectFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

/**
 * @author Sergey Grachev
 */
public final class ManifestUtils {

    private ManifestUtils() {
    }

    public static ManifestType parse(final File file) throws SAXException, JAXBException {
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema schema = schemaFactory.newSchema(ManifestUtils.class.getClassLoader().getResource("manifest.xsd"));
        final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(schema);
        return (ManifestType) ((JAXBElement) unmarshaller.unmarshal(file)).getValue();
    }

    public static String compressedFile(final String file) {
        if (file.isEmpty() || Patterns.COMPRESSED.matcher(file).matches()) {
            return file;
        }

        final String extension = FileUtils.extension(file);

        return file.substring(0, file.length() - extension.length() - 1) + "-min." + extension;
    }

    public static String makeModuleName(final String name) {
        if (name.isEmpty()) {
            return name;
        }

        final StringBuilder sb = new StringBuilder(name.replaceAll(Patterns.STRING_EXT_REPLACE, "").replaceAll("/", "."));
        int i;
        while ((i = sb.lastIndexOf("-")) > -1) {
            sb.replace(i, i + 2, "" + Character.toUpperCase(sb.charAt(i + 1)));
        }

        final int lastDotIndex = sb.lastIndexOf(".") + 1;
        sb.replace(lastDotIndex, lastDotIndex + 1, "" + Character.toUpperCase(sb.charAt(lastDotIndex)));
        return sb.toString();
    }

    public static boolean isSourceModule(final String fileName) {
        return Patterns.JS_MODULE_FILE_NAME.matcher(fileName).matches();
    }

    public static boolean isStyleModule(final String fileName) {
        return Patterns.CSS_MODULE_FILE_NAME.matcher(fileName).matches();
    }

    public static String relativePath(final File base, final File file) {
        return file.getAbsolutePath().substring(base.getAbsolutePath().length() + 1);
    }
}
