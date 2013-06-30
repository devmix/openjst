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

package org.openjst.client.android.theme.test;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import org.openjst.client.android.commons.widgets.tabhost.TabHostUtils;

/**
 * @author Sergey Grachev
 */
public final class MainActivity extends TabActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TabHost tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("common")
                .setIndicator(TabHostUtils.newIndicator(this, "Common"))
                .setContent(new Intent(this, CommonActivity.class)));

        tabHost.addTab(tabHost.newTabSpec("list_view")
                .setIndicator(TabHostUtils.newIndicator(this, "ListView"))
                .setContent(new Intent(this, ListViewActivity.class)));

        tabHost.addTab(tabHost.newTabSpec("expandable_list_view")
                .setIndicator(TabHostUtils.newIndicator(this, "ExpandableListView"))
                .setContent(new Intent(this, ExpandableListViewActivity.class)));

        tabHost.addTab(tabHost.newTabSpec("properties_expandable_list_view")
                .setIndicator(TabHostUtils.newIndicator(this, "PropertiesView"))
                .setContent(new Intent(this, PropertiesListViewActivity.class)));

        tabHost.setCurrentTab(3);
    }
}
