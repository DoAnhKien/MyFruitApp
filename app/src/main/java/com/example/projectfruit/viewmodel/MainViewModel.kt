package com.example.projectfruit.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfruit.dao.FruitDao
import com.example.projectfruit.model.Fruit
import com.example.projectfruit.model.FruitCategory
import com.example.projectfruit.model.FruitCategoryAndFruits
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val fruitDao: FruitDao
) : ViewModel() {


    private val mCategory = fruitDao.getFruitCategoryAndFruits()
    private val mFruitCategories = fruitDao.getAllFruitCategories()


    fun getMCategory(): LiveData<MutableList<FruitCategoryAndFruits>> {
        return mCategory
    }

    fun getMFruitCategories(): LiveData<MutableList<FruitCategory>?> {
        return mFruitCategories
    }

    fun insertCategory(data : MutableList<FruitCategory>) = viewModelScope.launch {
        fruitDao.insertFruitCategories(data)
    }

    fun insertCategory(data : FruitCategory) = viewModelScope.launch {
        fruitDao.insertFruitCategory(data)
    }

    fun insertFruit(data : Fruit) = viewModelScope.launch {
        fruitDao.insertFruit(data)
    }

}