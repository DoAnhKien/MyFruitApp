package com.example.projectfruit.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = FruitCategory::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("idFruitCategory"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
)
class Fruit(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var name: String? = null,
    var price: Int? = null,
    var idFruitCategory: Int? = null
)