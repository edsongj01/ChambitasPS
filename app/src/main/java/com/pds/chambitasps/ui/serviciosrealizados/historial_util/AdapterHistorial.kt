package com.pds.chambitasps.ui.serviciosrealizados.historial_util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.Timestamp
import com.pds.chambitasps.R
import java.util.*
import kotlin.collections.ArrayList

class AdapterHistorial(ctx: Context): BaseAdapter() {

    var lista = ArrayList<HistorialModel>()
    var context = ctx

    override fun getCount(): Int {
        return lista.size
    }

    override fun getItem(p0: Int): Any {
        return lista[p0]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val holder = ListaViewHolder()

        var myView = view

        val elementoInflater = LayoutInflater.from(context)

        val trabajo = lista[position].servicio
        val fecha = lista[position].fecha
        val estado = lista[position].estado
        val direction = lista[position].direction
        val regular = lista[position].regular_name

        myView = elementoInflater.inflate(R.layout.item_finalizados, null)

        holder.trabajo = myView.findViewById(R.id.textView41)
        holder.trabajo!!.text = trabajo

        holder.fecha = myView.findViewById(R.id.textView43)
        holder.fecha!!.text = fecha

        holder.estado = myView.findViewById(R.id.textView48)
        holder.estado!!.text = estado

        holder.regularName = myView.findViewById(R.id.textView45)
        holder.regularName!!.text = regular

        holder.direction = myView.findViewById(R.id.textView47)
        holder.direction!!.text = direction

        return myView
    }

    fun addElemento(elemento: HistorialModel) {
        lista.add(elemento)
        notifyDataSetChanged()
    }
}

internal class ListaViewHolder() {
    var trabajo: TextView? = null
    var fecha: TextView? = null
    var estado: TextView? = null
    var direction: TextView? = null
    var regularName: TextView? = null
}