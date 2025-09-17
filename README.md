# 📅 CalendarRangePicker  

A simple and customizable **Calendar Range Picker** library for Android that allows users to select a **start date** and **end date** with a beautiful Material Design UI.  

---

## ✨ Features
- 📆 **Date Range Selection**: Select start and end dates easily  
- 🎨 **Material Design UI**: Modern and clean user interface  
- ⚡ **Lightweight**: Easy to integrate into any project  
- 🚫 **Disable Dates**: Option to disable specific dates  
- 🛠️ **Customizable**: Change colors, styles, and behaviors  

---

## 📦 Installation

### 1️⃣ Add JitPack to `settings.gradle` (Project Level)
To fetch the library from JitPack, you need to add the repository in your **project-level** `settings.gradle` file:  

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

2️⃣ Add the dependency in your app/build.gradle

Now, add the following dependency in your app-level build.gradle file:

dependencies {
    implementation 'com.github.sudhir927:CalendarRangePicker:1.0.0'
}

🛠️ Usage
Kotlin Usage

You can use the CalendarRangePicker programmatically in your Activity or Fragment:

// Example usage of CalendarRangePicker
val calendarPicker = CalendarRangePicker(context)
calendarPicker.setOnDateSelectedListener { startDate, endDate ->
    // Handle selected start and end dates here
    Toast.makeText(context, "Start: $startDate, End: $endDate", Toast.LENGTH_SHORT).show()
}
calendarPicker.show()

XML Usage

You can also add the CalendarRangePicker view directly to your layout file:

<com.sudhirsingh.rangecalendar.RangeCalendarView
    android:id="@+id/rangeCalendar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />

Then, in your Kotlin code, you can access the view and get selected dates:

val rangeCalendar = findViewById<RangeCalendarView>(R.id.rangeCalendar)
rangeCalendar.setOnDateSelectedListener { startDate, endDate ->
    // Handle the selected dates from XML view
}

MIT License

Copyright (c) 2025 Sudhir

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

