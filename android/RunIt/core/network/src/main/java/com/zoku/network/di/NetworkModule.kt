package com.zoku.network.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.zoku.network.BuildConfig
import com.zoku.network.api.AttendanceApi
import com.zoku.network.api.ExpApi
import com.zoku.network.api.GroupApi
import com.zoku.network.api.LoginApi
import com.zoku.network.api.RouteApi
import com.zoku.network.api.RunningApi
import com.zoku.network.api.TestApi
import com.zoku.network.api.UserApi
import com.zoku.network.util.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        .setLenient()
        .create()

    @Singleton
    @Provides
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()
    }

    @Singleton
    @Provides
    @Named("test")
    fun provideTestRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    @Named("runit")
    fun provideRunitRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
//        .baseUrl("https://jsonplaceholder.typicode.com/")
        .baseUrl(BuildConfig.SERVER_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    fun provideTestApiService(@Named("test") retrofit: Retrofit): TestApi =
        retrofit.create(TestApi::class.java)

    @Singleton
    @Provides
    fun provideRunningApiService(@Named("runit") retrofit: Retrofit): RunningApi =
        retrofit.create(RunningApi::class.java)

    @Singleton
    @Provides
    fun provideLoginApiService(@Named("runit") retrofit: Retrofit): LoginApi =
        retrofit.create(LoginApi::class.java)

    @Singleton
    @Provides
    fun provideExpApiService(@Named("runit") retrofit: Retrofit) : ExpApi =
        retrofit.create(ExpApi::class.java)

    @Singleton
    @Provides
    fun provideUserApiService(@Named("runit") retrofit: Retrofit) : UserApi =
        retrofit.create(UserApi::class.java)

    @Singleton
    @Provides
    fun provideGroupService(@Named("runit") retrofit: Retrofit) : GroupApi =
        retrofit.create(GroupApi::class.java)

    @Singleton
    @Provides
    fun provideAttendanceService(@Named("runit") retrofit: Retrofit) : AttendanceApi =
        retrofit.create(AttendanceApi::class.java)

    @Singleton
    @Provides
    fun provideRouteService(@Named("runit") retrofit: Retrofit) : RouteApi =
        retrofit.create(RouteApi::class.java)
}