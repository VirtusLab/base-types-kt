sourceSets {
    getByName("main").java.srcDirs("src/main/kotlin")
    getByName("test").java.srcDirs("src/main/kotlin")
}

dependencies {
    val junit: String by project
    val kotlinVersion: String by project
    val arrow: String by project

    implementation(kotlin("stdlib", kotlinVersion))

    implementation("io.arrow-kt:arrow-core:$arrow")
    implementation("io.arrow-kt:arrow-core-data:$arrow")


    testImplementation("junit:junit:$junit")
}
