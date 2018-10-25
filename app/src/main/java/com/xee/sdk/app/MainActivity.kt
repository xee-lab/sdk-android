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

package com.xee.sdk.app

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.xee.sdk.api.XeeApi
import com.xee.sdk.api.model.Privacy
import com.xee.sdk.core.auth.*
import com.xee.sdk.core.common.XeeEnv
import com.xee.sdk.core.common.model.Error
import com.xee.sdk.fleet.XeeFleet
import com.xee.sdk.app.adapter.WebServiceAdapter
import com.xee.sdk.app.model.WS
import com.xee.sdk.app.model.WebService
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG: String? = MainActivity::class.java.simpleName
    private lateinit var xeeAuth: XeeAuth
    private lateinit var xeeApi: XeeApi
    private lateinit var xeeFleet: XeeFleet

    /**
     * Please note that all these fields are used for all requests in the sample.
     * So if you fire a request with one of this field which is not filled, the request will failed
     * TODO: please think to fill the fields you need
     */
    companion object DefaultValues {
        var VEHICLE_ID: String = ""
        var FLEET_ID: String = ""
        var TRIP_ID: String = ""
        var FLEET_DRIVER_ID: String = ""
        var FLEET_VEHICLE_ID: String = ""
        var FLEET_LOAN_ID: String = ""
        var AUTHORIZATION_ID: String = ""
    }

    interface DialogCallback {
        fun onInput(input1: String = "", input2: String = "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val webServiceList = Arrays.asList(
                WebService(title = "USER", isHeader = true),
                WebService(WS.GET_USER, "Get user"),
                WebService(WS.UPDATE_USER, "Update user"),
                WebService(WS.GET_USER_VEHICLES, "Get user's vehicles"),
                WebService(WS.GET_AUTHORIZATIONS, "Get user's authorizations"),
                WebService(WS.REVOKE_AUTHORIZATION, "Revoke authorization"),

                WebService(title = "VEHICLES", isHeader = true),
                WebService(WS.GET_VEHICLE, "Get a vehicle"),
                WebService(WS.GET_VEHICLE_DEVICE_DATA, "Get a vehicle's device data"),
                WebService(WS.GET_VEHICLE_PRIVACIES, "Get the vehicle's privacies"),
                WebService(WS.GET_VEHICLE_TRIPS, "Get the vehicle's trips"),
                WebService(WS.GET_VEHICLE_SIGNALS, "Get the vehicle's signals"),
                WebService(WS.GET_VEHICLE_LOCATIONS, "Get the vehicle's locations"),
                WebService(WS.GET_VEHICLE_ACCELEROMETERS, "Get the vehicle's accelerometers"),
                WebService(WS.GET_VEHICLE_STATUS, "Get the vehicle's status"),
                WebService(WS.UPDATE_VEHICLE, "Update vehicle"),
                WebService(WS.ENABLE_PRIVACY, "Enable privacy"),
                WebService(WS.DISABLE_PRIVACY, "Disable privacy"),
                WebService(WS.ASSOCIATE_VEHICLE, "Associate a XeeCONNECT"),
                WebService(WS.DISSOCIATE_VEHICLE, "Dissociate a XeeCONNECT"),

                WebService(title = "TRIPS", isHeader = true),
                WebService(WS.GET_TRIP, "Get trip"),
                WebService(WS.GET_TRIP_SIGNALS, "Get trip signals"),
                WebService(WS.GET_TRIP_LOCATIONS, "Get trip locations"),

                WebService(title = "FLEET", isHeader = true),
                WebService(WS.GET_FLEETS_MINE, "Get my fleets"),
                WebService(WS.GET_FLEET_DRIVERS_ME, "Get driver (me)"),
                WebService(WS.GET_FLEET_DRIVERS, "Get fleet drivers"),
                WebService(WS.GET_FLEET_VEHICLES, "Get fleet vehicles"),
                WebService(WS.GET_FLEET_VEHICLE_LOANS, "Get fleet vehicle loans"),
                WebService(WS.GET_FLEET_DRIVER_LOANS, "Get fleet driver loans"),
                WebService(WS.START_LOAN, "Start a loan"),
                WebService(WS.END_LOAN, "End a loan")
        )

        val scopes = resources.getStringArray(R.array.scopes)
        val oAuth2Client = OAuth2Client.Builder()
                .clientId(getString(R.string.client_id))
                .clientSecret(getString(R.string.client_secret))
                .scopes(scopes.toList())
                .build()

        val xeeEnv = XeeEnv(this, oAuth2Client, getString(R.string.client_env))
        xeeAuth = XeeAuth(xeeEnv, BuildConfig.ENABLE_LOGS)
        xeeApi = XeeApi(xeeEnv, BuildConfig.ENABLE_LOGS)
        xeeFleet = XeeFleet(xeeEnv, BuildConfig.ENABLE_LOGS)

        connectBtn.setOnClickListener {
            xeeAuth.connect(object : AuthenticationCallback {
                override fun onError(error: Throwable) {
                    if (error == AuthenticationActivity.BACK_PRESSED_THROWABLE) {
                        if (BuildConfig.ENABLE_LOGS) Log.w(TAG, getString(R.string.authentication_canceled))
                    } else {
                        if (BuildConfig.ENABLE_LOGS) Log.e(TAG, getString(R.string.authentication_error), error)
                        Snackbar.make(webServicesRecyclerView, R.string.authentication_error, Snackbar.LENGTH_SHORT).showWithColor(R.color.snackBarError)
                    }
                }

                override fun onSuccess() {
                    Snackbar.make(webServicesRecyclerView, R.string.authentication_success, Snackbar.LENGTH_SHORT).showWithColor(R.color.snackBarSuccess)
                }
            })
        }

        registerBtn.setOnClickListener {
            xeeAuth.register(object : RegistrationCallback {
                override fun onCanceled() {
                    Snackbar.make(webServicesRecyclerView, getString(R.string.registration_canceled), Snackbar.LENGTH_SHORT).showWithColor(R.color.snackBarWarning)
                }

                override fun onError(error: Throwable) {
                    Snackbar.make(webServicesRecyclerView, getString(R.string.registration_error), Snackbar.LENGTH_SHORT).showWithColor(R.color.snackBarError)
                }

                override fun onRegistered() {
                    Toast.makeText(this@MainActivity, getString(R.string.registration_success), Toast.LENGTH_SHORT).show()
                }

                override fun onLoggedAfterRegistration() {
                    Snackbar.make(webServicesRecyclerView, R.string.authentication_success, Snackbar.LENGTH_SHORT).showWithColor(R.color.snackBarSuccess)
                }
            })
        }

        webServicesRecyclerView.layoutManager = LinearLayoutManager(this)
        webServicesRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST))
        webServicesRecyclerView.adapter = WebServiceAdapter(webServiceList, {
            onWebServiceClicked(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item!!.itemId
        when (id) {
            R.id.action_disconnect -> disconnect()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Handle which web service is clicked
     * @param ws the webservice clicked
     */
    private fun onWebServiceClicked(ws: WebService) {
        when (ws.ws) {
        //region [USERS]
            WS.GET_USER -> xeeApi.getUser().composeSubAndObs()
                    .subscribe({ user ->
                        showDialogSuccess(ws, user)
                    }, { error -> showDialogError(ws, error) })

            WS.UPDATE_USER -> {
                showCustomDialogInputs(R.layout.dialog_two_inputs, ws.title,
                        R.string.dialog_content_update_user,
                        R.string.dialog_hint_update_user_firstName,
                        R.string.dialog_hint_update_user_lastName,
                        object : DialogCallback {
                            override fun onInput(input1: String, input2: String) {
                                xeeApi.getUser().composeSubAndObs().flatMap { user ->
                                    run {
                                        user.firstName = input1
                                        user.lastName = input2
                                        xeeApi.updateUser(user).composeSubAndObs()
                                    }
                                }.subscribe({ userUpdated -> showDialogSuccess(ws, userUpdated) }, { error -> showDialogError(ws, error) })
                            }
                        })
            }

            WS.GET_USER_VEHICLES -> xeeApi.getUserVehicles().composeSubAndObs()
                    .subscribe({ vehicles -> showDialogSuccess(ws, vehicles) }, { error -> showDialogError(ws, error) })

            WS.GET_AUTHORIZATIONS -> xeeApi.getAuthorizations().composeSubAndObs()
                    .subscribe({ authorizations -> showDialogSuccess(ws, authorizations) }, { error -> showDialogError(ws, error) })

            WS.REVOKE_AUTHORIZATION -> xeeApi.revokeAuthorization(AUTHORIZATION_ID).composeSubAndObs()
                    .subscribe({
                        showDialogSuccess(ws, "Authorization revoked")
                    }) { error ->
                        showDialogError(ws, error)
                    }
        //endregion

        //region [VEHICLES]
            WS.GET_VEHICLE -> xeeApi.getVehicle(VEHICLE_ID).composeSubAndObs()
                    .subscribe({ vehicles -> showDialogSuccess(ws, vehicles) }, { error -> showDialogError(ws, error) })

            WS.GET_VEHICLE_DEVICE_DATA -> xeeApi.getVehicleDeviceData(VEHICLE_ID).composeSubAndObs()
                    .subscribe({ data -> showDialogSuccess(ws, data) }, { error -> showDialogError(ws, error) })

            WS.GET_VEHICLE_PRIVACIES -> xeeApi.getVehiclePrivacies(VEHICLE_ID, Date().diff(-30), Date(), 10).composeSubAndObs()
                    .subscribe({ privacies -> showDialogSuccess(ws, privacies) }, { error -> showDialogError(ws, error) })

            WS.GET_VEHICLE_TRIPS -> xeeApi.getVehicleTrips(VEHICLE_ID).composeSubAndObs()
                    .subscribe({ trips -> showDialogSuccess(ws, trips) }, { error -> showDialogError(ws, error) })

            WS.GET_VEHICLE_SIGNALS -> xeeApi.getVehicleSignals(VEHICLE_ID, Date().diff(-30), Date(), signals = "LockSts,ComputedFuelLevel", limit = 20).composeSubAndObs()
                    .subscribe({ signals -> showDialogSuccess(ws, signals) }, { error -> showDialogError(ws, error) })

            WS.GET_VEHICLE_LOCATIONS -> xeeApi.getVehicleLocations(VEHICLE_ID, Date().diff(-30), Date(), 20).composeSubAndObs()
                    .subscribe({ signals -> showDialogSuccess(ws, signals) }, { error -> showDialogError(ws, error) })

            WS.GET_VEHICLE_ACCELEROMETERS -> xeeApi.getVehicleAccelerometers(VEHICLE_ID).composeSubAndObs()
                    .subscribe({ accelerometers -> showDialogSuccess(ws, accelerometers) }, { error -> showDialogError(ws, error) })

            WS.GET_VEHICLE_STATUS -> xeeApi.getVehicleStatus(VEHICLE_ID).composeSubAndObs()
                    .subscribe({ status -> showDialogSuccess(ws, status) }, { error -> showDialogError(ws, error) })

            WS.UPDATE_VEHICLE -> {
                showCustomDialogInputs(R.layout.dialog_two_inputs, ws.title,
                        R.string.dialog_content_update_vehicle,
                        R.string.dialog_hint_update_vehicle_name,
                        R.string.dialog_hint_update_vehicle_brand,
                        object : DialogCallback {
                            override fun onInput(input1: String, input2: String) {
                                xeeApi.getVehicle(VEHICLE_ID).composeSubAndObs().flatMap { vehicle ->
                                    run {
                                        vehicle.name = input1
                                        vehicle.brand = input2
                                        xeeApi.updateVehicle(vehicle.id, vehicle).composeSubAndObs()
                                    }
                                }.subscribe({ vehicleUpdated -> showDialogSuccess(ws, vehicleUpdated) }, { error -> showDialogError(ws, error) })
                            }
                        })
            }

            WS.ENABLE_PRIVACY -> xeeApi.enablePrivacy(VEHICLE_ID).composeSubAndObs()
                    .subscribe({ privacy -> showDialogSuccess(ws, privacy) }, { error -> showDialogError(ws, error) })

            WS.DISABLE_PRIVACY -> {
                xeeApi.getVehiclePrivacies(VEHICLE_ID).composeSubAndObs().flatMap { privacies ->
                    run {
                        val privacy: Privacy? = privacies[0]
                        xeeApi.disablePrivacy(privacy!!.id).composeSubAndObs()
                    }
                }.subscribe({ privacy -> showDialogSuccess(ws, privacy) }, { error -> showDialogError(ws, error) })
            }

            WS.ASSOCIATE_VEHICLE -> showCustomDialogInputs(R.layout.dialog_two_inputs, ws.title,
                    R.string.dialog_content_associate_xeeconnect,
                    R.string.dialog_hint_device_id,
                    R.string.dialog_hint_device_pin,
                    object : DialogCallback {
                        override fun onInput(input1: String, input2: String) {
                            xeeApi.associateVehicle(input1, input2).composeSubAndObs()
                                    .subscribe({ vehicle -> showDialogSuccess(ws, vehicle) }, { error -> showDialogError(ws, error) })
                        }
                    })

            WS.DISSOCIATE_VEHICLE -> showCustomDialogInputs(R.layout.dialog_two_inputs, ws.title,
                    R.string.dialog_content_dissociate_xeeconnect,
                    R.string.dialog_hint_vehicle_id,
                    dialogCallback = object : DialogCallback {
                        override fun onInput(input1: String, input2: String) {
                            xeeApi.dissociateVehicle(input1).composeSubAndObs()
                                    .subscribe({
                                        showDialogSuccess(ws, "Vehicle dissociated")
                                    }) { error ->
                                        showDialogError(ws, error)
                                    }
                        }
                    })
        //endregion

        //region [TRIPS]
            WS.GET_TRIP -> xeeApi.getTrip(TRIP_ID).composeSubAndObs()
                    .subscribe({ trip -> showDialogSuccess(ws, trip) }, { error -> showDialogError(ws, error) })

            WS.GET_TRIP_SIGNALS -> xeeApi.getTripSignals(TRIP_ID, Date().diff(-30), Date(), limit = 20).composeSubAndObs()
                    .subscribe({ signals -> showDialogSuccess(ws, signals) }, { error -> showDialogError(ws, error) })

            WS.GET_TRIP_LOCATIONS -> xeeApi.getTripLocations(TRIP_ID, Date().diff(-30), Date(), limit = 3).composeSubAndObs()
                    .subscribe({ locations -> showDialogSuccess(ws, locations) }, { error -> showDialogError(ws, error) })


        ////////////////////////////////////////////////////////////////////////////////////////
        //
        // FLEET PART
        //
        ////////////////////////////////////////////////////////////////////////////////////////

            WS.GET_FLEETS_MINE -> xeeFleet.getMyFleets().composeSubAndObs()
                    .subscribe({ fleets -> showDialogSuccess(ws, fleets) }, { error -> showDialogError(ws, error) })

            WS.GET_FLEET_DRIVERS -> xeeFleet.getFleetDrivers(FLEET_ID).composeSubAndObs()
                    .subscribe({ drivers -> showDialogSuccess(ws, drivers) }, { error -> showDialogError(ws, error) })

            WS.GET_FLEET_DRIVERS_ME -> xeeFleet.getDriver(FLEET_ID).composeSubAndObs()
                    .subscribe({ driver -> showDialogSuccess(ws, driver) }, { error -> showDialogError(ws, error) })

            WS.GET_FLEET_VEHICLES -> xeeFleet.getFleetVehicles(FLEET_ID).composeSubAndObs()
                    .subscribe({ vehicles -> showDialogSuccess(ws, vehicles) }, { error -> showDialogError(ws, error) })

            WS.GET_FLEET_VEHICLE_LOANS -> xeeFleet.getVehicleLoans(FLEET_ID, FLEET_VEHICLE_ID).composeSubAndObs()
                    .subscribe({ loans -> showDialogSuccess(ws, loans) }, { error -> showDialogError(ws, error) })

            WS.GET_FLEET_DRIVER_LOANS -> xeeFleet.getDriverLoans(FLEET_ID, FLEET_DRIVER_ID).composeSubAndObs()
                    .subscribe({ loans -> showDialogSuccess(ws, loans) }, { error -> showDialogError(ws, error) })

            WS.START_LOAN -> xeeFleet.startLoan(FLEET_ID, FLEET_VEHICLE_ID, FLEET_DRIVER_ID).composeSubAndObs()
                    .subscribe({ loans -> showDialogSuccess(ws, loans) }, { error -> showDialogError(ws, error) })

            WS.END_LOAN -> xeeFleet.endLoan(FLEET_ID, FLEET_LOAN_ID).composeSubAndObs()
                    .subscribe({
                        showDialogSuccess(ws, "Loan ended")
                    }) { error ->
                        showDialogError(ws, error)
                    }
            else -> {
                showDialogError(ws, Throwable(getString(R.string.webservice_not_handled)))
            }
        }
        //endregion
    }

    /**
     * Disconnect the user (by clearing the associated token)
     */
    private fun disconnect() {
        xeeAuth.disconnect(object : DisconnectCallback {
            override fun onCompleted() {
                Snackbar.make(webServicesRecyclerView, R.string.disconnect_success, Snackbar.LENGTH_SHORT).showWithColor(R.color.snackBarInfo)
            }
        })
    }

    /**
     * Display a dialog with data
     * @param ws the ws which was consumed
     * @param data the data which was returned
     */
    private fun showDialogSuccess(ws: WebService, data: Any) {
        val md = MaterialDialog.Builder(this@MainActivity)
                .title(ws.title)
                .titleColorRes(R.color.dialogTitleSuccess)
                .content(data.prettyJson())
                .positiveText(android.R.string.ok)
                .positiveColorRes(R.color.dialogTitleSuccess)
                .build()

        md.contentView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
        md.show()
    }

    /**
     * Display a dialog with error
     * @param ws the ws which was consumed
     * @param error the error which was returned
     */
    private fun showDialogError(ws: WebService, error: Throwable) {
        showDialogError(ws.title, error)
    }

    private fun showDialogError(title: String, error: Throwable) {
        val errorDialog: MaterialDialog.Builder = MaterialDialog.Builder(this@MainActivity)
                .title(title)
                .titleColorRes(R.color.dialogTitleError)
                .positiveText(android.R.string.ok)
                .positiveColorRes(R.color.dialogTitleError)

        if (error is Error) {
            var text = ""
            if (!error.error.isNullOrEmpty()) {
                text = text.plus("• error: ${error.error?.replace("\n", "")}\n")
            }
            if (!error.errorDescription.isNullOrEmpty()) {
                text = text.plus("• error description: ${error.errorDescription?.replace("\n", "")}\n")
            }
            if (error.errorDetails != null && error.errorDetails!!.isNotEmpty()) {
                text = text.plus("• error details: ${error.errorDetails?.toString()?.replace("\n", "")}\n")
            }
            if (error.code != null) {
                text = text.plus("• error code: ${error.code}")
            }
            errorDialog.content(text)
        } else {
            errorDialog.content(error.toString())
        }
        errorDialog.show()
    }

    private fun showCustomDialogInputs(@LayoutRes customLayout: Int, title: String, @StringRes content: Int = -1, @StringRes input1Hint: Int = -1, @StringRes input2Hint: Int = -1, dialogCallback: DialogCallback) {
        showCustomDialogInputs(customLayout, title, if (content != -1) getString(content) else null, if (input1Hint != -1) getString(input1Hint) else null, if (input2Hint != -1) getString(input2Hint) else null, dialogCallback)
    }

    private fun showCustomDialogInputs(@LayoutRes customLayout: Int, title: String, content: String? = null, input1Hint: String?, input2Hint: String? = null, dialogCallback: DialogCallback) {
        val inputsDialog: MaterialDialog = MaterialDialog.Builder(this@MainActivity)
                .title(title)
                .titleColorRes(R.color.dialogTitleInfo)
                .customView(customLayout, true)
                .positiveText(android.R.string.ok)
                .positiveColorRes(R.color.dialogTitleInfo)
                .negativeColorRes(R.color.dialogTitleInfo)
                .negativeText(android.R.string.cancel)
                .build()

        val contentDialog = inputsDialog.customView?.findViewById<TextView>(R.id.dialogContent)
        if (content.isNullOrEmpty()) {
            contentDialog?.text = getString(R.string.dialog_content)
            contentDialog?.visibility = View.GONE
        } else {
            contentDialog?.text = content
            contentDialog?.visibility = View.VISIBLE
        }

        val input1 = inputsDialog.customView?.findViewById<EditText>(R.id.dialogInput1)
        if (input1Hint != null) {
            input1?.hint = input1Hint
        } else {
            input1?.hint = getString(R.string.dialog_hint_id)
        }

        val input2 = inputsDialog.customView?.findViewById<EditText>(R.id.dialogInput2)
        if (input2Hint != null) {
            input2?.hint = input2Hint
            input2?.visibility = View.VISIBLE
        } else {
            input2?.visibility = View.GONE
        }

        inputsDialog.builder.onPositive { _, _ ->
            dialogCallback.onInput(input1?.text.toString(), input2?.text.toString())
        }

        inputsDialog.show()
    }
}