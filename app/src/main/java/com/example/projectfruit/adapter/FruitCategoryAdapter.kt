package com.example.projectfruit.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfruit.R
import com.example.projectfruit.model.Fruit
import com.example.projectfruit.model.FruitCategory
import com.example.projectfruit.model.FruitCategoryAndFruits

class FruitCategoryAdapter(
    private val mContext: Context,
    private val itemListener: FruitCategoryListener
) :
    RecyclerView.Adapter<FruitCategoryAdapter.RecyclerViewHolder>(), OnItemFruitCategoryClick {

    var mListFruitCategory: List<FruitCategoryAndFruits>? = null

    fun setListFruitCategory(listFruitCategory: List<FruitCategoryAndFruits>?) {
        mListFruitCategory = listFruitCategory
        notifyDataSetChanged()
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
        private val clTitle = view.findViewById<ConstraintLayout>(R.id.cl_title)
        private val ivAdd = view.findViewById<ImageView>(R.id.iv_add)

        fun bindCategory(data: FruitCategoryAndFruits) {
            tvCategory.text = data.fruitCategory.nameCategory

            if (data.fruitCategory.expanded)
                icArrow.setImageResource(R.drawable.ic_arrow_down)
            else
                icArrow.setImageResource(R.drawable.ic_arrow_up)

            data.fruits?.let {
                val mAdapter = FruitAdapter(it)
                rcvFruit.layoutManager = LinearLayoutManager(mContext)
                rcvFruit.adapter = mAdapter
            }

            ivAdd.setOnClickListener {
                itemListener.onClickListener(
                    data.fruitCategory.id,
                    data.fruitCategory.nameCategory ?: ""
                )
            }

            rcvFruit.visibility = if (data.fruitCategory.expanded) View.VISIBLE else View.GONE
            clTitle.setOnClickListener {
                data.fruitCategory.expanded = !data.fruitCategory.expanded
                notifyItemChanged(adapterPosition)
            }
        }
    }

    interface FruitCategoryListener {
        fun onClickListener(id: Int?, name: String)
    }

    override fun onClick(position: Int, fruitData: Fruit) {

    }

    override fun onLongClick(position: Int, fruitData: Fruit) {

    }
}