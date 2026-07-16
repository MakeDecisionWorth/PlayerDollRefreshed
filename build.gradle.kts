plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21" apply false
}

subprojects {
    apply(plugin = "java-library")

    group = "me.autobot"
    version = "3.0"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = 21
    }
}

tasks.register<Copy>("collectOutput") {
    from(project(":PlayerDoll-Core").tasks.named("jar"))
    from(project(":Addon-Doll-1216_1218").tasks.named("jar"))
    from(project(":Addon-Doll-1219_12111").tasks.named("jar"))
    from(project(":Addon-Doll-261_2612").tasks.named("jar"))
    into(layout.projectDirectory.dir("Output"))
}
