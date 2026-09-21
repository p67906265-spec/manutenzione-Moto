plugins { id("com.android.application") }

val signingReady = listOf("KEYSTORE_FILE", "KEYSTORE_PASSWORD", "KEY_ALIAS", "KEY_PASSWORD")
    .all { !System.getenv(it).isNullOrBlank() }

android {
    namespace = "it.paolofree.manutenzionemoto"
    compileSdk = 35

    defaultConfig {
        applicationId = "it.paolofree.manutenzionemoto"
        minSdk = 24
        targetSdk = 35
        versionCode = 10
        versionName = "1.9"
    }

    if (signingReady) {
        signingConfigs.create("release") {
            storeFile = file(System.getenv("KEYSTORE_FILE"))
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (signingReady) signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
}
