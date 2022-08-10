package com.example.listtest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class CustomAdapter(context: Context, list: ArrayList<Data>) : ArrayAdapter<Data>(context, 0, list) {
    private lateinit var data: Data
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view  = convertView
        if (view == null) {
            view  = LayoutInflater.from(context).inflate(R.layout.row_view, parent, false)
        }
        data = getItem(position) as Data
        view?.findViewById<ImageView>(R.id.user_icon).apply {  data.icon }
        view?.findViewById<TextView>(R.id.title)?.apply { text = data.title }
        return view!!
    }
}