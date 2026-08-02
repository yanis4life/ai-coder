package com.uibuilder.app.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.uibuilder.app.data.db.AppDatabase
import com.uibuilder.app.data.db.dao.AnimationDao
import com.uibuilder.app.data.db.dao.ComponentDao
import com.uibuilder.app.data.db.dao.HistoryDao
import com.uibuilder.app.data.db.dao.ProjectDao
import com.uibuilder.app.data.repository.ColorThemeProvider
import com.uibuilder.app.data.repository.ProjectRepository
import com.uibuilder.app.data.repository.TemplateProvider
import com.uibuilder.app.data.repository.TypographyProvider
import com.uibuilder.app.util.CssGenerator
import com.uibuilder.app.util.HtmlGenerator
import com.uibuilder.app.util.JavaScriptGenerator
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
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideProjectDao(db: AppDatabase): ProjectDao = db.projectDao()

    @Provides
    fun provideComponentDao(db: AppDatabase): ComponentDao = db.componentDao()

    @Provides
    fun provideAnimationDao(db: AppDatabase): AnimationDao = db.animationDao()

    @Provides
    fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historyDao()

    @Provides
    @Singleton
    fun provideProjectRepository(
        database: AppDatabase,
        moshi: Moshi
    ): ProjectRepository = ProjectRepository(database, moshi)

    @Provides
    @Singleton
    fun provideTemplateProvider(): TemplateProvider = TemplateProvider()

    @Provides
    @Singleton
    fun provideColorThemeProvider(): ColorThemeProvider = ColorThemeProvider()

    @Provides
    @Singleton
    fun provideTypographyProvider(): TypographyProvider = TypographyProvider()

    @Provides
    @Singleton
    fun provideHtmlGenerator(): HtmlGenerator = HtmlGenerator()

    @Provides
    @Singleton
    fun provideCssGenerator(): CssGenerator = CssGenerator()

    @Provides
    @Singleton
    fun provideJavaScriptGenerator(): JavaScriptGenerator = JavaScriptGenerator()
}
