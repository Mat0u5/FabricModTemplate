import java.util.zip.GZIPInputStream
plugins {
	id("mod-platform")
	id("net.minecraftforge.gradle")
	id("net.minecraftforge.jarjar")
}
val unobfuscated = stonecutter.eval(stonecutter.current.version, ">=26.1")

fun prop(key: String) = project.property(key) as String

platform {
	loader = "forge"
	jarTask.set("jarJar")
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
	if (stonecutter.eval(stonecutter.current.version, ">=1.17")) {
		mappings("official", prop("deps.minecraft"))
	}
	else {
		mappings(prop("deps.mappings_channel"), prop("deps.mappings_version"))
	}

	val atFile = rootProject.file("src/main/resources/aw/${stonecutter.current.version}.cfg")
	if (atFile.exists()) {
		accessTransformers = files(atFile)
	}

	runs {
		configureEach {
			workingDir.convention(layout.projectDirectory.dir("run"))
			systemProperty("forge.logging.console.level", "debug")
			args("--mixin.config=${prop("mod.id")}.mixins.json")
			//ideaModule = null
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

jarJar.register()

tasks.named<Jar>("jarJar") {
	archiveClassifier.set("")
	dependsOn("jar")
}

dependencies {
	implementation(minecraft.dependency("net.minecraftforge:forge:${prop("deps.forge")}"))

	if (!unobfuscated) {
		annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")
		annotationProcessor("io.github.llamalad7:mixinextras-common:${libs.versions.mixinextras.get()}")

		compileOnly("io.github.llamalad7:mixinextras-common:${libs.versions.mixinextras.get()}")
		implementation("io.github.llamalad7:mixinextras-forge:${libs.versions.mixinextras.get()}")
		"jarJar"("io.github.llamalad7:mixinextras-forge:${libs.versions.mixinextras.get()}")
	}
	implementation(libs.moulberry.mixinconstraints)
	"jarJar"(libs.moulberry.mixinconstraints)
}

if (!unobfuscated) {
	val mappingsGroup = if (stonecutter.eval(stonecutter.current.version, ">=1.17")) "mappings_official" else "mappings_snapshot"
	val mappingsRepoDir = rootProject.file(".gradle/mavenizer/repo/net/minecraft/$mappingsGroup")

	val extractMcpToSrg by tasks.registering {
		val outputFile = layout.buildDirectory.file("mappings/map2srg.tsrg")
		outputs.file(outputFile)
		doLast {
			val mcVersion = prop("deps.minecraft")
			val versionSuffix = if (stonecutter.eval(stonecutter.current.version, ">=1.17")) null else prop("deps.mappings_version")

			val matchDir = mappingsRepoDir.listFiles { f ->
				f.isDirectory && f.name.startsWith(mcVersion) && (versionSuffix == null || f.name.endsWith(versionSuffix))
			}?.firstOrNull()
				?: throw GradleException("No mavenizer mappings dir found for $mcVersion under $mappingsRepoDir - list its contents to check the actual naming.")

			val gzFile = matchDir.listFiles { f -> f.name.endsWith("-map2srg.tsrg.gz") }?.firstOrNull()
				?: throw GradleException("No map2srg.tsrg.gz found in $matchDir")

			val out = outputFile.get().asFile
			out.parentFile.mkdirs()
			GZIPInputStream(gzFile.inputStream()).use { gz -> out.outputStream().use { os -> gz.copyTo(os) } }
		}
	}
	tasks.withType<JavaCompile>().configureEach {
		dependsOn(extractMcpToSrg)
		val refMapFile = layout.buildDirectory.file("sourcesSets/main/${prop("mod.id")}.mixins.refmap.json")
		val outTsrgFile = layout.buildDirectory.file("mappings/compileJava-mappings.tsrg")
		options.compilerArgs.addAll(listOf(
			"-AoutRefMapFile=${refMapFile.get().asFile}",
			"-AreobfTsrgFile=${extractMcpToSrg.get().outputs.files.singleFile}",
			"-AoutTsrgFile=${outTsrgFile.get().asFile}",
			"-AmappingTypes=tsrg",
			"-AdefaultObfuscationEnv=searge"
		))
	}
}
tasks.named<Jar>("jar") {
	destinationDirectory.set(layout.buildDirectory.dir("intermediates/jar"))
	manifest {
		attributes["MixinConfigs"] = "${prop("mod.id")}.mixins.json"
	}
	from(layout.buildDirectory.file("sourcesSets/main/${prop("mod.id")}.mixins.refmap.json")) {
		into("/")
	}
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

sourceSets {
	main {
		resources.srcDir(
			"${rootDir}/versions/datagen/${stonecutter.current.version.split("-")[0]}/src/main/generated"
		)
	}
}
