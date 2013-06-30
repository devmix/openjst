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

package org.openjst.client.android.commons.widgets.progressbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

/**
 * @author Sergey Grachev
 */
public abstract class OperationProgressIndicator<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    protected final Context context;
    protected final String message;
    protected ProgressDialog dialog;

    protected OperationProgressIndicator(final Context context, final String message) {
        this.context = context;
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(final DialogInterface dialog) {
                onProgressCancel();
            }
        });
        dialog.show();
    }

    @Override
    protected void onPostExecute(final Result result) {
        dialog.dismiss();
        super.onPostExecute(result);
    }

    protected void onProgressCancel() {
        // override
    }
}
