dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.6-R0.1-SNAPSHOT")
    compileOnly("com.mojang:authlib:6.0.58")
    implementation(project(":PlayerDoll-API"))
}

tasks.jar {
    archiveBaseName = "PlayerDoll-Main"
    dependsOn(":PlayerDoll-API:jar")
    from(provider { zipTree(project(":PlayerDoll-API").tasks.jar.get().archiveFile) })
}
