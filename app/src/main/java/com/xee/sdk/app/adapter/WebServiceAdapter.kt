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

package com.xee.sdk.app.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.xee.sdk.app.R
import com.xee.sdk.app.model.WebService
import kotlinx.android.synthetic.main.item_webservice.view.*
import kotlinx.android.synthetic.main.item_webservice_header.view.*

class WebServiceAdapter(private val webServiceList: List<WebService>, private val itemClick: (WebService) -> Unit) : RecyclerView.Adapter<WebServiceAdapter.ViewHolder>() {

    companion object {
        const val HEADER: Int = 0
        const val ITEM: Int = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view:View = if(viewType == HEADER) {
            LayoutInflater.from(parent?.context).inflate(R.layout.item_webservice_header, parent, false)
        } else {
            LayoutInflater.from(parent?.context).inflate(R.layout.item_webservice, parent, false)
        }
        return ViewHolder(view, itemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindWebservice(webServiceList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return when (webServiceList[position].isHeader) {
            true -> HEADER
            false -> ITEM
        }
    }

    override fun getItemCount() = webServiceList.size

    class ViewHolder(view: View, private val itemClick: (WebService) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bindWebservice(webService: WebService) {
            with(webService) {
                if(isHeader) {
                    itemView.webServiceHeader.text = title
                } else {
//                    itemView.webServiceTitle.text = "[$method] $title"
                    itemView.webServiceTitle.text = title
                    itemView.setOnClickListener { itemClick(this) }
                }
            }
        }
    }
}