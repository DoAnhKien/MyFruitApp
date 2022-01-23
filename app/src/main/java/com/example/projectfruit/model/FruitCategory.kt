package com.example.projectfruit.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class FruitCategory(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var nameCategory: String? = null,
    var expanded: Boolean = false
)