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

import android.os.Bundle;
import org.openjst.client.android.R;
import org.openjst.client.android.activity.generic.AbstractActivity;
import org.openjst.client.android.commons.inject.annotations.Inject;
import org.openjst.client.android.commons.inject.annotations.android.ALayout;
import org.openjst.client.android.commons.inject.annotations.android.AView;
import org.openjst.client.android.commons.widgets.PropertiesView;
import org.openjst.client.android.dao.TrafficDAO;
import org.openjst.client.android.dto.TrafficSummary;
import org.openjst.client.android.utils.LocaleUtils;
import org.openjst.commons.conversion.units.InformationUnits;
import org.openjst.commons.dto.tuples.Pair;
import org.openjst.commons.dto.tuples.Tuples;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Grachev
 */
@ALayout(R.layout.activity_status_traffic)
public class StatusTrafficActivity extends AbstractActivity {

    @AView(R.id.list)
    private PropertiesView pvList;

    @Inject
    private TrafficDAO trafficDAO;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final List<Pair<String, List<PropertiesView.Property>>> properties = new ArrayList<Pair<String, List<PropertiesView.Property>>>();
        addSummary(properties);

        pvList.setPreventCollapse(true);
        pvList.setProperties(properties);
    }

    private void addSummary(final List<Pair<String, List<PropertiesView.Property>>> properties) {
        final List<PropertiesView.Property> list = new ArrayList<PropertiesView.Property>();

        final TrafficSummary summary = trafficDAO.getTrafficSummary();

        list.add(PropertiesView.Property.newHorizontal("RPC In:",
                LocaleUtils.unitCut(summary.getRPCIn(), InformationUnits.BYTE)));
        list.add(PropertiesView.Property.newHorizontal("RPC Out:",
                LocaleUtils.unitCut(summary.getRPCOut(), InformationUnits.BYTE)));
        list.add(PropertiesView.Property.newHorizontal("Date & time synchronization:", "0"));
        list.add(PropertiesView.Property.newHorizontal("Ping:", "0"));
        list.add(PropertiesView.Property.newHorizontal("Authorization:", "0"));
        list.add(PropertiesView.Property.newHorizontal("Presence:", "0"));
        list.add(PropertiesView.Property.newHorizontal("Total:", LocaleUtils.unitCut(summary.getTotal(), InformationUnits.BYTE)));

        properties.add(Tuples.newPair("Summary", list));
    }
}
