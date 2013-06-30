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

import android.os.Bundle;
import android.widget.EditText;
import org.openjst.client.android.Constants;
import org.openjst.client.android.R;
import org.openjst.client.android.activity.generic.AbstractActivity;
import org.openjst.client.android.commons.inject.annotations.AndroidLayout;
import org.openjst.client.android.commons.inject.annotations.AndroidView;
import org.openjst.client.android.commons.inject.annotations.JSTInject;
import org.openjst.client.android.commons.managers.SettingsManager;

/**
 * @author Sergey Grachev
 */
@SuppressWarnings("UnusedDeclaration")
@AndroidLayout(R.layout.activity_register_client)
public final class RegisterClientActivity extends AbstractActivity {

    @AndroidView(R.id.ed_account)
    private EditText edAccount;

    @JSTInject
    private SettingsManager settings;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        edAccount.setText(settings.getString(Constants.Settings.LOGIN_CLIENT_ACCOUNT));
    }
}
