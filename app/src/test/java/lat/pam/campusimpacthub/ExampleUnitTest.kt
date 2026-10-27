package lat.pam.campusimpacthub

import lat.pam.campusimpacthub.data.CampusRepository
import lat.pam.campusimpacthub.model.UserRole
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun aiProblemMatching_returnsDigitalMarketingForUmkm() {
        val result = CampusRepository.matchProblemWithAi("UMKM kesulitan pemasaran online")
        assertEquals("Digital Marketing", result.category)
        assertTrue(result.recommendedMajors.contains("Bisnis Digital"))
        assertTrue(result.recommendedMajors.contains("Informatika"))
        assertTrue(result.recommendedMajors.contains("DKV"))
    }

    @Test
    fun activityPointsAndRoles_areConfiguredCorrectly() {
        assertEquals(5, CampusRepository.activityPointRules.size)
        assertEquals(4, UserRole.entries.size)
        val json = CampusRepository.getMvpDatabaseJsonSnapshot()
        assertTrue(json.contains("ORG001"))
        assertTrue(json.contains("HIMATIF"))
    }
}
