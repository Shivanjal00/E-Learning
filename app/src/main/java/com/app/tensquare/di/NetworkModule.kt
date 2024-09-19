package com.app.tensquare.di

import android.util.Log
import com.app.tensquare.base.SharedPrefManager
import com.app.tensquare.constants.NetworkConstants
import com.app.tensquare.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    /*@Singleton
    @Provides
    fun provideSharedPref(
        @ApplicationContext context: Context
    ) = SharedPrefManager(context)*/


    /*@Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }*/

    @Provides
    @Named("baseUrl")
    fun provideBaseUrl() = NetworkConstants.BASE_URL

    @Provides
    @Singleton
    fun provideOkHttpClient(sharedPrefManager: SharedPrefManager): OkHttpClient /* if (BuildConfig.DEBUG) */ {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            this.setLevel(HttpLoggingInterceptor.Level.BODY)
        }


//        Log.e("User token =>" ,sharedPrefManager.getString("user_token").toString() )

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    //.addHeader("token", sharedPrefManager.getUserToken().toString())
//                    .addHeader("Authorization", "Bearer ${sharedPrefManager.getString("token")}")
                    .build()
                chain.proceed(newRequest)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }  /*else OkHttpClient
        .Builder()
        .build()*/

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create()

    @Singleton
    @Provides
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory, @Named("baseUrl") BASE_URL: String
    ): Retrofit {
        Log.e("PrintApiBaseUrl","URL -> $BASE_URL")
        return Retrofit.Builder().baseUrl("${NetworkConstants.BASE_URL}api/")
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

}