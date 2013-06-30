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

package org.openjst.client.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import org.openjst.client.android.R;
import org.openjst.client.android.activity.generic.AbstractTabActivity;
import org.openjst.client.android.activity.status.StatusApplicationActivity;
import org.openjst.client.android.activity.status.StatusConnectionActivity;
import org.openjst.client.android.activity.status.StatusTrafficActivity;
import org.openjst.client.android.commons.inject.annotations.AndroidLayout;
import org.openjst.client.android.commons.widgets.tabhost.TabHostUtils;

/**
 * Network (received, transferred, count packets per type), GPS status and etc.
 *
 * @author Sergey Grachev
 */
@AndroidLayout(R.layout.activity_status)
public final class StatusActivity extends AbstractTabActivity {

    private static final String TAB_APPLICATION = "Application";
    private static final String TAB_CONNECTION = "Connection";
    private static final String TAB_TRAFFIC = "Traffic";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TabHost tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec(TAB_APPLICATION)
                .setIndicator(TabHostUtils.newIndicator(this, "Application"))
                .setContent(new Intent(this, StatusApplicationActivity.class)));

        tabHost.addTab(tabHost.newTabSpec(TAB_CONNECTION)
                .setIndicator(TabHostUtils.newIndicator(this, "Connection"))
                .setContent(new Intent(this, StatusConnectionActivity.class)));

        tabHost.addTab(tabHost.newTabSpec(TAB_TRAFFIC)
                .setIndicator(TabHostUtils.newIndicator(this, "Traffic"))
                .setContent(new Intent(this, StatusTrafficActivity.class)));
    }
}
