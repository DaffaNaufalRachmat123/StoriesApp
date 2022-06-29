
object Libraries {
    // Timber for logging
    val timberLog                   = "com.github.ajalt:timberkt:1.5.1"
    // Koin for dependency injection
    val koin                        = "org.koin:koin-android:2.2.2"
    val koinViewModel               = "org.koin:koin-androidx-viewmodel:2.2.2"
    val koinScope                   = "org.koin:koin-androidx-scope:2.2.2"
    val koinExt                     = "org.koin:koin-androidx-ext:2.2.2"
    // View Binding
    val viewBindingKtx              = "com.github.DylanCaiCoding.ViewBindingKTX:viewbinding-ktx:1.2.4"
    // Shimmer
    val facebookShimmer             = "com.facebook.shimmer:shimmer:0.5.0"
    // Retrofit and OkHttp for networking
    val gson                        = "com.google.code.gson:gson:2.8.6"
    val retrofit                    = "com.squareup.retrofit2:retrofit:2.9.0"
    val retrofitGsonConverter       = "com.squareup.retrofit2:converter-gson:2.9.0"
    val okhttp                      = "com.squareup.okhttp3:okhttp:4.9.0"
    val httpLoggingInterceptor      = "com.squareup.okhttp3:logging-interceptor:4.9.0"
    val chuckerDebug                = "com.github.chuckerteam.chucker:library:3.5.2"
    val chuckerRelease              = "com.github.chuckerteam.chucker:library-no-op:3.5.2"
    // ReactiveX for Reactive Programming
    val rxJava                      = "io.reactivex.rxjava3:rxjava:3.0.1"
    val rxKotlin                    = "io.reactivex.rxjava3:rxkotlin:3.0.1"
    val rxAndroid                   = "io.reactivex.rxjava3:rxandroid:3.0.0"
    val kotPrefCore                 = "com.chibatching.kotpref:kotpref:2.13.0"
    val kotPrefInit                 = "com.chibatching.kotpref:initializer:2.13.0"
    val kotPrefGson                 = "com.chibatching.kotpref:gson-support:2.13.0"
    val BRVAH                       = "com.github.CymChad:BaseRecyclerViewAdapterHelper:2.9.50"
    val googleClient                = "com.google.api-client:google-api-client:1.30.2"
    val playServiceMaps             = "com.google.android.gms:play-services-maps:17.0.0"
    val playServiceLocation         = "com.google.android.gms:play-services-location:17.0.0"
    val sdp                         = "com.intuit.sdp:sdp-android:1.0.6"
    val lottie                      = "com.airbnb.android:lottie:3.6.0"
    // Paging 3
    val paging3                     = "androidx.paging:paging-runtime-ktx:3.0.0-alpha13"
    // Glide for Image Loader
    const val glide                 = "com.github.bumptech.glide:glide:4.11.0"
    const val glideHttpIntegration  = "com.github.bumptech.glide:okhttp3-integration:4.11.0"
    const val glideKapt             = "com.github.bumptech.glide:compiler:4.11.0"
    const val glideTransformations  = "jp.wasabeef:glide-transformations:4.3.0"
    //material dialogs
    val mdCore                      = "com.afollestad.material-dialogs:core:3.3.0"
    val mdDateTime                  = "com.afollestad.material-dialogs:datetime:3.3.0"
    val mdBottomSheet               = "com.afollestad.material-dialogs:bottomsheets:3.3.0"
    val mdLifecycle                 = "com.afollestad.material-dialogs:lifecycle:3.3.0"
}

object KotlinLibraries {
    val kotlin                      = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.20"
    val kotlinCoroutineCore         = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2"
    val kotlinCoroutineAndroid      = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.2"
    val kotlinCoroutinePlayService  = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.4.2"
}

object AndroidLibraries {
    // ANDROID
    val appCompat                   = "androidx.appcompat:appcompat:1.4.1"
    val activityKtx                 = "androidx.activity:activity-ktx:1.2.0"
    val material                    = "com.google.android.material:material:1.3.0-alpha04"
    val recyclerView                = "androidx.recyclerview:recyclerview:1.2.0-beta01"
    val coreKtx                     = "androidx.core:core-ktx:1.5.0-alpha05"
    val lifecycleViewModel          = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0-beta01"
    val lifecycleRuntimeKtx         = "androidx.lifecycle:lifecycle-runtime-ktx:2.3.0-beta01"
    val lifecycleCommon             = "androidx.lifecycle:lifecycle-common-java8:2.3.0-beta01"
    val lifecycleLiveData           = "androidx.lifecycle:lifecycle-livedata-ktx:2.3.0-beta01"
    val swipeRefreshLayout          = "androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01"
    // ROOM
    val roomCompiler                = "androidx.room:room-compiler:2.3.0-beta01"
    val roomRunTime                 = "androidx.room:room-runtime:2.3.0-beta01"
    val roomKtx                     = "androidx.room:room-ktx:2.3.0-beta01"
}

object TestLibraries {
    // Junit
    val junit = "androidx.test.ext:junit:1.1.2"
    // Android test
    val androidTestRunner = "androidx.test:runner:2.2.2"
    val archCoreTest = "androidx.arch.core:core-testing:2.1.0"
    // Kotlin Coroutine
    val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2"
    // Koin
    val koin = "org.koin:koin-test:2.2.2"
    // LiveData
    val liveData = "com.jraska.livedata:testing-ktx:1.1.0"
}