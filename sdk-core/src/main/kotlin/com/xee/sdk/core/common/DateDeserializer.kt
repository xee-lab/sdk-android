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

import android.util.Log
import com.google.gson.*
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Date deserializer
 * @author Julien Cholin
 * @since 4.0.0
 */
class DateDeserializer(enableLog: Boolean = false) : JsonSerializer<Date>, JsonDeserializer<Date> {

    init {
        ENABLE_LOG = enableLog
    }

    companion object {
        val TAG = DateDeserializer::class.java.simpleName
        const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        const val DATE_FORMAT_WITH_MS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        private val DATE_FORMATTER = SimpleDateFormat(DATE_FORMAT, Locale.US)
        var ENABLE_LOG:Boolean = false
    }

    @Synchronized override fun serialize(date: Date, type: Type, jsonSerializationContext: JsonSerializationContext): JsonElement {
        return JsonPrimitive(DATE_FORMATTER.format(date))
    }

    @Synchronized
    @Throws(JsonParseException::class)
    override fun deserialize(element: JsonElement, arg1: Type, arg2: JsonDeserializationContext): Date? {
        val date = element.asString
        var formatter = SimpleDateFormat(DATE_FORMAT, Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("UTC")

        try {
            return formatter.parse(date)
        } catch (e: ParseException) {
            try {
                // if date parsed has failed with the default format, then try with the second one
                formatter = SimpleDateFormat(DATE_FORMAT_WITH_MS, Locale.US)
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                return formatter.parse(date)
            } catch (e: ParseException) {
                if (ENABLE_LOG) Log.e(TAG, "Date deserialization error: ", e)
            }

            return null
        }
    }
}