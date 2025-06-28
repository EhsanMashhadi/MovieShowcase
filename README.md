# MovieShowcase

## ✨ Overview

MovieShowcase is an Android application designed to elegantly display information about movies. It
provides a beautiful and intuitive user experience for discovering, searching, and managing your
favorite films.

## 🚀 Features

* **Discover Movies:**

    * Showing top random movies

    * Showing the latest published movie

    * Seeing movies and filtering based on genres

* **Search Functionality:** Searching movies by name

* **Bookmarking:**

    * Bookmarking favorite movies

    * Seeing a dedicated list of bookmarked movies

* **Detailed Views:** Movies display comprehensive information including image, title, description,
  genres, and rating.

* **Customizable Theme:** Day and night theme support for comfortable viewing.

* **User-Friendly Interface:** A clean and modern design focusing on ease of use.

## 📸 Screenshots

TBD

## 💻 Tech Stack

* **Platform:** Android

* **Language:** Kotlin

* **Build System:** Gradle

* **UI Toolkit:** Jetpack Compose, Material 3

* **Dependency Injection:** Dagger Hilt

* **Networking:** Retrofit, Moshi, OkHttp

* **Local Database:** Room

* **Data Storage:** Jetpack DataStore Preferences

* **Asynchronous Programming:** Kotlin Coroutines

* **UI Navigation:** Jetpack Navigation Compose

* **Image Loading:** Coil

* **Pagination:** Jetpack Paging 3

* **Serialization:** Kotlinx Serialization

* **Analytics:** Firebase Analytics

* **Testing:** JUnit, AndroidX Test, Espresso, MockK, Robolectric, Turbine, Jetpack Compose Test

## 🛠️ Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

* Android Studio (Bumblebee or newer recommended)

* JDK 17 or higher

* An Android device or emulator running API level 26+

### Installation

1. **Clone the repository:**
   git clone https://github.com/EhsanMashhadi/MovieShowcase.git

2. **Open in Android Studio:**
   Open the cloned project in Android Studio. Gradle will sync automatically.

3. **Run the app:**
   Select a device or emulator and click the 'Run' button in Android Studio.

## ⚙️ CI/CD & Release Management

This project utilizes GitHub Actions for continuous integration and delivery.

### Pull Request CI

A dedicated CI workflow (`.github/workflows/pr-ci.yml`) runs on every pull request targeting the
`main` branch. This ensures code quality and stability before merging.

This workflow performs the following actions:

* **Build Debug APK:** Compiles the Android application to produce a debug APK.

* **Run Unit Tests:** Executes all unit tests to verify application logic.

### Nightly Debug Builds

A nightly debug build and distribution workflow (`.github/workflows/nightly-debug.yml`) is
automatically triggered every day at **00:00 UTC**, or can be manually dispatched. These builds
represent the latest development changes and are distributed for internal testing.

* **Versioning:** Nightly builds are versioned using a **`YYMMDD`** format for their `versionCode` (
  e.g., `250628` for June 28, 2025) and include a `versionName` suffix like `-nightly.YYMMDD` (e.g.,
  `1.0.0-nightly.250628`). This ensures each nightly build is unique and incremental.

* **Firebase App Distribution:** Builds are seamlessly uploaded and distributed to designated tester
  groups (`testers` group is explicitly configured) via Firebase App Distribution. Testers receive
  notifications when a new nightly build is available.

* The release notes for Firebase App Distribution include details like the build date and source
  branch.