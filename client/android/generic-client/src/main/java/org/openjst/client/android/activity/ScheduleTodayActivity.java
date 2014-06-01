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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import org.openjst.client.android.R;
import org.openjst.client.android.activity.generic.AbstractActivity;
import org.openjst.client.android.commons.GlobalContext;
import org.openjst.client.android.commons.inject.annotations.android.ALayout;
import org.openjst.client.android.commons.inject.annotations.android.AMenu;
import org.openjst.client.android.commons.inject.annotations.android.AOnMenuItemSelected;
import org.openjst.client.android.commons.inject.annotations.android.AView;
import org.openjst.client.android.commons.services.LookupServiceFuture;
import org.openjst.client.android.service.ServerConnectionService;

/**
 * @author Sergey Grachev
 */
@AMenu(R.menu.menu_after_login)
@ALayout(R.layout.activity_schedule_today)
public final class ScheduleTodayActivity extends AbstractActivity {

    @AView(R.id.btn_reconnect)
    private Button btnReconnect;

    @AView(R.id.btn_gc)
    private Button btnGC;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnReconnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                GlobalContext.lookupService(ServerConnectionService.class, new LookupServiceFuture<ServerConnectionService>() {
                    public void onBind(final ServerConnectionService service) {
                        service.reconnect();
                    }
                });
            }
        });

        btnGC.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                System.gc();
            }
        });
    }

    @AOnMenuItemSelected(R.id.menu_status)
    private boolean onMenuStatus(final MenuItem item) {
        startActivity(new Intent(this, StatusActivity.class));
        return true;
    }

    @AOnMenuItemSelected(R.id.menu_logout)
    private boolean onMenuLogout(final MenuItem item) {
        GlobalContext.lookupService(ServerConnectionService.class, new LookupServiceFuture<ServerConnectionService>() {
            public void onBind(final ServerConnectionService service) {
                startActivity(new Intent(GlobalContext.application(), LoginActivity.class)
                        .putExtra(LoginActivity.ATTR_AFTER_LOGOUT, true));
                service.disconnect();
                finish();
            }
        });
        return true;
    }
}
