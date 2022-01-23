package com.example.demofruit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demofruit.R
import com.example.demofruit.model.FruitCategory

class FruitCategoryAdapter :
    RecyclerView.Adapter<FruitCategoryAdapter.RecyclerViewHolder>() {

    var mContext: Context? = null
    var mListFruitCategory: List<FruitCategory>? = null

    fun setListFruitCategory(listFruitCategory: List<FruitCategory>) {
        mListFruitCategory = listFruitCategory
    }

    fun setContext(context: Context){
        mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_parent, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        mListFruitCategory?.get(position)?.let {
            holder.bindCategory(it)
        }
    }

    override fun getItemCount(): Int {
        return mListFruitCategory?.size ?: 0
    }

    inner class RecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val tvCategory = view.findViewById<TextView>(R.id.tv_category)
        private val rcvFruit = view.findViewById<RecyclerView>(R.id.rcv_fruits)
        private val icArrow = view.findViewById<ImageView>(R.id.ic_arrow)
        private val lnTitle = view.findViewById<LinearLayout>(R.id.ln_title)

        fun bindCategory(fruitCategory: FruitCategory) {
            tvCategory.text = fruitCategory.nameCategory

            if (fruitCategory.expanded)
                icArrow.setImageResource(R.drawable.ic_arrow_down)
            else
                icArrow.setImageResource(R.drawable.ic_arrow_up)

            fruitCategory.listNameFruitL?.let {
                rcvFruit.layoutManager = LinearLayoutManager(mContext)
                rcvFruit.adapter = FruitAdapter(it)
            }

            rcvFruit.visibility = if (fruitCategory.expanded) View.VISIBLE else View.GONE
            lnTitle.setOnClickListener {
                fruitCategory.expanded = !fruitCategory.expanded
                notifyItemChanged(adapterPosition)
            }
        }
    }

    fun filterList(filterList: List<FruitCategory>) {
        mListFruitCategory = filterList
        notifyDataSetChanged()
    }
}