# 🎓🌱 CAMPUS IMPACT HUB
### *One Platform for Campus Life & Social Impact*

> **"Menghubungkan Masalah, Talenta, Organisasi, dan Dampak dalam Satu Ekosistem Kampus."**

![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Kotlin-100%25-7F52FF?logo=kotlin&logoColor=white)
![UI](https://img.shields.io/badge/Design-Material%20Design%20%26%20LinkedIn%20Style-464FEB)
![Architecture](https://img.shields.io/badge/Backend-Supabase%20%7C%20PostgreSQL-10B981?logo=supabase&logoColor=white)
![MinSDK](https://img.shields.io/badge/Min%20SDK-27%20(Android%208.1)-1E3A8A)

---

## 💡 Elevator Pitch & Value Proposition

**Campus Impact Hub** adalah *Super App Ekosistem Kampus* yang mengintegrasikan pengabdian masyarakat, *marketplace* proyek sosial, rekrutmen organisasi, manajemen event, serta portofolio dampak mahasiswa dalam satu platform terpadu. Mahasiswa tidak hanya lulus dengan IPK, tetapi juga dengan **rekam jejak kontribusi sosial yang terukur dan dapat dibuktikan**.

* ❌ Bukan sekadar aplikasi akademik kampus biasa.
* ❌ Bukan sekadar aplikasi pengaduan masyarakat.
* ✅ **"Platform yang menghubungkan masyarakat, mahasiswa, dosen, organisasi kampus, dan aktivitas kampus menjadi satu ekosistem dampak sosial yang terukur."**

---

## 🔍 1. Latar Belakang Masalah

Saat ini berbagai elemen di dalam dan sekitar kampus masih berjalan sendiri-sendiri (*siloed*):

| Pemangku Kepentingan | Permasalahan Utama Saat Ini |
| :--- | :--- |
| **🤝 Masyarakat (UMKM, Desa, Sekolah, Komunitas)** | • UMKM membutuhkan digitalisasi pembukuan & pemasaran.<br>• Desa memiliki masalah lingkungan (bank sampah) dan administrasi.<br>• Sekolah membutuhkan inovasi media pendidikan.<br>• Komunitas membutuhkan relawan dan solusi teknologi tepat guna. |
| **🎓 Mahasiswa** | • Sulit mencari portofolio nyata (*real-world project*).<br>• Sulit menemukan proyek sosial lintas disiplin.<br>• Informasi event dan peluang kampus terpencar.<br>• Kesulitan menemukan UKM/organisasi yang sesuai minat & keahlian. |
| **🏛️ Organisasi Kampus (BEM / Himpunan / UKM)** | • Rekrutmen anggota (*Open Recruitment*) masih tersebar di media sosial.<br>• Informasi event tidak terpusat.<br>• Sulit mengelola absensi peserta dan distribusi sertifikat. |
| **🏫 Pihak Kampus** | • Dampak pengabdian masyarakat sulit diukur secara kuantitatif.<br>• Aktivitas MBKM, organisasi, dan kegiatan mahasiswa belum terintegrasi.<br>• Belum ada sistem yang mencatat kontribusi & kepemimpinan mahasiswa secara menyeluruh. |

---

## 🚀 2. Solusi Terintegrasi (5 Hub Utama)

1. **🛠️ Social Impact Hub** — Menghubungkan masalah nyata masyarakat (UMKM, Desa, Sekolah, Komunitas) dengan solusi kolaboratif mahasiswa.
2. **🏛️ Organization Hub** — Pusat informasi, verifikasi SK (`✅ VERIFIED ORGANIZATION`), dan *Open Recruitment* seluruh organisasi kampus.
3. **📅 Event Hub** — Satu tempat untuk seluruh kegiatan kampus (*Seminar, Workshop, Webinar, Bootcamp, Volunteer, Kompetisi, Pelatihan*) lengkap dengan **QR Event Ticket** & absensi QR.
4. **🌟 Impact & Portfolio Hub** — Mengukur **Impact Score**, **Leadership Score**, jam kontribusi, jumlah penerima manfaat, dan portofolio mahasiswa secara nyata.
5. **🤖 AI Recommendation Hub** — Memberikan rekomendasi otomatis pencocokan masalah masyarakat (*AI Problem Matching*), rekomendasi organisasi, dan rekomendasi event sesuai minat mahasiswa.

---

## 👥 3. Role Pengguna (Multi-Role Ecosystem)

Aplikasi mendukung **4 Role Pengguna** yang dapat diuji secara langsung melalui fitur *Role Switcher* di dalam aplikasi:

1. **🎓 Mahasiswa (`student`)**
   * Melihat dan mendaftar proyek sosial (*Anggota Tim* maupun *Ketua Tim Proyek*).
   * Bergabung ke organisasi kampus & mendaftar *Open Recruitment*.
   * Mendaftar event kampus & mendapatkan **QR Event Ticket**.
   * Mengelola **Student Portfolio**, memantau **Impact Score** & **Leadership Score**.
2. **🤝 Mitra Masyarakat (`partner`)**
   * Mengajukan masalah nyata masyarakat (*Submit Problem* lengkap dengan lokasi, kategori, foto, dan estimasi penerima manfaat).
   * Memantau progres pengerjaan proyek (`Open` → `On Going` → `Completed`).
   * Memberikan *feedback* dan melihat hasil implementasi pada **Impact Report** (*Sebelum vs Sesudah*).
3. **🏛️ Organisasi / UKM (`organization`)**
   * Mendaftarkan organisasi baru beserta dokumen SK & dosen pembina.
   * Membuka **Open Recruitment** dan mengelola tahapan seleksi pelamar.
   * Membuat event kampus, memantau kuota peserta, dan melakukan **Scan Absensi QR**.
4. **🛡️ Admin Kampus (`admin`)**
   * Memverifikasi legalitas & SK organisasi (`Pending` → `Verified` ✅ atau `Rejected`).
   * Memverifikasi proyek sosial dan event kampus.
   * Memantau analitik aktivitas serta total dampak sosial kampus secara menyeluruh.

---

## 🧩 4. Modul Utama Sistem

### Modul A — Social Impact Hub
* **Submit Problem**: Mitra masyarakat mengisi *Nama Organisasi, Judul Masalah, Deskripsi, Kategori, Lokasi,* dan *Foto Dokumentasi*.
* **Project Marketplace**: Menampilkan kartu proyek sosial dengan *Impact Score* (misal `85`), kebutuhan multidisiplin (contoh: `2 Informatika`, `1 Desain`), dan status progres (`Open`, `On Going`, `Completed`).
* **Project Application Flow**: `Lihat Detail → Daftar Proyek → Menunggu Persetujuan`.
* **Impact Report Terukur**: Membandingkan kondisi **Sebelum** (*Manual*) dan **Sesudah** (*Digital*) beserta total **Penerima Manfaat** (*Beneficiaries*).

### Modul B — Organization Hub
* **Registrasi & Direktori Organisasi**: Menampilkan profil UKM/Himpunan (*HIMATIF, BEM Fakultas Teknik, GDSC, Startup Club, Relawan Desa Mengabdi*) lengkap dengan SK Organisasi, Dosen Pembina, Kontak, dan lencana **✅ VERIFIED ORGANIZATION**.

### Modul C — Open Recruitment
* Organisasi dapat membuka pendaftaran pengurus/anggota baru (contoh: *BEM Fakultas Teknik — Open Recruitment 2027* untuk posisi *PSDM, Advokesma, Medkom*).
* **Alur Seleksi 4 Tahap**: `Pendaftaran → Administrasi → Interview → Pengumuman` dengan status pelamar (`Pending`, `Accepted`, `Rejected`).

### Modul D — Event Hub & QR Ticket
* Mendukung 7 kategori kegiatan: **Seminar, Workshop, Webinar, Bootcamp, Volunteer, Kompetisi, Pelatihan**.
* **Alur Registrasi & Kehadiran Hari H**:
  * Mahasiswa mendaftar → mendapatkan **QR Event Ticket** unik.
  * Pada Hari-H: `QR Scan → Attendance Tercatat → Poin Otomatis Bertambah (+10 Impact Score)`.

### Modul E — Impact & Portfolio Hub
* **My Impact Dashboard**: Menampilkan *Impact Score*, *Leadership Score*, jumlah proyek selesai, jumlah UMKM dibantu, total penerima manfaat (`1000+`), dan jam kontribusi.
* **Student Portfolio**: Mencatat riwayat *Project Sosial, Organisasi, Event, Sertifikat,* dan *Prestasi* yang siap diekspor sebagai bukti pendamping ijazah/CV.

### Modul F — Activity Point System
Setiap kontribusi mahasiswa terkonversi otomatis menjadi poin terukur:

| Aktivitas Kampus & Sosial | Poin | Kontribusi Skor |
| :--- | :---: | :--- |
| **Mengikuti Event** (via *QR Attendance Scan*) | **+10** | *Impact Score* |
| **Panitia Event** | **+20** | *Impact Score* & *Leadership Score* |
| **Pengurus Organisasi** (*Verified Organization*) | **+30** | *Impact Score* & *Leadership Score* |
| **Proyek Sosial** (Anggota Tim Implementasi) | **+50** | *Impact Score* |
| **Ketua Tim Proyek** (*Project Leader / Mentor*) | **+75** | *Impact Score* & *Leadership Score* |

### Modul G — AI Recommendation Hub
* **AI Problem Matching**: Menganalisis deskripsi masalah masyarakat (misal *"UMKM kesulitan pemasaran online"*) dan merekomendasikan kategori (*Digital Marketing*), jurusan mahasiswa yang dibutuhkan (*Bisnis Digital, Informatika, DKV*), serta estimasi dampak (*Tinggi*).
* **AI Organization & Event Recommendation**: Mencocokkan minat mahasiswa (*Programming, UI/UX, Startup*) dengan organisasi (*GDSC, HIMATIF, Startup Club*) dan event relevan (*Hackathon, UI/UX Workshop, Google Developer Event*).

---

## 📱 5. Daftar Screen MVP & Navigasi Aplikasi

Aplikasi menggunakan **6 Tab Bottom Navigation** (`Home`, `Projects`, `Organizations`, `Events`, `Impact`, `Profile`) yang mencakup seluruh **10 Screen MVP**:

1. **Screen 1 — Login / Register**: Otentikasi pengguna & pemilihan cepat 4 role ekosistem.
2. **Screen 2 — Dashboard (`Home`)**: Statistik *Total Mahasiswa Aktif, Total Organisasi, Total Event, Total Proyek, Total Dampak* + *AI Recommendation Hub*.
3. **Screen 3 — Project Marketplace (`Projects`)**: Katalog proyek sosial dengan filter kategori & status.
4. **Screen 4 — Project Detail**: Rincian masalah mitra, kebutuhan tim, *Impact Report*, dan tombol daftar proyek.
5. **Screen 5 — Organization Directory (`Organizations`)**: Direktori seluruh UKM/Himpunan & fitur verifikasi Admin Kampus.
6. **Screen 6 — Open Recruitment (`Organizations`)**: Daftar rekrutmen terbuka beserta pelacakan 4 tahap seleksi.
7. **Screen 7 — Event Hub (`Events`)**: Daftar kegiatan kampus, pendaftaran peserta, dan modal **QR Event Ticket** interaktif.
8. **Screen 8 — Submit Problem**: Formulir pengajuan masalah bagi Mitra Masyarakat (dilengkapi *AI Problem Matching Auto-Fill*).
9. **Screen 9 — My Impact (`Impact`)**: Analitik *Impact Score*, *Leadership Score*, *Activity Point System*, dan *Student Portfolio*.
10. **Screen 10 — Profile (`Profile`)**: Profil pengguna bergaya LinkedIn, pengubah role instan, inspeksi JSON database Supabase, dan peta jalan (*roadmap*).

---

## 🏗️ 6. Arsitektur Teknologi & Struktur Database (MVP)

```text
Android Studio (Kotlin - Material Design UI)
        ↓
   Supabase API (REST & Realtime)
        ↓
  Supabase Auth (Multi-Role Access)
        ↓
   PostgreSQL (Relational Database)
        ↓
Supabase Storage (Foto Masalah, SK Organisasi, Poster Event)
