package com.example.starline.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

interface AuthRepository {
    val currentUser: StateFlow<UserSession?>
    suspend fun login(email: String, password: String): Result<UserSession>
    suspend fun register(email: String, password: String, displayName: String): Result<UserSession>
    suspend fun logout(): Result<Unit>
    suspend fun verifySession(): Result<UserSession>
}

class FirebaseAuthRepository(context: Context) : AuthRepository {
    private val sharedPrefs = context.getSharedPreferences("starline_mock_auth", Context.MODE_PRIVATE)
    private val _currentUser = MutableStateFlow<UserSession?>(null)
    override val currentUser: StateFlow<UserSession?> = _currentUser.asStateFlow()

    private var useFirebase: Boolean = false
    private var firebaseAuth: FirebaseAuth? = null

    init {
        try {
            // Attempt to access Firebase Auth. If google-services.json is missing
            // or invalid, this will throw an exception.
            firebaseAuth = FirebaseAuth.getInstance()
            useFirebase = firebaseAuth != null
            
            // Synchronously check for cached user session on startup
            val initialFbUser = firebaseAuth?.currentUser
            if (initialFbUser != null) {
                _currentUser.value = UserSession(
                    uid = initialFbUser.uid,
                    email = initialFbUser.email ?: "",
                    displayName = initialFbUser.displayName ?: "Space Explorer",
                    isLoggedIn = true
                )
            } else {
                val isLoggedIn = sharedPrefs.getBoolean("is_logged_in", false)
                if (isLoggedIn) {
                    _currentUser.value = UserSession(
                        uid = sharedPrefs.getString("uid", "mock_uid") ?: "mock_uid",
                        email = sharedPrefs.getString("email", "explorer@cosmosapp.space") ?: "explorer@cosmosapp.space",
                        displayName = sharedPrefs.getString("display_name", "Space Explorer") ?: "Space Explorer",
                        isLoggedIn = true
                    )
                }
            }
            
            firebaseAuth?.addAuthStateListener { auth ->
                val fbUser = auth.currentUser
                if (fbUser != null) {
                    _currentUser.value = UserSession(
                        uid = fbUser.uid,
                        email = fbUser.email ?: "",
                        displayName = fbUser.displayName ?: "Space Explorer",
                        isLoggedIn = true
                    )
                } else {
                    val isLoggedIn = sharedPrefs.getBoolean("is_logged_in", false)
                    if (isLoggedIn) {
                        _currentUser.value = UserSession(
                            uid = sharedPrefs.getString("uid", "mock_uid") ?: "mock_uid",
                            email = sharedPrefs.getString("email", "explorer@cosmosapp.space") ?: "explorer@cosmosapp.space",
                            displayName = sharedPrefs.getString("display_name", "Space Explorer") ?: "Space Explorer",
                            isLoggedIn = true
                        )
                    } else {
                        _currentUser.value = null
                    }
                }
            }
        } catch (e: Exception) {
            useFirebase = false
            // Check local SharedPreferences for persistent mock session
            val isLoggedIn = sharedPrefs.getBoolean("is_logged_in", false)
            if (isLoggedIn) {
                _currentUser.value = UserSession(
                    uid = sharedPrefs.getString("uid", "mock_uid") ?: "mock_uid",
                    email = sharedPrefs.getString("email", "explorer@cosmosapp.space") ?: "explorer@cosmosapp.space",
                    displayName = sharedPrefs.getString("display_name", "Space Explorer") ?: "Space Explorer",
                    isLoggedIn = true
                )
            } else {
                _currentUser.value = null
            }
        }
    }

    override suspend fun login(email: String, password: String): Result<UserSession> {
        if (useFirebase && firebaseAuth != null) {
            return try {
                val authResult = firebaseAuth!!.signInWithEmailAndPassword(email, password).await()
                val fbUser = authResult.user!!
                val session = UserSession(
                    uid = fbUser.uid,
                    email = fbUser.email ?: email,
                    displayName = fbUser.displayName ?: "Space Explorer",
                    isLoggedIn = true
                )
                _currentUser.value = session
                Result.success(session)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            // Mock Login Flow
            return if (email.contains("@") && password.length >= 6) {
                val name = email.substringBefore("@").replaceFirstChar { it.uppercase() }
                val session = UserSession(
                    uid = "mock_${email.hashCode()}",
                    email = email,
                    displayName = name,
                    isLoggedIn = true
                )
                sharedPrefs.edit()
                    .putBoolean("is_logged_in", true)
                    .putString("uid", session.uid)
                    .putString("email", session.email)
                    .putString("display_name", session.displayName)
                    .apply()
                _currentUser.value = session
                Result.success(session)
            } else {
                Result.failure(Exception("Invalid email format or password must be at least 6 characters."))
            }
        }
    }

    override suspend fun register(email: String, password: String, displayName: String): Result<UserSession> {
        if (useFirebase && firebaseAuth != null) {
            return try {
                val authResult = firebaseAuth!!.createUserWithEmailAndPassword(email, password).await()
                val fbUser = authResult.user!!
                // Update profile display name
                try {
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build()
                    fbUser.updateProfile(profileUpdates).await()
                } catch (pe: Exception) {
                    // Suppress and continue
                }
                val session = UserSession(
                    uid = fbUser.uid,
                    email = fbUser.email ?: email,
                    displayName = displayName.ifEmpty { "Space Explorer" },
                    isLoggedIn = true
                )
                _currentUser.value = session
                Result.success(session)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            // Mock Register Flow
            return if (email.contains("@") && password.length >= 6) {
                val name = displayName.ifEmpty { email.substringBefore("@").replaceFirstChar { it.uppercase() } }
                val session = UserSession(
                    uid = "mock_${email.hashCode()}",
                    email = email,
                    displayName = name,
                    isLoggedIn = true
                )
                sharedPrefs.edit()
                    .putBoolean("is_logged_in", true)
                    .putString("uid", session.uid)
                    .putString("email", session.email)
                    .putString("display_name", session.displayName)
                    .apply()
                _currentUser.value = session
                Result.success(session)
            } else {
                Result.failure(Exception("Invalid email format or password must be at least 6 characters."))
            }
        }
    }

    override suspend fun logout(): Result<Unit> {
        if (useFirebase && firebaseAuth != null) {
            return try {
                firebaseAuth!!.signOut()
                _currentUser.value = null
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            sharedPrefs.edit().clear().apply()
            _currentUser.value = null
            return Result.success(Unit)
        }
    }

    override suspend fun verifySession(): Result<UserSession> {
        if (useFirebase && firebaseAuth != null) {
            val user = firebaseAuth!!.currentUser
            if (user != null) {
                return try {
                    user.reload().await()
                    val session = UserSession(
                        uid = user.uid,
                        email = user.email ?: "",
                        displayName = user.displayName ?: "Space Explorer",
                        isLoggedIn = true
                    )
                    _currentUser.value = session
                    Result.success(session)
                } catch (e: Exception) {
                    logout()
                    Result.failure(e)
                }
            }
            return Result.failure(Exception("No active session"))
        } else {
            val current = _currentUser.value
            return if (current != null) {
                Result.success(current)
            } else {
                Result.failure(Exception("No active session"))
            }
        }
    }
}
