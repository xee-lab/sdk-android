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

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class DividerItemDecoration : RecyclerView.ItemDecoration {

    private var mDivider: Drawable? = null

    private var mOrientation: Int = 0
    private var mPaddingLeft: Int = 0
    private var mPaddingTop: Int = 0
    private var mPaddingRight: Int = 0
    private var mPaddingBottom: Int = 0
    private var mSpanCount: Int = 0

    constructor(context: Context, @DrawableRes drawable: Int, orientation: Int, spanCount: Int, paddingLeft: Int, paddingTop: Int, paddingRight: Int, paddingBottom: Int) {
        val a = context.obtainStyledAttributes(ATTRS)
        if (drawable == -1) {
            mDivider = a.getDrawable(0)
        } else {
            mDivider = ContextCompat.getDrawable(context, drawable)
        }

        mSpanCount = spanCount
        mPaddingLeft = paddingLeft
        mPaddingTop = paddingTop
        mPaddingRight = paddingRight
        mPaddingBottom = paddingBottom
        a.recycle()
        setOrientation(orientation)
    }

    constructor(context: Context, orientation: Int, paddingHorizontal: Int, paddingVertical: Int) {
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = ContextCompat.getDrawable(context, R.drawable.recyclerview_divider)

        mSpanCount = 0
        mPaddingLeft = paddingHorizontal
        mPaddingTop = paddingVertical
        mPaddingRight = paddingHorizontal
        mPaddingBottom = paddingVertical
        a.recycle()
        setOrientation(orientation)
    }

    constructor(context: Context, orientation: Int) {
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        mDivider = ContextCompat.getDrawable(context, R.drawable.recyclerview_divider)
        a.recycle()
        setOrientation(orientation)
    }

    constructor(context: Context, orientation: Int, @DrawableRes dividerRes: Int) {
        val a = context.obtainStyledAttributes(ATTRS)
        mDivider = a.getDrawable(0)
        mDivider = ContextCompat.getDrawable(context, dividerRes)
        a.recycle()
        setOrientation(orientation)
    }

    fun setOrientation(orientation: Int) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw IllegalArgumentException("invalid orientation")
        }
        mOrientation = orientation
    }

    override fun onDraw(c: Canvas?, parent: RecyclerView?) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    fun drawVertical(c: Canvas?, parent: RecyclerView?) {
        val left = parent!!.paddingLeft + mPaddingLeft
        val right = parent.width - parent.paddingRight - mPaddingRight

        val childCount = if (parent.childCount > 1) parent.childCount - 1 else 0
        for (i in 0..childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin + mPaddingTop
            val bottom = top + mDivider!!.intrinsicHeight + mPaddingBottom
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(c!!)
        }
    }

    fun drawHorizontal(c: Canvas?, parent: RecyclerView?) {
        val top = parent!!.paddingTop - mPaddingTop
        val bottom = parent.height - parent.paddingBottom - mPaddingBottom

        val childCount = if (parent.childCount > 1) parent.childCount - 1 else 0
        for (i in 0..childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin + mPaddingLeft
            val right = left + mDivider!!.intrinsicHeight + mPaddingRight
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(c!!)
        }
    }

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView?) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDivider!!.intrinsicHeight)
        } else {
            outRect.set(0, 0, mDivider!!.intrinsicWidth, 0)
        }
    }

    companion object {

        private val ATTRS = intArrayOf(android.R.attr.listDivider)

        @JvmField val HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL

        @JvmField val VERTICAL_LIST = LinearLayoutManager.VERTICAL
    }
}