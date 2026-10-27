package lat.pam.campusimpacthub.data

import lat.pam.campusimpacthub.model.ActivityPointRule
import lat.pam.campusimpacthub.model.AiPersonalRecommendation
import lat.pam.campusimpacthub.model.AiProblemMatchResult
import lat.pam.campusimpacthub.model.ApplicantStatus
import lat.pam.campusimpacthub.model.CampusEvent
import lat.pam.campusimpacthub.model.CampusOrganization
import lat.pam.campusimpacthub.model.EventType
import lat.pam.campusimpacthub.model.ImpactReport
import lat.pam.campusimpacthub.model.OpenRecruitment
import lat.pam.campusimpacthub.model.PortfolioEntry
import lat.pam.campusimpacthub.model.ProjectStatus
import lat.pam.campusimpacthub.model.SelectionStage
import lat.pam.campusimpacthub.model.SocialProject
import lat.pam.campusimpacthub.model.User
import lat.pam.campusimpacthub.model.UserRole
import lat.pam.campusimpacthub.model.VerificationStatus

object CampusRepository {

    var isLoggedIn: Boolean = true

    val currentUser = User(
        id = "U001",
        name = "Budi Santoso",
        email = "budi@mail.com",
        role = UserRole.STUDENT,
        majorOrUnit = "S1 Teknik Informatika • Semester 6",
        identityNumber = "NIM 13523001",
        impactScore = 250,
        leadershipScore = 125,
        projectsCompleted = 8,
        umkmHelped = 5,
        beneficiariesReached = 1000,
        contributionHours = 64,
        interests = listOf("Programming", "UI/UX", "Startup"),
        skills = listOf("Kotlin Android", "UI/UX Design", "Supabase & PostgreSQL", "Project Management"),
        bio = "Mahasiswa Informatika yang berfokus pada pengembangan solusi teknologi tepat guna untuk digitalisasi UMKM dan desa mandiri."
    )

    val activityPointRules = listOf(
        ActivityPointRule(
            activityName = "Mengikuti Event",
            points = 10,
            scoreType = "Impact Score (+10)",
            description = "Hadir dan melakukan QR Scan pada seminar, workshop, webinar, atau volunteer kampus."
        ),
        ActivityPointRule(
            activityName = "Panitia Event",
            points = 20,
            scoreType = "Impact (+20) & Leadership (+20)",
            description = "Berkontribusi sebagai panitia pelaksana kegiatan atau ketua pelaksana event kampus."
        ),
        ActivityPointRule(
            activityName = "Pengurus Organisasi",
            points = 30,
            scoreType = "Impact (+30) & Leadership (+30)",
            description = "Menjadi pengurus aktif UKM, Himpunan Mahasiswa, atau BEM yang terverifikasi."
        ),
        ActivityPointRule(
            activityName = "Proyek Sosial",
            points = 50,
            scoreType = "Impact Score (+50)",
            description = "Terjun langsung menyelesaikan masalah mitra masyarakat, UMKM, sekolah, atau desa."
        ),
        ActivityPointRule(
            activityName = "Ketua Tim Proyek",
            points = 75,
            scoreType = "Impact (+75) & Leadership (+75)",
            description = "Memimpin tim lintas jurusan dalam implementasi solusi proyek dampak sosial."
        )
    )

    val projects = mutableListOf(
        SocialProject(
            id = "P001",
            organizationName = "UMKM Maju Jaya",
            title = "Digitalisasi UMKM",
            description = "Pencatatan transaksi harian masih manual menggunakan buku tulis dan belum memiliki katalog digital. Membutuhkan sistem kasir (POS) mobile sederhana serta desain identitas visual.",
            category = "Teknologi",
            location = "Kec. Coblong, Bandung",
            photoCaption = "Foto: Warung & Produksi UMKM Maju Jaya (Dokumentasi Lapangan)",
            impactScore = 85,
            requirements = listOf("2 Informatika", "1 Desain"),
            status = ProjectStatus.OPEN,
            verifiedByAdmin = true,
            applicantCount = 14,
            userApplicationState = "PENDING",
            userRoleInProject = "Ketua Tim Proyek (+75 Poin)",
            impactReport = ImpactReport(
                id = "I001",
                projectId = "P001",
                beforeCondition = "Manual (Pembukuan kertas & rekap bulanan rawan selisih)",
                afterCondition = "Digital (Aplikasi Kasir Android & Katalog Online Terintegrasi)",
                beneficiaries = 120,
                sdgCategory = "SDG 8: Pekerjaan Layak & Pertumbuhan Ekonomi",
                partnerFeedback = "Sangat membantu mempercepat pembukuan harian dan meningkatkan omzet pesanan online UMKM kami."
            )
        ),
        SocialProject(
            id = "P002",
            organizationName = "Desa Sukamaju Mandiri",
            title = "Digitalisasi Bank Sampah",
            description = "Desa memiliki masalah lingkungan dan administrasi penimbangan sampah warga. Dibutuhkan aplikasi pencatatan tabungan sampah warga berbasis QR dan dashboard rekapitulasi daur ulang.",
            category = "Lingkungan",
            location = "Desa Sukamaju, Kab. Bandung",
            photoCaption = "Foto: Posko Bank Sampah RW 04 Desa Sukamaju",
            impactScore = 90,
            requirements = listOf("2 Informatika", "1 Teknik Lingkungan"),
            status = ProjectStatus.OPEN,
            verifiedByAdmin = true,
            applicantCount = 19,
            userApplicationState = "NONE",
            userRoleInProject = "Anggota Proyek Sosial (+50 Poin)",
            impactReport = ImpactReport(
                id = "I002",
                projectId = "P002",
                beforeCondition = "Manual (Kartu kertas sering hilang & rekap tonase lambat)",
                afterCondition = "Digital (Buku Tabungan Sampah Digital & Grafik Reduksi Sampah)",
                beneficiaries = 320,
                sdgCategory = "SDG 11 & 13: Kota Berkelanjutan & Penanganan Perubahan Iklim",
                partnerFeedback = "Warga kini lebih semangat memilah sampah karena saldo tabungan bisa dicek langsung dari ponsel."
            )
        ),
        SocialProject(
            id = "P003",
            organizationName = "SDN 03 Harapan Bangsa",
            title = "Solusi Media Edukasi & Literasi Sekolah",
            description = "Sekolah membutuhkan solusi pendidikan berupa modul pembelajaran sains dan literasi numerasi interaktif agar siswa lebih mudah memahami konsep dasar secara visual.",
            category = "Pendidikan",
            location = "Kec. Cibiru, Bandung",
            photoCaption = "Foto: Ruang Kelas & Perpustakaan SDN 03 Harapan Bangsa",
            impactScore = 75,
            requirements = listOf("1 Informatika", "1 DKV", "1 Pendidikan"),
            status = ProjectStatus.ON_GOING,
            verifiedByAdmin = true,
            applicantCount = 11,
            userApplicationState = "APPROVED",
            userRoleInProject = "Anggota Proyek Sosial (+50 Poin)",
            impactReport = ImpactReport(
                id = "I003",
                projectId = "P003",
                beforeCondition = "Manual (Alat peraga terbatas & metode satu arah)",
                afterCondition = "Digital (Media Kuis Interaktif & Modul Ilustrasi Digital)",
                beneficiaries = 210,
                sdgCategory = "SDG 4: Pendidikan Berkualitas",
                partnerFeedback = "Antusiasme belajar siswa meningkat drastis sejak menggunakan media interaktif buatan mahasiswa."
            )
        ),
        SocialProject(
            id = "P004",
            organizationName = "Komunitas Tenun & Kriya Desa",
            title = "Akselerasi Pemasaran Online & Branding UMKM",
            description = "UMKM dan komunitas pengrajin kesulitan pemasaran online di marketplace nasional. Membutuhkan pendampingan fotografi produk, copywriting, dan manajemen toko digital.",
            category = "Digital Marketing",
            location = "Kab. Sumedang",
            photoCaption = "Foto: Sentra Kerajinan Komunitas Tenun & Kriya",
            impactScore = 80,
            requirements = listOf("2 Bisnis Digital", "1 Informatika", "1 DKV"),
            status = ProjectStatus.COMPLETED,
            verifiedByAdmin = true,
            applicantCount = 22,
            userApplicationState = "APPROVED",
            userRoleInProject = "Ketua Tim Proyek (+75 Poin)",
            impactReport = ImpactReport(
                id = "I004",
                projectId = "P004",
                beforeCondition = "Manual (Penjualan hanya mengandalkan tengkulak lokal)",
                afterCondition = "Digital (Toko Resmi di 3 Marketplace & Katalog Media Sosial)",
                beneficiaries = 350,
                sdgCategory = "SDG 8 & 9: Inovasi Industri & UMKM Berdaya",
                partnerFeedback = "Penjualan pengrajin naik 140% dalam 2 bulan pasca pendampingan mahasiswa."
            )
        )
    )

    val organizations = mutableListOf(
        CampusOrganization(
            id = "ORG001",
            name = "HIMATIF",
            fullName = "Himpunan Mahasiswa Teknik Informatika",
            category = "Technology",
            logoInitials = "HT",
            skNumber = "SK/REKTOR/ORG-001/2026",
            advisorName = "Dr. Hendra Wijaya, S.T., M.T.",
            contact = "himatif@campusimpact.ac.id • @himatif.official",
            description = "Organisasi kemahasiswaan jurusan Teknik Informatika yang berfokus pada pengembangan talenta software engineering, riset AI, dan pengabdian masyarakat berbasis teknologi.",
            memberCount = 245,
            verificationStatus = VerificationStatus.VERIFIED,
            isUserJoined = true,
            isUserOfficer = true
        ),
        CampusOrganization(
            id = "ORG002",
            name = "BEM Fakultas Teknik",
            fullName = "Badan Eksekutif Mahasiswa Fakultas Teknik",
            category = "Leadership & Social Impact",
            logoInitials = "BEM",
            skNumber = "SK/DEKAN-FT/ORG-002/2026",
            advisorName = "Ir. Siti Rahmawati, M.Eng.",
            contact = "bem.ft@campusimpact.ac.id • @bemft.kampus",
            description = "Lembaga eksekutif mahasiswa tingkat fakultas yang menggerakkan advokasi mahasiswa, pengembangan SDM, dan aksi sosial desa binaan.",
            memberCount = 180,
            verificationStatus = VerificationStatus.VERIFIED,
            isUserJoined = false,
            isUserOfficer = false
        ),
        CampusOrganization(
            id = "ORG003",
            name = "GDSC",
            fullName = "Google Developer Student Clubs Campus",
            category = "Technology",
            logoInitials = "GD",
            skNumber = "SK/KEMAHASISWAAN/ORG-003/2026",
            advisorName = "Rizky Pratama, S.Kom., M.Cs.",
            contact = "gdsc@campusimpact.ac.id • @gdsc.campus",
            description = "Komunitas pengembang teknologi mahasiswa yang menjembatani teori kampus dan praktik industri melalui Solution Challenge, Android, Cloud, dan Machine Learning.",
            memberCount = 310,
            verificationStatus = VerificationStatus.VERIFIED,
            isUserJoined = true,
            isUserOfficer = false
        ),
        CampusOrganization(
            id = "ORG004",
            name = "Startup Club",
            fullName = "Campus Startup & Technopreneurship Club",
            category = "Entrepreneurship",
            logoInitials = "SC",
            skNumber = "SK/KEMAHASISWAAN/ORG-004/2026",
            advisorName = "Dr. Nadia Kusuma, M.B.A.",
            contact = "startupclub@campusimpact.ac.id • @startupclub.id",
            description = "Inkubator mahasiswa lintas disiplin untuk membangun wirausaha sosial (social enterprise), pendampingan bisnis digital UMKM, dan persiapan kompetisi bisnis.",
            memberCount = 156,
            verificationStatus = VerificationStatus.VERIFIED,
            isUserJoined = false,
            isUserOfficer = false
        ),
        CampusOrganization(
            id = "ORG005",
            name = "Relawan Desa Mengabdi",
            fullName = "UKM Relawan Sosial & Lingkungan Kampus",
            category = "Social & Environment",
            logoInitials = "RD",
            skNumber = "SK/PENDING/ORG-005/2026",
            advisorName = "Dr. Bambang Sutrisno, M.Si.",
            contact = "relawandesa@campusimpact.ac.id • 0812-3456-7890",
            description = "Komunitas relawan lintas fakultas untuk program desa binaan, konservasi lingkungan, bank sampah, dan pengajaran sekolah pelosok.",
            memberCount = 92,
            verificationStatus = VerificationStatus.PENDING,
            isUserJoined = false,
            isUserOfficer = false
        )
    )

    val openRecruitments = mutableListOf(
        OpenRecruitment(
            id = "REC001",
            organizationId = "ORG002",
            organizationName = "BEM Fakultas Teknik",
            title = "Open Recruitment 2027",
            positions = listOf("PSDM", "Advokesma", "Medkom"),
            deadline = "20 Agustus 2027",
            status = "open",
            currentStage = SelectionStage.INTERVIEW,
            selectedPosition = "Medkom",
            applicantStatus = ApplicantStatus.PENDING,
            totalApplicants = 86
        ),
        OpenRecruitment(
            id = "REC002",
            organizationId = "ORG001",
            organizationName = "HIMATIF",
            title = "Open Recruitment Staff & Riset 2027",
            positions = listOf("Software Engineering", "UI/UX Design", "Pengabdian Masyarakat"),
            deadline = "10 September 2027",
            status = "open",
            currentStage = SelectionStage.PENGUMUMAN,
            selectedPosition = "Pengabdian Masyarakat",
            applicantStatus = ApplicantStatus.ACCEPTED,
            totalApplicants = 114
        ),
        OpenRecruitment(
            id = "REC003",
            organizationId = "ORG003",
            organizationName = "GDSC",
            title = "Core Team & Mentor Open Recruitment",
            positions = listOf("Android Dev Lead", "AI/ML Curriculum", "Community & Event"),
            deadline = "25 September 2027",
            status = "open",
            currentStage = SelectionStage.PENDAFTARAN,
            selectedPosition = null,
            applicantStatus = ApplicantStatus.NONE,
            totalApplicants = 142
        )
    )

    val events = mutableListOf(
        CampusEvent(
            id = "EV001",
            title = "UI UX Workshop",
            organizerName = "HIMATIF",
            type = EventType.WORKSHOP,
            description = "Workshop intensif merancang antarmuka aplikasi berdampak sosial dengan pendekatan Human-Centered Design, Figma Prototyping, dan Google Material Design.",
            date = "14 November 2026 • 09:00 - 15:00 WIB",
            location = "Auditorium Kampus Lt. 3",
            quota = 200,
            registeredCount = 178,
            pointsReward = 10,
            committeePointsReward = 20,
            posterSummary = "POSTER: UI/UX Design for Social Impact • Free E-Certificate & +10 Impact Points",
            verifiedByAdmin = true,
            isRegistered = true,
            isAttended = false,
            qrTicketCode = "CIH-EV001-U001"
        ),
        CampusEvent(
            id = "EV002",
            title = "Hackathon: Solusi Digital UMKM & Desa",
            organizerName = "GDSC & BEM Fakultas Teknik",
            type = EventType.KOMPETISI,
            description = "Kompetisi inovasi 24 jam membangun prototipe aplikasi nyata untuk menyelesaikan masalah mitra UMKM, koperasi desa, dan sekolah mitra kampus.",
            date = "22 November 2026 • 08:00 WIB",
            location = "Innovation Hub & Co-Working Space",
            quota = 150,
            registeredCount = 132,
            pointsReward = 10,
            committeePointsReward = 20,
            posterSummary = "POSTER: Campus Impact Hackathon 2026 • Total Hadiah Pendanaan Proyek Rp 25 Juta",
            verifiedByAdmin = true,
            isRegistered = true,
            isAttended = true,
            qrTicketCode = "CIH-EV002-U001"
        ),
        CampusEvent(
            id = "EV003",
            title = "Google Developer Event: Android Kotlin & AI",
            organizerName = "GDSC",
            type = EventType.SEMINAR,
            description = "Seminar nasional pengembangan aplikasi Android modern berbasis Kotlin, Supabase Backend, dan integrasi AI Recommendation untuk ekosistem kampus.",
            date = "29 November 2026 • 13:00 WIB",
            location = "Aula Utama Kampus",
            quota = 300,
            registeredCount = 264,
            pointsReward = 10,
            committeePointsReward = 20,
            posterSummary = "POSTER: Google Developer Tech Talk • Speaker: Google Developer Experts",
            verifiedByAdmin = true,
            isRegistered = false,
            isAttended = false,
            qrTicketCode = "CIH-EV003-U001"
        ),
        CampusEvent(
            id = "EV004",
            title = "Bootcamp Pendampingan Digital Marketing UMKM",
            organizerName = "Startup Club",
            type = EventType.BOOTCAMP,
            description = "Bootcamp 3 hari membekali mahasiswa dengan keterampilan manajemen katalog online, iklan digital, dan pembukuan keuangan UMKM.",
            date = "6 Desember 2026 • 09:00 WIB",
            location = "Lab Bisnis Digital & Hybrid",
            quota = 100,
            registeredCount = 85,
            pointsReward = 10,
            committeePointsReward = 20,
            posterSummary = "POSTER: Social Enterprise Bootcamp • Terintegrasi Proyek Pengabdian Masyarakat",
            verifiedByAdmin = true,
            isRegistered = false,
            isAttended = false,
            qrTicketCode = "CIH-EV004-U001"
        ),
        CampusEvent(
            id = "EV005",
            title = "Aksi Volunteer Mengajar & Bank Sampah Desa",
            organizerName = "Relawan Desa Mengabdi",
            type = EventType.VOLUNTEER,
            description = "Kegiatan relawan akhir pekan bersama masyarakat Desa Sukamaju: sosialisasi pemilahan sampah rumah tangga dan kelas inspirasi anak SD.",
            date = "13 Desember 2026 • 07:30 WIB",
            location = "Desa Sukamaju, Kab. Bandung",
            quota = 80,
            registeredCount = 69,
            pointsReward = 10,
            committeePointsReward = 20,
            posterSummary = "POSTER: One Day Social Volunteer • Transportasi & Sertifikat Pengabdian Disediakan",
            verifiedByAdmin = true,
            isRegistered = false,
            isAttended = false,
            qrTicketCode = "CIH-EV005-U001"
        ),
        CampusEvent(
            id = "EV006",
            title = "Webinar Kepemimpinan & Pengukuran Dampak SDG",
            organizerName = "BEM Fakultas Teknik",
            type = EventType.WEBINAR,
            description = "Membedah cara menyusun Impact Report terukur untuk portofolio kerja, beasiswa, dan konversi SKS pengabdian masyarakat (MBKM).",
            date = "18 Desember 2026 • 19:00 WIB",
            location = "Live Streaming Campus Hub",
            quota = 400,
            registeredCount = 310,
            pointsReward = 10,
            committeePointsReward = 20,
            posterSummary = "POSTER: Leadership & SDG Impact Measurement Webinar",
            verifiedByAdmin = true,
            isRegistered = false,
            isAttended = false,
            qrTicketCode = "CIH-EV006-U001"
        ),
        CampusEvent(
            id = "EV007",
            title = "Pelatihan Manajemen Proyek Sosial & Riset Desa",
            organizerName = "HIMATIF",
            type = EventType.PELATIHAN,
            description = "Pelatihan metodologi pemetaan masalah masyarakat, analisis pemangku kepentingan, dan penyusunan dokumen implementasi solusi teknologi.",
            date = "21 Desember 2026 • 09:00 WIB",
            location = "Ruang Seminar Gedung B",
            quota = 90,
            registeredCount = 74,
            pointsReward = 10,
            committeePointsReward = 20,
            posterSummary = "POSTER: Pelatihan Ketua Tim Proyek Sosial (+75 Leadership Readiness)",
            verifiedByAdmin = true,
            isRegistered = false,
            isAttended = false,
            qrTicketCode = "CIH-EV007-U001"
        )
    )

    val portfolioEntries = mutableListOf(
        PortfolioEntry(
            id = "PF001",
            title = "Ketua Tim Proyek: Akselerasi E-Commerce Komunitas Tenun",
            organizationOrPartner = "Komunitas Tenun & Kriya Desa",
            category = "Project Sosial",
            date = "Oktober 2026",
            impactPoints = 75,
            leadershipPoints = 75,
            outcomeSummary = "Berhasil mendigitalisasi 15 pengrajin lokal dengan peningkatan omzet 140% dan 350 penerima manfaat.",
            credentialCode = "CERT-PRJ-2026-004"
        ),
        PortfolioEntry(
            id = "PF002",
            title = "Anggota Tim Teknis: Media Edukasi Interaktif SDN 03",
            organizationOrPartner = "SDN 03 Harapan Bangsa",
            category = "Project Sosial",
            date = "September 2026",
            impactPoints = 50,
            leadershipPoints = 0,
            outcomeSummary = "Mengembangkan aplikasi kuis literasi & numerasi tablet untuk 210 siswa sekolah dasar.",
            credentialCode = "CERT-PRJ-2026-003"
        ),
        PortfolioEntry(
            id = "PF003",
            title = "Kepala Divisi Pengabdian Masyarakat HIMATIF",
            organizationOrPartner = "HIMATIF (Verified Organization)",
            category = "Organisasi",
            date = "Periode 2026/2027",
            impactPoints = 30,
            leadershipPoints = 30,
            outcomeSummary = "Menginisiasi 4 program desa binaan teknologi dan kolaborasi lintas UKM kampus.",
            credentialCode = "SK-ORG-HIMATIF-2026"
        ),
        PortfolioEntry(
            id = "PF004",
            title = "Ketua Panitia Campus Tech for Good Symposium",
            organizationOrPartner = "GDSC & HIMATIF",
            category = "Event",
            date = "Agustus 2026",
            impactPoints = 20,
            leadershipPoints = 20,
            outcomeSummary = "Memimpin 45 panitia dan menghadirkan 320 peserta mahasiswa serta 12 mitra UMKM.",
            credentialCode = "CERT-PAN-2026-019"
        ),
        PortfolioEntry(
            id = "PF005",
            title = "Juara 1 Best Social Impact App — Campus Hackathon",
            organizationOrPartner = "BEM Fakultas Teknik & Direktorat Inovasi",
            category = "Prestasi",
            date = "Juli 2026",
            impactPoints = 50,
            leadershipPoints = 0,
            outcomeSummary = "Merancang sistem integrasi bank sampah desa berbasis QR dan analitik dampak real-time.",
            credentialCode = "AWARD-HACK-2026-01"
        ),
        PortfolioEntry(
            id = "PF006",
            title = "Sertifikat Kompetensi: Full-Stack Android & Cloud Impact",
            organizationOrPartner = "Google Developer Student Clubs (GDSC)",
            category = "Sertifikat",
            date = "Juni 2026",
            impactPoints = 25,
            leadershipPoints = 0,
            outcomeSummary = "Lulus ujian proyek akhir pembuatan aplikasi layanan publik dengan skor 96/100.",
            credentialCode = "GDSC-CERT-88412"
        )
    )

    // --- Dashboard Statistics ---
    fun getTotalActiveStudents(): Int = 4850 + projects.sumOf { it.applicantCount }
    fun getTotalOrganizations(): Int = organizations.size
    fun getVerifiedOrganizationsCount(): Int = organizations.count { it.verificationStatus == VerificationStatus.VERIFIED }
    fun getTotalEvents(): Int = events.size
    fun getTotalProjects(): Int = projects.size
    fun getTotalBeneficiaries(): Int = projects.sumOf { it.impactReport.beneficiaries } + 200

    // --- Role Switching & Auth ---
    fun switchRole(role: UserRole) {
        currentUser.role = role
        when (role) {
            UserRole.STUDENT -> {
                currentUser.name = "Budi Santoso"
                currentUser.email = "budi@mail.com"
                currentUser.majorOrUnit = "S1 Teknik Informatika • Semester 6"
                currentUser.identityNumber = "ID: U001 • NIM 13523001"
            }
            UserRole.PARTNER -> {
                currentUser.name = "H. Suryono (UMKM Maju Jaya)"
                currentUser.email = "mitra@majujaya.id"
                currentUser.majorOrUnit = "Mitra Masyarakat • UMKM & Desa Binaan"
                currentUser.identityNumber = "ID: MITRA-014 • Terverifikasi Kampus"
            }
            UserRole.ORGANIZATION -> {
                currentUser.name = "Admin Pengurus HIMATIF"
                currentUser.email = "himatif@campusimpact.ac.id"
                currentUser.majorOrUnit = "Organisasi / UKM • Technology"
                currentUser.identityNumber = "ID: ORG001 • SK/REKTOR/ORG-001/2026"
            }
            UserRole.ADMIN -> {
                currentUser.name = "Dr. Prasetyo (Admin Kampus)"
                currentUser.email = "admin.dampak@campusimpact.ac.id"
                currentUser.majorOrUnit = "Direktorat Kemahasiswaan & Pengabdian"
                currentUser.identityNumber = "ID: ADM-001 • NIP 19820411201001"
            }
        }
    }

    // --- Modul A: Social Impact Hub Actions ---
    fun applyForProject(project: SocialProject, asTeamLeader: Boolean): String {
        val pointsEarned = if (asTeamLeader) 75 else 50
        val roleTitle = if (asTeamLeader) "Ketua Tim Proyek (+75 Poin)" else "Proyek Sosial (+50 Poin)"
        project.userApplicationState = "PENDING"
        project.userRoleInProject = roleTitle
        project.applicantCount += 1

        currentUser.impactScore += pointsEarned
        if (asTeamLeader) {
            currentUser.leadershipScore += 75
        }
        currentUser.projectsCompleted += 1
        currentUser.contributionHours += 8

        portfolioEntries.add(
            0,
            PortfolioEntry(
                id = "PF00${portfolioEntries.size + 1}",
                title = "${if (asTeamLeader) "Ketua Tim" else "Kontributor"}: ${project.title}",
                organizationOrPartner = project.organizationName,
                category = "Project Sosial",
                date = "Baru Saja",
                impactPoints = pointsEarned,
                leadershipPoints = if (asTeamLeader) 75 else 0,
                outcomeSummary = "Terdaftar pada proyek solusi masyarakat (${project.category}) untuk ${project.impactReport.beneficiaries} penerima manfaat.",
                credentialCode = "APP-${project.id}-${currentUser.id}"
            )
        )
        return "Berhasil mendaftar ke \"${project.title}\" sebagai $roleTitle! Status: Menunggu Persetujuan Mitra."
    }

    fun submitProblem(
        orgName: String,
        title: String,
        description: String,
        category: String,
        location: String,
        photoLabel: String,
        requirementsInput: String,
        estimatedBeneficiaries: Int
    ): SocialProject {
        val newId = "P00${projects.size + 1}"
        val reqList = requirementsInput.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .ifEmpty { listOf("2 Informatika", "1 Desain") }

        val newProject = SocialProject(
            id = newId,
            organizationName = orgName,
            title = title,
            description = description,
            category = category,
            location = location,
            photoCaption = photoLabel.ifBlank { "Foto Dokumentasi Lapangan: $orgName" },
            impactScore = 85,
            requirements = reqList,
            status = ProjectStatus.OPEN,
            verifiedByAdmin = currentUser.role == UserRole.ADMIN,
            applicantCount = 1,
            userApplicationState = "NONE",
            userRoleInProject = "Anggota Proyek Sosial (+50 Poin)",
            impactReport = ImpactReport(
                id = "I00${projects.size + 1}",
                projectId = newId,
                beforeCondition = "Manual / Belum Terdigitalisasi",
                afterCondition = "Target: Implementasi Solusi Digital & Pendampingan Mahasiswa",
                beneficiaries = estimatedBeneficiaries.coerceAtLeast(50),
                sdgCategory = "SDG 8 & 11: Pemberdayaan Komunitas Berkelanjutan",
                partnerFeedback = "Masalah baru diajukan oleh mitra masyarakat dan siap dikerjakan tim mahasiswa."
            )
        )
        projects.add(0, newProject)
        currentUser.umkmHelped += 1
        currentUser.beneficiariesReached += estimatedBeneficiaries.coerceAtLeast(50)
        return newProject
    }

    fun cycleProjectStatus(project: SocialProject): ProjectStatus {
        project.status = when (project.status) {
            ProjectStatus.OPEN -> ProjectStatus.ON_GOING
            ProjectStatus.ON_GOING -> ProjectStatus.COMPLETED
            ProjectStatus.COMPLETED -> ProjectStatus.OPEN
        }
        if (project.userApplicationState == "PENDING" && project.status != ProjectStatus.OPEN) {
            project.userApplicationState = "APPROVED"
        }
        return project.status
    }

    // --- Modul B: Organization Hub Actions ---
    fun registerNewOrganization(
        name: String,
        fullName: String,
        category: String,
        skNumber: String,
        advisorName: String,
        contact: String,
        description: String
    ): CampusOrganization {
        val initials = name.trim().split(" ")
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .joinToString("")
            .take(3)
            .ifEmpty { "UKM" }

        val newOrg = CampusOrganization(
            id = "ORG00${organizations.size + 1}",
            name = name,
            fullName = fullName.ifBlank { name },
            category = category,
            logoInitials = initials,
            skNumber = skNumber,
            advisorName = advisorName,
            contact = contact,
            description = description,
            memberCount = 25,
            verificationStatus = VerificationStatus.PENDING,
            isUserJoined = true,
            isUserOfficer = true
        )
        organizations.add(0, newOrg)
        return newOrg
    }

    fun verifyOrganization(org: CampusOrganization, approve: Boolean) {
        org.verificationStatus = if (approve) VerificationStatus.VERIFIED else VerificationStatus.REJECTED
    }

    fun joinOrganizationAsOfficer(org: CampusOrganization) {
        if (!org.isUserJoined) {
            org.isUserJoined = true
            org.isUserOfficer = true
            org.memberCount += 1
            currentUser.impactScore += 30
            currentUser.leadershipScore += 30
            portfolioEntries.add(
                0,
                PortfolioEntry(
                    id = "PF00${portfolioEntries.size + 1}",
                    title = "Pengurus Aktif ${org.name}",
                    organizationOrPartner = org.fullName,
                    category = "Organisasi",
                    date = "Periode Aktif",
                    impactPoints = 30,
                    leadershipPoints = 30,
                    outcomeSummary = "Bergabung sebagai pengurus organisasi kampus terverifikasi (${org.skNumber}).",
                    credentialCode = "ORG-MEM-${org.id}"
                )
            )
        }
    }

    // --- Modul C: Open Recruitment Actions ---
    fun createOpenRecruitment(
        orgName: String,
        title: String,
        positionsCsv: String,
        deadline: String
    ): OpenRecruitment {
        val posList = positionsCsv.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .ifEmpty { listOf("PSDM", "Advokesma", "Medkom") }

        val newOprec = OpenRecruitment(
            id = "REC00${openRecruitments.size + 1}",
            organizationId = "ORG001",
            organizationName = orgName,
            title = title,
            positions = posList,
            deadline = deadline,
            status = "open",
            currentStage = SelectionStage.PENDAFTARAN,
            selectedPosition = null,
            applicantStatus = ApplicantStatus.NONE,
            totalApplicants = 1
        )
        openRecruitments.add(0, newOprec)
        return newOprec
    }

    fun applyToRecruitment(rec: OpenRecruitment, chosenPosition: String) {
        rec.selectedPosition = chosenPosition
        rec.applicantStatus = ApplicantStatus.PENDING
        rec.currentStage = SelectionStage.ADMINISTRASI
        rec.totalApplicants += 1
    }

    fun advanceRecruitmentSelection(rec: OpenRecruitment, acceptAtEnd: Boolean = true) {
        when (rec.currentStage) {
            SelectionStage.PENDAFTARAN -> {
                rec.currentStage = SelectionStage.ADMINISTRASI
                rec.applicantStatus = ApplicantStatus.PENDING
            }
            SelectionStage.ADMINISTRASI -> {
                rec.currentStage = SelectionStage.INTERVIEW
                rec.applicantStatus = ApplicantStatus.PENDING
            }
            SelectionStage.INTERVIEW -> {
                rec.currentStage = SelectionStage.PENGUMUMAN
                rec.applicantStatus = if (acceptAtEnd) ApplicantStatus.ACCEPTED else ApplicantStatus.REJECTED
                if (acceptAtEnd) {
                    currentUser.impactScore += 30
                    currentUser.leadershipScore += 30
                    portfolioEntries.add(
                        0,
                        PortfolioEntry(
                            id = "PF00${portfolioEntries.size + 1}",
                            title = "Staf Divisi ${rec.selectedPosition ?: "Inti"} — ${rec.organizationName}",
                            organizationOrPartner = rec.organizationName,
                            category = "Organisasi",
                            date = "Lulus Seleksi Oprec",
                            impactPoints = 30,
                            leadershipPoints = 30,
                            outcomeSummary = "Lulus seluruh tahapan seleksi (Pendaftaran → Administrasi → Interview → Pengumuman).",
                            credentialCode = "OPREC-${rec.id}-PASS"
                        )
                    )
                }
            }
            SelectionStage.PENGUMUMAN -> {
                rec.applicantStatus = if (rec.applicantStatus == ApplicantStatus.ACCEPTED) {
                    ApplicantStatus.REJECTED
                } else {
                    ApplicantStatus.ACCEPTED
                }
            }
        }
    }

    // --- Modul D: Event Hub & QR Ticket Actions ---
    fun createNewEvent(
        title: String,
        organizer: String,
        type: EventType,
        description: String,
        date: String,
        location: String,
        quota: Int,
        posterCaption: String
    ): CampusEvent {
        val newId = "EV00${events.size + 1}"
        val event = CampusEvent(
            id = newId,
            title = title,
            organizerName = organizer,
            type = type,
            description = description,
            date = date,
            location = location,
            quota = quota.coerceAtLeast(20),
            registeredCount = 1,
            pointsReward = 10,
            committeePointsReward = 20,
            posterSummary = posterCaption.ifBlank { "POSTER RESMI: $title oleh $organizer" },
            verifiedByAdmin = true,
            isRegistered = true,
            isAttended = false,
            qrTicketCode = "CIH-$newId-${currentUser.id}"
        )
        events.add(0, event)
        return event
    }

    fun registerForEvent(event: CampusEvent) {
        if (!event.isRegistered) {
            event.isRegistered = true
            event.registeredCount = (event.registeredCount + 1).coerceAtMost(event.quota)
            event.qrTicketCode = "CIH-${event.id}-${currentUser.id}"
        }
    }

    fun confirmEventAttendanceByQr(event: CampusEvent, asCommittee: Boolean = false): Int {
        if (!event.isRegistered) {
            registerForEvent(event)
        }
        if (event.isAttended) {
            return 0
        }
        event.isAttended = true
        val earnedPoints = if (asCommittee) 20 else event.pointsReward
        currentUser.impactScore += earnedPoints
        if (asCommittee) {
            currentUser.leadershipScore += 20
        }
        currentUser.contributionHours += 4

        portfolioEntries.add(
            0,
            PortfolioEntry(
                id = "PF00${portfolioEntries.size + 1}",
                title = "${if (asCommittee) "Panitia Event" else "Peserta Terverifikasi QR"}: ${event.title}",
                organizationOrPartner = event.organizerName,
                category = "Sertifikat",
                date = event.date,
                impactPoints = earnedPoints,
                leadershipPoints = if (asCommittee) 20 else 0,
                outcomeSummary = "Kehadiran Hari-H tervalidasi via QR Scan (${event.qrTicketCode}).",
                credentialCode = "QR-CERT-${event.id}"
            )
        )
        return earnedPoints
    }

    // --- Modul G: AI Recommendation Hub ---
    fun matchProblemWithAi(problemInput: String): AiProblemMatchResult {
        val cleanInput = problemInput.trim().ifEmpty { "UMKM kesulitan pemasaran online" }
        val lower = cleanInput.lowercase()

        return when {
            lower.contains("pemasaran") || lower.contains("marketing") || lower.contains("umkm") || lower.contains("jualan") || lower.contains("toko") -> {
                AiProblemMatchResult(
                    problemInput = cleanInput,
                    category = "Digital Marketing",
                    recommendedMajors = listOf("Bisnis Digital", "Informatika", "DKV"),
                    estimatedImpact = "Tinggi (Estimasi +85 Impact Score • 120+ Penerima Manfaat)",
                    suggestedImpactScore = 85,
                    recommendedSolutionTitle = "Akselerasi Pemasaran Digital & E-Commerce UMKM"
                )
            }
            lower.contains("sampah") || lower.contains("lingkungan") || lower.contains("desa") || lower.contains("air") || lower.contains("tani") -> {
                AiProblemMatchResult(
                    problemInput = cleanInput,
                    category = "Teknologi Lingkungan & Smart Village",
                    recommendedMajors = listOf("Informatika", "Teknik Lingkungan", "Perencanaan Wilayah & Kota"),
                    estimatedImpact = "Sangat Tinggi (Estimasi +90 Impact Score • 300+ Warga Desa)",
                    suggestedImpactScore = 90,
                    recommendedSolutionTitle = "Sistem Digitalisasi Bank Sampah & Administrasi Desa"
                )
            }
            lower.contains("sekolah") || lower.contains("pendidikan") || lower.contains("belajar") || lower.contains("guru") || lower.contains("siswa") -> {
                AiProblemMatchResult(
                    problemInput = cleanInput,
                    category = "EdTech & Inovasi Pendidikan",
                    recommendedMajors = listOf("Teknologi Pendidikan", "Informatika", "DKV"),
                    estimatedImpact = "Tinggi (Estimasi +80 Impact Score • 200+ Siswa Terbantu)",
                    suggestedImpactScore = 80,
                    recommendedSolutionTitle = "Platform Modul Pembelajaran Interaktif Sekolah Mitra"
                )
            }
            else -> {
                AiProblemMatchResult(
                    problemInput = cleanInput,
                    category = "Teknologi & Inovasi Sosial",
                    recommendedMajors = listOf("Informatika", "Sistem Informasi", "Bisnis Digital", "DKV"),
                    estimatedImpact = "Tinggi (Estimasi +85 Impact Score • 150+ Penerima Manfaat)",
                    suggestedImpactScore = 85,
                    recommendedSolutionTitle = "Transformasi Digital & Otomatisasi Layanan Masyarakat"
                )
            }
        }
    }

    fun getAiStudentRecommendations(major: String, interests: List<String>): AiPersonalRecommendation {
        val orgs = mutableListOf<String>()
        val evts = mutableListOf<String>()
        val projs = mutableListOf<String>()

        val joinedInterests = interests.joinToString(" ").lowercase() + " " + major.lowercase()

        if (joinedInterests.contains("programming") || joinedInterests.contains("informatika") || joinedInterests.contains("ui/ux")) {
            orgs.addAll(listOf("GDSC", "HIMATIF"))
            evts.addAll(listOf("Hackathon", "UI/UX Workshop", "Google Developer Event"))
            projs.addAll(listOf("Digitalisasi UMKM (85 Impact Score)", "Digitalisasi Bank Sampah (90 Impact Score)"))
        }
        if (joinedInterests.contains("startup") || joinedInterests.contains("bisnis")) {
            if (!orgs.contains("Startup Club")) orgs.add("Startup Club")
            evts.add("Bootcamp Pendampingan Digital Marketing UMKM")
            projs.add("Akselerasi Pemasaran Online & Branding UMKM")
        }
        if (joinedInterests.contains("sosial") || joinedInterests.contains("leadership") || joinedInterests.contains("volunteer")) {
            orgs.addAll(listOf("BEM Fakultas Teknik", "Relawan Desa Mengabdi"))
            evts.addAll(listOf("Aksi Volunteer Mengajar & Bank Sampah Desa", "Webinar Kepemimpinan & Pengukuran Dampak SDG"))
            projs.add("Solusi Media Edukasi & Literasi Sekolah")
        }

        if (orgs.isEmpty()) {
            orgs.addAll(listOf("GDSC", "HIMATIF", "Startup Club"))
        } else if (!orgs.contains("Startup Club") && interests.any { it.equals("Startup", true) }) {
            orgs.add("Startup Club")
        }

        if (evts.isEmpty()) {
            evts.addAll(listOf("Hackathon", "UI/UX Workshop", "Google Developer Event"))
        }

        return AiPersonalRecommendation(
            majorInput = major,
            interestsInput = interests,
            recommendedOrganizations = orgs.distinct(),
            recommendedEvents = evts.distinct(),
            recommendedProjects = projs.distinct()
        )
    }

    // --- Section 6 & 8: Live MVP Database Schema & Supabase API Payload Preview ---
    fun getMvpDatabaseJsonSnapshot(): String {
        val firstProject = projects.first()
        val firstOrg = organizations.first()
        val firstRec = openRecruitments.first()
        val firstEvent = events.first()
        return """
{
  "architecture": "Android Studio (Kotlin) -> Supabase API -> Supabase Auth -> PostgreSQL -> Storage",
  "Users": {
    "id": "${currentUser.id}",
    "name": "${currentUser.name}",
    "email": "${currentUser.email}",
    "role": "${currentUser.role.code}",
    "impact_score": ${currentUser.impactScore},
    "leadership_score": ${currentUser.leadershipScore}
  },
  "Projects": {
    "id": "${firstProject.id}",
    "title": "${firstProject.title}",
    "organization": "${firstProject.organizationName}",
    "category": "${firstProject.category}",
    "impact_score": ${firstProject.impactScore},
    "status": "${firstProject.status.label.lowercase()}"
  },
  "Applications": {
    "id": "A001",
    "project_id": "${firstProject.id}",
    "student_id": "${currentUser.id}",
    "status": "${firstProject.userApplicationState.lowercase()}"
  },
  "Organizations": {
    "id": "${firstOrg.id}",
    "name": "${firstOrg.name}",
    "category": "${firstOrg.category}",
    "verified": ${firstOrg.verificationStatus == VerificationStatus.VERIFIED}
  },
  "Recruitment": {
    "id": "${firstRec.id}",
    "organization": "${firstRec.organizationName}",
    "positions": ${firstRec.positions.joinToString(prefix = "[\"", separator = "\", \"", postfix = "\"]")},
    "deadline": "${firstRec.deadline}",
    "status": "${firstRec.status}"
  },
  "Events": {
    "id": "${firstEvent.id}",
    "title": "${firstEvent.title}",
    "type": "${firstEvent.type.label}",
    "quota": ${firstEvent.quota},
    "qr_ticket": "${firstEvent.qrTicketCode}"
  },
  "Impact_Reports": {
    "id": "${firstProject.impactReport.id}",
    "project_id": "${firstProject.id}",
    "before": "${firstProject.impactReport.beforeCondition}",
    "after": "${firstProject.impactReport.afterCondition}",
    "beneficiaries": ${firstProject.impactReport.beneficiaries}
  }
}
        """.trimIndent()
    }
}
