sourceSets {
    getByName("main").java.srcDirs("src/main/kotlin")
    getByName("test").java.srcDirs("src/main/kotlin")
}

dependencies {
    val junit: String by project
    val kotlinVersion: String by project
    implementation(kotlin("stdlib", kotlinVersion))

    implementation("io.projectreactor:reactor-core:3.2.12.RELEASE")
    implementation("com.github.kittinunf.result:result:2.2.0")


    testImplementation("junit:junit:$junit")
}
