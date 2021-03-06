import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    val arrow: String by project
    val kotlintest: String by project

    implementation(kotlin("stdlib", kotlinVersion))

    api("io.arrow-kt:arrow-core:$arrow")

    testImplementation("io.kotlintest:kotlintest-runner-junit5:$kotlintest")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junit")
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-XXLanguage:+InlineClasses")
}
