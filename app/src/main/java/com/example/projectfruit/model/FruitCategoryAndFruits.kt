package com.example.projectfruit.model

import androidx.room.Embedded
import androidx.room.Relation

data class FruitCategoryAndFruits(
    @Embedded
    val fruitCategory: FruitCategory,
    @Relation(
        parentColumn = "id",
        entityColumn = "idFruitCategory",
        entity = Fruit::class
    )
    val fruits: List<Fruit>?
)