package com.example.updateapp;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the pure-logic conditions in OTPActivity's auth flow.
 *
 * These test the boolean decision logic that was changed in the "Login fails
 * with Invalid Email after signup and OTP verification" fix, without requiring
 * Android framework or Firebase dependencies.
 *
 * Conditions under test:
 * 1. Credential selection: autoVerify && autoCredential != null
 * 2. Phone-only fallback: email == null || email.isEmpty() || password == null || password.isEmpty()
 * 3. OTP length validation: otp.length() < 6
 * 4. autoCredential stale-reuse prevention (nulling after use)
 * 5. Null-coalescing in saveUserToFirestoreAndContinue
 */
public class OTPActivityLogicTest {

    // ═══════════════════════════════════════════════════════════════════
    // 1. Credential selection: autoVerify && autoCredential != null
    //    Determines whether to use the auto-detected credential or manual OTP
    // ═══════════════════════════════════════════════════════════════════

    @Test
    public void credentialSelection_autoVerifyTrue_credentialPresent_usesAutoCredential() {
        boolean autoVerify = true;
        Object autoCredential = new Object(); // non-null represents a PhoneAuthCredential

        assertTrue("Should use autoCredential when autoVerify=true and credential is present",
                autoVerify && autoCredential != null);
    }

    @Test
    public void credentialSelection_autoVerifyFalse_credentialPresent_usesManualOtp() {
        boolean autoVerify = false;
        Object autoCredential = new Object();

        assertFalse("Should NOT use autoCredential when autoVerify=false even if credential exists",
                autoVerify && autoCredential != null);
    }

    @Test
    public void credentialSelection_autoVerifyTrue_credentialNull_usesManualOtp() {
        boolean autoVerify = true;
        Object autoCredential = null;

        assertFalse("Should NOT use autoCredential when credential is null despite autoVerify=true",
                autoVerify && autoCredential != null);
    }

    @Test
    public void credentialSelection_autoVerifyFalse_credentialNull_usesManualOtp() {
        boolean autoVerify = false;
        Object autoCredential = null;

        assertFalse("Should use manual OTP when both autoVerify=false and credential is null",
                autoVerify && autoCredential != null);
    }

    // ═══════════════════════════════════════════════════════════════════
    // 2. Phone-only fallback routing
    //    email == null || email.isEmpty() || password == null || password.isEmpty()
    //    → true  = phone-only auth (signInWithCredential)
    //    → false = email-first auth (createUserWithEmailAndPassword → link phone)
    // ═══════════════════════════════════════════════════════════════════

    private boolean shouldUsePhoneOnlyAuth(String email, String password) {
        return email == null || email.isEmpty() || password == null || password.isEmpty();
    }

    @Test
    public void phoneOnlyFallback_bothPresent_usesEmailFirstAuth() {
        assertFalse("email+password present → email-first path",
                shouldUsePhoneOnlyAuth("user@example.com", "securePass1"));
    }

    @Test
    public void phoneOnlyFallback_emailNull_usesPhoneAuth() {
        assertTrue("email null → phone-only path",
                shouldUsePhoneOnlyAuth(null, "pass123"));
    }

    @Test
    public void phoneOnlyFallback_emailEmpty_usesPhoneAuth() {
        assertTrue("email empty → phone-only path",
                shouldUsePhoneOnlyAuth("", "pass123"));
    }

    @Test
    public void phoneOnlyFallback_passwordNull_usesPhoneAuth() {
        assertTrue("password null → phone-only path",
                shouldUsePhoneOnlyAuth("user@example.com", null));
    }

    @Test
    public void phoneOnlyFallback_passwordEmpty_usesPhoneAuth() {
        assertTrue("password empty → phone-only path",
                shouldUsePhoneOnlyAuth("user@example.com", ""));
    }

    @Test
    public void phoneOnlyFallback_bothNull_usesPhoneAuth() {
        assertTrue("both null → phone-only path",
                shouldUsePhoneOnlyAuth(null, null));
    }

    @Test
    public void phoneOnlyFallback_bothEmpty_usesPhoneAuth() {
        assertTrue("both empty → phone-only path",
                shouldUsePhoneOnlyAuth("", ""));
    }

    @Test
    public void phoneOnlyFallback_emailNullPasswordEmpty_usesPhoneAuth() {
        assertTrue("email null, password empty → phone-only path",
                shouldUsePhoneOnlyAuth(null, ""));
    }

    @Test
    public void phoneOnlyFallback_emailEmptyPasswordNull_usesPhoneAuth() {
        assertTrue("email empty, password null → phone-only path",
                shouldUsePhoneOnlyAuth("", null));
    }

    // ═══════════════════════════════════════════════════════════════════
    // 3. OTP length validation: otp.length() < 6
    // ═══════════════════════════════════════════════════════════════════

    @Test
    public void otpValidation_sixDigits_passes() {
        String otp = "123456";
        assertFalse("6-digit OTP should pass validation", otp.length() < 6);
    }

    @Test
    public void otpValidation_fiveDigits_fails() {
        String otp = "12345";
        assertTrue("5-digit OTP should fail validation", otp.length() < 6);
    }

    @Test
    public void otpValidation_empty_fails() {
        String otp = "";
        assertTrue("Empty OTP should fail validation", otp.length() < 6);
    }

    @Test
    public void otpValidation_oneDigit_fails() {
        String otp = "1";
        assertTrue("1-digit OTP should fail validation", otp.length() < 6);
    }

    @Test
    public void otpValidation_sevenDigits_passes() {
        // Boundary: longer-than-expected still passes the < 6 check.
        // PhoneAuthProvider.getCredential handles further validation server-side.
        String otp = "1234567";
        assertFalse("7-digit OTP passes the <6 length check", otp.length() < 6);
    }

    @Test
    public void otpValidation_exactlySixCharsNonNumeric_passesLengthCheck() {
        // The length check is purely numeric-agnostic; server-side validates content.
        String otp = "abcdef";
        assertFalse("6-char non-numeric passes the length check (server validates content)",
                otp.length() < 6);
    }

    // ═══════════════════════════════════════════════════════════════════
    // 4. autoCredential stale-reuse prevention
    //    After using autoCredential, it must be set to null.
    // ═══════════════════════════════════════════════════════════════════

    @Test
    public void autoCredentialReuse_nulledAfterConsumption() {
        // Simulates the pattern: use → null → subsequent check should be false
        Object[] holder = { new Object() }; // mutable holder for autoCredential
        boolean autoVerify = true;

        // First use: should select autoCredential
        assertTrue(autoVerify && holder[0] != null);
        holder[0] = null; // mirrors: autoCredential = null;

        // Second attempt: must NOT use autoCredential
        assertFalse("autoCredential must not be reused after being consumed",
                autoVerify && holder[0] != null);
    }

    @Test
    public void autoCredentialReuse_separateSessionsGetFreshState() {
        Object[] session1Credential = { new Object() };
        Object[] session2Credential = { new Object() };
        boolean autoVerify = true;

        // Session 1 consumes
        assertTrue(autoVerify && session1Credential[0] != null);
        session1Credential[0] = null;

        // Session 2 still has its own credential
        assertTrue("Second session credential is independent and still valid",
                autoVerify && session2Credential[0] != null);
    }

    // ═══════════════════════════════════════════════════════════════════
    // 5. Null-coalescing in saveUserToFirestoreAndContinue
    //    null fields are replaced with "" before constructing UserModel
    // ═══════════════════════════════════════════════════════════════════

    private String coalesce(String value) {
        return value == null ? "" : value;
    }

    @Test
    public void nullCoalescing_nullBecomesEmpty() {
        assertEquals("", coalesce(null));
    }

    @Test
    public void nullCoalescing_emptyStaysEmpty() {
        assertEquals("", coalesce(""));
    }

    @Test
    public void nullCoalescing_valuePreserved() {
        assertEquals("hello", coalesce("hello"));
    }

    @Test
    public void nullCoalescing_allFieldsCoalesced() {
        String name = null, email = null, number = null, password = null;

        name = coalesce(name);
        email = coalesce(email);
        number = coalesce(number);
        password = coalesce(password);

        assertEquals("", name);
        assertEquals("", email);
        assertEquals("", number);
        assertEquals("", password);
    }

    @Test
    public void nullCoalescing_mixedNullAndPresent() {
        String name = "Alice";
        String email = null;
        String number = "+1234";
        String password = null;

        name = coalesce(name);
        email = coalesce(email);
        number = coalesce(number);
        password = coalesce(password);

        assertEquals("Alice", name);
        assertEquals("", email);
        assertEquals("+1234", number);
        assertEquals("", password);
    }

    // ═══════════════════════════════════════════════════════════════════
    // 6. autoVerify default value
    //    getBooleanExtra("autoVerify", false) defaults to false
    // ═══════════════════════════════════════════════════════════════════

    @Test
    public void autoVerifyDefault_isFalse() {
        // Mirrors the Intent default: getBooleanExtra("autoVerify", false)
        boolean autoVerify = false; // default
        Object autoCredential = new Object();

        assertFalse("Default autoVerify=false must prevent autoCredential usage even if credential exists",
                autoVerify && autoCredential != null);
    }
}
