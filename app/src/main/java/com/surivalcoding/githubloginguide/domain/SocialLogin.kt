package com.surivalcoding.githubloginguide.domain

interface SocialLogin {
    suspend fun login(): Boolean
    suspend fun logout()
}