package com.example.loginproject.data.service

import com.example.loginproject.data.local.TokenManager
import com.example.loginproject.data.remote.api.ApiService
import com.example.loginproject.models.RefreshRequest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

/**
 * وقتی سرور 401 برگردونه (یعنی access token منقضی شده)،
 * OkHttp خودکار این کلاس رو صدا می‌زنه. اینجا با refresh token
 * یه access token تازه می‌گیریم و همون درخواست رو با توکن جدید
 * دوباره می‌فرستیم.
 *
 * از Provider<ApiService> استفاده شده (نه ApiService مستقیم) چون
 * ApiService خودش از همین OkHttpClient ساخته میشه که این
 * Authenticator روشه — تزریق مستقیم باعث چرخه (circular dependency) میشه.
 */
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val apiServiceProvider: Provider<ApiService>
) : Authenticator {

    companion object {
        private val refreshMutex = Mutex()
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        return runBlocking {
            refreshMutex.withLock {
                val failedAccessToken = response.request.header("Authorization")
                    ?.removePrefix("Bearer ")

                val currentAccess = tokenManager.getAccessTokenBlocking()
                if (currentAccess != null && currentAccess != failedAccessToken) {
                    return@withLock response.request.newBuilder()
                        .header("Authorization", "Bearer $currentAccess")
                        .build()
                }

                val refreshToken = tokenManager.getRefreshTokenBlocking()
                    ?: return@withLock null

                try {
                    val refreshResponse = apiServiceProvider.get()
                        .refreshToken(RefreshRequest(refreshToken))

                    if (refreshResponse.isSuccessful) {
                        val newAccess = refreshResponse.body()?.access ?: return@withLock null
                        tokenManager.updateAccessTokenBlocking(newAccess)
                        refreshResponse.body()?.refresh?.let { newRefresh ->
                            tokenManager.updateRefreshTokenBlocking(newRefresh)
                        }
                        response.request.newBuilder()
                            .header("Authorization", "Bearer $newAccess")
                            .build()
                    } else {
                        tokenManager.clearSession()
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
