package com.example.updateapp;

import com.example.updateapp.models.UserModel;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for UserModel — verifies construction, getters/setters,
 * and null-safety behaviour that mirrors saveUserToFirestoreAndContinue().
 */
public class UserModelTest {

    // ── Normal: full construction ──────────────────────────────────────

    @Test
    public void constructor_allFields_setsCorrectly() {
        UserModel user = new UserModel("Alice", "alice@example.com", "+1234567890", "pass123", "https://img.url/pic.png");

        assertEquals("Alice", user.getName());
        assertEquals("alice@example.com", user.getEmail());
        assertEquals("+1234567890", user.getNumber());
        assertEquals("pass123", user.getPassword());
        assertEquals("https://img.url/pic.png", user.getProfile());
    }

    @Test
    public void defaultConstructor_fieldsAreNull() {
        UserModel user = new UserModel();

        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getNumber());
        assertNull(user.getPassword());
        assertNull(user.getProfile());
    }

    // ── Normal: setters ────────────────────────────────────────────────

    @Test
    public void setters_overwriteValues() {
        UserModel user = new UserModel("A", "a@b.c", "111", "pw", "url");

        user.setName("Bob");
        user.setEmail("bob@example.com");
        user.setNumber("+9876543210");
        user.setPassword("newpass");
        user.setProfile("https://new.url/img.png");

        assertEquals("Bob", user.getName());
        assertEquals("bob@example.com", user.getEmail());
        assertEquals("+9876543210", user.getNumber());
        assertEquals("newpass", user.getPassword());
        assertEquals("https://new.url/img.png", user.getProfile());
    }

    // ── Boundary: null fields (mirrors saveUserToFirestoreAndContinue null-coalescing) ──

    @Test
    public void constructor_nullName_acceptsNull() {
        UserModel user = new UserModel(null, "e@x.com", "+1", "pw", "url");
        assertNull(user.getName());
    }

    @Test
    public void constructor_nullEmail_acceptsNull() {
        UserModel user = new UserModel("N", null, "+1", "pw", "url");
        assertNull(user.getEmail());
    }

    @Test
    public void constructor_nullNumber_acceptsNull() {
        UserModel user = new UserModel("N", "e@x.com", null, "pw", "url");
        assertNull(user.getNumber());
    }

    @Test
    public void constructor_nullPassword_acceptsNull() {
        UserModel user = new UserModel("N", "e@x.com", "+1", null, "url");
        assertNull(user.getPassword());
    }

    @Test
    public void constructor_allNulls_acceptsNull() {
        UserModel user = new UserModel(null, null, null, null, null);
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getNumber());
        assertNull(user.getPassword());
        assertNull(user.getProfile());
    }

    // ── Boundary: empty strings ────────────────────────────────────────

    @Test
    public void constructor_emptyStrings_storedAsEmpty() {
        UserModel user = new UserModel("", "", "", "", "");

        assertEquals("", user.getName());
        assertEquals("", user.getEmail());
        assertEquals("", user.getNumber());
        assertEquals("", user.getPassword());
        assertEquals("", user.getProfile());
    }

    @Test
    public void setName_toNull_allowsNull() {
        UserModel user = new UserModel("Name", "e@x.com", "+1", "pw", "url");
        user.setName(null);
        assertNull(user.getName());
    }

    @Test
    public void setEmail_toEmpty_allowsEmpty() {
        UserModel user = new UserModel("Name", "e@x.com", "+1", "pw", "url");
        user.setEmail("");
        assertEquals("", user.getEmail());
    }
}
