package com.example.projectfruit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.projectfruit.R
import com.example.projectfruit.model.FruitCategoryAndFruits

class SpinnerCategoryAdapter(var context: Context, var dataSource: List<FruitCategoryAndFruits>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val itemHolder: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.layout_spinner_item_category, parent, false)
            itemHolder = ItemHolder(view)
            view?.tag = itemHolder
        } else {
            view = convertView
            itemHolder = view.tag as ItemHolder
        }
        itemHolder.tvCategory.text = dataSource[position].fruitCategory.nameCategory

        return view
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private class ItemHolder(row: View?) {
        val tvCategory: TextView = row?.findViewById(R.id.tv_category_spinner) as TextView
    }

}