package com.storiesapp.core.di

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.github.ajalt.timberkt.e
import kotlinx.coroutines.Dispatchers
import com.storiesapp.core.AppConstant
import com.storiesapp.core.AppDispatchers
import com.storiesapp.core.BuildConfig
import com.storiesapp.core.local.DatabaseProvider
import com.storiesapp.core.local.UserSession
import com.storiesapp.core.network.NetworkResponseAdapterFactory
import com.storiesapp.core.network.Tls12SocketFactory
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.Throws

val coreModule = module {
    single { provideOkHttpClient(androidContext()) }
    single { provideRetrofit(get()) }
    single { DatabaseProvider(get()) }
    factory { AppDispatchers(Dispatchers.Main, Dispatchers.IO) }
}

fun provideOkHttpClient(context: Context): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .retryOnConnectionFailure(true)
        .connectionSpecs(listOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS))
        //.addInterceptor(chuckerInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.BODY
            }
        })
        //TODO: enable tracking interceptor
//      .addInterceptor(TrackingInterceptor())
        .addNetworkInterceptor { chain ->
            val request: Request = chain.request()
            val builder: Request.Builder

            builder = request.newBuilder()
            builder.addHeader(AppConstant.Accept, AppConstant.asJson)
            builder.addHeader(AppConstant.Authorization , UserSession.authToken)
            val newRequest: Request = builder.removeHeader("@").build()
            chain.proceed(newRequest)
        }
        .enableTls12OnPreLollipop()
        .build()

}

fun provideOkHttpClientTest(): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .retryOnConnectionFailure(true)
        .connectionSpecs(listOf(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS))
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        })
        //TODO: enable tracking interceptor
//      .addInterceptor(TrackingInterceptor())
        .addNetworkInterceptor { chain ->
            val request: Request = chain.request()
            val builder: Request.Builder

            builder = request.newBuilder()
            builder.addHeader(AppConstant.Accept, AppConstant.asJson)
            //builder.addHeader(AppConstant.Authorization, UserSession.userToken)
            val newRequest: Request = builder.removeHeader("@").build()
            chain.proceed(newRequest)
        }
        .enableTls12OnPreLollipop()
        .build()

}

/**
 * Provide Retrofit
 *
 * @param okHttpClient [OkHttpClient]
 * @return Retrofit [Retrofit]
 */
fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .build()
}

inline fun <reified T> provideApiService(retrofit: Retrofit): T {
    return retrofit.create(T::class.java)
}

private fun OkHttpClient.Builder.enableTls12OnPreLollipop(): OkHttpClient.Builder {
    if (Build.VERSION.SDK_INT in Build.VERSION_CODES.JELLY_BEAN..Build.VERSION_CODES.KITKAT) {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String
                ) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            val sc = SSLContext.getInstance("TLSv1.2")
            sc.init(null, trustAllCerts, java.security.SecureRandom())
            sslSocketFactory(
                Tls12SocketFactory(sc.socketFactory),
                trustAllCerts[0] as X509TrustManager
            )

            val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2).build()

            val specs = arrayListOf(cs, ConnectionSpec.COMPATIBLE_TLS, ConnectionSpec.CLEARTEXT)

            connectionSpecs(specs)
        } catch (exc: Exception) {
            e(exc) {
                "Error while setting TLS 1.2"
            }
        }
    }

    return this
}
