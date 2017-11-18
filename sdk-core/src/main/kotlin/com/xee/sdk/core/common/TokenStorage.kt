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

package com.xee.sdk.core.common

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.xee.sdk.core.common.model.Token

/**
 * TokenStorage stores and provides the [Token]
 * @author Julien Cholin
 * @since 4.0.0
 */
class TokenStorage private constructor(context: Context) : UniqueStorage<Token> {
    private val preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    override fun store(item: Token) {
        preferences.edit().putString(TOKEN_ID, GsonBuilder().create().toJson(item)).apply()
    }

    override fun get(): Token? {
        val tokenFromPrefs = preferences.getString(TOKEN_ID, null)
        return if (tokenFromPrefs != null) {
            GsonBuilder().create().fromJson<Token>(tokenFromPrefs, Token::class.java)
        } else {
            null
        }
    }

    fun dump() {
        preferences.edit().remove(TOKEN_ID).apply()
    }

    companion object {
        private val NAME = "com.xee.sdk.v4"
        private val TOKEN_ID = "com.xee.token.v4"
        private var sInstance: TokenStorage? = null

        operator fun get(context: Context): TokenStorage? {
            if (sInstance == null) {
                sInstance = TokenStorage(context)
            }
            return sInstance
        }
    }
}

interface UniqueStorage<in T> {
    fun store(item: T)
    fun get(): Token?
}