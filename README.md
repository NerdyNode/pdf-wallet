<div align="center">

# 📄 PDF Wallet

**Your travel and ID documents, captured automatically and organized in one place.**

Share a boarding pass, ticket, or government ID once — PDF Wallet extracts, classifies, and files it away, ready to find later, even offline.

[![Status](https://img.shields.io/badge/status-Phase%201%20in%20progress-orange)](./implementation_plan_final.md)
[![Platform](https://img.shields.io/badge/platform-Android-3DDC84?logo=android&logoColor=white)](#)
[![Min SDK](https://img.shields.io/badge/minSdk-26%20(Android%208.0)-blue)](#)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.x-7F52FF?logo=kotlin&logoColor=white)](#)
[![License](https://img.shields.io/badge/license-TBD-lightgrey)](#license)

</div>

---

## 📚 Table of Contents

- [Why](#-why)
- [Status](#-status)
- [Features](#-features)
- [How It Works](#-how-it-works)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Setup](#-setup)
- [Privacy](#-privacy)
- [Contributing](#-contributing)
- [License](#-license)

---

## 💡 Why

Tickets and IDs pile up across email, chat apps, and screenshots — scattered and hard to find when you actually need them at a gate or a checkpoint. **PDF Wallet** removes the manual filing step: share a PDF once, and it's automatically read, categorized, and stored for offline access.

## 🚧 Status

**Phase 1 — in progress.** Core infrastructure, automatic capture, and on-device processing.
See [`implementation_plan_final.md`](./implementation_plan_final.md) for the full build plan and current scope.

| Phase | Focus | Status |
|---|---|---|
| **Phase 1** | Capture, extraction, classification, wallet UI, security | 🔨 In progress |
| **Phase 2** | Background folder scanning, deeper field extraction, smarter classification | 📋 Planned |
| **Phase 3** | Backup / multi-device sync | 💭 Future |

## ✨ Features

### Available in Phase 1

| | Feature | Details |
|---|---|---|
| 📤 | **Automatic capture** | Share any PDF from Gmail, WhatsApp, Chrome, or Files straight into PDF Wallet — no re-upload needed |
| 📁 | **Manual import** | Pick a PDF directly from the app as a fallback |
| 🔍 | **Text & barcode extraction** | Embedded text via PdfBox; QR/PDF417 barcode scanning via ML Kit, including IATA boarding-pass parsing |
| 🖼️ | **OCR fallback** | ML Kit Text Recognition kicks in for scanned documents with no embedded text layer |
| 🏷️ | **Automatic classification** | Rule-based tagging as Airline / Train / Bus / Government ID |
| 🗂️ | **Wallet view** | Searchable, filterable list of every captured document with thumbnails |
| 🔒 | **Encrypted local storage** | SQLCipher-encrypted database; documents never leave the device |
| 🔐 | **App lock** | Biometric/PIN gate before any document content is shown |

### Planned for later phases

- 🗄️ Background scanning of Downloads/other folders — no share action required
- 🪪 Deeper field extraction per document type (full boarding-pass fields, ID MRZ parsing)
- 🧠 Trained classification model in place of rule-based matching
- ☁️ Backup / sync across devices

## ⚙️ How It Works

```
   📥 CAPTURE                💾 STORE                 ⚙️ PROCESS                  📋 SHOW
Share sheet or FAB   →   Encrypted, app-private   →   Extract → OCR fallback  →   Wallet list,
                          storage + dedup hash          → Barcode scan →            searchable &
                                                          Classify → Thumbnail       filterable
```

1. **Capture** — a PDF arrives via the Android share sheet ("Share → PDF Wallet") or manual import via the in-app FAB.
2. **Store** — the file is copied into encrypted, app-private storage; a content hash prevents duplicate entries.
3. **Process** *(background, via WorkManager)*:
   - Extract embedded text (PdfBox), falling back to OCR (ML Kit) for scanned PDFs
   - Scan for barcodes/QR codes, parsing structured data where possible (e.g. IATA BCBP for boarding passes)
   - Classify the document type and extract key fields (name, ID number, dates, route)
   - Generate a thumbnail from the first page
4. **Show** — the document lands in the Wallet list, searchable and filterable by type, with a live status indicator while processing runs.

> All processing happens **on-device** — no document content is ever uploaded.

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Repository pattern |
| Dependency Injection | Hilt |
| Local Database | Room + SQLCipher *(encrypted)* |
| Background Work | WorkManager |
| PDF Rendering / Text | Android `PdfRenderer`, PdfBox Android |
| OCR & Barcode | ML Kit (Text Recognition, Barcode Scanning — bundled models) |
| Image Loading | Coil |

`minSdk 26` (Android 8.0) · `targetSdk 34` (Android 14)

## 📂 Project Structure

```
com.pdfwallet/
├── data/
│   ├── db/              # Room entities, DAO, database
│   ├── local/            # File storage management
│   └── repository/       # Repository layer
├── service/
│   ├── pdf/               # Text extraction, OCR, barcode parsing, thumbnails
│   ├── classification/    # Document type classification
│   └── worker/            # WorkManager background processing
├── ui/
│   ├── home/              # Home screen
│   ├── wallet/            # Wallet list, document cards
│   ├── capture/           # Share-intent receiver
│   └── lock/              # App lock screen
└── MainActivity.kt
```

## 🚀 Setup

```bash
git clone https://github.com/<your-username>/pdf-wallet.git
cd pdf-wallet
```

Open in **Android Studio** (Hedgehog or later), let Gradle sync, then run on an emulator or device running Android 8.0+.

> **Testing note:** use a **Play Store–enabled emulator image** — ML Kit requires Google Play Services, and a bare AOSP image will cause extraction to fail silently.

## 🔐 Privacy

PDF Wallet keeps sensitive documents — including government IDs — private by default:

- ✅ All extraction and classification run **on-device**; nothing is sent to a server
- ✅ The local database is **encrypted at rest**
- ✅ Documents are stored in **app-private storage**, inaccessible to other apps
- ✅ Optional **biometric/PIN lock** gates access to the entire app

## 🤝 Contributing

This project is in early development. Issues and PRs are welcome once Phase 1 lands — see the [implementation plan](./implementation_plan_final.md) for current scope and what's deferred to later phases.

## 📄 License

_TBD_

---

<div align="center">
<sub>Built for people tired of digging through email for a boarding pass.</sub>
</div>
