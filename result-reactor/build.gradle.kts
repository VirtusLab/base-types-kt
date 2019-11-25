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
    val reactor = "3.2.12.RELEASE"

    implementation(kotlin("stdlib", kotlinVersion))

    implementation("io.projectreactor:reactor-core:$reactor")
    implementation("com.github.kittinunf.result:result:2.2.0")


    testImplementation("io.kotlintest:kotlintest-runner-junit5:$kotlintest")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junit")
    testImplementation("io.projectreactor:reactor-test:$reactor")
}
