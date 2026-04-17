buildscript {
	repositories {
		maven("https://maven.minecraftforge.net")
		maven("https://repo.spongepowered.org/repository/maven-public/")
		mavenCentral()
	}
	dependencies {
		classpath("net.minecraftforge.gradle:net.minecraftforge.gradle.gradle.plugin:7.0.20")
		classpath("org.spongepowered:mixingradle:0.7-SNAPSHOT")
	}
}

apply(plugin = "net.minecraftforge.gradle")
apply(plugin = "org.spongepowered.mixin")
apply(plugin = "mod-platform")

fun prop(key: String) = project.property(key) as String

(extensions.getByName("minecraft") as net.minecraftforge.gradle.userdev.UserDevExtension).apply {
	mappings("official", prop("deps.minecraft"))
	copyIdeResources = true

	val atFile = rootProject.file("src/main/resources/aw/${stonecutter.current.version}.cfg")
	if (atFile.exists()) {
		accessTransformers.from(atFile)
	}

	runs {
		create("client") {
			workingDirectory(project.file("run"))
			property("forge.logging.console.level", "debug")
			args("--username", "Player")
			mods {
				create(prop("mod.id")) {
					source(sourceSets.main.get())
				}
			}
		}
		create("server") {
			workingDirectory(project.file("run"))
			property("forge.logging.console.level", "debug")
			mods {
				create(prop("mod.id")) {
					source(sourceSets.main.get())
				}
			}
		}
	}
}

(extensions.getByName("mixin") as org.spongepowered.asm.gradle.plugins.MixinExtension).apply {
	add(sourceSets.main.get(), "${prop("mod.id")}.mixins.refmap.json")
	config("${prop("mod.id")}.mixins.json")
}

dependencies {
	"minecraft"("net.minecraftforge:forge:${prop("deps.minecraft")}-${prop("deps.forge")}")
	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")
	implementation(libs.moulberry.mixinconstraints)
	"jarJar"(libs.moulberry.mixinconstraints)
}

sourceSets {
	main {
		resources.srcDir(
			"${rootDir}/versions/datagen/${stonecutter.current.version.split("-")[0]}/src/main/generated"
		)
	}
}

tasks.named("stonecutterGenerate") {
	mustRunAfter(tasks.named("generateModMetadata"))
}
