package com.immanlv.trem.di

import android.app.Application
import com.immanlv.trem.BaseApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBaseClass(context: Application): BaseApplication {
        return context as BaseApplication
    }

}