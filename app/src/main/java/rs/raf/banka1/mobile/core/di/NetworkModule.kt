package rs.raf.banka1.mobile.core.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import rs.raf.banka1.mobile.BuildConfig
import rs.raf.banka1.mobile.core.network.call_adapters.NetworkResultCallAdapterFactory
import rs.raf.banka1.mobile.core.network.interceptors.AuthInterceptor
import rs.raf.banka1.mobile.core.util.JsonParser
import rs.raf.banka1.mobile.data.apis.AccountApi
import rs.raf.banka1.mobile.data.apis.AuthApi
import rs.raf.banka1.mobile.data.apis.CardApi
import rs.raf.banka1.mobile.data.apis.ExchangeApi
import rs.raf.banka1.mobile.data.apis.TransactionApi
import rs.raf.banka1.mobile.data.apis.TransferApi
import rs.raf.banka1.mobile.data.apis.VerificationApi
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    fun provideJsonParser(moshi: Moshi) = JsonParser(moshi)

    @Singleton
    @Provides
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(authInterceptor)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
        jsonParser: JsonParser
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(NetworkResultCallAdapterFactory.create(jsonParser))
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Singleton
    @Provides
    fun provideAccountApi(retrofit: Retrofit): AccountApi {
        return retrofit.create(AccountApi::class.java)
    }

    @Singleton
    @Provides
    fun provideCardApi(retrofit: Retrofit): CardApi {
        return retrofit.create(CardApi::class.java)
    }

    @Singleton
    @Provides
    fun provideTransferApi(retrofit: Retrofit): TransferApi {
        return retrofit.create(TransferApi::class.java)
    }

    @Singleton
    @Provides
    fun provideTransactionApi(retrofit: Retrofit): TransactionApi {
        return retrofit.create(TransactionApi::class.java)
    }

    @Singleton
    @Provides
    fun provideExchangeApi(retrofit: Retrofit): ExchangeApi {
        return retrofit.create(ExchangeApi::class.java)
    }

    @Singleton
    @Provides
    fun provideVerificationApi(retrofit: Retrofit): VerificationApi {
        return retrofit.create(VerificationApi::class.java)
    }
}