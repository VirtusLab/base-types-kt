sourceSets {
    getByName("main").java.srcDirs("src/main/kotlin")
    getByName("test").java.srcDirs("src/main/kotlin")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    val junit: String by project
    val kotlinVersion: String by project
    val kotlintest: String by project
    val arrow: String by project

    implementation(kotlin("stdlib", kotlinVersion))

    api("io.reactivex.rxjava2:rxjava:2.2.14")
    api("io.arrow-kt:arrow-fx-rx2:$arrow")
    implementation("io.arrow-kt:arrow-core:$arrow")
    implementation("io.arrow-kt:arrow-fx:$arrow")

    testImplementation("io.kotlintest:kotlintest-runner-junit5:$kotlintest")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junit")
}
