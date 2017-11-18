/*
 * Copyright 2017 Xee
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xee.sdk.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xee.sdk.api.XeeApi;
import com.xee.sdk.api.model.Accelerometer;
import com.xee.sdk.api.model.Location;
import com.xee.sdk.api.model.Status;
import com.xee.sdk.api.model.Trip;
import com.xee.sdk.api.model.Vehicle;
import com.xee.sdk.core.auth.AuthenticationActivity;
import com.xee.sdk.core.auth.AuthenticationCallback;
import com.xee.sdk.core.auth.DisconnectCallback;
import com.xee.sdk.core.auth.OAuth2Client;
import com.xee.sdk.core.auth.RegistrationCallback;
import com.xee.sdk.core.auth.SignInButton;
import com.xee.sdk.core.auth.SignUpButton;
import com.xee.sdk.api.model.Privacy;
import com.xee.sdk.api.model.Signal;
import com.xee.sdk.api.model.User;
import com.xee.sdk.core.common.XeeEnv;
import com.xee.sdk.core.auth.XeeAuth;
import com.xee.sdk.core.common.model.Error;
import com.xee.sdk.app.adapter.WebServiceAdapter;
import com.xee.sdk.app.model.WS;
import com.xee.sdk.app.model.WebService;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Java class
 */

public class MainActivityJava extends AppCompatActivity {
    private static final String TAG = MainActivityJava.class.getSimpleName();
    private XeeApi xeeApi;
    private XeeAuth xeeAuth;

    private RecyclerView webServicesRecyclerView;

    /**
     * Please note that all these fields are used for all requests in the sample.
     * So if you fire a request with one of this field which is not filled, the request will failed
     * TODO: please think to fill the fields you need
     */
    private String VEHICLE_ID = "";
    private String TRIP_ID = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        webServicesRecyclerView = findViewById(R.id.webServicesRecyclerView);

        setSupportActionBar(toolbar);

        List<WebService> webServiceList = Arrays.asList(
                new WebService("USER", true),
                new WebService(WS.GET_USER, "Get user"),
                new WebService(WS.UPDATE_USER, "Update user"),
                new WebService(WS.GET_USER_VEHICLES, "Get user's vehicles"),

                new WebService("VEHICLES", true),
                new WebService(WS.GET_VEHICLE, "Get a vehicle"),
                new WebService(WS.GET_VEHICLE_PRIVACIES, "Get the vehicle's privacies"),
                new WebService(WS.GET_VEHICLE_TRIPS, "Get the vehicle's trips"),
                new WebService(WS.GET_VEHICLE_SIGNALS, "Get the vehicle's signals"),
                new WebService(WS.GET_VEHICLE_LOCATIONS, "Get the vehicle's locations"),
                new WebService(WS.GET_VEHICLE_ACCELEROMETERS, "Get the vehicle's accelerometers"),
                new WebService(WS.GET_VEHICLE_STATUS, "Get the vehicle's status"),
                new WebService(WS.UPDATE_VEHICLE, "Update vehicle"),

                new WebService("TRIPS", true),
                new WebService(WS.GET_TRIP, "Get trip"),
                new WebService(WS.GET_TRIP_SIGNALS, "Get trip signals"),
                new WebService(WS.GET_TRIP_LOCATIONS, "Get trip locations")
        );

        final OAuth2Client oAuthClient = new OAuth2Client.Builder()
                                      .clientId(getString(R.string.client_id))
                                      .clientSecret(getString(R.string.client_secret))
                                      .scopes(Arrays.asList(getResources().getStringArray(R.array.scopes)))
                                      .build();

        XeeEnv xeeEnv = new XeeEnv(this, oAuthClient, getString(R.string.client_env));
        xeeApi = new XeeApi(xeeEnv, BuildConfig.ENABLE_LOGS);
        xeeAuth = new XeeAuth(xeeEnv, BuildConfig.ENABLE_LOGS);

        SignInButton signInButton = findViewById(R.id.connectBtn);
        signInButton.setOnSignInClickResult(xeeAuth, new AuthenticationCallback() {
            @Override
            public void onError(@NotNull Throwable error) {
                if (error == AuthenticationActivity.BACK_PRESSED_THROWABLE) {
                    if (BuildConfig.ENABLE_LOGS) Log.w(TAG, getString(R.string.authentication_canceled));
                } else {
                    if (BuildConfig.ENABLE_LOGS) Log.e(TAG, getString(R.string.authentication_error), error);
                }
            }

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivityJava.this, R.string.authentication_success, Toast.LENGTH_SHORT).show();
            }
        });

        SignUpButton signUpButton = findViewById(R.id.registerBtn);
        signUpButton.setOnSignUpClickResult(xeeAuth, new RegistrationCallback() {
            @Override
            public void onCanceled() {
                if (BuildConfig.ENABLE_LOGS) Log.w(TAG, getString(R.string.registration_canceled));
                Snackbar.make(webServicesRecyclerView, getString(R.string.registration_canceled), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NotNull Throwable error) {
                if (BuildConfig.ENABLE_LOGS) Log.e(TAG, getString(R.string.registration_error), error);
                Snackbar.make(webServicesRecyclerView, getString(R.string.registration_error), Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onRegistered() {
                if (BuildConfig.ENABLE_LOGS) Log.i(TAG, getString(R.string.registration_success));
                Toast.makeText(MainActivityJava.this, getString(R.string.registration_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoggedAfterRegistration() {
                if (BuildConfig.ENABLE_LOGS) Log.w(TAG, getString(R.string.authentication_success));
                Snackbar.make(webServicesRecyclerView, getString(R.string.authentication_success), Snackbar.LENGTH_SHORT).show();
            }
        });

        webServicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        webServicesRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        webServicesRecyclerView.setAdapter(new WebServiceAdapter(webServiceList, new Function1<WebService, Unit>() {
            @Override
            public Unit invoke(WebService webService) {
                onWebServiceClicked(webService);
                return null;
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_disconnect:
                disconnect();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onWebServiceClicked(final WebService ws) {
        switch (ws.getWs()) {
            case GET_USER:
                xeeApi.getUser()
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Consumer<User>() {
                          @Override
                          public void accept(User user) throws Exception {
                              showDialogSuccess(ws, user);
                          }
                      }, new Consumer<Throwable>() {
                          @Override
                          public void accept(Throwable throwable) throws Exception {
                              showDialogError(ws, throwable);
                          }
                      });
                break;
            case GET_USER_VEHICLES:
                xeeApi.getUserVehicles()
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Consumer<List<Vehicle>>() {
                          @Override
                          public void accept(List<Vehicle> vehicles) throws Exception {
                              showDialogSuccess(ws, vehicles);
                          }
                      }, new Consumer<Throwable>() {
                          @Override
                          public void accept(Throwable throwable) throws Exception {
                              showDialogError(ws, throwable);
                          }
                      });
                break;
            case GET_VEHICLE:
                xeeApi.getVehicle(VEHICLE_ID)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Consumer<Vehicle>() {
                          @Override
                          public void accept(Vehicle vehicle) throws Exception {
                              showDialogSuccess(ws, vehicle);
                          }
                      }, new Consumer<Throwable>() {
                          @Override
                          public void accept(Throwable throwable) throws Exception {
                              showDialogError(ws, throwable);
                          }
                      });
                break;
            case GET_VEHICLE_PRIVACIES:
                xeeApi.getVehiclePrivacies(VEHICLE_ID, diff(new Date(), -2), new Date(), 10)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Consumer<List<Privacy>>() {
                          @Override
                          public void accept(List<Privacy> privacies) throws Exception {
                              showDialogSuccess(ws, privacies);
                          }
                      }, new Consumer<Throwable>() {
                          @Override
                          public void accept(Throwable throwable) throws Exception {
                              showDialogError(ws, throwable);
                          }
                      });
                break;
            case GET_VEHICLE_TRIPS:
                xeeApi.getVehicleTrips(VEHICLE_ID)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Consumer<List<Trip>>() {
                          @Override
                          public void accept(List<Trip> trips) throws Exception {
                              showDialogSuccess(ws, trips);
                          }
                      }, new Consumer<Throwable>() {
                          @Override
                          public void accept(Throwable throwable) throws Exception {
                              showDialogError(ws, throwable);
                          }
                      });
                break;
            case GET_VEHICLE_SIGNALS:
                Map<String, Object> optionalParametersSignals = new ArrayMap<>();
                optionalParametersSignals.put("signals", "LockSts");
                optionalParametersSignals.put("from", XeeApi.DATE_FORMATTER.format(diff(new Date(), -2)));
                optionalParametersSignals.put("to", XeeApi.DATE_FORMATTER.format(new Date()));
                xeeApi.getVehicleSignals(VEHICLE_ID, optionalParametersSignals)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Consumer<List<Signal>>() {
                          @Override
                          public void accept(List<Signal> signals) throws Exception {
                              showDialogSuccess(ws, signals);
                          }
                      }, new Consumer<Throwable>() {
                          @Override
                          public void accept(Throwable throwable) throws Exception {
                              showDialogError(ws, throwable);
                          }
                      });
                break;
            case GET_VEHICLE_LOCATIONS:
                xeeApi.getVehicleLocations(VEHICLE_ID, diff(new Date(), -30), new Date(), 20)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Consumer<List<Location>>() {
                          @Override
                          public void accept(List<Location> locations) throws Exception {
                              showDialogSuccess(ws, locations);
                          }
                      }, new Consumer<Throwable>() {
                          @Override
                          public void accept(Throwable throwable) throws Exception {
                              showDialogError(ws, throwable);
                          }
                      });
                break;
            case GET_VEHICLE_ACCELEROMETERS:
                xeeApi.getVehicleAccelerometers(VEHICLE_ID)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Consumer<List<Accelerometer>>() {
                          @Override
                          public void accept(List<Accelerometer> accelerometers) throws Exception {
                              showDialogSuccess(ws, accelerometers);
                          }
                      }, new Consumer<Throwable>() {
                          @Override
                          public void accept(Throwable throwable) throws Exception {
                              showDialogError(ws, throwable);
                          }
                      });
                break;
            case GET_VEHICLE_STATUS:
                xeeApi.getVehicleStatus(VEHICLE_ID)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Consumer<Status>() {
                          @Override
                          public void accept(Status status) throws Exception {
                              showDialogSuccess(ws, status);
                          }
                      }, new Consumer<Throwable>() {
                          @Override
                          public void accept(Throwable throwable) throws Exception {
                              showDialogError(ws, throwable);
                          }
                      });
                break;
            case GET_TRIP:
                xeeApi.getTrip(TRIP_ID)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Consumer<Trip>() {
                          @Override
                          public void accept(Trip trip) throws Exception {
                              showDialogSuccess(ws, trip);
                          }
                      }, new Consumer<Throwable>() {
                          @Override
                          public void accept(Throwable throwable) throws Exception {
                              showDialogError(ws, throwable);
                          }
                      });
                break;
            case GET_TRIP_SIGNALS:
                xeeApi.getTripSignals(TRIP_ID, diff(new Date(), -30), new Date(), null, 20)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Consumer<List<Signal>>() {
                          @Override
                          public void accept(List<Signal> signals) throws Exception {
                              showDialogSuccess(ws, signals);
                          }
                      }, new Consumer<Throwable>() {
                          @Override
                          public void accept(Throwable throwable) throws Exception {
                              showDialogError(ws, throwable);
                          }
                      });
                break;
            case GET_TRIP_LOCATIONS:
                xeeApi.getTripLocations(TRIP_ID, diff(new Date(), -30), new Date(), 20)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(new Consumer<List<Location>>() {
                          @Override
                          public void accept(List<Location> locations) throws Exception {
                              showDialogSuccess(ws, locations);
                          }
                      }, new Consumer<Throwable>() {
                          @Override
                          public void accept(Throwable throwable) throws Exception {
                              showDialogError(ws, throwable);
                          }
                      });
                break;
        }
    }

    private void showDialogSuccess(WebService ws, Object data) {
        new MaterialDialog.Builder(MainActivityJava.this)
                .title(ws.getTitle())
                .titleColorRes(R.color.dialogTitleSuccess)
                .content(prettyJson(data))
                .positiveText(android.R.string.ok)
                .positiveColorRes(R.color.dialogTitleSuccess)
                .show();
    }

    private void showDialogError(WebService ws, Object error) {
        MaterialDialog.Builder errorDialog = new MaterialDialog.Builder(MainActivityJava.this);
        errorDialog.title(ws.getTitle());
        errorDialog.titleColorRes(R.color.dialogTitleError);
        errorDialog.positiveText(android.R.string.ok);
        errorDialog.positiveColorRes(R.color.dialogTitleError);
        if (error instanceof Error) {
            CharSequence text = "• error: " + ((Error) error).getError() + "\n• error description: " + ((Error) error).getErrorDescription() + "\n• error details: " + ((Error) error).getErrorDetails() + "\n• error code: " + ((Error) error).getCode();
            errorDialog.content(text);
        } else {
            errorDialog.content(error.toString());
        }
        errorDialog.show();
    }

    private void disconnect() {
        xeeAuth.disconnect(new DisconnectCallback() {
            @Override
            public void onCompleted() {
                Snackbar.make(webServicesRecyclerView, R.string.disconnect_success, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private String prettyJson(Object data) {
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
        return gson.toJson(data);
    }

    private Date diff(Date date, Integer days){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }
}