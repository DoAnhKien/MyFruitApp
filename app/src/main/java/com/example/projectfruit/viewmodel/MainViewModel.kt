package com.example.projectfruit.viewmodel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfruit.dao.FruitDao
import com.example.projectfruit.model.Fruit
import com.example.projectfruit.model.FruitCategory
import com.example.projectfruit.model.FruitCategoryAndFruits
import com.google.firebase.database.*
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    private val fruitDao: FruitDao
) : ViewModel() {


    private val mCategory = fruitDao.getFruitCategoryAndFruits()
    private val mFruitCategories = fruitDao.getAllFruitCategories()
    private val refProduct: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference.child("fruit")
    }

    fun getMCategory(): LiveData<MutableList<FruitCategoryAndFruits>> {
        return mCategory
    }

    fun getMFruitCategories(): LiveData<MutableList<FruitCategory>?> {
        return mFruitCategories
    }

    fun insertCategory(data: MutableList<FruitCategory>) = viewModelScope.launch {
        fruitDao.insertFruitCategories(data)
    }

    fun insertCategory(data: FruitCategory) = viewModelScope.launch {
        fruitDao.insertFruitCategory(data)
    }

    fun insertFruit(data: Fruit) = viewModelScope.launch {
        fruitDao.insertFruit(data)
    }

    fun updateDataToFirebase(title: String, key: String, fruit: Fruit) = viewModelScope.launch {
        refProduct.child(title).child(key).setValue(fruit)

    }

    fun addNewFruitOnFirebase(title: String, fruit: Fruit) = viewModelScope.launch {
        refProduct.child(title).push().setValue(fruit)
    }

    fun addNewCategory(category: String){
        refProduct.child(category).setValue(category)
    }


    fun getDataFromFirebase() {
        refProduct.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for ((count, data) in snapshot.children.withIndex()) {
                    val fruitCategory = FruitCategory()
                    fruitCategory.nameCategory = data.key
                    fruitCategory.id = count
                    refProduct.child(data.key ?: "")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (fruitData in snapshot.children) {
//                                    val data = fruitData.getValue(Fruit::class.java)
//                                    insertFruit(fruitData.getValue(Fruit::class.java)!!)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                    insertCategory(fruitCategory)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}