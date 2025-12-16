package cl.duoc.app.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
  
    // - EMULADOR Android Studio: usa "10.0.2.2"
    // - DISPOSITIVO FÍSICO: usa la IP de tu PC (ejecuta: ipconfig)
    //   Ejemplo: "192.168.1.100" o "192.168.43.1" o "10.0.0.5"
    private const val HOST_IP = "192.168.1.112"  // 
    // URLs de los BFF (Backend for Frontend)
    private const val USERS_BASE_URL = "http://$HOST_IP:8083/bff/"
    private const val NEWS_BASE_URL = "http://$HOST_IP:8183/bff/"
    private const val BLOGS_BASE_URL = "http://$HOST_IP:8283/bff/"

    // Logging interceptor para debug
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente HTTP con timeouts y logging
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Retrofit para Users
    private val retrofitUsers: Retrofit = Retrofit.Builder()
        .baseUrl(USERS_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Retrofit para News
    private val retrofitNews: Retrofit = Retrofit.Builder()
        .baseUrl(NEWS_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Retrofit para Blogs
    private val retrofitBlogs: Retrofit = Retrofit.Builder()
        .baseUrl(BLOGS_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val userApiService: UserApiService = retrofitUsers.create(UserApiService::class.java)
    val newsApiService: NewsApiService = retrofitNews.create(NewsApiService::class.java)
    val blogApiService: BlogApiService = retrofitBlogs.create(BlogApiService::class.java)
}