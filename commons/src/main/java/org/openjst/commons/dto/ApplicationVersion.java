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

package org.openjst.commons.dto;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * @author Sergey Grachev
 */
public class ApplicationVersion implements Serializable {

    private static final long serialVersionUID = -3950977397271774081L;

    private int minor;
    private int major;
    private int build;
    private long timestamp;
    private String description;

    @SuppressWarnings("UnusedDeclaration")
    public ApplicationVersion() { // for RPC
    }

    public ApplicationVersion(final long timestamp, final int major, final int minor, final int build, final String description) {
        this.timestamp = timestamp;
        this.major = major;
        this.minor = minor;
        this.build = build;
        this.description = description;
    }

    public ApplicationVersion(final int major, final int minor, final int build) {
        this.major = major;
        this.minor = minor;
        this.build = build;
    }

    /**
     * Parse version string. Format - {major}.{minor}.{build}
     *
     * @param version string representation of the version
     * @return version with fields: major, minor, build
     * @throws IllegalArgumentException if version name has incorrect format
     * @throws NumberFormatException    if the string does not contain a parsable integer.
     */
    public static ApplicationVersion parse(final String version) {
        final String[] numbers = version.split("\\.");
        if (numbers.length == 0) {
            throw new IllegalArgumentException("Incorrect format of version '" + version + "'");
        }
        return new ApplicationVersion(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]), Integer.parseInt(numbers[2]));
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(final int minor) {
        this.minor = minor;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(final int major) {
        this.major = major;
    }

    public int getBuild() {
        return build;
    }

    public void setBuild(final int build) {
        this.build = build;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable final String description) {
        this.description = description;
    }

    public boolean isEquals(final ApplicationVersion version) {
        return major == version.major && minor == version.minor && build == version.build;
    }

    public boolean isGreater(final ApplicationVersion version) {
        return !isEquals(version)
                && (major > version.major || (major == version.major
                && (minor > version.minor || (minor == version.minor
                && (build > version.build)))));
    }

    public boolean isLess(final ApplicationVersion version) {
        return !isEquals(version)
                && (major < version.major || (major == version.major
                && (minor < version.minor || (minor == version.minor
                && (build < version.build)))));
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + build;
    }
}
