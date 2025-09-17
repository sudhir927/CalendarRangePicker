// Add this at the bottom of the file
tasks.register<Copy>("copyAar") {
    from("build/outputs/aar/")
    into("../outputs/")
    include("*.aar")
}

tasks.named("assemble") {
    dependsOn("copyAar")
}
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id ("maven-publish")
}

android {
    namespace = "com.sudhirsingh.rangecalendar"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = false
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
// RecyclerView (might already be included if you're using Material Components)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
// If you want to use Material Design components
    implementation("com.google.android.material:material:1.9.0")
}
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.sudhir927"   // Your GitHub username
                artifactId = "calendar-range"        // Library name
                version = "1.0.0"                    // Version

                pom {
                    name.set("Calendar Range Picker")
                    description.set("A simple Android calendar range picker library")
                    url.set("https://github.com/sudhir927/CalendarRangePicker")
                }
            }
        }
    }
}
