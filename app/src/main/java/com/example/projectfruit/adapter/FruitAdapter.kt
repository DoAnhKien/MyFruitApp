package com.example.projectfruit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.projectfruit.R
import com.example.projectfruit.model.Fruit

class FruitAdapter(
    var mListFruit: List<Fruit>
) :
    RecyclerView.Adapter<FruitAdapter.RecyclerViewHolder>() {

    private val viewBindHolder = ViewBinderHelper()

    fun setNewData(){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_child, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val fruit = mListFruit[position]
        holder.bindCategory(fruit)
    }

    override fun getItemCount(): Int {
        return mListFruit.size
    }

    inner class RecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvName: TextView = view.findViewById(R.id.tv_name)
        private val tvPrice: TextView = view.findViewById(R.id.tv_price)
        private val swipeRevealLayout: SwipeRevealLayout =
            view.findViewById(R.id.swipe_reveal_layout)

        fun bindCategory(fruit: Fruit) {
            viewBindHolder.setOpenOnlyOne(true)
            viewBindHolder.bind(swipeRevealLayout, fruit.id.toString())
            viewBindHolder.closeLayout(fruit.id.toString())
            tvName.text = fruit.name
            tvPrice.text = fruit.price.toString()
        }
    }


}