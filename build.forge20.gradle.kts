plugins {
	id("mod-platform")
	id("net.minecraftforge.gradle")
	id("net.minecraftforge.jarjar")
}

fun prop(key: String) = project.property(key) as String

platform {
	loader = "forge"
	dependencies {
		required("minecraft") {
			forgeVersionRange = "[${prop("deps.minecraft")}]"
		}
		required("forge") {
			forgeVersionRange = "[1,)"
		}
	}
}

minecraft {
	mappings("official", prop("deps.minecraft"))

	val atFile = rootProject.file("src/main/resources/aw/${stonecutter.current.version}.cfg")
	if (atFile.exists()) {
		accessTransformers = files(atFile)
	}

	runs {
		configureEach {
			workingDir.convention(layout.projectDirectory.dir("run"))
			systemProperty("forge.logging.console.level", "debug")
			args("--mixin.config=${prop("mod.id")}.mixins.json")
		}
		register("client") {
			args("--username", "Player")
		}
		register("server") {
			args("--nogui")
		}
	}
}

sourceSets.configureEach {
	val dir = layout.buildDirectory.dir("sourcesSets/$name")
	output.setResourcesDir(dir)
	java.destinationDirectory.set(dir)
}

repositories {
	minecraft.mavenizer(this)
	maven(fg.forgeMaven)
	maven(fg.minecraftLibsMaven)
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	mavenCentral()
}

jarJar.register() {
	archiveClassifier = null
}

dependencies {
	implementation(minecraft.dependency("net.minecraftforge:forge:${prop("deps.forge")}"))
	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")
	implementation(libs.moulberry.mixinconstraints)
	"jarJar"(libs.moulberry.mixinconstraints)
}

tasks.named<Jar>("jar") {
	manifest {
		attributes["MixinConfigs"] = "${prop("mod.id")}.mixins.json"
	}
}
tasks.configureEach {
	if (name.startsWith("run")) {
		dependsOn(tasks.named("classes"))
		dependsOn(tasks.named("processResources"))
	}
}

sourceSets {
	main {
		resources.srcDir(
			"${rootDir}/versions/datagen/${stonecutter.current.version.split("-")[0]}/src/main/generated"
		)
	}
}
