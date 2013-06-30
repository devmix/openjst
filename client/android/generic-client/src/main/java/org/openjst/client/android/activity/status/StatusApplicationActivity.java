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

package org.openjst.client.android.activity.status;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ExpandableListView;
import org.openjst.client.android.R;
import org.openjst.client.android.activity.generic.AbstractActivity;
import org.openjst.client.android.commons.inject.annotations.AndroidLayout;
import org.openjst.client.android.commons.inject.annotations.AndroidView;
import org.openjst.client.android.commons.inject.annotations.JSTInject;
import org.openjst.client.android.commons.managers.ApplicationManager;
import org.openjst.client.android.commons.widgets.PropertiesView;
import org.openjst.client.android.dao.SessionDAO;
import org.openjst.commons.dto.ApplicationVersion;
import org.openjst.commons.dto.tuples.Pair;
import org.openjst.commons.dto.tuples.Tuples;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Sergey Grachev
 */
@AndroidLayout(R.layout.activity_status_application)
public class StatusApplicationActivity extends AbstractActivity {

    @AndroidView(R.id.list)
    private ExpandableListView listView;

    @JSTInject
    private SessionDAO sessionDAO;

    @JSTInject
    private ApplicationManager applicationManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final List<Pair<String, List<PropertiesView.Property>>> properties = new ArrayList<Pair<String, List<PropertiesView.Property>>>();
        addVersion(properties);
        addNewVersion(properties);

        final PropertiesView view = (PropertiesView) findViewById(R.id.list);
        view.setPreventCollapse(true);
        view.setProperties(properties);
    }

    private void addVersion(final List<Pair<String, List<PropertiesView.Property>>> properties) {
        final List<PropertiesView.Property> list = new ArrayList<PropertiesView.Property>();

        try {
            final PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            list.add(PropertiesView.Property.newHorizontal(getString(R.string.version), packageInfo.versionName));
            list.add(PropertiesView.Property.newHorizontal(getString(R.string.code), String.valueOf(packageInfo.versionCode)));
        } catch (PackageManager.NameNotFoundException ignore) {
        }

        properties.add(Tuples.newPair(getString(R.string.current_version), list));
    }

    private void addNewVersion(final List<Pair<String, List<PropertiesView.Property>>> properties) {
        final List<PropertiesView.Property> list = new ArrayList<PropertiesView.Property>();

        final ApplicationVersion currentVersion = applicationManager.getVersion();
        final ApplicationVersion version = sessionDAO.getLatestVersion();
        if (version != null && version.isGreater(currentVersion)) {
            list.add(PropertiesView.Property.newHorizontal(getString(R.string.version), version.toString()));
            list.add(PropertiesView.Property.newHorizontal(getString(R.string.published), new Date(version.getTimestamp()).toLocaleString()));
            list.add(PropertiesView.Property.newVertical(getString(R.string.description), version.getDescription()));
        }

        properties.add(Tuples.newPair(getString(R.string.available_new_version), list));
    }
}
