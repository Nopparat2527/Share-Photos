package com.example

import com.example.data.model.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testLanguageStrings() {
        val vaultTh = LanguageStrings.get("tab_vault", isThai = true)
        val vaultEn = LanguageStrings.get("tab_vault", isThai = false)
        assertEquals("คลังรูป", vaultTh)
        assertEquals("Vault", vaultEn)

        val searchTh = LanguageStrings.get("search_hint", isThai = true)
        assertTrue(searchTh.contains("ค้นหา"))

        val faceTh = LanguageStrings.get("face_search_title", isThai = true)
        assertTrue(faceTh.contains("ใบหน้า"))
    }

    @Test
    fun testPhotoCategoryEnum() {
        assertEquals("all", PhotoCategory.ALL.id)
        assertEquals("people", PhotoCategory.PEOPLE.id)
        assertEquals("travel", PhotoCategory.TRAVEL.id)
        assertEquals("cyberpunk", PhotoCategory.CYBERPUNK.id)
    }

    @Test
    fun testPurchasePointDiscountMath() {
        val price = 200.0
        val pointsUsed = 500
        val discount = pointsUsed / 10.0 // 50.0 THB
        val net = price - discount // 150.0 THB
        val pointsEarned = (net * 0.1).toInt() // 15 points

        assertEquals(50.0, discount, 0.001)
        assertEquals(150.0, net, 0.001)
        assertEquals(15, pointsEarned)
    }

    @Test
    fun testAuthAndFaceRecognitionModels() {
        val user = UserProfile(
            userId = "usr-1",
            name = "Test User",
            email = "user@test.com",
            provider = AuthProvider.FACEBOOK
        )
        assertEquals(AuthProvider.FACEBOOK, user.provider)
        assertTrue(user.isLoggedIn)

        val photo = VaultPhoto(
            id = "p-1",
            title = "Portrait Test",
            description = "Test face",
            category = "Portrait",
            tags = listOf("face", "smile"),
            detectedFaces = listOf("Emma", "Alice")
        )
        assertEquals(2, photo.detectedFaces.size)
        assertTrue(photo.detectedFaces.contains("Emma"))
    }
}
