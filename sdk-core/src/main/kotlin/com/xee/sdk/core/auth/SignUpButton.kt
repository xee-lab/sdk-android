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

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Button
import com.xee.sdk.core.R
import java.lang.ref.WeakReference

/**
 * SignUp button to register the user
 */
class SignUpButton : Button {
    private var sizeIcon: Int = 0
    private var sizeNormal: Int = 0
    private var sizeLarge: Int = 0
    private var paddingVertical: Int = 0
    private var paddingHorizontal: Int = 0
    private var paddingDrawable: Int = 0
    private var size = -1
    private var theme = -1
    private var drawable: Drawable? = null
    private var showIcon: Boolean = true
    private var onClickListener: OnClickListener? = null
    private var registrationCallback: RegistrationCallback? = null
    private var xeeWeakReference: WeakReference<XeeAuth>? = null

    enum class Theme {
        WHITE,
        GREY,
        GREEN
    }

    enum class Size {
        ICON,
        NORMAL,
        LARGE
    }

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.SignUpButton, defStyle, 0)
        theme = a.getInt(R.styleable.SignUpButton_signUpBtnTheme, Theme.GREY.ordinal)
        size = a.getInt(R.styleable.SignUpButton_signUpBtnSize, sizeNormal)

        sizeIcon = getRealDimen(R.dimen.sign_in_button_icon)
        sizeNormal = getRealDimen(R.dimen.sign_in_button_normal)
        sizeLarge = getRealDimen(R.dimen.sign_in_button_large)

        paddingHorizontal = getRealDimen(R.dimen.sign_up_padding_horizontal)
        paddingVertical = getRealDimen(R.dimen.sign_up_padding_vertical)
        paddingDrawable = getRealDimen(R.dimen.sign_up_padding_drawable)

        showIcon = a.getBoolean(R.styleable.SignUpButton_signUpBtnShowIcon, true)

        setBtnSize(context, size)
        setBtnTheme(theme)

        a.recycle()
    }

    /**
     * Set the button size
     *
     * @param size the button size [Size]
     */
    fun setBtnSize(size: Size) {
        when (size) {
            Size.ICON -> setBtnSize(context, sizeIcon)
            Size.NORMAL -> setBtnSize(context, sizeNormal)
            Size.LARGE -> setBtnSize(context, sizeLarge)
        }
    }

    /**
     * Set the button size
     *
     * @param size    the button size value
     * @param context the context
     */
    private fun setBtnSize(context: Context, size: Int) {
        this.size = size
        val minHeight = dpToPx(size) + dpToPx(paddingVertical * 2)
        if (size == sizeIcon) {
            text = ""
            setBtnPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
            height = minHeight
            compoundDrawablePadding = dpToPx(0)
        } else if (size == sizeLarge) {
            text = context.getString(R.string.sign_up_with_xee)
            setBtnPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
            height = minHeight
            compoundDrawablePadding = dpToPx(paddingDrawable)
        } else {
            text = context.getString(R.string.sign_up_with_xee)
            setBtnPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)
            height = minHeight
            compoundDrawablePadding = dpToPx(paddingDrawable)
        }
    }

    /**
     * Set the button theme
     *
     * @param theme the button theme value
     */
    @Suppress("DEPRECATION")
    private fun setBtnTheme(theme: Int) {
        this.theme = theme
        when (this.theme) {
            0 -> {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable = context.resources.getDrawable(R.drawable.xee_auth_btn_logo_black, context.theme)
                } else {
                    drawable = context.resources.getDrawable(R.drawable.xee_auth_btn_logo_black)
                }
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    backgroundTintList = context.resources.getColorStateList(R.color.sign_in_button_bg_white, context.theme)
                    setTextColor(context.getColor(R.color.sign_in_button_text_grey))
                } else {
                    background = context.resources.getDrawable(R.drawable.xee_auth_btn_bg_white)
                    setTextColor(context.resources.getColor(R.color.sign_in_button_text_grey))
                }
            }
            1 -> {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable = context.resources.getDrawable(R.drawable.xee_auth_btn_logo_white, context.theme)
                } else {
                    drawable = context.resources.getDrawable(R.drawable.xee_auth_btn_logo_white)
                }
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    backgroundTintList = context.resources.getColorStateList(R.color.sign_in_button_bg_grey, context.theme)
                    setTextColor(context.getColor(R.color.sign_in_button_text_white))
                } else {
                    background = context.resources.getDrawable(R.drawable.xee_auth_btn_bg_grey)
                    setTextColor(context.resources.getColor(R.color.sign_in_button_text_white))
                }
            }
            2 -> {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    drawable = context.resources.getDrawable(R.drawable.xee_auth_btn_logo_white, context.theme)
                } else {
                    drawable = context.resources.getDrawable(R.drawable.xee_auth_btn_logo_white)
                }
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    backgroundTintList = context.resources.getColorStateList(R.color.sign_in_button_bg_green, context.theme)
                    setTextColor(context.getColor(R.color.sign_in_button_text_white))
                } else {
                    background = context.resources.getDrawable(R.drawable.xee_auth_btn_bg_green)
                    setTextColor(context.resources.getColor(R.color.sign_in_button_text_white))
                }
            }
        }

        if (!showIcon && size != sizeIcon) {
            drawable!!.setBounds(0, 0, 0, 0)
            setCompoundDrawables(null, null, null, null)
        } else {
            drawable!!.setBounds(0, 0, dpToPx(sizeIcon), dpToPx(sizeIcon))
            setCompoundDrawables(null, null, drawable, null)
        }
    }

    /**
     * Set the button theme
     *
     * @param theme the button theme [Theme]
     */
    fun setBtnTheme(theme: Theme) {
        this.theme = theme.ordinal
        setBtnTheme(this.theme)
    }

    /**
     * Set a click listener invoked when the button is clicked
     *
     * @param listener the listener
     */
    override fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }

    /**
     * Set the sign in callback when the button is clicked
     *
     * @param xee                the [Xee] instance
     * @param registrationCallback the registration callback
     */
    fun setOnSignUpClickResult(xeeAuth: XeeAuth, registrationCallback: RegistrationCallback) {
        this.registrationCallback = registrationCallback
        xeeWeakReference = WeakReference(xeeAuth)
    }

    /**
     * Set the button padding
     *
     * @param left   the padding left
     * @param top    the padding top
     * @param right  the padding right
     * @param bottom the padding bottom
     */
    private fun setBtnPadding(left: Int, top: Int, right: Int, bottom: Int) {
        setPadding(dpToPx(left), dpToPx(top), dpToPx(right), dpToPx(bottom))
    }

    /**
     * Get dimen based on current screen density
     *
     * @param dimen the dimen
     * @return
     */
    private fun getRealDimen(dimen: Int): Int {
        val scaleRatio = resources.displayMetrics.density
        val dimenPix = resources.getDimension(dimen)
        return (dimenPix / scaleRatio).toInt()
    }

    /**
     * Class which allows to save and restore the button state
     */
    private class SavedState : BaseSavedState {
        internal var mmTheme = -1
        internal var mmSize = -1

        internal constructor(superState: Parcelable) : super(superState) {}

        private constructor(`in`: Parcel) : super(`in`) {
            mmTheme = `in`.readInt()
            mmSize = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(mmTheme)
            out.writeInt(mmSize)
        }

        companion object {

            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.mmTheme = theme
        ss.mmSize = size
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        theme = ss.mmTheme
        size = ss.mmSize
        setBtnSize(context, size)
        setBtnTheme(theme)
    }

    override fun setMinimumWidth(minWidth: Int) {
        super.setMinimumWidth(0)
    }

    override fun getMinWidth(): Int {
        return 0
    }

    override fun setMinWidth(minpixels: Int) {
        super.setMinWidth(0)
    }

    override fun getMinimumWidth(): Int {
        return 0
    }

    override fun getSuggestedMinimumWidth(): Int {
        return 0
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            // detect if user is clicking outside
            val viewRect = Rect()
            getGlobalVisibleRect(viewRect)
            if (viewRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                onClickListener?.onClick(this)
                if (registrationCallback != null) {
                    signUp()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP && (event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER || event.keyCode == KeyEvent.KEYCODE_ENTER)) {
            onClickListener?.onClick(this)
            if (registrationCallback != null) {
                signUp()
            }
        }
        return super.dispatchKeyEvent(event)
    }

    /**
     * Launch the sign up process and notify result
     */
    private fun signUp() {
        if (xeeWeakReference != null && xeeWeakReference!!.get() != null) {
            xeeWeakReference!!.get()?.register(object : RegistrationCallback {
                override fun onCanceled() {
                    registrationCallback?.onCanceled()
                }

                override fun onRegistered() {
                    registrationCallback?.onRegistered()
                }

                override fun onError(error: Throwable) {
                    registrationCallback?.onError(error)
                }

                override fun onLoggedAfterRegistration() {
                    registrationCallback?.onLoggedAfterRegistration()
                }
            })
        }
    }

    /**
     * Convert do to px
     *
     * @param dp the dp to convert
     * @return
     */
    private fun dpToPx(dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }
}