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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import org.openjst.client.android.Constants;
import org.openjst.client.android.R;
import org.openjst.client.android.activity.generic.AbstractActivity;
import org.openjst.client.android.commons.events.annotations.OnConnectionEvent;
import org.openjst.client.android.commons.events.types.ConnectionEvent;
import org.openjst.client.android.commons.inject.annotations.*;
import org.openjst.client.android.commons.managers.SettingsManager;
import org.openjst.client.android.dao.LogsDAO;
import org.openjst.client.android.managers.RPCManager;
import org.openjst.client.android.service.ServerConnectionService;

/**
 * @author Sergey Grachev
 */
@AndroidMenu(R.menu.menu_before_login)
@AndroidLayout(R.layout.activity_login)
public final class LoginActivity extends AbstractActivity {

    public static final String ATTR_AFTER_LOGOUT = "afterLogout";

    @AndroidView(R.id.btn_login)
    private Button btnLogin;

    @AndroidView(R.id.ed_client_id)
    private EditText edClientId;

    @AndroidView(R.id.ed_secret_key)
    private EditText edSecretKey;

    @AndroidView(R.id.btn_register)
    private Button btnRegister;

    @AndroidView(R.id.cb_remember_me)
    private CheckBox cbRememberMe;

    @JSTInject
    private SettingsManager settingsManager;

    @JSTInject
    private RPCManager rpcManager;

    @JSTInject
    private LogsDAO logsDAO;

    private ProgressDialog progress;
    private ServerConnectionService connection;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                login();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                startActivity(new Intent(LoginActivity.this, RegisterClientActivity.class));
            }
        });

        cbRememberMe.setChecked(Boolean.TRUE.equals(settingsManager.getBoolean(Constants.Settings.LOGIN_REMEMBER)));
        edClientId.setText(settingsManager.getString(Constants.Settings.LOGIN_CLIENT_ID));
        edSecretKey.setText(settingsManager.getString(Constants.Settings.LOGIN_CLIENT_SECRET_KEY));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // auto login
        if (cbRememberMe.isChecked() && !getIntent().getBooleanExtra(ATTR_AFTER_LOGOUT, false)) {
            login();
        }
    }

    @AndroidOnMenuItemSelected(R.id.menu_settings)
    private boolean onMenuSettings(final MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
    }

    @AndroidOnMenuItemSelected(R.id.menu_exit)
    private boolean onMenuExit(final MenuItem item) {
        stopService(new Intent(this, ServerConnectionService.class));
        finish();
        System.runFinalizersOnExit(true);
        System.exit(0);
        return true;
    }

    @AndroidService(ServerConnectionService.class)
    private void onBindServerConnectionService(final ServerConnectionService service) {
        connection = service;
    }

    @OnConnectionEvent
    private void onConnectionEvent(final ConnectionEvent event) {
        if (event.isSuccess()) {
//            startActivity(new Intent(this, StatusActivity.class));
            startActivity(new Intent(this, ScheduleTodayActivity.class));
            finish();
        }

        if (progress != null) {
            progress.dismiss();
            progress = null;
        }
    }

    // todo
//    @OnGPSEvent(interval = TimeUnits.MILLISECONDS_IN_MINUTE)
//    @OnMessageEvent
//    @OnJobEvent({JobEvent.Type.CREATE, JobEvent.Type.DELETE, JobEvent.Type.UPDATE})
//    private void onEvent(final Event event) {
//
//    }

    private void login() {
        if (edClientId.getText().length() == 0) {
            edClientId.setError(getString(R.string.error_empty_login));
            return;
        }

        if (cbRememberMe.isChecked()) {
            settingsManager.edit()
                    .putBoolean(Constants.Settings.LOGIN_REMEMBER, true)
                    .putString(Constants.Settings.LOGIN_CLIENT_ID, edClientId.getText().toString())
                    .putString(Constants.Settings.LOGIN_CLIENT_SECRET_KEY, edSecretKey.getText().toString())
                    .commit();
        } else {
            settingsManager.edit()
                    .putBoolean(Constants.Settings.LOGIN_REMEMBER, false)
                    .putString(Constants.Settings.LOGIN_CLIENT_ID, null)
                    .putString(Constants.Settings.LOGIN_CLIENT_SECRET_KEY, null)
                    .commit();
        }

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.connecting));
        progress.setIndeterminate(true);
        progress.setCancelable(true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(final DialogInterface dialog) {
                connection.disconnect();
            }
        });
        progress.show();

        if (connection == null || !connection.reconnect()) {
            Toast.makeText(LoginActivity.this, "fail to connect", Toast.LENGTH_SHORT).show();
            progress.hide();
        }
    }
}
