package com.example.projectfruit.adapter

import com.example.projectfruit.model.Fruit
import com.example.projectfruit.model.FruitCategory

interface OnItemFruitCategoryClick {
    fun onClick(position: Int, fruitData: Fruit)
    fun onLongClick(position: Int, fruitData: Fruit)
}