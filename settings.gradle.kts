plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "options-beyond-jpa"
include("hiberate")
include("jdbi")
include("jooq")
include("spring-data")
include("spring-jdbc")
