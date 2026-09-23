package com.castillo.campusnav

import com.castillo.campusnav.data.*
import com.castillo.campusnav.navigation.Screen
import org.junit.Assert.*
import org.junit.Test

/** Reglas puras: se ejecutan sin emulador ni servicios externos. */
class CampusRulesTest {
    @Test fun directoryHasEightUniqueIds() {
        assertEquals(8, students.size)
        assertEquals(8, students.map { it.id }.toSet().size)
    }
    @Test fun searchIgnoresAccentsAndCase() {
        assertEquals(listOf(2), filterStudents(students, " MARIA ", DirectoryFilter.ALL, emptySet()).map { it.id })
    }
    @Test fun searchMatchesCareer() {
        assertEquals(listOf(1), filterStudents(students, "moviles", DirectoryFilter.ALL, emptySet()).map { it.id })
    }
    @Test fun favoritesCombineWithSearch() {
        assertEquals(listOf(3), filterStudents(students, "Carlos", DirectoryFilter.FAVORITES, setOf(2, 3)).map { it.id })
    }
    @Test fun emptyFavoritesReturnNoResults() {
        assertTrue(filterStudents(students, "", DirectoryFilter.FAVORITES, emptySet()).isEmpty())
    }
    @Test fun technologyFilterExcludesOtherAreas() {
        assertEquals(listOf(1, 3, 6), filterStudents(students, "", DirectoryFilter.TECHNOLOGY, emptySet()).map { it.id })
    }
    @Test fun unknownSearchIsEmpty() {
        assertTrue(filterStudents(students, "zzzzzz", DirectoryFilter.ALL, emptySet()).isEmpty())
    }
    @Test fun demoLoginRequiresBothCredentials() {
        assertTrue(validDemoLogin(" DEMO@CAMPUS.TEST ", "NavLab2026"))
        assertFalse(validDemoLogin("demo@campus.test", "incorrecta"))
        assertFalse(validDemoLogin("alguien@example.com", "NavLab2026"))
    }
    @Test fun defaultProfileIsValid() { assertNull(validateProfile(Profile())) }
    @Test fun invalidEmailIsRejected() { assertNotNull(validateProfile(Profile(email = "sin-arroba"))) }
    @Test fun shortNameIsRejected() { assertNotNull(validateProfile(Profile(name = "  A "))) }
    @Test fun optionalPhoneCanBeBlank() { assertNull(validateProfile(Profile(phone = ""))) }
    @Test fun invalidPhoneIsRejected() { assertNotNull(validateProfile(Profile(phone = "abcdefghi"))) }
    @Test fun biographyLimitIsEnforced() { assertNotNull(validateProfile(Profile(bio = "a".repeat(241)))) }
    @Test fun detailRouteUsesTheSelectedId() { assertEquals("detail/8", Screen.Detail.createRoute(8)) }
}
