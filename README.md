# 🌿 Vaylora — On-Device AI Waste Classification & Reasoning Engine

<p align="center">
  <img src="app/src/main/res/drawable/vaylora_logo.png" alt="Vaylora Logo" width="180" />
</p>

<p align="center">
  <b>A real-time, button-free Android application that transforms waste management using on-device vision, local RAG retrieval, and AI reasoning.</b>
</p>

---

## 🚀 Key Features

* **📷 Vaylora Vision (Hands-Free AI Scanner):**
  Continuous CameraX frame analysis using **Google AI Edge MediaPipe Tasks Vision** with an INT8 quantized EfficientDet-Lite0 model. No tap-to-scan buttons required.
* **🧠 On-Device RAG Reasoning Engine:**
  Combines local **Room Database** rules with **MediaPipe GenAI (LLM Inference)** to generate dynamic, location-grounded disposal explanations rather than static hardcoded responses.
* **🗑️ Where To Throw? (Municipal Bin Directory):**
  Searchable directory of household waste materials with color-coded municipal bin assignments (Recycling, Compost, Landfill, E-Waste).
* **🌱 Vaylora Reborn (Creative Upcycling):**
  Curated DIY upcycling blueprints for common items (plastic bottles into self-watering planters, glass jars into rustic fairy lanterns, cardboard dividers, organic plant teas).
* **⚠️ Contamination Coach:**
  Detects contamination cues (e.g. food residue on pizza boxes, liquids in bottles) and provides preparation instructions to prevent recycling stream contamination.
* **📊 Eco-Pulse Footprint Tracker:**
  Weekly activity tracking, items diverted metrics, and estimated carbon savings (kg CO₂e).

---

## 🏗️ Architecture & Tech Stack

* **UI Framework:** Jetpack Compose (Material 3)
* **Camera Pipeline:** AndroidX CameraX (Preview + ImageAnalysis)
* **Computer Vision:** Google AI Edge MediaPipe Tasks Vision (`0.10.28`) (16 KB page-size aligned)
* **Vector Store & Cache:** AndroidX Room Database 2.7.2 with KSP
* **On-Device LLM:** MediaPipe Tasks GenAI (`tasks-genai`)
* **Language:** Kotlin 2.2 / JDK 24+

---

## 🛠️ Getting Started

### Prerequisites
* Android Studio Ladybug / Meerkat (or newer)
* Android SDK 35+ (Min SDK 25)
* Java 17+ or 21+

### Build and Run
1. Clone this repository:
   ```bash
   git clone https://github.com/Vedantkhare31/vaylora.git
   ```
2. Open the project in Android Studio.
3. Allow Gradle to sync.
4. Connect an Android phone with USB Debugging enabled (or start an AVD emulator).
5. Click **Run (▶)** to install and launch.

---

## 📄 License
This project is licensed under the Apache 2.0 License.
