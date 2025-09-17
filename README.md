# CalendarRangePicker

A simple and customizable Calendar Range Picker library for Android.

---

## Features
- Select start and end dates easily
- Material Design UI
- Lightweight and easy to integrate

---

## Installation

Add the JitPack repository in your root `settings.gradle` or `build.gradle`:

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
## 📦 Installation

Add this dependency in your app-level `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.sudhir927:CalendarRangePicker:1.0.0'
}

// Example usage of CalendarRangePicker
val calendarPicker = CalendarRangePicker(context)
calendarPicker.setOnDateSelectedListener { startDate, endDate ->
    // Handle selected dates
}
calendarPicker.show()

