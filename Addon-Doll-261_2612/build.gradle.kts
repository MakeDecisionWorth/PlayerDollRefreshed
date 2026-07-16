plugins {
    id("io.papermc.paperweight.userdev")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle("26.1.2.build.+")
    compileOnly(project(":PlayerDoll-API"))
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 25
}
