import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
        mavenCentral()
    }

    val kotlinVersion: String by project
    val bintray: String by project
    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:$bintray")
    }
}

allprojects {
    repositories {
        jcenter()
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin<JavaLibraryPlugin>()
        plugin<KotlinPlatformJvmPlugin>()
        plugin("maven-publish")
        plugin("com.jfrog.bintray")
        plugin("jacoco")
    }

    val artifactPublish: String by project
    val artifactGroupId: String by project

    version = artifactPublish
    group = artifactGroupId

    //publishing
    configure<PublishingExtension> {

        val sourceSets = project.the<SourceSetContainer>()

        val sourcesJar by tasks.registering(Jar::class) {
            from(sourceSets["main"].allSource)
            classifier = "sources"
        }

        val javadocJar by tasks.creating(Jar::class) {
            val doc by tasks.creating(Javadoc::class) {
                isFailOnError = false
                source = sourceSets["main"].allJava
            }

            dependsOn(doc)
            from(doc)

            classifier = "javadoc"
        }

        publications {
            register(project.name, MavenPublication::class) {
                from(components["java"])
                artifact(sourcesJar.get())
                artifact(javadocJar)
                groupId = artifactGroupId
                artifactId = project.name
                version = artifactPublish

                pom {
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("http://www.opensource.org/licenses/mit-license.php")
                        }
                    }
                }
            }
        }
    }

    // bintray
    configure<BintrayExtension> {
        user = findProperty("BINTRAY_USER") as? String
        key = findProperty("BINTRAY_KEY") as? String
        setPublications(project.name)
        publish = true
        pkg.apply {
            repo = "maven"
            name = "base-types-kt"
            desc = "The modelling for success/failure of operations in Kotlin"
            userOrg = "VirtusLab"
            websiteUrl = "https://github.com/VirtusLab/base-types-kt"
            vcsUrl = "https://github.com/VirtusLab/base-types-kt"
            setLicenses("Apache License 2.0")
            version.apply {
                name = artifactPublish
            }
        }
    }

    // jacoco
    configure<JacocoPluginExtension> {
        toolVersion = extra.get("jacoco") as String
    }

    tasks.withType<JacocoReport> {
        reports {
            html.isEnabled = true
            xml.isEnabled = true
            csv.isEnabled = false
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
