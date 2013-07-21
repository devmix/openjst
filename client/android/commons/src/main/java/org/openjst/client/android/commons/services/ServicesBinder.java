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

package org.openjst.client.android.commons.services;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import org.openjst.client.android.commons.ApplicationContext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Sergey Grachev
 */
public final class ServicesBinder implements ServiceConnection {

    private final BlockingQueue<Class<? extends Service>> bindServicesQueue = new LinkedBlockingQueue<Class<? extends Service>>();
    private final Map<Class, List<LookupServiceFuture>> bindFutures = new HashMap<Class, List<LookupServiceFuture>>();

    private AsyncTask<Void, Void, Void> bindServicesTask;

    @SuppressWarnings("unchecked")
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        final Service serviceInstance = (Service) ((LocalServiceBinder) service).getService();
        try {
            final Class serviceClass = Class.forName(name.getClassName());
            synchronized (bindFutures) {
                final List<LookupServiceFuture> list = bindFutures.get(serviceClass);
                if (list != null) {
                    for (final LookupServiceFuture future : list) {
                        future.onBind(serviceInstance);
                    }
                }
                bindFutures.remove(serviceClass);
            }
            ApplicationContext.addAndroidService(serviceClass, serviceInstance);
        } catch (ClassNotFoundException e) {
            Log.e("ServicesBinder", "onServiceConnected", e);
        }
    }

    public void onServiceDisconnected(final ComponentName name) {
        try {
            final Class serviceClass = Class.forName(name.getClassName());
            ApplicationContext.removeAndroidService(serviceClass);
        } catch (ClassNotFoundException e) {
            Log.e("ServicesBinder", "onServiceConnected", e);
        }
    }

    public void bind(final Class<? extends Service> serviceClass, final LookupServiceFuture<? extends Service> future) {
        checkTask();
        addBindFutures(serviceClass, future);
    }

    private void addBindFutures(final Class<? extends Service> serviceClass, final LookupServiceFuture future) {
        synchronized (bindFutures) {
            bindServicesQueue.add(serviceClass);

            List<LookupServiceFuture> list = bindFutures.get(serviceClass);
            if (list == null) {
                bindFutures.put(serviceClass, list = new LinkedList<LookupServiceFuture>());
            }

            list.add(future);
        }
    }

    private void checkTask() {
        if (bindServicesTask != null) {
            return;
        }

        bindServicesTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                while (!Thread.currentThread().isInterrupted() && Thread.currentThread().isAlive()) {
                    try {
                        final Class<? extends Service> serviceClass = bindServicesQueue.take();
                        final Application application = ApplicationContext.getApplication();
                        final Intent intent = new Intent(application, serviceClass);
                        application.startService(intent);
                        application.bindService(intent, ServicesBinder.this, Context.BIND_AUTO_CREATE);
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e) {
                        Log.e("ServicesBinder", "Can't bind service", e);
                    }
                }
                return null;
            }
        };

        bindServicesTask.execute();
    }
}

