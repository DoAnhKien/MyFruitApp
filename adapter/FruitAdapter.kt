package com.example.demofruit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.demofruit.R
import com.example.demofruit.model.Fruit

class FruitAdapter (private val listFruit: List<Fruit>) :
    RecyclerView.Adapter<FruitAdapter.RecyclerViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_child, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val fruit = listFruit[position]
        holder.bindCategory(fruit)
    }

    override fun getItemCount(): Int {
        return listFruit.size
    }

    inner class RecyclerViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val tvName = view.findViewById<TextView>(R.id.tv_name)

        fun bindCategory(fruit: Fruit){
            tvName.text = fruit.name
        }
    }
}