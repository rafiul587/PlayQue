package com.example.youtubeapitesting

import android.content.Context
import androidx.room.Room
import com.example.youtubeapitesting.Constants.Companion.BASE_URL
import com.example.youtubeapitesting.data.AppDatabase
import com.example.youtubeapitesting.models.NetworkResultCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addInterceptor {
                val originalHttpUrl = it.request().url
                val url = originalHttpUrl.newBuilder()
                    .addQueryParameter("key", "YOUR_YOUTUBE_API_KEY")
                    .addQueryParameter("maxResults", "20").build()
                val request = it.request().newBuilder()
                request.url(url)
                return@addInterceptor it.proceed(request.build())
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideAdapterFactory(): NetworkResultCallAdapterFactory =
        NetworkResultCallAdapterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideCurrencyService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        "my_app_db"
    ).enableMultiInstanceInvalidation()
        .build() // The reason we can construct a database for the repo

    @Singleton
    @Provides
    fun providePlaylistDao(db: AppDatabase) = db.playlistDao()

    @Singleton
    @Provides
    fun provideVideosDao(db: AppDatabase) = db.videoDao()

    @Singleton
    @Provides
    fun provideRemindManagers(
        @ApplicationContext applicationContext: Context
    ) = RemindersManager(applicationContext)
}