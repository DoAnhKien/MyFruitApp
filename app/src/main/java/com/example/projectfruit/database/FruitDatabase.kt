package com.example.projectfruit.database

import android.app.Application
import androidx.room.Room

import androidx.room.RoomDatabase

import androidx.room.Database
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.projectfruit.dao.FruitDao
import com.example.projectfruit.di.ApplicationScope
import com.example.projectfruit.model.Fruit
import com.example.projectfruit.model.FruitCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider


@Database(entities = [FruitCategory::class, Fruit::class], version = 1, exportSchema = false)
abstract class FruitDatabase : RoomDatabase() {
    abstract fun getFruitDao(): FruitDao?

    class CallBack @Inject constructor(
        private val appDatabase: Provider<FruitDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val questionDao = appDatabase.get().getFruitDao()
            applicationScope.launch {
                // todo it just for once
            }
        }

    }
}