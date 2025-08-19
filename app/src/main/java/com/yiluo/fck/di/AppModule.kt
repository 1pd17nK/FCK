package com.yiluo.fck.di
import android.content.Context
import com.yiluo.fck.data.AppSettingsManager
import com.yiluo.fck.data.QuizManager
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
    fun provideAppSettingsManager(
        @ApplicationContext context: Context
    ): AppSettingsManager {
        return AppSettingsManager(context)
    }

    @Provides
    @Singleton
    fun provideQuizManager(
        @ApplicationContext context: Context
    ): QuizManager {
        return QuizManager(context)
    }
}
