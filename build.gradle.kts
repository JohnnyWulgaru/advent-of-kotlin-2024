plugins {
    kotlin("jvm") version "2.0.21"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.11"
    }
}

dependencies {
    implementation("io.arrow-kt:arrow-core:2.0.0")
    implementation("io.arrow-kt:arrow-fx-coroutines:2.0.0")
}
