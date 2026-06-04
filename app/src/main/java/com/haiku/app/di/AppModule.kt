package com.haiku.app.di

import android.content.Context
import androidx.room.Room
import com.haiku.app.data.db.HaikuDao
import com.haiku.app.data.db.HaikuDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HaikuDatabase =
        Room.databaseBuilder(context, HaikuDatabase::class.java, "haiku.db").build()

    @Provides
    fun provideHaikuDao(db: HaikuDatabase): HaikuDao = db.haikuDao()
}
