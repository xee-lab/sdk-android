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
 * Authentication [Activity] to authenticate user with OAuth2
 * @author Julien Cholin
 * @since 4.0.0
 */
class AuthenticationActivity : Activity() {

    val TAG: String = AuthenticationActivity::class.java.simpleName

    interface CodeCallback {
        /**
         * Triggered when an error occurs in the authentication process

         * @param error the error that has occurred
         */
        fun onError(error: Throwable)

        /**
         * Triggered when the authorization code is obtained
         */
        fun onSuccess(code: String, redirectUri: String?)
    }

    /**
     * THe webview used to connect user
     */
    private lateinit var authenticationWebView: WebView

    /**
     * Authentication url (used for unit test too)
     */
    private var authenticationUrl: String? = null

    /**
     * The OAuth2Client
     */
    private lateinit var client:OAuth2Client

    /**
     * Special oauth WebView client, checks the urls in the WebView to find out when the access code is found.
     */
    private val mmAuthenticationWebViewClient = object : WebViewClient() {

        @TargetApi(Build.VERSION_CODES.M)
        override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
            super.onReceivedError(view, request, error)
            callback!!.onError(Exception("HTTP error received from the authentication server {url : " + request.url + ", desc:" + error.description + "}"))
        }

        override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
            callback!!.onError(Exception("HTTP error received from the authentication server {url : $failingUrl, desc:$description}"))
            this@AuthenticationActivity.finish()
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
         * *
         * @param url the url to check
         * *
         * @return true if url contains "code" field, otherwise false
         */
        private fun doesUrlContainsCode(view: WebView, url: String): Boolean {
            // Check if there is an access denied error
            val realUrl = Uri.parse(url)
            val errorCode = realUrl.getQueryParameter(ERROR_CODE)
            val accessCode = realUrl.getQueryParameter(ACCESS_CODE)
            if (errorCode != null && !errorCode.isEmpty()) {
                if (errorCode == "access_denied") {
                    callback!!.onError(ACCESS_DENIED_THROWABLE)
                } else {
                    callback!!.onError(Exception(errorCode))
                }
                this@AuthenticationActivity.finish()
                // Cancel loading
                return true
            } else if (accessCode != null && !accessCode.isEmpty()) {
                // clear the cache
                view.clearCache(true)
                // Retrieve the access code
                // Get the access token from the access code
                callback!!.onSuccess(accessCode, client.redirectUri)
                // Close the activity when we got the access code
                finish()
                // Cancel loading
                return true
            }// Check if there is the access code in the url
            // Load the page
            return false
        }
    }

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
        authenticationWebView = WebView(this)
        authenticationWebView.webViewClient = mmAuthenticationWebViewClient
        authenticationWebView.clearCache(true)
        disableRememberPasswordDialog(authenticationWebView)

        // Show the WebView on screen
        setContentView(authenticationWebView)

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
        authenticationUrl = String.format(Locale.FRANCE, XeeAuth.ROUTE_BASE, host)
                .plus(AuthEndpoint.Routes.AUTHENTICATION)
                .plus("?").plus(AuthEndpoint.Parameters.CLIENT_ID).plus("=").plus(client.clientId)
                .plus("&response_type=code")
                .plus("&scope=").plus(client.scopes.joinToString("+"))

        if (callbackURI.isNotEmpty()) {
            authenticationUrl = authenticationUrl.plus("&" + AuthEndpoint.Parameters.REDIRECT_URL + "=" + callbackURI)
        }

        if (XeeAuth.enableOAuthLog) Log.d(TAG, "OAUTH URL : $authenticationUrl")

        // Load the authentication url
        authenticationWebView.loadUrl(authenticationUrl)
    }

    /**
     * Disable the "remember password dialog", only useful on KITKAT or JELLY BEAN
     * @param webView the WebView we want to disable the dialog
     */
    @Suppress("DEPRECATION")
    private fun disableRememberPasswordDialog(webView: WebView) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            webView.settings.savePassword = false
        }
    }

    override fun onBackPressed() {
        if (authenticationWebView.canGoBack()) {
            authenticationWebView.goBack()
        } else {
            callback!!.onError(BACK_PRESSED_THROWABLE)
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

        @JvmField val BACK_PRESSED_THROWABLE = Throwable("User has pressed back button")
        @JvmField val ACCESS_DENIED_THROWABLE = Throwable("Access denied to Authentication")

        /**
         * Access denied urn
         */
        private const val ERROR_CODE = "error"
        /**
         * Code parameter in url
         */
        private const val ACCESS_CODE = "code"

        /**
         * The authentication activity intent to connect the user
         * @param context the context
         * @param client  the [OAuth2Client]
         * @param host    the host to connect to
         * @return the [Intent]
         */
        fun intent(context: Context, client: OAuth2Client, host: String): Intent {
            val intent = Intent(context, AuthenticationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(EXTRA_CLIENT, client)
            intent.putExtra(EXTRA_HOST, host)
            return intent
        }
    }
}