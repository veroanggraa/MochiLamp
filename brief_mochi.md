# 📋 Implementation Brief: Kawaii Pull-Cord Lamp di Jetpack Compose 1.13

Dokumen ini berisi pemetaan teknis untuk membangun ulang prototipe interaktif **Lampu Tarik — Kawaii & Clean** secara murni menggunakan **Jetpack Compose 1.13.0** (BOM `2026.11.01`) dan **Material 3 1.5.0** tanpa *library* grafis pihak ketiga.

---

## 🛠️ Setup & Prerequisites (`libs.versions.toml`)
- **Compose BOM:** `2026.11.01` (Compose Core `1.13.0`, Material 3 `1.5.0`)
- **Compile SDK:** `compileSdk = 37` (`minorApiLevel = 1`)
- **Kotlin:** `2.2+`

---

## 🧩 Pemetaan Komponen & API Terbaru Compose 1.13

### 1. Gantungan Tali Bintang & Efek Glow (`#cord-handle`)
- **Komponen / API:** `PolygonShape` + `MorphPolygonShape` *(Baru & Stabil di `androidx.compose.foundation.shape` pada Compose 1.13)*
- **Implementasi:**
  - Menggantikan path SVG bintang manual dengan `PolygonShape` bawaan (5 titik dengan sudut membulat lembut / *rounded vertices*) tanpa perlu menambahkan dependensi eksternal `graphics-shapes`.
  - Menggunakan `MorphPolygonShape` agar bentuk bintang sedikit mengembang/membulat saat jari pengguna menariknya melewati *threshold*.
  - Efek *night glow halo* di sekitar bintang menggunakan `Modifier.blur(BlurRadiusSpec.radial(...))` saat mode malam aktif.

### 2. Efek Pancaran Cahaya & Aura Ruangan (`#light-burst` & `#light-aura`)
- **Komponen / API:** `Modifier.blur` dengan `BlurRadiusSpec` *(Progressive / Radial Blur baru di Compose 1.13)*
- **Implementasi:**
  - **Radial Light Burst:** Menggunakan `Canvas` atau `Box` dengan `graphicsLayer { scaleX; scaleY }` yang berpusat tepat pada titik koordinat bohlam (`pivot = Offset(centerX, bulbY)`), beranimasi dari `0f` ke `2.5f` menggunakan `Spring.DampingRatioLowBouncy`.
  - **Warm Aura Glow:** Memanfaatkan `BlurRadiusSpec` (gradien radial blur bertahap) untuk menciptakan pendaran cahaya hangat/sakura/langit yang lembut di belakang kap lampu tanpa garis tepi yang kaku.

### 3. Karakter Mochi Lamp & 5 Ekspresi Wajah
- **Komponen / API:** `Canvas` (`DrawScope`) + `CapturedAnimatedVisibility` / `Crossfade` + `computedStateOf`
- **Implementasi:**
  - **Bentuk Kap & Telinga Mochi:** Digambar dengan `DrawScope` (`drawRoundRect`, `drawPath`, dan `Brush.verticalGradient`) untuk menghasilkan efek gradasi pastel dan bayangan lembut (*rim shadow*).
  - **Transisi 5 Ekspresi (`Sleepy`, `Dragging`, `Threshold`, `Happy`, `Blush`):**
    - Menggunakan **`CapturedAnimatedVisibility`** (fitur baru Compose 1.13) saat memunculkan/menyembunyikan elemen ekspresi seperti gelembung `zZz` atau kilau bintang, sehingga animasi keluar tetap mulus tanpa perlu menahan *state* lama.
    - Menggunakan **`computedStateOf`** (pengganti `derivedStateOf` yang lebih ringan untuk evaluasi per-frame di Compose 1.13) untuk menentukan pergantian ekspresi wajah secara *real-time* berdasarkan jarak tarikan tali (`pullY >= PULL_THRESHOLD`).
  - **Eye Tracking & Auto Blink:**
    - Menggunakan `Modifier.pointerInput` (`awaitPointerEventScope`) untuk melacak posisi sentuhan layar dan menggerakkan pupil mata secara halus.
    - `rememberInfiniteTransition` untuk kedipan mata otomatis setiap 3–5 detik serta animasi napas (*sleep breathe*) saat lampu mati.

### 4. Tali Elastis, Bandul & Squash-and-Stretch Physics
- **Komponen / API:** `Animatable` + `SpringSpec` + `Path.quadraticTo`
- **Implementasi:**
  - **Tali Bezier Dinamis:** `Canvas` menggambar kurva kuadratik (`Path.quadraticTo`) dari dasar kap lampu ke titik tarikan bintang, lengkap dengan 2 manik-manik yang posisinya dihitung menggunakan interpolasi Bezier ($t = 0.32$ dan $t = 0.66$).
  - **Gestur Tarik & Resistansi Karet:** `Modifier.pointerInput` dengan `detectDragGestures` menerapkan redaman karet (*rubber-band damping*) saat tali ditarik melewati batas maksimal, serta memicu `LocalHapticFeedback.current` (`HapticFeedbackType.TextHandleMove`) tepat saat melewati *threshold*.
  - **Ayunan Bandul & Squish Bounce:**
    - `Animatable(0f)` untuk sudut ayunan seluruh grup lampu (`graphicsLayer { rotationZ = swayAngle }`).
    - `Animatable(0f)` untuk deformasi elastis (*squash-and-stretch*) pada badan lampu saat tali dilepas atau saat badan lampu diketuk (*Easter Egg*).

### 5. Easter Egg: Ketuk Badan Lampu (Squish, Blush & Floating Hearts)
- **Komponen / API:** `Modifier.clickable` (tanpa ripple) + `Animatable` Partikel
- **Implementasi:**
  - Mengetuk badan lampu memicu impuls pegas `squishVel` dan mengubah ekspresi menjadi `FaceState.BLUSH` selama 1,2 detik.
  - Memunculkan 3 partikel hati (`Path` / `PolygonShape`) yang melayang ke atas dengan lintasan melengkung acak dan memudar (*fade out*) menggunakan coroutine `launch` paralel.

### 6. Header Status Pill & Pemilih 3 Nuansa Cahaya (`Warm`, `Sakura`, `Sky`)
- **Komponen / API:** **Material 3 Expressive (`1.5.0`)** + `AnimatedVisibility` + `animateColorAsState`
- **Implementasi:**
  - **Status Pill (`🌙 Malam` / `☀️ Siang`) & Tombol Audio:** Menggunakan komponen kapsul Material 3 Expressive dengan `animateColorAsState` untuk transisi warna latar belakang dan ikon yang mulus.
  - **3-Dot Light Tint Picker:** Muncul dengan animasi `slideInVertically` + `fadeIn` hanya saat mode terang aktif, menggunakan `SingleChoiceSegmentedButtonRow` atau tombol lingkaran kustom dengan indikator cincin aktif.
  - **Tipografi Dinamis:** Menggunakan `BasicText` / `Text` dengan `styleProvider` (Compose 1.13) agar transisi warna teks judul dan subjudul berjalan mulus tanpa *overhead* rekomposisi berlebih.

---

## 🎯 Highlight Utama untuk Bahan Artikel Medium
Jika dijadikan artikel tutorial, ada **4 API unggulan Compose 1.13** yang bisa ditonjolkan sebagai nilai jual utama:
1. **`PolygonShape` & `MorphPolygonShape`** — Membuat liontin bintang kawaii secara *native* tanpa library tambahan.
2. **`BlurRadiusSpec` (Progressive Blur)** — Menciptakan pendaran cahaya lampu dan efek kaca modern.
3. **`CapturedAnimatedVisibility`** — Transisi keluar elemen visual (seperti gelembung `zZz`) yang bebas *flicker*.
4. **`computedStateOf`** — Evaluasi *state* gestur tarik tali 60fps yang super ringan tanpa *overhead* caching.