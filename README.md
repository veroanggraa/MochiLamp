# 🪔 MochiLamp

**[English](#english) | [Bahasa Indonesia](#bahasa-indonesia)**

![MochiLamp demo](media/mochilamp_demo.gif)

---

## English

An interactive pull-string lamp, built purely with **Jetpack Compose 1.13** and **Material 3 Expressive** - no third-party graphics libraries. Pull the string, turn on the light, and watch Mochi Lamp react with its own facial expressions.

### ✨ Features

- **5 dynamic facial expressions** - `Sleepy`, `Dragging`, `Threshold`, `Happy`, `Blush` that change in real time based on interaction
- **Elastic string & spring physics** - the string pull uses a quadratic Bezier curve with rubber-band damping and haptic feedback when crossing the threshold
- **Pendulum swing & squash-and-stretch** - the lamp body swings and deforms elastically like a physical object
- **Easter egg** - tap the lamp body to trigger a blush expression and floating heart particles
- **3 light tones**: `Warm`, `Sakura`, `Sky` - selected via a Material 3 capsule picker
- **Eye tracking & auto blink** - eyes follow the touch position and blink automatically
- **Light & glow effects** - radial light burst and warm aura glow around the lampshade when lit

### 🛠️ Tech Stack

- Kotlin 2.2+
- Jetpack Compose 1.13.0 (BOM `2026.11.01`)
- Material 3 1.5.0
- `compileSdk 37`, `minSdk 26`

### 🚀 Running the Project

```bash
git clone <repo-url>
cd MochiLamp
./gradlew installDebug
```

Or open this folder in **Android Studio** and run directly from the `app` module.

### 📁 Project Structure

```
app/src/main/java/com/veroanggra/mochilamp/
├── audio/            # Procedural sound synthesis (click, switch, etc.)
├── model/            # Lamp state, facial expressions, color tones
├── ui/lamp/          # Main screen & lamp canvas components
└── ui/theme/         # Material 3 theme
```

---

## Bahasa Indonesia

Lampu tarik interaktif, dibangun murni dengan **Jetpack Compose 1.13** dan **Material 3 Expressive** - tanpa library grafis pihak ketiga. Tarik talinya, nyalakan lampu, dan lihat si Mochi Lamp bereaksi dengan ekspresi wajahnya sendiri.

### ✨ Fitur

- **5 ekspresi wajah dinamis** - `Sleepy`, `Dragging`, `Threshold`, `Happy`, `Blush` yang berubah real-time sesuai interaksi
- **Tali elastis & fisika pegas** - tarikan tali memakai kurva Bezier kuadratik dengan redaman karet (*rubber-band damping*) dan haptic feedback saat melewati threshold
- **Ayunan bandul & squash-and-stretch** - badan lampu berayun dan berdeformasi secara elastis layaknya objek fisik
- **Easter egg** - ketuk badan lampu untuk memicu ekspresi blush dan partikel hati yang melayang
- **3 nuansa cahaya**: `Warm`, `Sakura`, `Sky` -  dipilih lewat picker kapsul Material 3
- **Eye tracking & auto blink** - mata mengikuti posisi sentuhan dan berkedip otomatis
- **Efek cahaya & glow** - radial light burst dan warm aura glow di sekitar kap lampu saat menyala

### 🛠️ Tech Stack

- Kotlin 2.2+
- Jetpack Compose 1.13.0 (BOM `2026.11.01`)
- Material 3 1.5.0
- `compileSdk 37`, `minSdk 26`

### 🚀 Menjalankan Proyek

```bash
git clone <repo-url>
cd MochiLamp
./gradlew installDebug
```

Atau buka folder ini di **Android Studio** dan jalankan langsung dari modul `app`.

### 📁 Struktur Proyek

```
app/src/main/java/com/veroanggra/mochilamp/
├── audio/            # Sintesis suara prosedural (klik, switch, dsb.)
├── model/            # State lampu, ekspresi wajah, nuansa warna
├── ui/lamp/          # Layar utama & komponen kanvas lampu
└── ui/theme/         # Tema Material 3
```
