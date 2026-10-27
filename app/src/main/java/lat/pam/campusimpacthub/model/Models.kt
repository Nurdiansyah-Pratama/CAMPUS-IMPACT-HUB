package lat.pam.campusimpacthub.model

enum class UserRole(
    val code: String,
    val label: String,
    val badgeTitle: String,
    val description: String
) {
    STUDENT(
        code = "student",
        label = "Mahasiswa",
        badgeTitle = "Talenta & Kontributor Kampus",
        description = "Melihat & mendaftar proyek sosial, bergabung organisasi, daftar event (QR Ticket), dan mengelola portofolio dampak."
    ),
    PARTNER(
        code = "partner",
        label = "Mitra Masyarakat",
        badgeTitle = "UMKM / Desa / Sekolah / Komunitas",
        description = "Submit masalah nyata masyarakat, monitoring proyek mahasiswa, memberikan feedback, dan melihat laporan dampak."
    ),
    ORGANIZATION(
        code = "organization",
        label = "Organisasi / UKM",
        badgeTitle = "Pengurus BEM / Himpunan / UKM",
        description = "Mendaftarkan organisasi, membuka Open Recruitment, membuat event kampus, monitoring peserta, dan scan absensi QR."
    ),
    ADMIN(
        code = "admin",
        label = "Admin Kampus",
        badgeTitle = "Direktorat Kemahasiswaan & Pengabdian",
        description = "Verifikasi organisasi (SK), verifikasi proyek sosial & event, serta monitoring analitik dampak kampus & MBKM."
    )
}

data class User(
    val id: String,
    var name: String,
    var email: String,
    var role: UserRole,
    var majorOrUnit: String,
    var identityNumber: String,
    var impactScore: Int,
    var leadershipScore: Int,
    var projectsCompleted: Int,
    var umkmHelped: Int,
    var beneficiariesReached: Int,
    var contributionHours: Int,
    var interests: List<String>,
    var skills: List<String>,
    var bio: String
)

enum class ProjectStatus(val label: String) {
    OPEN("Open"),
    ON_GOING("On Going"),
    COMPLETED("Completed")
}

data class ImpactReport(
    val id: String,
    val projectId: String,
    var beforeCondition: String,
    var afterCondition: String,
    var beneficiaries: Int,
    var sdgCategory: String,
    var partnerFeedback: String
)

data class SocialProject(
    val id: String,
    val organizationName: String,
    val title: String,
    val description: String,
    val category: String,
    val location: String,
    val photoCaption: String,
    val impactScore: Int,
    val requirements: List<String>,
    var status: ProjectStatus,
    var verifiedByAdmin: Boolean,
    var applicantCount: Int,
    var userApplicationState: String, // "NONE", "PENDING", "APPROVED"
    var userRoleInProject: String,    // "Anggota Tim (+50 Poin)" or "Ketua Tim Proyek (+75 Poin)"
    var impactReport: ImpactReport
)

enum class VerificationStatus(val label: String) {
    PENDING("Pending"),
    VERIFIED("Verified"),
    REJECTED("Rejected")
}

data class CampusOrganization(
    val id: String,
    val name: String,
    val fullName: String,
    val category: String,
    val logoInitials: String,
    val skNumber: String,
    val advisorName: String,
    val contact: String,
    val description: String,
    var memberCount: Int,
    var verificationStatus: VerificationStatus,
    var isUserJoined: Boolean,
    var isUserOfficer: Boolean
)

enum class SelectionStage(val stepNumber: Int, val label: String) {
    PENDAFTARAN(1, "Pendaftaran"),
    ADMINISTRASI(2, "Administrasi"),
    INTERVIEW(3, "Interview"),
    PENGUMUMAN(4, "Pengumuman")
}

enum class ApplicantStatus(val label: String) {
    NONE("Belum Mendaftar"),
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected")
}

data class OpenRecruitment(
    val id: String,
    val organizationId: String,
    val organizationName: String,
    val title: String,
    val positions: List<String>,
    val deadline: String,
    val status: String, // "open" or "closed"
    var currentStage: SelectionStage,
    var selectedPosition: String?,
    var applicantStatus: ApplicantStatus,
    var totalApplicants: Int
)

enum class EventType(val label: String) {
    SEMINAR("Seminar"),
    WORKSHOP("Workshop"),
    WEBINAR("Webinar"),
    BOOTCAMP("Bootcamp"),
    VOLUNTEER("Volunteer"),
    KOMPETISI("Kompetisi"),
    PELATIHAN("Pelatihan")
}

data class CampusEvent(
    val id: String,
    val title: String,
    val organizerName: String,
    val type: EventType,
    val description: String,
    val date: String,
    val location: String,
    val quota: Int,
    var registeredCount: Int,
    val pointsReward: Int,
    val committeePointsReward: Int,
    val posterSummary: String,
    var verifiedByAdmin: Boolean,
    var isRegistered: Boolean,
    var isAttended: Boolean,
    var qrTicketCode: String
)

data class ActivityPointRule(
    val activityName: String,
    val points: Int,
    val scoreType: String, // "Impact Score" or "Impact + Leadership"
    val description: String
)

data class PortfolioEntry(
    val id: String,
    val title: String,
    val organizationOrPartner: String,
    val category: String, // "Project Sosial", "Organisasi", "Event", "Sertifikat", "Prestasi"
    val date: String,
    val impactPoints: Int,
    val leadershipPoints: Int,
    val outcomeSummary: String,
    val credentialCode: String
)

data class AiProblemMatchResult(
    val problemInput: String,
    val category: String,
    val recommendedMajors: List<String>,
    val estimatedImpact: String,
    val suggestedImpactScore: Int,
    val recommendedSolutionTitle: String
)

data class AiPersonalRecommendation(
    val majorInput: String,
    val interestsInput: List<String>,
    val recommendedOrganizations: List<String>,
    val recommendedEvents: List<String>,
    val recommendedProjects: List<String>
)
