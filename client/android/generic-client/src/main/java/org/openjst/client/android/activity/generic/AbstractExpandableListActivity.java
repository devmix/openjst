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

package org.openjst.client.android.activity.generic;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import org.openjst.client.android.commons.inject.ActivityInjector;
import org.openjst.client.android.commons.inject.DefaultActivityInjector;
import org.openjst.client.android.commons.inject.Injector;

/**
 * @author Sergey Grachev
 */
public abstract class AbstractExpandableListActivity extends ExpandableListActivity {

    protected final ActivityInjector injector;

    protected AbstractExpandableListActivity() {
        this.injector = (ActivityInjector) onCreateInjector();
    }

    protected Injector onCreateInjector() {
        return new DefaultActivityInjector(this);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injector.apply(this);
        injector.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        injector.enableEvents(this);
    }

    @Override
    protected void onPause() {
        injector.disableEvents(this);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        return injector.onCreateOptionsMenu(menu) || super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return injector.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
