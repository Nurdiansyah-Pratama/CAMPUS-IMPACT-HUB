package lat.pam.campusimpacthub.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import lat.pam.campusimpacthub.data.CampusRepository
import lat.pam.campusimpacthub.model.CampusEvent
import lat.pam.campusimpacthub.model.EventType
import lat.pam.campusimpacthub.model.SocialProject
import lat.pam.campusimpacthub.model.UserRole
import lat.pam.campusimpacthub.ui.UiComponents.dp
import lat.pam.campusimpacthub.util.QrCodeGenerator

object AppDialogs {

    // --- Screen 4: Project Detail & Application Dialog ---
    fun showProjectDetailDialog(
        context: Context,
        project: SocialProject,
        onStateChanged: () -> Unit
    ) {
        val scroll = ScrollView(context)
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val pad = context.dp(20)
            setPadding(pad, pad, pad, pad)
            setBackgroundColor(Color.WHITE)
        }
        scroll.addView(root)

        // Header Badges
        val badgeRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        badgeRow.addView(
            UiComponents.createPillBadge(
                context,
                text = project.category,
                textColorHex = "#1E3A8A",
                bgColorHex = "#DBEAFE"
            )
        )
        badgeRow.addView(
            UiComponents.createPillBadge(
                context,
                text = "🌱 Impact Score: ${project.impactScore}",
                textColorHex = "#047857",
                bgColorHex = "#D1FAE5"
            )
        )
        badgeRow.addView(
            UiComponents.createPillBadge(
                context,
                text = project.status.label,
                textColorHex = "#B45309",
                bgColorHex = "#FEF3C7"
            )
        )
        root.addView(badgeRow)

        // Title & Partner
        root.addView(TextView(context).apply {
            text = project.title
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 19f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(10)
            }
        })

        root.addView(TextView(context).apply {
            text = "Mitra Masyarakat: ${project.organizationName} • 📍 ${project.location}"
            setTextColor(Color.parseColor("#464FEB"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(4)
            }
        })

        // Photo Documentation Box
        val photoBox = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = UiComponents.roundedBg("#EEF2FF", 12, "#C7D2FE", 2, context)
            val p = context.dp(12)
            setPadding(p, p, p, p)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(12)
            }
        }
        photoBox.addView(TextView(context).apply {
            text = "📷 ${project.photoCaption}"
            setTextColor(Color.parseColor("#1E3A8A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.BOLD)
        })
        photoBox.addView(TextView(context).apply {
            text = project.description
            setTextColor(Color.parseColor("#334155"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(6)
            }
        })
        root.addView(photoBox)

        // Talent Requirements
        root.addView(TextView(context).apply {
            text = "Kebutuhan Talenta Mahasiswa:"
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(12)
                bottomMargin = context.dp(6)
            }
        })

        val reqRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        project.requirements.forEach { req ->
            reqRow.addView(
                UiComponents.createPillBadge(
                    context = context,
                    text = "👨‍💻 $req",
                    textColorHex = "#1E3A8A",
                    bgColorHex = "#E0E7FF"
                )
            )
        }
        root.addView(reqRow)

        // Project Application Flow (Lihat Detail -> Daftar Proyek -> Menunggu Persetujuan)
        val flowBox = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = UiComponents.roundedBg("#F8FAFC", 12, "#E2E8F0", 2, context)
            val p = context.dp(12)
            setPadding(p, p, p, p)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(12)
            }
        }
        flowBox.addView(TextView(context).apply {
            text = "Alur Project Application:"
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.BOLD)
        })
        val currentStepText = when (project.userApplicationState) {
            "APPROVED" -> "✅ 1. Lihat Detail  →  ✅ 2. Daftar Proyek  →  🎉 3. Disetujui (${project.userRoleInProject})"
            "PENDING" -> "✅ 1. Lihat Detail  →  ✅ 2. Daftar Proyek  →  ⏳ 3. Menunggu Persetujuan (${project.userRoleInProject})"
            else -> "👁️ 1. Lihat Detail  →  📝 2. Daftar Proyek  →  ⏳ 3. Menunggu Persetujuan"
        }
        flowBox.addView(TextView(context).apply {
            text = currentStepText
            setTextColor(Color.parseColor("#464FEB"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(4)
            }
        })
        root.addView(flowBox)

        // Impact Report Card (Sebelum vs Sesudah & Penerima Manfaat)
        val impactBox = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = UiComponents.roundedBg("#ECFDF5", 12, "#6EE7B7", 2, context)
            val p = context.dp(12)
            setPadding(p, p, p, p)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(12)
            }
        }
        impactBox.addView(TextView(context).apply {
            text = "📊 Impact Report (Laporan Dampak Terukur)"
            setTextColor(Color.parseColor("#065F46"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setTypeface(typeface, Typeface.BOLD)
        })
        impactBox.addView(TextView(context).apply {
            text = "• Sebelum: ${project.impactReport.beforeCondition}\n" +
                    "• Sesudah: ${project.impactReport.afterCondition}\n" +
                    "• Penerima Manfaat: ${project.impactReport.beneficiaries} Orang\n" +
                    "• Target SDG: ${project.impactReport.sdgCategory}\n" +
                    "• Feedback Mitra: \"${project.impactReport.partnerFeedback}\""
            setTextColor(Color.parseColor("#047857"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(6)
            }
        })
        root.addView(impactBox)

        val dialog = AlertDialog.Builder(context)
            .setView(scroll)
            .setNegativeButton("Tutup", null)
            .create()

        // Action Buttons inside Dialog
        root.addView(
            UiComponents.createButton(
                context = context,
                label = "🚀 Daftar Proyek Sosial (+50 Impact Score)",
                bgColorHex = "#464FEB"
            ) {
                val msg = CampusRepository.applyForProject(project, asTeamLeader = false)
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                dialog.dismiss()
                onStateChanged()
            }.apply {
                (layoutParams as LinearLayout.LayoutParams).topMargin = context.dp(14)
            }
        )

        root.addView(
            UiComponents.createButton(
                context = context,
                label = "👑 Daftar sebagai Ketua Tim Proyek (+75 Impact & +75 Leadership)",
                bgColorHex = "#059669"
            ) {
                val msg = CampusRepository.applyForProject(project, asTeamLeader = true)
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                dialog.dismiss()
                onStateChanged()
            }.apply {
                (layoutParams as LinearLayout.LayoutParams).topMargin = context.dp(8)
            }
        )

        root.addView(
            UiComponents.createButton(
                context = context,
                label = "🔄 Ubah Status Progress (${project.status.label} → Tahap Berikutnya)",
                bgColorHex = "#E2E8F0",
                textColorHex = "#1E293B"
            ) {
                val nextStatus = CampusRepository.cycleProjectStatus(project)
                Toast.makeText(
                    context,
                    "Status proyek \"${project.title}\" diperbarui menjadi: ${nextStatus.label}",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
                onStateChanged()
            }.apply {
                (layoutParams as LinearLayout.LayoutParams).topMargin = context.dp(8)
            }
        )

        dialog.show()
    }

    // --- Screen 8: Submit Problem Dialog (Untuk Mitra Masyarakat) ---
    fun showSubmitProblemDialog(
        context: Context,
        onSubmitted: () -> Unit
    ) {
        val scroll = ScrollView(context)
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val pad = context.dp(20)
            setPadding(pad, pad, pad, pad)
            setBackgroundColor(Color.WHITE)
        }
        scroll.addView(root)

        root.addView(TextView(context).apply {
            text = "🤝 Submit Masalah Masyarakat"
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
        })
        root.addView(TextView(context).apply {
            text = "Hubungkan masalah UMKM, Desa, Sekolah, atau Komunitas dengan talenta mahasiswa kampus."
            setTextColor(Color.parseColor("#475569"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(4)
                bottomMargin = context.dp(12)
            }
        })

        val orgField = UiComponents.createInputField(
            context,
            "Nama Organisasi / Mitra Masyarakat",
            "Contoh: UMKM Maju Jaya / Desa Sukamaju",
            "UMKM Berkah Mandiri"
        )
        val titleField = UiComponents.createInputField(
            context,
            "Judul Masalah",
            "Contoh: Pencatatan transaksi masih manual",
            "UMKM kesulitan pemasaran online & katalog digital"
        )
        val descField = UiComponents.createInputField(
            context,
            "Deskripsi Masalah",
            "Jelaskan kendala yang dihadapi mitra masyarakat...",
            "Produk kuliner lokal belum memiliki toko online dan pencatatan stok masih ditulis manual di buku.",
            multiline = true
        )
        val categoryField = UiComponents.createInputField(
            context,
            "Kategori",
            "Teknologi / Digital Marketing / Lingkungan / Pendidikan",
            "Digital Marketing"
        )
        val locationField = UiComponents.createInputField(
            context,
            "Lokasi",
            "Contoh: Kec. Coblong, Kota Bandung",
            "Kec. Sukasari, Bandung"
        )
        val photoField = UiComponents.createInputField(
            context,
            "Foto / Dokumentasi Lokasi",
            "Nama file atau deskripsi foto lokasi",
            "foto_lapangan_umkm_berkah.jpg (Terlampir)"
        )
        val reqField = UiComponents.createInputField(
            context,
            "Kebutuhan Mahasiswa (Rekomendasi AI)",
            "Contoh: 2 Informatika, 1 Desain",
            "1 Bisnis Digital, 1 Informatika, 1 DKV"
        )
        val benField = UiComponents.createInputField(
            context,
            "Estimasi Penerima Manfaat (Orang)",
            "Contoh: 120",
            "150"
        )

        // Button: Auto-Analyze with AI Problem Matching
        val aiButton = UiComponents.createButton(
            context = context,
            label = "✨ Analisis Otomatis dengan AI Problem Matching",
            bgColorHex = "#EEF2FF",
            textColorHex = "#464FEB"
        ) {
            val match = CampusRepository.matchProblemWithAi(
                "${titleField.editText.text} ${descField.editText.text}"
            )
            categoryField.editText.setText(match.category)
            reqField.editText.setText(match.recommendedMajors.joinToString(", ") { "1 $it" })
            Toast.makeText(
                context,
                "AI Problem Matching: Kategori ${match.category} • Estimasi Dampak: ${match.estimatedImpact}",
                Toast.LENGTH_LONG
            ).show()
        }.apply {
            (layoutParams as LinearLayout.LayoutParams).bottomMargin = context.dp(10)
        }

        root.addView(orgField.container)
        root.addView(titleField.container)
        root.addView(descField.container)
        root.addView(aiButton)
        root.addView(categoryField.container)
        root.addView(locationField.container)
        root.addView(photoField.container)
        root.addView(reqField.container)
        root.addView(benField.container)

        val dialog = AlertDialog.Builder(context)
            .setView(scroll)
            .setNegativeButton("Batal", null)
            .create()

        root.addView(
            UiComponents.createButton(
                context = context,
                label = "📤 Submit Masalah ke Project Marketplace",
                bgColorHex = "#10B981"
            ) {
                val orgName = orgField.editText.text.toString().ifBlank { "UMKM Maju Jaya" }
                val title = titleField.editText.text.toString().ifBlank { "Digitalisasi UMKM" }
                val desc = descField.editText.text.toString().ifBlank { "Pencatatan transaksi masih manual" }
                val cat = categoryField.editText.text.toString().ifBlank { "Teknologi" }
                val loc = locationField.editText.text.toString().ifBlank { "Bandung" }
                val photo = photoField.editText.text.toString()
                val req = reqField.editText.text.toString()
                val ben = benField.editText.text.toString().toIntOrNull() ?: 120

                val created = CampusRepository.submitProblem(
                    orgName = orgName,
                    title = title,
                    description = desc,
                    category = cat,
                    location = loc,
                    photoLabel = photo,
                    requirementsInput = req,
                    estimatedBeneficiaries = ben
                )
                Toast.makeText(
                    context,
                    "Masalah \"${created.title}\" berhasil disubmit ke Project Marketplace!",
                    Toast.LENGTH_LONG
                ).show()
                dialog.dismiss()
                onSubmitted()
            }
        )

        dialog.show()
    }

    // --- Modul B: Registrasi Organisasi Dialog ---
    fun showRegisterOrganizationDialog(
        context: Context,
        onRegistered: () -> Unit
    ) {
        val scroll = ScrollView(context)
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val pad = context.dp(20)
            setPadding(pad, pad, pad, pad)
            setBackgroundColor(Color.WHITE)
        }
        scroll.addView(root)

        root.addView(TextView(context).apply {
            text = "🏛️ Registrasi Organisasi / UKM Kampus"
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
        })
        root.addView(TextView(context).apply {
            text = "Daftarkan UKM, Himpunan, atau Komunitas untuk mendapatkan lencana ✅ VERIFIED ORGANIZATION dari Admin Kampus."
            setTextColor(Color.parseColor("#475569"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(4)
                bottomMargin = context.dp(12)
            }
        })

        val nameField = UiComponents.createInputField(
            context, "Nama Singkatan Organisasi", "Contoh: HIMATIF / UKM Robotika", "UKM AI & Robotika"
        )
        val fullNameField = UiComponents.createInputField(
            context, "Nama Lengkap Organisasi", "Contoh: Unit Kegiatan Mahasiswa Robotika", "Unit Kegiatan Mahasiswa AI & Robotika Kampus"
        )
        val categoryField = UiComponents.createInputField(
            context, "Kategori", "Technology / Social / Entrepreneurship / Arts", "Technology"
        )
        val skField = UiComponents.createInputField(
            context, "Nomor SK Organisasi", "Contoh: SK/REKTOR/052/2026", "SK/KEMAHASISWAAN/052/2026"
        )
        val advisorField = UiComponents.createInputField(
            context, "Nama Dosen Pembina", "Contoh: Dr. Ir. Budi Rahardjo, M.T.", "Dr. Eng. Fajar Pratama, S.T., M.T."
        )
        val contactField = UiComponents.createInputField(
            context, "Kontak Resmi (Email / Instagram)", "Contoh: robotika@kampus.ac.id", "ai.robotika@campusimpact.ac.id • @ukm.robotika"
        )
        val descField = UiComponents.createInputField(
            context,
            "Deskripsi Organisasi",
            "Jelaskan visi, misi, dan fokus kegiatan organisasi...",
            "Wadah inovasi mahasiswa dalam riset kecerdasan buatan, otomasi tepat guna, dan kompetisi robotika nasional.",
            multiline = true
        )

        root.addView(nameField.container)
        root.addView(fullNameField.container)
        root.addView(categoryField.container)
        root.addView(skField.container)
        root.addView(advisorField.container)
        root.addView(contactField.container)
        root.addView(descField.container)

        val dialog = AlertDialog.Builder(context)
            .setView(scroll)
            .setNegativeButton("Batal", null)
            .create()

        root.addView(
            UiComponents.createButton(
                context = context,
                label = "📋 Ajukan Registrasi Organisasi (Status: Pending)",
                bgColorHex = "#464FEB"
            ) {
                val created = CampusRepository.registerNewOrganization(
                    name = nameField.editText.text.toString().ifBlank { "UKM Baru" },
                    fullName = fullNameField.editText.text.toString(),
                    category = categoryField.editText.text.toString().ifBlank { "Technology" },
                    skNumber = skField.editText.text.toString().ifBlank { "SK/PENDING/2026" },
                    advisorName = advisorField.editText.text.toString().ifBlank { "Dosen Pembina" },
                    contact = contactField.editText.text.toString().ifBlank { "org@kampus.ac.id" },
                    description = descField.editText.text.toString().ifBlank { "Organisasi mahasiswa kampus." }
                )
                Toast.makeText(
                    context,
                    "Organisasi ${created.name} terdaftar dengan status PENDING. Admin Kampus dapat memverifikasi SK organisasi.",
                    Toast.LENGTH_LONG
                ).show()
                dialog.dismiss()
                onRegistered()
            }
        )

        dialog.show()
    }

    // --- Modul C: Buka Open Recruitment Dialog ---
    fun showCreateRecruitmentDialog(
        context: Context,
        onCreated: () -> Unit
    ) {
        val scroll = ScrollView(context)
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val pad = context.dp(20)
            setPadding(pad, pad, pad, pad)
            setBackgroundColor(Color.WHITE)
        }
        scroll.addView(root)

        root.addView(TextView(context).apply {
            text = "📢 Buka Open Recruitment Baru"
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
        })

        val orgField = UiComponents.createInputField(
            context, "Nama Organisasi", "Contoh: BEM Fakultas Teknik", "BEM Fakultas Teknik"
        )
        val titleField = UiComponents.createInputField(
            context, "Judul Rekrutmen", "Contoh: Open Recruitment 2027", "Open Recruitment Kabinet Kolaborasi 2027"
        )
        val posField = UiComponents.createInputField(
            context, "Posisi Tersedia (Pisahkan dengan koma)", "Contoh: PSDM, Advokesma, Medkom", "PSDM, Advokesma, Medkom, Sosmas"
        )
        val deadlineField = UiComponents.createInputField(
            context, "Deadline Pendaftaran", "Contoh: 20 Agustus 2027", "20 Agustus 2027"
        )

        root.addView(orgField.container)
        root.addView(titleField.container)
        root.addView(posField.container)
        root.addView(deadlineField.container)

        val dialog = AlertDialog.Builder(context)
            .setView(scroll)
            .setNegativeButton("Batal", null)
            .create()

        root.addView(
            UiComponents.createButton(
                context = context,
                label = "🚀 Publikasikan Open Recruitment",
                bgColorHex = "#464FEB"
            ) {
                val created = CampusRepository.createOpenRecruitment(
                    orgName = orgField.editText.text.toString().ifBlank { "HIMATIF" },
                    title = titleField.editText.text.toString().ifBlank { "Open Recruitment 2027" },
                    positionsCsv = posField.editText.text.toString(),
                    deadline = deadlineField.editText.text.toString().ifBlank { "20 Agustus 2027" }
                )
                Toast.makeText(
                    context,
                    "Open Recruitment ${created.organizationName} berhasil dibuka!",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
                onCreated()
            }
        )

        dialog.show()
    }

    // --- Modul D: Buat Event Kampus Dialog ---
    fun showCreateEventDialog(
        context: Context,
        onCreated: () -> Unit
    ) {
        val scroll = ScrollView(context)
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val pad = context.dp(20)
            setPadding(pad, pad, pad, pad)
            setBackgroundColor(Color.WHITE)
        }
        scroll.addView(root)

        root.addView(TextView(context).apply {
            text = "📅 Buat Event Kampus Baru"
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
        })

        val nameField = UiComponents.createInputField(
            context, "Nama Event", "Contoh: UI UX Workshop", "Social Tech Bootcamp & Demo Day"
        )
        val orgField = UiComponents.createInputField(
            context, "Organisasi Penyelenggara", "Contoh: HIMATIF / GDSC", "HIMATIF & Startup Club"
        )
        val typeField = UiComponents.createInputField(
            context,
            "Jenis Event (Seminar / Workshop / Webinar / Bootcamp / Volunteer / Kompetisi / Pelatihan)",
            "Workshop",
            "Workshop"
        )
        val descField = UiComponents.createInputField(
            context,
            "Deskripsi Event",
            "Jelaskan materi dan manfaat kegiatan...",
            "Pelatihan intensif merancang solusi digital untuk mitra UMKM dan desa binaan.",
            multiline = true
        )
        val dateField = UiComponents.createInputField(
            context, "Tanggal & Waktu", "Contoh: 24 November 2026 • 09:00 WIB", "30 November 2026 • 09:00 WIB"
        )
        val locField = UiComponents.createInputField(
            context, "Lokasi", "Contoh: Auditorium Kampus", "Aula Timur Kampus"
        )
        val quotaField = UiComponents.createInputField(
            context, "Kuota Peserta", "Contoh: 200", "200"
        )
        val posterField = UiComponents.createInputField(
            context, "Poster Event", "Deskripsi atau tautan poster", "poster_social_tech_bootcamp.png (Siap Publikasi)"
        )

        root.addView(nameField.container)
        root.addView(orgField.container)
        root.addView(typeField.container)
        root.addView(descField.container)
        root.addView(dateField.container)
        root.addView(locField.container)
        root.addView(quotaField.container)
        root.addView(posterField.container)

        val dialog = AlertDialog.Builder(context)
            .setView(scroll)
            .setNegativeButton("Batal", null)
            .create()

        root.addView(
            UiComponents.createButton(
                context = context,
                label = "✨ Publikasikan Event & Aktifkan QR Ticket",
                bgColorHex = "#464FEB"
            ) {
                val typeStr = typeField.editText.text.toString().trim()
                val matchedType = EventType.entries.firstOrNull {
                    it.label.equals(typeStr, ignoreCase = true)
                } ?: EventType.WORKSHOP

                val event = CampusRepository.createNewEvent(
                    title = nameField.editText.text.toString().ifBlank { "Campus Impact Event" },
                    organizer = orgField.editText.text.toString().ifBlank { "HIMATIF" },
                    type = matchedType,
                    description = descField.editText.text.toString().ifBlank { "Kegiatan pengembangan kapasitas mahasiswa." },
                    date = dateField.editText.text.toString().ifBlank { "Segera" },
                    location = locField.editText.text.toString().ifBlank { "Kampus Utama" },
                    quota = quotaField.editText.text.toString().toIntOrNull() ?: 200,
                    posterCaption = posterField.editText.text.toString()
                )
                Toast.makeText(
                    context,
                    "Event \"${event.title}\" berhasil dibuat! QR Ticket otomatis aktif.",
                    Toast.LENGTH_LONG
                ).show()
                dialog.dismiss()
                onCreated()
            }
        )

        dialog.show()
    }

    // --- Modul D: QR Event Ticket & Hari-H Attendance Scan Dialog ---
    fun showQrTicketDialog(
        context: Context,
        event: CampusEvent,
        onAttendanceUpdated: () -> Unit
    ) {
        val scroll = ScrollView(context)
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            val pad = context.dp(20)
            setPadding(pad, pad, pad, pad)
            setBackgroundColor(Color.WHITE)
        }
        scroll.addView(root)

        root.addView(
            UiComponents.createPillBadge(
                context = context,
                text = if (event.isAttended) "✅ ATTENDANCE VERIFIED (+10 POIN)" else "🎟️ E-TICKET RESMI CAMPUS IMPACT HUB",
                textColorHex = if (event.isAttended) "#047857" else "#1E3A8A",
                bgColorHex = if (event.isAttended) "#D1FAE5" else "#DBEAFE",
                endMarginDp = 0
            )
        )

        root.addView(TextView(context).apply {
            text = event.title
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(8)
            }
        })

        root.addView(TextView(context).apply {
            text = "${event.organizerName} • ${event.type.label}\n📅 ${event.date}\n📍 ${event.location}"
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#475569"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(4)
                bottomMargin = context.dp(12)
            }
        })

        // Dynamic QR Code Image
        val qrBitmap = QrCodeGenerator.generateTicketQrBitmap(
            payload = "${event.qrTicketCode}|${CampusRepository.currentUser.id}|${event.title}",
            sizePx = context.dp(210)
        )
        val qrImageView = ImageView(context).apply {
            setImageBitmap(qrBitmap)
            layoutParams = LinearLayout.LayoutParams(
                context.dp(210),
                context.dp(210)
            ).apply {
                bottomMargin = context.dp(10)
            }
        }
        root.addView(qrImageView)

        root.addView(TextView(context).apply {
            text = "KODE TIKET: ${event.qrTicketCode}"
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#1E3A8A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTypeface( Typeface.MONOSPACE, Typeface.BOLD)
            background = UiComponents.roundedBg("#EEF2FF", 8, "#C7D2FE", 2, context)
            setPadding(context.dp(14), context.dp(6), context.dp(14), context.dp(6))
        })

        root.addView(TextView(context).apply {
            text = "Pemegang Tiket: ${CampusRepository.currentUser.name} (${CampusRepository.currentUser.identityNumber})"
            gravity = Gravity.CENTER
            setTextColor(Color.parseColor("#64748B"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(8)
            }
        })

        // Flow explanation: Hari H: QR Scan -> Attendance -> Poin Bertambah
        val flowCard = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            background = UiComponents.roundedBg("#ECFDF5", 12, "#6EE7B7", 2, context)
            val p = context.dp(12)
            setPadding(p, p, p, p)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(12)
            }
        }
        flowCard.addView(TextView(context).apply {
            text = "Alur Kehadiran Hari H:"
            setTextColor(Color.parseColor("#065F46"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.BOLD)
        })
        flowCard.addView(TextView(context).apply {
            text = if (event.isAttended) {
                "✅ QR Scan Selesai  →  ✅ Attendance Tercatat  →  🎉 +10 Poin Impact Ditambahkan!"
            } else {
                "📲 QR Scan  →  📋 Attendance  →  🌱 Poin Bertambah (+10 Impact Score)"
            }
            setTextColor(Color.parseColor("#047857"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            setTypeface(typeface, Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(4)
            }
        })
        root.addView(flowCard)

        val dialog = AlertDialog.Builder(context)
            .setView(scroll)
            .setNegativeButton("Tutup", null)
            .create()

        if (!event.isAttended) {
            root.addView(
                UiComponents.createButton(
                    context = context,
                    label = "📷 Simulasi Scan QR Kehadiran (Hari H) • +10 Poin",
                    bgColorHex = "#10B981"
                ) {
                    val pts = CampusRepository.confirmEventAttendanceByQr(event, asCommittee = false)
                    Toast.makeText(
                        context,
                        "QR Scan Berhasil! Kehadiran tercatat & Impact Score bertambah +$pts Poin!",
                        Toast.LENGTH_LONG
                    ).show()
                    dialog.dismiss()
                    onAttendanceUpdated()
                }.apply {
                    (layoutParams as LinearLayout.LayoutParams).topMargin = context.dp(14)
                }
            )

            root.addView(
                UiComponents.createButton(
                    context = context,
                    label = "⭐ Scan QR sebagai Panitia Event • +20 Impact & +20 Leadership",
                    bgColorHex = "#464FEB"
                ) {
                    val pts = CampusRepository.confirmEventAttendanceByQr(event, asCommittee = true)
                    Toast.makeText(
                        context,
                        "QR Scan Panitia Berhasil! +$pts Impact Score & +20 Leadership Score ditambahkan!",
                        Toast.LENGTH_LONG
                    ).show()
                    dialog.dismiss()
                    onAttendanceUpdated()
                }.apply {
                    (layoutParams as LinearLayout.LayoutParams).topMargin = context.dp(8)
                }
            )
        }

        dialog.show()
    }

    // --- Role Switcher Dialog (4 Roles in Campus Impact Hub) ---
    fun showRoleSwitcherDialog(
        context: Context,
        onRoleChanged: () -> Unit,
        onOpenAuthScreen: () -> Unit
    ) {
        val scroll = ScrollView(context)
        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val pad = context.dp(20)
            setPadding(pad, pad, pad, pad)
            setBackgroundColor(Color.WHITE)
        }
        scroll.addView(root)

        root.addView(TextView(context).apply {
            text = "🔄 Pilih Role Pengguna (4 Role Ekosistem)"
            setTextColor(Color.parseColor("#0F172A"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
            setTypeface(typeface, Typeface.BOLD)
        })
        root.addView(TextView(context).apply {
            text = "Uji seluruh fitur Campus Impact Hub dari sudut pandang Mahasiswa, Mitra Masyarakat, Organisasi/UKM, atau Admin Kampus."
            setTextColor(Color.parseColor("#475569"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = context.dp(4)
                bottomMargin = context.dp(12)
            }
        })

        val dialog = AlertDialog.Builder(context)
            .setView(scroll)
            .setNegativeButton("Tutup", null)
            .create()

        UserRole.entries.forEach { role ->
            val isSelected = CampusRepository.currentUser.role == role
            val cardHolder = UiComponents.createCard(
                context = context,
                bgColorHex = if (isSelected) "#EEF2FF" else "#F8FAFC",
                strokeColorHex = if (isSelected) "#464FEB" else "#CBD5E1",
                cornerRadiusDp = 12,
                elevationDp = 0,
                paddingDp = 12,
                bottomMarginDp = 10
            )
            cardHolder.body.addView(TextView(context).apply {
                text = "${if (isSelected) "✅ " else "👤 "}${role.label} (${role.badgeTitle})"
                setTextColor(Color.parseColor(if (isSelected) "#1E3A8A" else "#0F172A"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTypeface(typeface, Typeface.BOLD)
            })
            cardHolder.body.addView(TextView(context).apply {
                text = role.description
                setTextColor(Color.parseColor("#475569"))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = context.dp(4)
                }
            })
            cardHolder.card.setOnClickListener {
                CampusRepository.switchRole(role)
                Toast.makeText(
                    context,
                    "Mode aktif: ${role.label} (${CampusRepository.currentUser.name})",
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
                onRoleChanged()
            }
            root.addView(cardHolder.card)
        }

        root.addView(
            UiComponents.createButton(
                context = context,
                label = "🔐 Buka Screen 1: Login & Register Lengkap",
                bgColorHex = "#1E3A8A"
            ) {
                dialog.dismiss()
                onOpenAuthScreen()
            }
        )

        dialog.show()
    }
}
