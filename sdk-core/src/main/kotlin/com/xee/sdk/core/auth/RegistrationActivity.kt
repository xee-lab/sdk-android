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

package com.xee.sdk.core.auth

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.xee.sdk.core.auth.endpoint.AuthEndpoint
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*

/**
 * Registration [Activity] to register the user
 * @author Julien Cholin
 * @since 4.0.0
 */
class RegistrationActivity : Activity() {

    private val TAG: String = RegistrationActivity::class.java.simpleName

    interface CodeCallback {

        /**
         * Triggered when user cancels the registration by pressing back key
         */
        fun onCanceled()

        /**
         * Triggered when an error occurs in the registration process
         *
         * @param error the error that has occurred
         */
        fun onError(error: Throwable)

        /**
         * Triggered when the authorization code is obtained
         *
         * @param code the authorization code to use to register
         */
        fun onRegistered()

        /**
         * Triggered when the registration process has been successful and user has logged in
         */
        fun onLoggedAfterRegistration(code: String, redirectUri:String?)
    }

    /**
     * THe webview used to register user
     */
    private lateinit var registerWebView: WebView

    /**
     * Register url (used for unit test too)
     */
    private var registrationUrl: String? = null

    /**
     * The OAuth2Client
     */
    private lateinit var client:OAuth2Client

    /**
     * Special oauth WebView client, checks the urls in the WebView to find out when the access code is found.
     */
    private val mmRegisterWebViewClient = object : WebViewClient() {

        @TargetApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            super.onReceivedError(view, request, error)
            callback!!.onError(Exception("HTTP error received from the registration server {url : " + request.url + ", desc:" + error.description + "}"))
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            callback!!.onError(Exception("HTTP error received from the registration server {url : $failingUrl, desc:$description}"))
            this@RegistrationActivity.finish()
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            return doesUrlContainsCode(view, url)
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return doesUrlContainsCode(view, request.url.toString())
        }

        /**
         * Check if url contains "code" field
         * @param view the webview holding the url
         * @param url the url to check
         * @return true if url contains "code" field, otherwise false
         */
        private fun doesUrlContainsCode(view: WebView, url: String): Boolean {
            // Check if there is an access denied error
            val realUrl = Uri.parse(url)
            if (!realUrl.isHierarchical) {
                if (url.startsWith("mailto:")) {
                    val i = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                    startActivity(i)
                    return true
                }
            }

            if (url.endsWith(".pdf")) {
                val browserIntent = Intent(Intent.ACTION_VIEW, realUrl)
                startActivity(browserIntent)
                return true
            }

            val errorCode = realUrl.getQueryParameter(ERROR_CODE)
            val accessCode = realUrl.getQueryParameter(ACCESS_CODE)
            if (errorCode != null && !errorCode.isEmpty()) {
                callback!!.onError(Exception(errorCode))
                this@RegistrationActivity.finish()
                // Cancel loading
                return true
            } else if (accessCode != null && !accessCode.isEmpty()) {
                // clear the cache
                view.clearCache(true)
                // Retrieve the access code
                // Get the access token from the access code
                callback!!.onLoggedAfterRegistration(accessCode, client.redirectUri)
                // Close the activity when we got the access code
                finish()
                // Cancel loading
                return true
            } else {
                println("should not happen")
                callback!!.onRegistered()
            }
            // Check if there is the access code in the url
            // Load the page
            return false
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        client = intent.getParcelableExtra<OAuth2Client>(EXTRA_CLIENT)
        val host = intent.getStringExtra(EXTRA_HOST)

        // Hide action bar if visible
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val actionBar = actionBar
        if (actionBar != null && actionBar.isShowing) {
            actionBar.hide()
        }

        // Create the WebView with the custom client
        registerWebView = WebView(this)
        registerWebView.webViewClient = mmRegisterWebViewClient
        registerWebView.settings.javaScriptEnabled = true
        registerWebView.clearCache(true)
        disableRememberPasswordDialog(registerWebView)

        // Show the WebView on screen
        setContentView(registerWebView)

        // Load the authentication URL
        val callbackURI: String
        try {
            callbackURI = URLEncoder.encode(client.redirectUri, "utf-8")
        } catch (e: UnsupportedEncodingException) {
            // Failed to encode the callback uri
            callback!!.onError(e)
            finish()
            return
        }

        // Build the url
        registrationUrl = String.format(Locale.FRANCE, XeeAuth.ROUTE_BASE, host)
                .plus(AuthEndpoint.Routes.REGISTER)
                .plus("?").plus(AuthEndpoint.Parameters.CLIENT_ID).plus("=").plus(client.clientId)

        registrationUrl = registrationUrl.plus("&scope=").plus(client.scopes.joinToString("+"))

        if (callbackURI.isNotEmpty()) {
            registrationUrl = registrationUrl.plus("&" + AuthEndpoint.Parameters.REDIRECT_URL + "=" + callbackURI)
        }

        if (XeeAuth.enableOAuthLog) Log.d(TAG, "REGISTRATION URL : $registrationUrl")

        // Load the registration url
        registerWebView.loadUrl(registrationUrl)
    }

    /**
     * Disable the "remember password dialog", only useful on KITKAT or JELLY BEAN
     *
     * @param webView the WebView we want to disable the dialog
     */
    @Suppress("DEPRECATION")
    @SuppressLint("Deprecation")
    private fun disableRememberPasswordDialog(webView: WebView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            webView.settings.savePassword = false
        }
    }

    override fun onBackPressed() {
        if (registerWebView.canGoBack()) {
            registerWebView.goBack()
        } else {
            callback!!.onCanceled()
            finish()
        }
    }

    companion object {

        /**
         * The extra intent for client
         */
        private const val EXTRA_CLIENT = "extra_client"
        /**
         * The extra intent for host
         */
        private const val EXTRA_HOST = "extra_host"
        /**
         * The callback for intercepting code when user try to connect
         */
        var callback: CodeCallback? = null

        /**
         * Access denied urn
         */
        private val ERROR_CODE = "error"
        /**
         * Code parameter in url
         */
        private val ACCESS_CODE = "code"

        /**
         * The registration activity intent to register the user
         * @param context the context
         * @param client  the [OAuth2Client]
         * @param host    the host to connect to
         * @return the [Intent]
         */
        fun intent(context: Context, client: OAuth2Client, host: String): Intent {
            val intent = Intent(context, RegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(EXTRA_CLIENT, client)
            intent.putExtra(EXTRA_HOST, host)
            return intent
        }
    }
}