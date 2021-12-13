package com.pds.chambitasps.body.chat_utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.pds.chambitasps.R

class AdapterChat(ctx: Context): BaseAdapter() {

    var messages = ArrayList<ChatModel>()
    var context = ctx

    override fun getCount(): Int {
        return messages.size
    }

    override fun getItem(item: Int): Any {
        return messages[item]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val holder = MessageViewHolder()
        var myView = view

        val messageInflater = LayoutInflater.from(context)
        val message = messages[position].message

        if (messages[position].type == "regular") {

            myView = messageInflater.inflate(R.layout.su_mensaje, null)
            holder.messageBody = myView.findViewById(R.id.txtcuerpoMsg)
            holder.messageBody!!.text = message

        } else {

            myView = messageInflater.inflate(R.layout.mi_mensaje, null)
            holder.messageBody = myView.findViewById(R.id.txtcuerpoMsg)
            holder.messageBody!!.text = message

        }

        return myView
    }

    fun addMessage(chatMessage: ChatModel) {
        messages.add(chatMessage)
        notifyDataSetChanged()
    }
}

internal class MessageViewHolder() {
    var messageBody: TextView? = null
}