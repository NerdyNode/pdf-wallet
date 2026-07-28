buildscript {
    ext {
        set("compose_bom_version", "2024.02.00")
        set("room_version", "2.6.1")
        set("hilt_version", "2.50")
        set("mlkit_version", "16.0.0")
        set("mlkit_barcode_version", "17.2.0")
    }
}

plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.dagger.hilt.android") version "2.50" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
}
