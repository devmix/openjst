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

import android.app.Activity;
import android.os.Bundle;
import org.openjst.client.android.commons.widgets.PropertiesView;
import org.openjst.commons.dto.tuples.Pair;
import org.openjst.commons.dto.tuples.Tuples;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Grachev
 */
public class PropertiesListViewActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.properties_list_view_activity);

        final List<Pair<String, List<PropertiesView.Property>>> properties = new ArrayList<Pair<String, List<PropertiesView.Property>>>();
        properties.add(createProperties("Group 1"));
        properties.add(createProperties("Group 2"));
        properties.add(createProperties("Group 3"));
        properties.add(createProperties("Group 4"));
        properties.add(createProperties("Group 5"));
        properties.add(createProperties("Group 6"));

        final PropertiesView view = (PropertiesView) findViewById(R.id.list);
        view.setPreventCollapse(true);
        view.setProperties(properties);
    }

    private Pair<String, List<PropertiesView.Property>> createProperties(final String name) {
        final List<PropertiesView.Property> list = new ArrayList<PropertiesView.Property>();
        list.add(PropertiesView.Property.newHorizontal("Property 1", "value 1"));
        list.add(PropertiesView.Property.newHorizontal("Property 2", "value 2"));
        list.add(PropertiesView.Property.newHorizontal("Property 3", "value 3"));
        list.add(PropertiesView.Property.newVertical("Property 4",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. \n" +
                        "Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan. \n" +
                        "Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna. \n" +
                        "Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus. \n" +
                        "Vestibulum commodo. Ut rhoncus gravida arcu. "));
        list.add(PropertiesView.Property.newHorizontal("Property 5", "value 5"));
        list.add(PropertiesView.Property.newHorizontal("Property 6", "value 6"));
        return Tuples.newPair(name, list);
    }
}
