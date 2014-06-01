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
import org.openjst.client.android.R;
import org.openjst.client.android.activity.generic.AbstractActivity;
import org.openjst.client.android.commons.GlobalContext;
import org.openjst.client.android.commons.inject.annotations.android.ALayout;
import org.openjst.client.android.commons.services.LookupServiceFuture;
import org.openjst.client.android.service.ServerConnectionService;

/**
 * @author Sergey Grachev
 */
@ALayout(R.layout.activity_startup)
public final class StartupActivity extends AbstractActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GlobalContext.lookupService(ServerConnectionService.class, new LookupServiceFuture<ServerConnectionService>() {
            public void onBind(final ServerConnectionService service) {
                if (service.isConnected()) {
                    startActivity(new Intent(GlobalContext.application(), ScheduleTodayActivity.class));
                } else {
                    startActivity(new Intent(GlobalContext.application(), LoginActivity.class));
                }
                finish();
            }
        });
    }
}
