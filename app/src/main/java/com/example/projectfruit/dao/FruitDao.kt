package com.example.projectfruit.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.projectfruit.model.Fruit
import com.example.projectfruit.model.FruitCategoryAndFruits
import com.example.projectfruit.model.FruitCategory

@Dao
interface FruitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFruitCategory(fruitCategory: FruitCategory?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFruitCategories(fruitCategories: MutableList<FruitCategory>)

    @Update
    suspend fun updateFruitCategory(fruitCategory: FruitCategory?)

    @Delete
    suspend fun deleteFruitCategory(fruitCategory: FruitCategory?)

    @Query("SELECT * FROM FruitCategory")
    fun getAllFruitCategories(): LiveData<MutableList<FruitCategory>?>

    @Query("DELETE FROM FruitCategory WHERE id = :id")
    suspend fun deleteCFruitCategoryById(id: Int?)

    @Transaction
    @Query("SELECT * FROM FruitCategory")
    fun getFruitCategoryAndFruits(): LiveData<MutableList<FruitCategoryAndFruits>>

    // Fruit
    @Insert
    suspend fun insertFruit(fruit: Fruit?)

    @Delete
    suspend fun deleteFruit(fruit: Fruit?)

    @Query("UPDATE FRUIT SET name = :name, price= :price WHERE id =:id")
    suspend fun updateFruit(id: Int?, name: String?, price: Int?)
}