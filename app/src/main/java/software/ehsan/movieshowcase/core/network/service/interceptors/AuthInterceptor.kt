package software.ehsan.movieshowcase.core.network.service.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

class AuthInterceptor @Inject constructor(@Named("authorization_token") private val token: String) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest =
            chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
        return chain.proceed(newRequest)
    }
}