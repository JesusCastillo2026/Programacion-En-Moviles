package com.castillo.navlab.base

import com.castillo.navlab.base.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationRoutesTest {
    @Test fun destinationsMatchLaboratory() {
        assertEquals("home", Screen.Home.route)
        assertEquals("list", Screen.List.route)
        assertEquals("profile", Screen.Profile.route)
        assertEquals("detail/{itemId}", Screen.Detail.route)
    }

    @Test fun allEightElementsKeepTheirId() {
        (1..8).forEach { assertEquals("detail/$it", Screen.Detail.createRoute(it)) }
    }
}
