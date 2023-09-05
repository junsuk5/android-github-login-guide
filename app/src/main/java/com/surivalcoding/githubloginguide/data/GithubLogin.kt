package com.surivalcoding.githubloginguide.data

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.surivalcoding.githubloginguide.domain.SocialLogin
import kotlinx.coroutines.tasks.await

class GithubLogin(private val activity: Activity) : SocialLogin {
    private val firebaseAuth = FirebaseAuth.getInstance()

    override suspend fun login(): Boolean {
        val provider = OAuthProvider.newBuilder("github.com")
        provider.scopes = listOf("user:email")

        val pendingResultTask = firebaseAuth.pendingAuthResult
        if (pendingResultTask != null) {
            // There's something already here! Finish the sign-in for your user.
            var result = false
            pendingResultTask
                .addOnSuccessListener {
                    // User is signed in.
                    // IdP data available in
                    // authResult.getAdditionalUserInfo().getProfile().
                    // The OAuth access token can also be retrieved:
                    // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                    // The OAuth secret can be retrieved by calling:
                    // ((OAuthCredential)authResult.getCredential()).getSecret().
                    result = true
                }
                .addOnFailureListener {
                    // Handle failure.
                    result = false
                }.await()
            return result
        } else {
            // There's no pending result so you need to start the sign-in flow.
            // See below.
            return signInWithGithub(provider)
        }
    }

    override suspend fun logout() {
        Firebase.auth.signOut()
    }

    private suspend fun signInWithGithub(provider: OAuthProvider.Builder): Boolean {
        var result = false;
        firebaseAuth
            .startActivityForSignInWithProvider(activity, provider.build())
            .addOnSuccessListener {
                // User is signed in.
                // IdP data available in
                // authResult.getAdditionalUserInfo().getProfile().
                // The OAuth access token can also be retrieved:
                // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                // The OAuth secret can be retrieved by calling:
                // ((OAuthCredential)authResult.getCredential()).getSecret().
                result = true
            }
            .addOnFailureListener {
                // Handle failure.
                result = false
            }.await()
        return result
    }
}