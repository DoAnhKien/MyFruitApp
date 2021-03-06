package com.example.projectfruit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.projectfruit.R
import com.example.projectfruit.common.Constant
import com.example.projectfruit.model.Fruit
import com.example.projectfruit.model.FruitCategory
import org.greenrobot.eventbus.EventBus

class FruitAdapter(
    var mListFruit: List<Fruit>,
    var fruitCategory: FruitCategory
) :
    RecyclerView.Adapter<FruitAdapter.RecyclerViewHolder>() {

    private val viewBindHolder = ViewBinderHelper()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.layout_fruit, parent, false)
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
        private val ivDelete: LinearLayout = view.findViewById(R.id.iv_delete)
        private val ivAdd: LinearLayout = view.findViewById(R.id.iv_add)
        private val swipeRevealLayout: SwipeRevealLayout =
            view.findViewById(R.id.swipe_reveal_layout)

        fun bindCategory(fruit: Fruit) {
            viewBindHolder.setOpenOnlyOne(true)
            viewBindHolder.bind(swipeRevealLayout, fruit.id.toString())
            viewBindHolder.closeLayout(fruit.id.toString())
            tvName.text = fruit.name
            tvPrice.text = fruit.price.toString()

            ivAdd.setOnClickListener {
                EventBus.getDefault()
                    .post(Triple(fruit, Constant.KeyEvent.UPDATE_FRUIT, fruitCategory))
            }

            ivDelete.setOnClickListener {
                EventBus.getDefault()
                    .post(Triple(fruit, Constant.KeyEvent.DELETE_FRUIT, fruitCategory))
            }
        }
    }

}