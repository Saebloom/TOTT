package com.example.tott

import com.example.tott.auth.IAuthManager
import com.example.tott.auth.Localization
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import com.google.firebase.auth.FirebaseUser
import kotlin.Result

// A simple fake FirebaseUser for testing purposes
class FakeUser(val uid: String) : FirebaseUser() {
    override fun getUid(): String = uid
    // The rest of FirebaseUser methods are not needed for these tests and can throw UnsupportedOperationException
    override fun getProviderId(): String { throw UnsupportedOperationException() }
    override fun isAnonymous(): Boolean { throw UnsupportedOperationException() }
    override fun getDisplayName(): String? { throw UnsupportedOperationException() }
    override fun getPhotoUrl(): android.net.Uri? { throw UnsupportedOperationException() }
    override fun getEmail(): String? { throw UnsupportedOperationException() }
    override fun getPhoneNumber(): String? { throw UnsupportedOperationException() }
    override fun getTenantId(): String? { throw UnsupportedOperationException() }
    override fun getMetadata(): com.google.firebase.auth.FirebaseUserMetadata? { throw UnsupportedOperationException() }
    override fun getProviderData(): MutableList<com.google.firebase.auth.UserInfo> { throw UnsupportedOperationException() }
    override fun getIdToken(forceRefresh: Boolean) = throw UnsupportedOperationException()
    override fun reload() = throw UnsupportedOperationException()
    override fun delete() = throw UnsupportedOperationException()
    override fun linkWithCredential(credential: com.google.firebase.auth.AuthCredential) = throw UnsupportedOperationException()
    override fun unlink(provider: String) = throw UnsupportedOperationException()
    override fun reauthenticate(credential: com.google.firebase.auth.AuthCredential) = throw UnsupportedOperationException()
    override fun getEmailVerified() = throw UnsupportedOperationException()
}

// Fake auth manager implementing IAuthManager for testing flows
class FakeAuthManager(var succeed: Boolean = true) : IAuthManager {
    private var signedInUid: String? = null

    override fun init(context: android.content.Context) { /* no-op */ }
    override fun isSignedIn(): Boolean = signedInUid != null
    override fun getUid(): String? = signedInUid
    override suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser?> {
        return if (succeed) {
            signedInUid = "fake-uid-123"
            Result.success(FakeUser(signedInUid!!))
        } else {
            Result.failure(Exception("Registro fallido (fake)"))
        }
    }
    override suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser?> {
        return if (succeed) {
            signedInUid = "fake-uid-123"
            Result.success(FakeUser(signedInUid!!))
        } else {
            Result.failure(Exception("Credenciales inválidas (fake)"))
        }
    }
    override fun signOut() { signedInUid = null }
}

class AuthFlowUnitTest {

    @Test
    fun validation_accepts_good_email_and_password() {
        val email = "test@example.com"
        val password = "correcthorsebattery"
        assertTrue(android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        assertTrue(password.length >= 6)
    }

    @Test
    fun fakeAuth_signUp_success() = runBlocking {
        val fake = FakeAuthManager(succeed = true)
        val res = fake.signUpWithEmail("a@b.com", "password123")
        assertTrue(res.isSuccess)
        val user = res.getOrNull()
        assertNotNull(user)
        assertEquals("fake-uid-123", user?.uid)
        assertTrue(fake.isSignedIn())
    }

    @Test
    fun fakeAuth_signUp_failure() = runBlocking {
        val fake = FakeAuthManager(succeed = false)
        val res = fake.signUpWithEmail("a@b.com", "pass")
        assertTrue(res.isFailure)
        assertFalse(fake.isSignedIn())
    }

    @Test
    fun fakeAuth_signIn_success_and_signOut() = runBlocking {
        val fake = FakeAuthManager(succeed = true)
        val res = fake.signInWithEmail("a@b.com", "password")
        assertTrue(res.isSuccess)
        assertTrue(fake.isSignedIn())
        fake.signOut()
        assertFalse(fake.isSignedIn())
    }
}
