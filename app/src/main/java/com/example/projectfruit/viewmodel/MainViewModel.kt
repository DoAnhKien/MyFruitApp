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

    fun updateCategory(data: FruitCategory) = viewModelScope.launch {
        fruitDao.updateFruitCategory(data)
    }

    fun deleteCategory(data: FruitCategory) = viewModelScope.launch {
        fruitDao.deleteFruitCategory(data)
    }

    fun deleteAllFruitOfCategory(id: Int) = viewModelScope.launch {
        fruitDao.deleteAllFruitOfCategory(id)
    }

    fun getTheLastFruitItem() = fruitDao.getTheLastFruitItem()

    fun updateDataToFirebase(title: String, key: String, fruit: Fruit) = viewModelScope.launch {
        refProduct.child(title).child(key).setValue(fruit)
    }

    fun addNewFruitOnFirebase(title: String, fruit: Fruit) {
        refProduct.child(title).push().setValue(fruit)
    }

    fun addNewCategory(category: String) = viewModelScope.launch {
        refProduct.child(category).setValue(category)
    }

    fun deleteFruit(fruit: Fruit, fruitCategory: FruitCategory) = viewModelScope.launch {
        fruitDao.deleteFruit(fruit)
        refProduct.child(fruitCategory.nameCategory!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        val currentFruit = data.getValue(Fruit::class.java)
                        if (currentFruit?.idFruitCategory == fruit.idFruitCategory) {
                            data.key?.let {
                                refProduct.child(fruitCategory.nameCategory!!).child(it)
                                    .setValue(null)
                            }
                            return
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("kienda", "onCancelled: $error")
                }

            })
    }

    fun updateFruit(id: Int?, name: String?, price: Int?) = viewModelScope.launch {
        fruitDao.updateFruit(id, name, price)
    }

    fun deleteCategoryForFirebase(title: String) {
        refProduct.child(title).removeValue()
    }

    fun updateCategoryForFirebase(
        fromPathInput: String,
        toPathInput: String
    ) {
        val fromPath: DatabaseReference = refProduct.child(fromPathInput)
        val toPath: DatabaseReference = refProduct.child(toPathInput)
        val valueEventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                toPath.setValue(dataSnapshot.value).addOnCompleteListener { task ->
                    if (task.isComplete) {
                        refProduct.child(fromPathInput).removeValue()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        fromPath.addListenerForSingleValueEvent(valueEventListener)
    }

    fun updateDataForFirebase(title: String, fruit: Fruit) {
        val fruitHashMap: HashMap<String, String> = HashMap<String, String>()
        val fruitHashMapInt: HashMap<String, Int> = HashMap<String, Int>()
        fruitHashMap["name"] = fruit.name.toString()
        fruitHashMapInt["price"] = fruit.price!!.toInt()
        refProduct.child(title).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val currentFruit = data.getValue(Fruit::class.java)
                    if (currentFruit?.idFruitCategory == fruit.idFruitCategory) {
                        refProduct.child(title).child(data.key ?: "")
                            .updateChildren(fruitHashMap as Map<String, String>)
                            .addOnSuccessListener {
                            }.addOnFailureListener {

                            }
                        refProduct.child(title).child(data.key ?: "")
                            .updateChildren(fruitHashMapInt as Map<String, Int>)
                            .addOnSuccessListener {

                            }.addOnFailureListener {

                            }
                        return
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("kienda", "onCancelled: $error")
            }

        })
    }

    fun getDataFromFirebase() = viewModelScope.launch {
        fruitDao.deleteAllFruitCategory()
        fruitDao.deleteAllFruit()
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
                                    val data = fruitData.getValue(Fruit::class.java)
                                    data?.idFruitCategory = count
                                    insertFruit(data!!)
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