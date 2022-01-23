package com.example.projectfruit.di

import android.app.Application
import androidx.room.Room
import com.example.projectfruit.database.FruitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModel {

    @Provides
    @Singleton
    fun providesQuestionDatabase(
        app: Application,
        callBack: FruitDatabase.CallBack
    ) = Room.databaseBuilder(app, FruitDatabase::class.java, "question.db")
        .fallbackToDestructiveMigration()
        .addCallback(callBack)
        .build()

    @Provides
    fun providesFruitDao(db: FruitDatabase) = db.getFruitDao()


    @ApplicationScope
    @Provides
    @Singleton
    fun providesApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope