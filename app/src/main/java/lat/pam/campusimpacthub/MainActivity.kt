package lat.pam.campusimpacthub

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import lat.pam.campusimpacthub.data.CampusRepository
import lat.pam.campusimpacthub.model.ApplicantStatus
import lat.pam.campusimpacthub.model.EventType
import lat.pam.campusimpacthub.model.ProjectStatus
import lat.pam.campusimpacthub.model.SelectionStage
import lat.pam.campusimpacthub.model.UserRole
import lat.pam.campusimpacthub.model.VerificationStatus
import lat.pam.campusimpacthub.ui.AppDialogs
import lat.pam.campusimpacthub.ui.UiComponents
import lat.pam.campusimpacthub.ui.UiComponents.dp

class MainActivity : AppCompatActivity() {

    enum class MainTab {
        AUTH_SCREEN,
        HOME,
        PROJECTS,
        ORGANIZATIONS,
        EVENTS,
        IMPACT,
        PROFILE
    }

    private var currentTab: MainTab = MainTab.HOME
    private var isRegisterModeInAuth: Boolean = false
    private var selectedProjectCategoryFilter: String = "Semua"
    private var selectedOrgSubTab: Int = 0 // 0 = Directory Organisasi (Screen 5), 1 = Open Recruitment (Screen 6)
    private var selectedEventTypeFilter: String = "Semua"
    private var selectedPortfolioFilter: String = "Semua"
    private var currentAiProblemQuery: String = "UMKM kesulitan pemasaran online"

    private lateinit var topHeaderBar: LinearLayout
    private lateinit var btnRoleSwitcher: MaterialButton
    private lateinit var tvHeaderImpactPill: TextView
    private lateinit var tvHeaderLeadershipPill: TextView
    private lateinit var btnOpenLoginScreen: TextView
    private lateinit var mainScrollView: ScrollView
    private lateinit var contentContainer: LinearLayout
    private lateinit var bottomNavBar: LinearLayout
    private lateinit var bottomNavDivider: View

    private lateinit var navTabs: Map<MainTab, Pair<ImageView, TextView>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        topHeaderBar = findViewById(R.id.topHeaderBar)
        btnRoleSwitcher = findViewById(R.id.btnRoleSwitcher)
        tvHeaderImpactPill = findViewById(R.id.tvHeaderImpactPill)
        tvHeaderLeadershipPill = findViewById(R.id.tvHeaderLeadershipPill)
        btnOpenLoginScreen = findViewById(R.id.btnOpenLoginScreen)
        mainScrollView = findViewById(R.id.mainScrollView)
        contentContainer = findViewById(R.id.contentContainer)
        bottomNavBar = findViewById(R.id.bottomNavBar)
        bottomNavDivider = findViewById(R.id.bottomNavDivider)

        navTabs = mapOf(
            MainTab.HOME to Pair(findViewById(R.id.ivNavHome), findViewById(R.id.tvNavHome)),
            MainTab.PROJECTS to Pair(findViewById(R.id.ivNavProjects), findViewById(R.id.tvNavProjects)),
            MainTab.ORGANIZATIONS to Pair(findViewById(R.id.ivNavOrganizations), findViewById(R.id.tvNavOrganizations)),
            MainTab.EVENTS to Pair(findViewById(R.id.ivNavEvents), findViewById(R.id.tvNavEvents)),
            MainTab.IMPACT to Pair(findViewById(R.id.ivNavImpact), findViewById(R.id.tvNavImpact)),
            MainTab.PROFILE to Pair(findViewById(R.id.ivNavProfile), findViewById(R.id.tvNavProfile))
        )

        findViewById<View>(R.id.navTabHome).setOnClickListener { switchToTab(MainTab.HOME) }
        findViewById<View>(R.id.navTabProjects).setOnClickListener { switchToTab(MainTab.PROJECTS) }
        findViewById<View>(R.id.navTabOrganizations).setOnClickListener { switchToTab(MainTab.ORGANIZATIONS) }
        findViewById<View>(R.id.navTabEvents).setOnClickListener { switchToTab(MainTab.EVENTS) }
        findViewById<View>(R.id.navTabImpact).setOnClickListener { switchToTab(MainTab.IMPACT) }
        findViewById<View>(R.id.navTabProfile).setOnClickListener { switchToTab(MainTab.PROFILE) }

        btnRoleSwitcher.setOnClickListener {
            AppDialogs.showRoleSwitcherDialog(
                context = this,
                onRoleChanged = { refreshScreen() },
                onOpenAuthScreen = { switchToTab(MainTab.AUTH_SCREEN) }
            )
        }

        btnOpenLoginScreen.setOnClickListener {
            switchToTab(MainTab.AUTH_SCREEN)
        }

        tvHeaderImpactPill.setOnClickListener {
            switchToTab(MainTab.IMPACT)
        }
        tvHeaderLeadershipPill.setOnClickListener {
            switchToTab(MainTab.IMPACT)
        }

        refreshScreen()
    }

    private fun switchToTab(tab: MainTab) {
        currentTab = tab
        refreshScreen(scrollToTop = true)
    }

    private fun refreshScreen(scrollToTop: Boolean = false) {
        val user = CampusRepository.currentUser
        btnRoleSwitcher.text = "${user.role.label} ▾"
        tvHeaderImpactPill.text = "🌱 Impact: ${user.impactScore}"
        tvHeaderLeadershipPill.text = "⭐ Leadership: ${user.leadershipScore}"

        val isAuthScreen = (currentTab == MainTab.AUTH_SCREEN)
        bottomNavBar.visibility = if (isAuthScreen) View.GONE else View.VISIBLE
        bottomNavDivider.visibility = if (isAuthScreen) View.GONE else View.VISIBLE

        // Highlight active bottom navigation item
        val activeColor = Color.parseColor("#464FEB")
        val inactiveColor = Color.parseColor("#64748B")
        navTabs.forEach { (tab, views) ->
            val isSelected = (tab == currentTab)
            views.first.setColorFilter(if (isSelected) activeColor else inactiveColor)
            views.second.setTextColor(if (isSelected) activeColor else inactiveColor)
            views.second.setTypeface(null, if (isSelected) Typeface.BOLD else Typeface.NORMAL)
        }

        contentContainer.removeAllViews()

        when (currentTab) {
            MainTab.AUTH_SCREEN -> renderAuthScreen()
            MainTab.HOME -> renderHomeDashboard()
            MainTab.PROJECTS -> renderProjectsMarketplace()
            MainTab.ORGANIZATIONS -> renderOrganizationsHub()
            MainTab.EVENTS -> renderEventsHub()
            MainTab.IMPACT -> renderImpactAndPortfolioHub()
            MainTab.PROFILE -> renderProfileScreen()
        }

        if (scrollToTop) {
            mainScrollView.post { mainScrollView.smoothScrollTo(0, 0) }
        }
    }

    // =========================================================================
    // SCREEN 1: LOGIN & REGISTER (Supabase Auth + 4 Role Ekosistem)
    // =========================================================================
    private fun renderAuthScreen() {
        val heroCard = UiComponents.createCard(
            context = this,
            bgColorHex = "#1E3A8A",
            strokeColorHex = "#3B82F6",
            cornerRadiusDp = 18,
            elevationDp = 4,
            paddingDp = 20
        )
        heroCard.body.addView(
            UiComponents.createPillBadge(
                context = this,
                text = "✨ SCREEN 1 • SUPABASE AUTHENTICATION",
                textColorHex = "#1E3A8A",
                bgColorHex = "#DBEAFE"
            )
        )
        heroCard.body.addView(TextView(this).apply {
            text = "CAMPUS IMPACT HUB"
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(10)
            }
        })
        heroCard.body.addView(TextView(this).apply {
            text = "\"Menghubungkan Masalah, Talenta, Organisasi, dan Dampak dalam Satu Ekosistem Kampus.\""
            setTextColor(Color.parseColor("#DBEAFE"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTypeface(typeface, Typeface.ITALIC)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(4)
            }
        })
        contentContainer.addView(heroCard.card)

        // Login / Register Card
        val formCard = UiComponents.createCard(this, paddingDp = 18)

        // Mode Toggle (Login vs Register)
        val toggleRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(14)
            }
        }
        toggleRow.addView(
            UiComponents.createButton(
                context = this,
                label = "🔐 Masuk (Login)",
                bgColorHex = if (!isRegisterModeInAuth) "#464FEB" else "#EEF2FF",
                textColorHex = if (!isRegisterModeInAuth) "#FFFFFF" else "#1E3A8A",
                weight = 1f,
                endMarginDp = 6
            ) {
                isRegisterModeInAuth = false
                refreshScreen()
            }
        )
        toggleRow.addView(
            UiComponents.createButton(
                context = this,
                label = "📝 Daftar (Register)",
                bgColorHex = if (isRegisterModeInAuth) "#464FEB" else "#EEF2FF",
                textColorHex = if (isRegisterModeInAuth) "#FFFFFF" else "#1E3A8A",
                weight = 1f
            ) {
                isRegisterModeInAuth = true
                refreshScreen()
            }
        )
        formCard.body.addView(toggleRow)

        val nameInput = UiComponents.createInputField(
            this,
            "Nama Lengkap / Nama Instansi",
            "Masukkan nama lengkap",
            CampusRepository.currentUser.name
        )
        val emailInput = UiComponents.createInputField(
            this,
            "Email Kampus / Mitra (Supabase Auth)",
            "budi@mail.com",
            CampusRepository.currentUser.email
        )
        val majorInput = UiComponents.createInputField(
            this,
            "Program Studi / Unit / Organisasi",
            "Contoh: Teknik Informatika / UMKM Maju Jaya",
            CampusRepository.currentUser.majorOrUnit
        )
        val passInput = UiComponents.createInputField(
            this,
            "Kata Sandi",
            "••••••••",
            "campusimpact2026",
            isPassword = true
        )

        if (isRegisterModeInAuth) {
            formCard.body.addView(nameInput.container)
            formCard.body.addView(majorInput.container)
        }
        formCard.body.addView(emailInput.container)
        formCard.body.addView(passInput.container)

        // Role Picker inside Auth Screen
        formCard.body.addView(TextView(this).apply {
            text = "Pilih Role Masuk (4 Role Ekosistem):"
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(6)
            }
        })

        UserRole.entries.forEach { role ->
            val selected = CampusRepository.currentUser.role == role
            formCard.body.addView(
                UiComponents.createButton(
                    context = this,
                    label = "${if (selected) "✅ " else ""}${role.label} — ${role.badgeTitle}",
                    bgColorHex = if (selected) "#DBEAFE" else "#F8FAFC",
                    textColorHex = if (selected) "#1E3A8A" else "#334155"
                ) {
                    CampusRepository.switchRole(role)
                    refreshScreen()
                }.apply {
                    (layoutParams as LinearLayout.LayoutParams).bottomMargin = dp(6)
                }
            )
        }

        formCard.body.addView(
            UiComponents.createButton(
                context = this,
                label = if (isRegisterModeInAuth) "🚀 Buat Akun & Masuk ke Dashboard" else "🚀 Masuk ke Dashboard Campus Impact Hub",
                bgColorHex = "#10B981"
            ) {
                if (isRegisterModeInAuth) {
                    CampusRepository.currentUser.name =
                        nameInput.editText.text.toString().ifBlank { CampusRepository.currentUser.name }
                    CampusRepository.currentUser.majorOrUnit =
                        majorInput.editText.text.toString().ifBlank { CampusRepository.currentUser.majorOrUnit }
                }
                CampusRepository.currentUser.email =
                    emailInput.editText.text.toString().ifBlank { CampusRepository.currentUser.email }
                CampusRepository.isLoggedIn = true
                Toast.makeText(
                    this,
                    "Selamat datang, ${CampusRepository.currentUser.name} (${CampusRepository.currentUser.role.label})!",
                    Toast.LENGTH_SHORT
                ).show()
                switchToTab(MainTab.HOME)
            }.apply {
                (layoutParams as LinearLayout.LayoutParams).topMargin = dp(8)
            }
        )

        contentContainer.addView(formCard.card)
    }

    // =========================================================================
    // SCREEN 2: HOME DASHBOARD + MODUL G (AI RECOMMENDATION HUB)
    // =========================================================================
    private fun renderHomeDashboard() {
        val user = CampusRepository.currentUser

        // 1. LinkedIn-style Welcome & Value Proposition Hero Card
        val hero = UiComponents.createCard(
            context = this,
            bgColorHex = "#FFFFFF",
            strokeColorHex = "#C7D2FE",
            cornerRadiusDp = 18,
            elevationDp = 3,
            paddingDp = 16
        )

        val topTagRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        topTagRow.addView(
            UiComponents.createPillBadge(
                context = this,
                text = "👤 Role: ${user.role.label}",
                textColorHex = "#1E3A8A",
                bgColorHex = "#DBEAFE"
            )
        )
        topTagRow.addView(
            UiComponents.createPillBadge(
                context = this,
                text = "✅ Supabase Live Sync",
                textColorHex = "#047857",
                bgColorHex = "#D1FAE5"
            )
        )
        hero.body.addView(topTagRow)

        hero.body.addView(TextView(this).apply {
            text = "Halo, ${user.name}!"
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(8)
            }
        })

        hero.body.addView(TextView(this).apply {
            text = "\"Menghubungkan Masalah, Talenta, Organisasi, dan Dampak dalam Satu Ekosistem Kampus.\""
            setTextColor(Color.parseColor("#464FEB"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.BOLD_ITALIC)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(4)
            }
        })

        hero.body.addView(TextView(this).apply {
            text = getString(R.string.app_value_proposition)
            setTextColor(Color.parseColor("#475569"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(4)
            }
        })

        // Quick Action Row inside Hero
        val quickBtnRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(12)
            }
        }
        quickBtnRow.addView(
            UiComponents.createButton(
                context = this,
                label = "🤝 Submit Masalah",
                bgColorHex = "#10B981",
                weight = 1f,
                endMarginDp = 6
            ) {
                AppDialogs.showSubmitProblemDialog(this) {
                    switchToTab(MainTab.PROJECTS)
                }
            }
        )
        quickBtnRow.addView(
            UiComponents.createButton(
                context = this,
                label = "🚀 Cari Proyek",
                bgColorHex = "#464FEB",
                weight = 1f,
                endMarginDp = 6
            ) {
                switchToTab(MainTab.PROJECTS)
            }
        )
        quickBtnRow.addView(
            UiComponents.createButton(
                context = this,
                label = "🎟️ QR Event",
                bgColorHex = "#1E3A8A",
                weight = 1f
            ) {
                switchToTab(MainTab.EVENTS)
            }
        )
        hero.body.addView(quickBtnRow)
        contentContainer.addView(hero.card)

        // 2. Screen 2: Dashboard Analitik Kampus (5 Indikator Utama)
        contentContainer.addView(
            UiComponents.createSectionTitle(
                context = this,
                title = "📊 Dashboard Analitik Ekosistem Kampus",
                subtitle = "Statistik real-time kontribusi mahasiswa, organisasi, event, proyek sosial, dan dampak masyarakat."
            )
        )

        val statsRow1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(8)
            }
        }
        statsRow1.addView(
            UiComponents.createStatCardTile(
                context = this,
                iconEmoji = "🎓",
                metricValue = "${CampusRepository.getTotalActiveStudents()}",
                metricLabel = "Total Mahasiswa Aktif",
                subLabel = "Talenta Lintas Jurusan",
                valueColorHex = "#464FEB",
                badgeBgHex = "#EEF2FF"
            )
        )
        statsRow1.addView(
            UiComponents.createStatCardTile(
                context = this,
                iconEmoji = "🏛️",
                metricValue = "${CampusRepository.getTotalOrganizations()} UKM",
                metricLabel = "Total Organisasi",
                subLabel = "${CampusRepository.getVerifiedOrganizationsCount()} Verified Organization",
                valueColorHex = "#1E3A8A",
                badgeBgHex = "#DBEAFE"
            )
        )
        contentContainer.addView(statsRow1)

        val statsRow2 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(8)
            }
        }
        statsRow2.addView(
            UiComponents.createStatCardTile(
                context = this,
                iconEmoji = "📅",
                metricValue = "${CampusRepository.getTotalEvents()} Event",
                metricLabel = "Total Event Kampus",
                subLabel = "Terintegrasi QR Ticket",
                valueColorHex = "#7C3AED",
                badgeBgHex = "#EDE9FE"
            )
        )
        statsRow2.addView(
            UiComponents.createStatCardTile(
                context = this,
                iconEmoji = "🛠️",
                metricValue = "${CampusRepository.getTotalProjects()} Proyek",
                metricLabel = "Total Proyek Sosial",
                subLabel = "UMKM, Desa & Sekolah",
                valueColorHex = "#B45309",
                badgeBgHex = "#FEF3C7"
            )
        )
        contentContainer.addView(statsRow2)

        // Total Dampak Highlight Banner (Green Impact Card)
        val totalImpactBanner = UiComponents.createCard(
            context = this,
            bgColorHex = "#065F46",
            strokeColorHex = "#10B981",
            cornerRadiusDp = 16,
            elevationDp = 3,
            paddingDp = 16
        )
        totalImpactBanner.body.addView(TextView(this).apply {
            text = "🌍 TOTAL DAMPAK SOSIAL KAMPUS TERUKUR"
            setTextColor(Color.parseColor("#A7F3D0"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTypeface(typeface, Typeface.BOLD)
        })
        totalImpactBanner.body.addView(TextView(this).apply {
            text = "${CampusRepository.getTotalBeneficiaries()}+ Penerima Manfaat • ${user.umkmHelped} UMKM & Desa Terdigitalisasi"
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(4)
            }
        })
        totalImpactBanner.body.addView(TextView(this).apply {
            text = "My Impact Score: ${user.impactScore} Poin  |  Leadership Score: ${user.leadershipScore} Poin  |  Kontribusi: ${user.contributionHours} Jam"
            setTextColor(Color.parseColor("#D1FAE5"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(4)
            }
        })
        totalImpactBanner.card.setOnClickListener { switchToTab(MainTab.IMPACT) }
        contentContainer.addView(totalImpactBanner.card)

        // 3. Modul G — AI Recommendation Hub (AI Problem Matching + AI Org & Event Recommendation)
        contentContainer.addView(
            UiComponents.createSectionTitle(
                context = this,
                title = "🤖 Modul G — AI Recommendation Hub",
                subtitle = "Rekomendasi cerdas pemetaan masalah masyarakat, organisasi UKM, dan event sesuai minat."
            )
        )

        val aiCard = UiComponents.createCard(
            context = this,
            bgColorHex = "#FFFFFF",
            strokeColorHex = "#93C5FD",
            cornerRadiusDp = 16,
            elevationDp = 2,
            paddingDp = 16
        )
        aiCard.body.addView(
            UiComponents.createPillBadge(
                context = this,
                text = "⚡ AI PROBLEM MATCHING ENGINE",
                textColorHex = "#1E3A8A",
                bgColorHex = "#DBEAFE"
            )
        )
        val problemInputHolder = UiComponents.createInputField(
            context = this,
            label = "Input Masalah Masyarakat (UMKM / Desa / Sekolah):",
            hint = "Contoh: UMKM kesulitan pemasaran online",
            defaultValue = currentAiProblemQuery
        )
        problemInputHolder.container.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            topMargin = dp(8)
            bottomMargin = dp(8)
        }
        aiCard.body.addView(problemInputHolder.container)

        // Preset Problem Chips
        val presetRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(8)
            }
        }
        listOf(
            "UMKM kesulitan pemasaran online",
            "Desa butuh bank sampah digital",
            "Sekolah butuh media edukasi"
        ).forEach { sample ->
            presetRow.addView(
                UiComponents.createPillBadge(
                    context = this,
                    text = sample.take(22) + "…",
                    textColorHex = "#464FEB",
                    bgColorHex = "#EEF2FF"
                ).apply {
                    setOnClickListener {
                        currentAiProblemQuery = sample
                        refreshScreen()
                    }
                }
            )
        }
        aiCard.body.addView(presetRow)

        aiCard.body.addView(
            UiComponents.createButton(
                context = this,
                label = "🔍 Jalankan AI Problem Matching",
                bgColorHex = "#464FEB"
            ) {
                currentAiProblemQuery = problemInputHolder.editText.text.toString()
                    .ifBlank { "UMKM kesulitan pemasaran online" }
                refreshScreen()
            }
        )

        val aiMatch = CampusRepository.matchProblemWithAi(currentAiProblemQuery)
        val aiOutputBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = UiComponents.roundedBg("#F0FDF4", 12, "#86EFAC", 2, this@MainActivity)
            val p = dp(12)
            setPadding(p, p, p, p)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(10)
            }
        }
        aiOutputBox.addView(TextView(this).apply {
            text = "Hasil AI Problem Matching:"
            setTextColor(Color.parseColor("#065F46"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.BOLD)
        })
        aiOutputBox.addView(TextView(this).apply {
            text = "• Input: \"${aiMatch.problemInput}\"\n" +
                    "• Kategori: ${aiMatch.category}\n" +
                    "• Rekomendasi Mahasiswa:\n  - ${aiMatch.recommendedMajors.joinToString("\n  - ")}\n" +
                    "• Estimasi Dampak: ${aiMatch.estimatedImpact}"
            setTextColor(Color.parseColor("#047857"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(4)
            }
        })
        aiCard.body.addView(aiOutputBox)

        // AI Organization & Event Recommendation Sub-section
        aiCard.body.addView(UiComponents.createDivider(this, 12))
        val personalRec = CampusRepository.getAiStudentRecommendations(
            major = "Mahasiswa Informatika",
            interests = user.interests
        )
        aiCard.body.addView(TextView(this).apply {
            text = "🎯 AI Organization & Event Recommendation"
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTypeface(typeface, Typeface.BOLD)
        })
        aiCard.body.addView(TextView(this).apply {
            text = "Input Minat: ${user.interests.joinToString(", ")}  |  Profil: Mahasiswa Informatika\n" +
                    "• Output Organisasi: ${personalRec.recommendedOrganizations.joinToString(", ")}\n" +
                    "• Output Event: ${personalRec.recommendedEvents.joinToString(", ")}"
            setTextColor(Color.parseColor("#334155"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(4)
            }
        })

        contentContainer.addView(aiCard.card)

        // 4. Featured Social Impact Projects Preview
        contentContainer.addView(
            UiComponents.createSectionTitle(
                context = this,
                title = "🔥 Proyek Sosial Prioritas",
                subtitle = "Masalah nyata masyarakat yang membutuhkan kolaborasi lintas jurusan.",
                actionLabel = "Lihat Semua (${CampusRepository.projects.size})"
            ) {
                switchToTab(MainTab.PROJECTS)
            }
        )

        CampusRepository.projects.take(2).forEach { project ->
            contentContainer.addView(buildProjectCard(project))
        }
    }

    // =========================================================================
    // SCREEN 3, 4, 8: PROJECT MARKETPLACE, DETAIL & SUBMIT PROBLEM (MODUL A)
    // =========================================================================
    private fun renderProjectsMarketplace() {
        contentContainer.addView(
            UiComponents.createSectionTitle(
                context = this,
                title = "🛠️ Modul A — Social Impact Hub",
                subtitle = "Project Marketplace: Menghubungkan masalah masyarakat (UMKM, Desa, Sekolah) dengan solusi mahasiswa.",
                actionLabel = "+ Submit Masalah"
            ) {
                AppDialogs.showSubmitProblemDialog(this) { refreshScreen() }
            }
        )

        // Mitra Masyarakat Call-to-Action Card (Screen 8 Entry)
        val submitBanner = UiComponents.createCard(
            context = this,
            bgColorHex = "#EEF2FF",
            strokeColorHex = "#818CF8",
            cornerRadiusDp = 14,
            elevationDp = 1,
            paddingDp = 14
        )
        submitBanner.body.addView(TextView(this).apply {
            text = "🤝 Screen 8 — Punya Masalah UMKM, Desa, Sekolah, atau Komunitas?"
            setTextColor(Color.parseColor("#1E3A8A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTypeface(typeface, Typeface.BOLD)
        })
        submitBanner.body.addView(TextView(this).apply {
            text = "Mitra masyarakat dapat mengajukan masalah (Nama Organisasi, Judul, Deskripsi, Kategori, Lokasi, Foto) untuk dikerjakan oleh tim mahasiswa."
            setTextColor(Color.parseColor("#334155"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(4)
                bottomMargin = dp(8)
            }
        })
        submitBanner.body.addView(
            UiComponents.createButton(
                context = this,
                label = "📤 Buka Form Submit Problem (Mitra Masyarakat)",
                bgColorHex = "#464FEB"
            ) {
                AppDialogs.showSubmitProblemDialog(this) { refreshScreen() }
            }
        )
        contentContainer.addView(submitBanner.card)

        // Category & Status Filter Chips
        val filterScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(12)
            }
        }
        val filterRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        val filters = listOf(
            "Semua", "Open", "On Going", "Completed", "Teknologi", "Lingkungan", "Pendidikan", "Digital Marketing"
        )
        filters.forEach { filterName ->
            val selected = (selectedProjectCategoryFilter == filterName)
            filterRow.addView(
                UiComponents.createPillBadge(
                    context = this,
                    text = filterName,
                    textColorHex = if (selected) "#FFFFFF" else "#1E3A8A",
                    bgColorHex = if (selected) "#464FEB" else "#DBEAFE",
                    endMarginDp = 8
                ).apply {
                    setPadding(dp(14), dp(7), dp(14), dp(7))
                    setOnClickListener {
                        selectedProjectCategoryFilter = filterName
                        refreshScreen()
                    }
                }
            )
        }
        filterScroll.addView(filterRow)
        contentContainer.addView(filterScroll)

        // Filtered Project Cards
        val filteredProjects = CampusRepository.projects.filter { p ->
            when (selectedProjectCategoryFilter) {
                "Semua" -> true
                "Open" -> p.status == ProjectStatus.OPEN
                "On Going" -> p.status == ProjectStatus.ON_GOING
                "Completed" -> p.status == ProjectStatus.COMPLETED
                else -> p.category.equals(selectedProjectCategoryFilter, ignoreCase = true)
            }
        }

        filteredProjects.forEach { project ->
            contentContainer.addView(buildProjectCard(project))
        }
    }

    private fun buildProjectCard(project: lat.pam.campusimpacthub.model.SocialProject): View {
        val holder = UiComponents.createCard(this, paddingDp = 16)

        // Top Row: Category + Impact Score + Status Badge
        val badgeRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        badgeRow.addView(
            UiComponents.createPillBadge(
                context = this,
                text = project.category,
                textColorHex = "#1E3A8A",
                bgColorHex = "#DBEAFE"
            )
        )
        badgeRow.addView(
            UiComponents.createPillBadge(
                context = this,
                text = "🌱 Impact Score: ${project.impactScore}",
                textColorHex = "#047857",
                bgColorHex = "#D1FAE5"
            )
        )
        val (statusTextColor, statusBgColor) = when (project.status) {
            ProjectStatus.OPEN -> "#1E3A8A" to "#E0E7FF"
            ProjectStatus.ON_GOING -> "#B45309" to "#FEF3C7"
            ProjectStatus.COMPLETED -> "#047857" to "#D1FAE5"
        }
        badgeRow.addView(
            UiComponents.createPillBadge(
                context = this,
                text = project.status.label,
                textColorHex = statusTextColor,
                bgColorHex = statusBgColor
            )
        )
        holder.body.addView(badgeRow)

        // Title & Organization
        holder.body.addView(TextView(this).apply {
            text = project.title
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(8)
            }
        })
        holder.body.addView(TextView(this).apply {
            text = "🏢 Mitra: ${project.organizationName}  •  📍 ${project.location}"
            setTextColor(Color.parseColor("#464FEB"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(2)
            }
        })

        // Problem Description
        holder.body.addView(TextView(this).apply {
            text = project.description
            setTextColor(Color.parseColor("#475569"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(6)
            }
        })

        // Kebutuhan Mahasiswa Row
        holder.body.addView(TextView(this).apply {
            text = "Kebutuhan Talenta: ${project.requirements.joinToString(" • ")}"
            setTextColor(Color.parseColor("#1E293B"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(8)
            }
        })

        // Compact Impact Report Strip (Sebelum -> Sesudah | Penerima Manfaat)
        val reportStrip = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = UiComponents.roundedBg("#F0FDF4", 10, "#BBF7D0", 2, this@MainActivity)
            val p = dp(10)
            setPadding(p, p, p, p)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(8)
            }
        }
        reportStrip.addView(TextView(this).apply {
            text = "📊 Impact Report — Penerima Manfaat: ${project.impactReport.beneficiaries} Orang"
            setTextColor(Color.parseColor("#065F46"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTypeface(typeface, Typeface.BOLD)
        })
        reportStrip.addView(TextView(this).apply {
            text = "Sebelum: ${project.impactReport.beforeCondition}\nSesudah: ${project.impactReport.afterCondition}"
            setTextColor(Color.parseColor("#047857"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(2)
            }
        })
        holder.body.addView(reportStrip)

        // Application status indicator if user already applied
        if (project.userApplicationState != "NONE") {
            val appStatusLabel = if (project.userApplicationState == "APPROVED") {
                "✅ Status Anda: Diterima dalam Tim (${project.userRoleInProject})"
            } else {
                "⏳ Status Anda: Menunggu Persetujuan Mitra (${project.userRoleInProject})"
            }
            holder.body.addView(TextView(this).apply {
                text = appStatusLabel
                setTextColor(Color.parseColor("#1E3A8A"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(8)
                }
            })
        }

        // Action Buttons (Lihat Detail [Screen 4] & Daftar Proyek)
        val btnRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(10)
            }
        }
        btnRow.addView(
            UiComponents.createButton(
                context = this,
                label = "👁️ Lihat Detail",
                bgColorHex = "#EEF2FF",
                textColorHex = "#1E3A8A",
                weight = 1f,
                endMarginDp = 8
            ) {
                AppDialogs.showProjectDetailDialog(this, project) { refreshScreen() }
            }
        )
        btnRow.addView(
            UiComponents.createButton(
                context = this,
                label = if (project.userApplicationState == "NONE") "🚀 Daftar Proyek (+50)" else "👑 Upgrade Ketua (+75)",
                bgColorHex = "#464FEB",
                weight = 1f
            ) {
                val asLeader = (project.userApplicationState != "NONE")
                val msg = CampusRepository.applyForProject(project, asTeamLeader = asLeader)
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                refreshScreen()
            }
        )
        holder.body.addView(btnRow)

        holder.card.setOnClickListener {
            AppDialogs.showProjectDetailDialog(this, project) { refreshScreen() }
        }

        return holder.card
    }

    // =========================================================================
    // SCREEN 5 & 6: ORGANIZATION DIRECTORY (MODUL B) & OPEN RECRUITMENT (MODUL C)
    // =========================================================================
    private fun renderOrganizationsHub() {
        // Sub-Tab Switcher: Screen 5 (Directory Organisasi) vs Screen 6 (Open Recruitment)
        val subTabRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(12)
            }
        }
        subTabRow.addView(
            UiComponents.createButton(
                context = this,
                label = "🏛️ Directory Organisasi (${CampusRepository.organizations.size})",
                bgColorHex = if (selectedOrgSubTab == 0) "#464FEB" else "#FFFFFF",
                textColorHex = if (selectedOrgSubTab == 0) "#FFFFFF" else "#1E3A8A",
                weight = 1f,
                endMarginDp = 6
            ) {
                selectedOrgSubTab = 0
                refreshScreen()
            }
        )
        subTabRow.addView(
            UiComponents.createButton(
                context = this,
                label = "📢 Open Recruitment (${CampusRepository.openRecruitments.size})",
                bgColorHex = if (selectedOrgSubTab == 1) "#464FEB" else "#FFFFFF",
                textColorHex = if (selectedOrgSubTab == 1) "#FFFFFF" else "#1E3A8A",
                weight = 1f
            ) {
                selectedOrgSubTab = 1
                refreshScreen()
            }
        )
        contentContainer.addView(subTabRow)

        if (selectedOrgSubTab == 0) {
            renderOrganizationDirectorySection()
        } else {
            renderOpenRecruitmentSection()
        }
    }

    private fun renderOrganizationDirectorySection() {
        contentContainer.addView(
            UiComponents.createSectionTitle(
                context = this,
                title = "🏛️ Screen 5 — Organization Hub & Directory",
                subtitle = "Pusat seluruh UKM, Himpunan, dan BEM Kampus dengan lencana verifikasi resmi.",
                actionLabel = "+ Registrasi UKM"
            ) {
                AppDialogs.showRegisterOrganizationDialog(this) { refreshScreen() }
            }
        )

        CampusRepository.organizations.forEach { org ->
            val holder = UiComponents.createCard(this, paddingDp = 16)

            // Top Row: Monogram Logo + Name + Verification Badge
            val headerRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            val logoView = TextView(this).apply {
                text = org.logoInitials
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTypeface(typeface, Typeface.BOLD)
                background = UiComponents.roundedBg("#464FEB", 12, context = this@MainActivity)
                layoutParams = LinearLayout.LayoutParams(dp(44), dp(44)).apply {
                    marginEnd = dp(12)
                }
            }
            headerRow.addView(logoView)

            val titleCol = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }
            titleCol.addView(TextView(this).apply {
                text = "${org.name} (${org.id})"
                setTextColor(Color.parseColor("#0F172A"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTypeface(typeface, Typeface.BOLD)
            })
            titleCol.addView(TextView(this).apply {
                text = "${org.fullName} • ${org.category}"
                setTextColor(Color.parseColor("#475569"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            })
            headerRow.addView(titleCol)
            holder.body.addView(headerRow)

            // Badge Row: ✅ VERIFIED ORGANIZATION or Pending / Rejected
            val badgeRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(10)
                }
            }
            when (org.verificationStatus) {
                VerificationStatus.VERIFIED -> {
                    badgeRow.addView(
                        UiComponents.createPillBadge(
                            context = this,
                            text = "✅ VERIFIED ORGANIZATION",
                            textColorHex = "#047857",
                            bgColorHex = "#D1FAE5"
                        )
                    )
                }
                VerificationStatus.PENDING -> {
                    badgeRow.addView(
                        UiComponents.createPillBadge(
                            context = this,
                            text = "⏳ STATUS: PENDING VERIFICATION",
                            textColorHex = "#B45309",
                            bgColorHex = "#FEF3C7"
                        )
                    )
                }
                VerificationStatus.REJECTED -> {
                    badgeRow.addView(
                        UiComponents.createPillBadge(
                            context = this,
                            text = "❌ STATUS: REJECTED",
                            textColorHex = "#B91C1C",
                            bgColorHex = "#FEE2E2"
                        )
                    )
                }
            }
            badgeRow.addView(
                UiComponents.createPillBadge(
                    context = this,
                    text = "👥 ${org.memberCount} Anggota",
                    textColorHex = "#1E3A8A",
                    bgColorHex = "#DBEAFE"
                )
            )
            holder.body.addView(badgeRow)

            // Description & Metadata (SK, Pembina, Kontak)
            holder.body.addView(TextView(this).apply {
                text = org.description
                setTextColor(Color.parseColor("#334155"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(8)
                }
            })

            holder.body.addView(TextView(this).apply {
                text = "📄 SK Organisasi: ${org.skNumber}\n👨‍🏫 Pembina: ${org.advisorName}\n📞 Kontak: ${org.contact}"
                setTextColor(Color.parseColor("#64748B"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(6)
                }
            })

            // Action Buttons (Join Organization + Admin Verification Controls)
            val actionRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(10)
                }
            }

            actionRow.addView(
                UiComponents.createButton(
                    context = this,
                    label = if (org.isUserJoined) "✅ Pengurus Aktif (+30 Poin)" else "🤝 Gabung Pengurus (+30 Poin)",
                    bgColorHex = if (org.isUserJoined) "#D1FAE5" else "#464FEB",
                    textColorHex = if (org.isUserJoined) "#065F46" else "#FFFFFF",
                    weight = 1f,
                    endMarginDp = if (org.verificationStatus != VerificationStatus.VERIFIED || CampusRepository.currentUser.role == UserRole.ADMIN) 6 else 0
                ) {
                    if (!org.isUserJoined) {
                        CampusRepository.joinOrganizationAsOfficer(org)
                        Toast.makeText(
                            this,
                            "Bergabung di ${org.name}! +30 Impact Score & +30 Leadership Score ditambahkan.",
                            Toast.LENGTH_LONG
                        ).show()
                        refreshScreen()
                    } else {
                        selectedOrgSubTab = 1
                        refreshScreen()
                    }
                }
            )

            if (org.verificationStatus != VerificationStatus.VERIFIED || CampusRepository.currentUser.role == UserRole.ADMIN) {
                actionRow.addView(
                    UiComponents.createButton(
                        context = this,
                        label = if (org.verificationStatus == VerificationStatus.VERIFIED) "🔄 Set Pending" else "🛡️ Verify SK (Admin)",
                        bgColorHex = "#10B981",
                        weight = 1f
                    ) {
                        val approve = (org.verificationStatus != VerificationStatus.VERIFIED)
                        CampusRepository.verifyOrganization(org, approve = approve)
                        Toast.makeText(
                            this,
                            if (approve) "✅ ${org.name} resmi berstatus VERIFIED ORGANIZATION!" else "Status ${org.name} diubah.",
                            Toast.LENGTH_SHORT
                        ).show()
                        refreshScreen()
                    }
                )
            }

            holder.body.addView(actionRow)
            contentContainer.addView(holder.card)
        }
    }

    private fun renderOpenRecruitmentSection() {
        contentContainer.addView(
            UiComponents.createSectionTitle(
                context = this,
                title = "📢 Screen 6 — Modul C: Open Recruitment",
                subtitle = "Alur Seleksi Terintegrasi: Pendaftaran → Administrasi → Interview → Pengumuman.",
                actionLabel = "+ Buka Oprec"
            ) {
                AppDialogs.showCreateRecruitmentDialog(this) { refreshScreen() }
            }
        )

        CampusRepository.openRecruitments.forEach { rec ->
            val holder = UiComponents.createCard(this, paddingDp = 16)

            // Top Badges: Status Open + Applicant Status
            val badgeRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            badgeRow.addView(
                UiComponents.createPillBadge(
                    context = this,
                    text = "OPEN RECRUITMENT",
                    textColorHex = "#1E3A8A",
                    bgColorHex = "#DBEAFE"
                )
            )
            val (appTextCol, appBgCol) = when (rec.applicantStatus) {
                ApplicantStatus.ACCEPTED -> "#047857" to "#D1FAE5"
                ApplicantStatus.PENDING -> "#B45309" to "#FEF3C7"
                ApplicantStatus.REJECTED -> "#B91C1C" to "#FEE2E2"
                ApplicantStatus.NONE -> "#475569" to "#F1F5F9"
            }
            badgeRow.addView(
                UiComponents.createPillBadge(
                    context = this,
                    text = "Status Pelamar: ${rec.applicantStatus.label}",
                    textColorHex = appTextCol,
                    bgColorHex = appBgCol
                )
            )
            holder.body.addView(badgeRow)

            holder.body.addView(TextView(this).apply {
                text = "${rec.organizationName} — ${rec.title}"
                setTextColor(Color.parseColor("#0F172A"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(8)
                }
            })

            holder.body.addView(TextView(this).apply {
                text = "⏰ Deadline: ${rec.deadline}  •  👥 ${rec.totalApplicants} Pendaftar"
                setTextColor(Color.parseColor("#464FEB"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(2)
                }
            })

            // Posisi Tersedia (Klik untuk pilih & daftar posisi)
            holder.body.addView(TextView(this).apply {
                text = "Posisi Dibuka (Ketuk posisi untuk mendaftar):"
                setTextColor(Color.parseColor("#1E293B"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(8)
                    bottomMargin = dp(6)
                }
            })

            val posRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            rec.positions.forEach { pos ->
                val isChosen = (rec.selectedPosition == pos)
                posRow.addView(
                    UiComponents.createPillBadge(
                        context = this,
                        text = if (isChosen) "✅ $pos" else "📌 $pos",
                        textColorHex = if (isChosen) "#FFFFFF" else "#1E3A8A",
                        bgColorHex = if (isChosen) "#464FEB" else "#EEF2FF"
                    ).apply {
                        setPadding(dp(12), dp(6), dp(12), dp(6))
                        setOnClickListener {
                            CampusRepository.applyToRecruitment(rec, pos)
                            Toast.makeText(
                                this@MainActivity,
                                "Terdaftar pada posisi $pos di ${rec.organizationName}! Status: Pending (Tahap Administrasi).",
                                Toast.LENGTH_SHORT
                            ).show()
                            refreshScreen()
                        }
                    }
                )
            }
            holder.body.addView(posRow)

            // Alur Seleksi Stepper Visual
            val stageBox = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = UiComponents.roundedBg("#F8FAFC", 10, "#E2E8F0", 2, this@MainActivity)
                val p = dp(10)
                setPadding(p, p, p, p)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(10)
                }
            }
            stageBox.addView(TextView(this).apply {
                text = "Alur Seleksi (Tahap Saat Ini: ${rec.currentStage.label}):"
                setTextColor(Color.parseColor("#0F172A"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                setTypeface(typeface, Typeface.BOLD)
            })
            val stagesText = SelectionStage.entries.joinToString("  →  ") { stage ->
                if (stage.stepNumber <= rec.currentStage.stepNumber) "🔵 ${stage.label}" else "⚪ ${stage.label}"
            }
            stageBox.addView(TextView(this).apply {
                text = stagesText
                setTextColor(Color.parseColor("#464FEB"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(4)
                }
            })
            holder.body.addView(stageBox)

            // Action Buttons
            val btnRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(10)
                }
            }
            btnRow.addView(
                UiComponents.createButton(
                    context = this,
                    label = if (rec.selectedPosition == null) "📝 Daftar Posisi ${rec.positions.first()}" else "⏩ Lanjut Tahap Seleksi",
                    bgColorHex = "#464FEB",
                    weight = 1f
                ) {
                    if (rec.selectedPosition == null) {
                        CampusRepository.applyToRecruitment(rec, rec.positions.first())
                        Toast.makeText(
                            this,
                            "Pendaftaran posisi ${rec.positions.first()} berhasil dikirim!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        CampusRepository.advanceRecruitmentSelection(rec, acceptAtEnd = true)
                        Toast.makeText(
                            this,
                            "Tahap seleksi diperbarui ke: ${rec.currentStage.label} (Status: ${rec.applicantStatus.label})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    refreshScreen()
                }
            )
            holder.body.addView(btnRow)

            contentContainer.addView(holder.card)
        }
    }

    // =========================================================================
    // SCREEN 7: EVENT HUB, PENDAFTARAN EVENT & QR EVENT TICKET (MODUL D)
    // =========================================================================
    private fun renderEventsHub() {
        contentContainer.addView(
            UiComponents.createSectionTitle(
                context = this,
                title = "📅 Screen 7 — Modul D: Event Hub Kampus",
                subtitle = "Satu tempat untuk seluruh Seminar, Workshop, Webinar, Bootcamp, Volunteer, Kompetisi & Pelatihan.",
                actionLabel = "+ Buat Event"
            ) {
                AppDialogs.showCreateEventDialog(this) { refreshScreen() }
            }
        )

        // Filter 7 Jenis Event
        val filterScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(12)
            }
        }
        val filterRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        val types = listOf("Semua") + EventType.entries.map { it.label }
        types.forEach { tLabel ->
            val selected = (selectedEventTypeFilter == tLabel)
            filterRow.addView(
                UiComponents.createPillBadge(
                    context = this,
                    text = tLabel,
                    textColorHex = if (selected) "#FFFFFF" else "#1E3A8A",
                    bgColorHex = if (selected) "#464FEB" else "#DBEAFE",
                    endMarginDp = 8
                ).apply {
                    setPadding(dp(14), dp(7), dp(14), dp(7))
                    setOnClickListener {
                        selectedEventTypeFilter = tLabel
                        refreshScreen()
                    }
                }
            )
        }
        filterScroll.addView(filterRow)
        contentContainer.addView(filterScroll)

        val filteredEvents = CampusRepository.events.filter {
            selectedEventTypeFilter == "Semua" || it.type.label.equals(selectedEventTypeFilter, ignoreCase = true)
        }

        filteredEvents.forEach { event ->
            val holder = UiComponents.createCard(this, paddingDp = 16)

            // Top Badges: Type + Points + Attendance Status
            val badgeRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            badgeRow.addView(
                UiComponents.createPillBadge(
                    context = this,
                    text = event.type.label,
                    textColorHex = "#7C3AED",
                    bgColorHex = "#EDE9FE"
                )
            )
            badgeRow.addView(
                UiComponents.createPillBadge(
                    context = this,
                    text = "🌱 +${event.pointsReward} Poin Impact",
                    textColorHex = "#047857",
                    bgColorHex = "#D1FAE5"
                )
            )
            if (event.isAttended) {
                badgeRow.addView(
                    UiComponents.createPillBadge(
                        context = this,
                        text = "✅ Hadir (QR Scanned)",
                        textColorHex = "#047857",
                        bgColorHex = "#D1FAE5"
                    )
                )
            } else if (event.isRegistered) {
                badgeRow.addView(
                    UiComponents.createPillBadge(
                        context = this,
                        text = "🎟️ Terdaftar",
                        textColorHex = "#1E3A8A",
                        bgColorHex = "#DBEAFE"
                    )
                )
            }
            holder.body.addView(badgeRow)

            // Event Title & Organizer
            holder.body.addView(TextView(this).apply {
                text = event.title
                setTextColor(Color.parseColor("#0F172A"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(8)
                }
            })

            holder.body.addView(TextView(this).apply {
                text = "🏛️ Penyelenggara: ${event.organizerName}\n📅 ${event.date}  •  📍 ${event.location}"
                setTextColor(Color.parseColor("#464FEB"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(2)
                }
            })

            // Poster Banner Box
            holder.body.addView(TextView(this).apply {
                text = "🖼️ ${event.posterSummary}"
                setTextColor(Color.parseColor("#1E3A8A"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                background = UiComponents.roundedBg("#EEF2FF", 8, "#C7D2FE", 2, this@MainActivity)
                setPadding(dp(10), dp(6), dp(10), dp(6))
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(8)
                }
            })

            holder.body.addView(TextView(this).apply {
                text = event.description
                setTextColor(Color.parseColor("#475569"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(6)
                }
            })

            // Quota Progress Bar
            holder.body.addView(TextView(this).apply {
                text = "Kuota Peserta: ${event.registeredCount} / ${event.quota} Terdaftar"
                setTextColor(Color.parseColor("#334155"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(8)
                }
            })
            val quotaBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
                max = event.quota
                progress = event.registeredCount
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(8)
                ).apply {
                    topMargin = dp(4)
                }
            }
            holder.body.addView(quotaBar)

            // Action Buttons: Daftar Event / Lihat QR Ticket / Scan QR Hari H
            val btnRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(10)
                }
            }

            if (!event.isRegistered) {
                btnRow.addView(
                    UiComponents.createButton(
                        context = this,
                        label = "📝 Daftar Event & Dapatkan QR Ticket",
                        bgColorHex = "#464FEB",
                        weight = 1f
                    ) {
                        CampusRepository.registerForEvent(event)
                        Toast.makeText(
                            this,
                            "Berhasil terdaftar di ${event.title}! QR Ticket Anda: ${event.qrTicketCode}",
                            Toast.LENGTH_SHORT
                        ).show()
                        refreshScreen()
                        AppDialogs.showQrTicketDialog(this, event) { refreshScreen() }
                    }
                )
            } else {
                btnRow.addView(
                    UiComponents.createButton(
                        context = this,
                        label = "🎟️ Lihat QR Ticket (${event.qrTicketCode})",
                        bgColorHex = "#1E3A8A",
                        weight = 1f,
                        endMarginDp = 6
                    ) {
                        AppDialogs.showQrTicketDialog(this, event) { refreshScreen() }
                    }
                )
                btnRow.addView(
                    UiComponents.createButton(
                        context = this,
                        label = if (event.isAttended) "✅ Poin Masuk" else "📷 Scan QR (+10)",
                        bgColorHex = if (event.isAttended) "#059669" else "#10B981",
                        weight = 1f
                    ) {
                        AppDialogs.showQrTicketDialog(this, event) { refreshScreen() }
                    }
                )
            }

            holder.body.addView(btnRow)
            contentContainer.addView(holder.card)
        }
    }

    // =========================================================================
    // SCREEN 9: MY IMPACT, LEADERSHIP SCORE, ACTIVITY POINTS & PORTFOLIO (MODUL E & F)
    // =========================================================================
    private fun renderImpactAndPortfolioHub() {
        val user = CampusRepository.currentUser

        // 1. Hero Card: My Impact & Leadership Score (Modul E)
        val impactHero = UiComponents.createCard(
            context = this,
            bgColorHex = "#065F46",
            strokeColorHex = "#10B981",
            cornerRadiusDp = 18,
            elevationDp = 4,
            paddingDp = 18
        )
        impactHero.body.addView(
            UiComponents.createPillBadge(
                context = this,
                text = "🌟 SCREEN 9 • MODUL E: IMPACT & PORTFOLIO HUB",
                textColorHex = "#065F46",
                bgColorHex = "#D1FAE5"
            )
        )
        impactHero.body.addView(TextView(this).apply {
            text = "My Impact & Leadership Analytics"
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(8)
            }
        })

        // Dual Big Scores Row (Impact Score & Leadership Score)
        val dualScoreRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(12)
                bottomMargin = dp(12)
            }
        }

        val impactScoreBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = UiComponents.roundedBg("#047857", 14, "#34D399", 2, this@MainActivity)
            val p = dp(12)
            setPadding(p, p, p, p)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = dp(6)
            }
        }
        impactScoreBox.addView(TextView(this).apply {
            text = "🌱 Impact Score"
            setTextColor(Color.parseColor("#A7F3D0"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTypeface(typeface, Typeface.BOLD)
        })
        impactScoreBox.addView(TextView(this).apply {
            text = "${user.impactScore}"
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
            setTypeface(typeface, Typeface.BOLD)
        })
        impactScoreBox.addView(TextView(this).apply {
            text = "Top 3% Kontributor Kampus"
            setTextColor(Color.parseColor("#D1FAE5"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
        })

        val leadershipScoreBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = UiComponents.roundedBg("#1E3A8A", 14, "#60A5FA", 2, this@MainActivity)
            val p = dp(12)
            setPadding(p, p, p, p)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = dp(6)
            }
        }
        leadershipScoreBox.addView(TextView(this).apply {
            text = "⭐ Leadership Score"
            setTextColor(Color.parseColor("#BFDBFE"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTypeface(typeface, Typeface.BOLD)
        })
        leadershipScoreBox.addView(TextView(this).apply {
            text = "${user.leadershipScore}"
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
            setTypeface(typeface, Typeface.BOLD)
        })
        leadershipScoreBox.addView(TextView(this).apply {
            text = "Pengurus • Ketua Panitia • Ketua Tim"
            setTextColor(Color.parseColor("#DBEAFE"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
        })

        dualScoreRow.addView(impactScoreBox)
        dualScoreRow.addView(leadershipScoreBox)
        impactHero.body.addView(dualScoreRow)

        // 4 Summary Metrics matching prompt example (Project: 8, UMKM Dibantu: 5, Beneficiaries: 1000+, Jam Kontribusi)
        impactHero.body.addView(TextView(this).apply {
            text = "📌 Project : ${user.projectsCompleted}   |   🏪 UMKM Dibantu : ${user.umkmHelped}   |   👥 Beneficiaries : ${user.beneficiariesReached}+   |   ⏱️ Jam Kontribusi : ${user.contributionHours} Jam"
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.BOLD)
        })

        contentContainer.addView(impactHero.card)

        // 2. Modul F — Activity Point System Table
        contentContainer.addView(
            UiComponents.createSectionTitle(
                context = this,
                title = "🏆 Modul F — Activity Point System",
                subtitle = "Setiap aktivitas terverifikasi otomatis menambah Impact Score & Leadership Score mahasiswa."
            )
        )

        val pointsCard = UiComponents.createCard(this, paddingDp = 14)
        CampusRepository.activityPointRules.forEachIndexed { idx, rule ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            val infoCol = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }
            infoCol.addView(TextView(this).apply {
                text = rule.activityName
                setTextColor(Color.parseColor("#0F172A"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                setTypeface(typeface, Typeface.BOLD)
            })
            infoCol.addView(TextView(this).apply {
                text = rule.description
                setTextColor(Color.parseColor("#64748B"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            })
            row.addView(infoCol)

            row.addView(
                UiComponents.createPillBadge(
                    context = this,
                    text = "+${rule.points} Poin",
                    textColorHex = "#047857",
                    bgColorHex = "#D1FAE5",
                    endMarginDp = 0
                )
            )
            pointsCard.body.addView(row)
            if (idx < CampusRepository.activityPointRules.lastIndex) {
                pointsCard.body.addView(UiComponents.createDivider(this, 8))
            }
        }
        contentContainer.addView(pointsCard.card)

        // 3. Student Portfolio Section (Project Sosial, Organisasi, Event, Sertifikat, Prestasi)
        contentContainer.addView(
            UiComponents.createSectionTitle(
                context = this,
                title = "🎓 Student Portfolio Terverifikasi",
                subtitle = "Rekam jejak kontribusi sosial yang terukur dan dapat dibuktikan saat lulus.",
                actionLabel = "📤 Export CV Dampak"
            ) {
                AlertDialog.Builder(this)
                    .setTitle("📄 Portofolio Dampak & SKPI Mahasiswa")
                    .setMessage(
                        "Nama: ${user.name} (${user.identityNumber})\n" +
                                "Program Studi: ${user.majorOrUnit}\n" +
                                "Impact Score: ${user.impactScore} Poin\n" +
                                "Leadership Score: ${user.leadershipScore} Poin\n" +
                                "Total Proyek Sosial: ${user.projectsCompleted} Proyek (${user.umkmHelped} UMKM, ${user.beneficiariesReached}+ Penerima Manfaat)\n\n" +
                                "Dokumen portofolio terverifikasi siap dibagikan ke LinkedIn & sistem MBKM Kampus!"
                    )
                    .setPositiveButton("Bagikan / Salin Tautan") { _, _ ->
                        Toast.makeText(
                            this,
                            "Tautan Portofolio Dampak (${user.id}) berhasil disalin!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .setNegativeButton("Tutup", null)
                    .show()
            }
        )

        // Portfolio Filter Chips
        val pfScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = dp(10)
            }
        }
        val pfRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        listOf("Semua", "Project Sosial", "Organisasi", "Event", "Sertifikat", "Prestasi").forEach { cat ->
            val selected = (selectedPortfolioFilter == cat)
            pfRow.addView(
                UiComponents.createPillBadge(
                    context = this,
                    text = cat,
                    textColorHex = if (selected) "#FFFFFF" else "#1E3A8A",
                    bgColorHex = if (selected) "#464FEB" else "#DBEAFE",
                    endMarginDp = 8
                ).apply {
                    setPadding(dp(12), dp(6), dp(12), dp(6))
                    setOnClickListener {
                        selectedPortfolioFilter = cat
                        refreshScreen()
                    }
                }
            )
        }
        pfScroll.addView(pfRow)
        contentContainer.addView(pfScroll)

        val filteredPf = CampusRepository.portfolioEntries.filter {
            selectedPortfolioFilter == "Semua" || it.category.equals(selectedPortfolioFilter, ignoreCase = true)
        }

        filteredPf.forEach { entry ->
            val holder = UiComponents.createCard(this, paddingDp = 14)
            val topRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }
            topRow.addView(
                UiComponents.createPillBadge(
                    context = this,
                    text = entry.category,
                    textColorHex = "#1E3A8A",
                    bgColorHex = "#DBEAFE"
                )
            )
            topRow.addView(
                UiComponents.createPillBadge(
                    context = this,
                    text = "🌱 +${entry.impactPoints} Impact",
                    textColorHex = "#047857",
                    bgColorHex = "#D1FAE5"
                )
            )
            if (entry.leadershipPoints > 0) {
                topRow.addView(
                    UiComponents.createPillBadge(
                        context = this,
                        text = "⭐ +${entry.leadershipPoints} Leadership",
                        textColorHex = "#B45309",
                        bgColorHex = "#FEF3C7"
                    )
                )
            }
            holder.body.addView(topRow)

            holder.body.addView(TextView(this).apply {
                text = entry.title
                setTextColor(Color.parseColor("#0F172A"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(6)
                }
            })

            holder.body.addView(TextView(this).apply {
                text = "${entry.organizationOrPartner} • ${entry.date} • ID: ${entry.credentialCode}"
                setTextColor(Color.parseColor("#464FEB"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(2)
                }
            })

            holder.body.addView(TextView(this).apply {
                text = entry.outcomeSummary
                setTextColor(Color.parseColor("#475569"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = dp(4)
                }
            })

            contentContainer.addView(holder.card)
        }
    }

    // =========================================================================
    // SCREEN 10: PROFILE, ROLE MANAGEMENT, SUPABASE ARCHITECTURE & ROADMAP
    // =========================================================================
    private fun renderProfileScreen() {
        val user = CampusRepository.currentUser

        // LinkedIn-style Profile Card
        val profileCard = UiComponents.createCard(this, paddingDp = 18)

        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val avatar = TextView(this).apply {
            text = user.name.take(2).uppercase()
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            background = UiComponents.roundedBg("#464FEB", 999, context = this@MainActivity)
            layoutParams = LinearLayout.LayoutParams(dp(56), dp(56)).apply {
                marginEnd = dp(14)
            }
        }
        topRow.addView(avatar)

        val infoCol = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        infoCol.addView(TextView(this).apply {
            text = user.name
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
        })
        infoCol.addView(TextView(this).apply {
            text = "${user.majorOrUnit}\n${user.email} • ${user.identityNumber}"
            setTextColor(Color.parseColor("#475569"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        })
        topRow.addView(infoCol)
        profileCard.body.addView(topRow)

        profileCard.body.addView(TextView(this).apply {
            text = user.bio
            setTextColor(Color.parseColor("#334155"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(10)
            }
        })

        // Skills & Interests
        profileCard.body.addView(TextView(this).apply {
            text = "Keahlian & Minat AI Recommendation: ${user.skills.joinToString(" • ")} | Minat: ${user.interests.joinToString(", ")}"
            setTextColor(Color.parseColor("#1E3A8A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(8)
            }
        })

        val authBtnRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(12)
            }
        }
        authBtnRow.addView(
            UiComponents.createButton(
                context = this,
                label = "🔄 Ganti Role Pengguna",
                bgColorHex = "#464FEB",
                weight = 1f,
                endMarginDp = 6
            ) {
                AppDialogs.showRoleSwitcherDialog(
                    context = this,
                    onRoleChanged = { refreshScreen() },
                    onOpenAuthScreen = { switchToTab(MainTab.AUTH_SCREEN) }
                )
            }
        )
        authBtnRow.addView(
            UiComponents.createButton(
                context = this,
                label = "🔐 Screen 1: Login/Register",
                bgColorHex = "#EEF2FF",
                textColorHex = "#1E3A8A",
                weight = 1f
            ) {
                switchToTab(MainTab.AUTH_SCREEN)
            }
        )
        profileCard.body.addView(authBtnRow)
        contentContainer.addView(profileCard.card)

        // Section 8 & 6: Arsitektur Teknologi & Struktur Database MVP (Supabase)
        contentContainer.addView(
            UiComponents.createSectionTitle(
                context = this,
                title = "☁️ Arsitektur Teknologi & Database MVP",
                subtitle = "Android Studio (Kotlin) → Supabase API → Supabase Auth → PostgreSQL → Storage"
            )
        )

        val archCard = UiComponents.createCard(this, paddingDp = 16)
        archCard.body.addView(TextView(this).apply {
            text = "Stack Arsitektur Sistem:\n" +
                    "1. Android Studio (Kotlin) — UI Modern Material Design & LinkedIn Card\n" +
                    "2. Supabase API — RESTful & Realtime Sync\n" +
                    "3. Supabase Auth — Multi-Role Authentication (Student, Partner, Organization, Admin)\n" +
                    "4. PostgreSQL — Relasi Tabel Users, Projects, Applications, Organizations, Recruitment, Events, Impact Reports\n" +
                    "5. Supabase Storage — Penyimpanan Foto Masalah Mitra, SK Organisasi & Poster Event"
            setTextColor(Color.parseColor("#334155"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        })

        archCard.body.addView(
            UiComponents.createButton(
                context = this,
                label = "🗄️ Lihat JSON Struktur Database MVP (Live Data)",
                bgColorHex = "#1E3A8A"
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Struktur Database MVP (PostgreSQL / Supabase)")
                    .setMessage(CampusRepository.getMvpDatabaseJsonSnapshot())
                    .setPositiveButton("Tutup", null)
                    .show()
            }.apply {
                (layoutParams as LinearLayout.LayoutParams).topMargin = dp(10)
            }
        )
        contentContainer.addView(archCard.card)

        // Roadmap Paling Realistis & Elevator Pitch Card
        val roadmapCard = UiComponents.createCard(this, paddingDp = 16)
        roadmapCard.body.addView(TextView(this).apply {
            text = "🗺️ Roadmap Pengembangan Campus Impact Hub"
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            setTypeface(typeface, Typeface.BOLD)
        })
        roadmapCard.body.addView(TextView(this).apply {
            text = "• MVP (2 Minggu): ✅ Login/Register, ✅ Dashboard, ✅ Project Marketplace, ✅ Submit Problem, ✅ Organization Directory, ✅ Open Recruitment, ✅ Event Hub, ✅ Impact Score\n" +
                    "• V1 (1 Bulan): ✅ Chat Tim, ✅ Upload Dokumen, ✅ QR Attendance, ✅ Leadership Score\n" +
                    "• V2 (2-3 Bulan): ✅ AI Recommendation, ✅ AI Problem Matching, ✅ Heatmap Masalah Sosial, ✅ SDGs Tracker\n" +
                    "• V3 (6 Bulan): ✅ Integrasi MBKM, ✅ Integrasi Skripsi, ✅ Integrasi Pengabdian Dosen, ✅ Sertifikat Otomatis, ✅ Campus Reputation Analytics"
            setTextColor(Color.parseColor("#334155"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(6)
            }
        })
        roadmapCard.body.addView(UiComponents.createDivider(this, 10))
        roadmapCard.body.addView(TextView(this).apply {
            text = "💡 Elevator Pitch:\n\"Campus Impact Hub adalah Super App Ekosistem Kampus yang mengintegrasikan pengabdian masyarakat, marketplace proyek sosial, rekrutmen organisasi, manajemen event, serta portofolio dampak mahasiswa dalam satu platform. Mahasiswa tidak hanya lulus dengan IPK, tetapi juga dengan rekam jejak kontribusi sosial yang terukur dan dapat dibuktikan.\""
            setTextColor(Color.parseColor("#047857"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.ITALIC)
        })
        contentContainer.addView(roadmapCard.card)
    }
}
