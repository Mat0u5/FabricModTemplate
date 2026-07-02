import java.util.zip.GZIPInputStream

plugins {
	id("mod-platform")
	id("net.minecraftforge.gradle")
	id("net.minecraftforge.jarjar")
}

fun prop(key: String) = project.property(key) as String

val unobfuscated = stonecutter.eval(stonecutter.current.version, ">=26.1")
val legacyForge = stonecutter.eval(stonecutter.current.version, "<=1.20")
val usesOfficialMappings = stonecutter.eval(stonecutter.current.version, ">=1.17")

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
	if (usesOfficialMappings) {
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

val runJavaVersion = when {
	stonecutter.eval(stonecutter.current.version, ">=26") -> 25
	stonecutter.eval(stonecutter.current.version, ">=1.20.5") -> 21
	// dev classes are compiled as J17 bytecode even for <=1.16
	else -> 17
}
tasks.withType<JavaExec>().matching { it.name.startsWith("run") }.configureEach {
	javaLauncher.set(javaToolchains.launcherFor {
		languageVersion.set(JavaLanguageVersion.of(runJavaVersion))
	})
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

if (legacyForge) {
	val mappingsGroup = if (usesOfficialMappings) "mappings_official" else "mappings_snapshot"
	val mappingsRepoDir = rootProject.file(".gradle/mavenizer/repo/net/minecraft/$mappingsGroup")

	val extractMcpToSrg by tasks.registering {
		val outputFile = layout.buildDirectory.file("mappings/map2srg.tsrg")
		outputs.file(outputFile)
		doLast {
			val mcVersion = prop("deps.minecraft")
			val versionSuffix = if (usesOfficialMappings) null else prop("deps.mappings_version")

			val matchDir = mappingsRepoDir.listFiles { f ->
				f.isDirectory && f.name.startsWith(mcVersion) && (versionSuffix == null || f.name.endsWith(versionSuffix))
			}?.firstOrNull()
				?: throw GradleException("No mavenizer mappings dir found for $mcVersion under $mappingsRepoDir")

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
else if (!unobfuscated) {
	tasks.withType<JavaCompile>().configureEach {
		options.compilerArgs.addAll(
			listOf(
				"-Amixin.refmap.name=${prop("mod.id")}.mixins.refmap.json",
				"-AoutRefMapFile=${layout.buildDirectory.file("sourcesSets/main/${prop("mod.id")}.mixins.refmap.json").get().asFile}"
			)
		)
	}
}

if (legacyForge) {
	// ---------------------------------------------------------------------
	// Production reobfuscation (official/MCP names -> SRG names).
	//
	// Forge <=1.20.x runs Minecraft under SRG names in production, but this
	// setup compiles against official (or MCP) names and has no reobfJar
	// task, so the finished jar would ship call sites like getDisplayName()
	// into a runtime that only has m_5446_() -> NoSuchMethodError. The same
	// map2srg.tsrg used for the refmap is exactly the mapping needed, and
	// ForgeAutoRenamingTool (FART) applies it to the finished jar in place.
	// Running in-place inside jarJar's doLast means everything downstream
	// (downgradeJar/shadeDowngradedApi on <=1.16, buildAndCollect,
	// publishing) automatically consumes the reobfuscated jar.
	// ---------------------------------------------------------------------
	val fart: Configuration by configurations.creating {
		isTransitive = false
	}

	dependencies {
		fart("net.minecraftforge:ForgeAutoRenamingTool:1.1.0:all")
	}

	tasks.named<Jar>("jarJar") {
		dependsOn("extractMcpToSrg")
		doLast {
			val jarFile = archiveFile.get().asFile
			val tmp = File(jarFile.parentFile, jarFile.name + ".reobf")
			val tsrg = layout.buildDirectory.file("mappings/map2srg.tsrg").get().asFile
			val javaBin = File(System.getProperty("java.home"), "bin/java")
			val proc = ProcessBuilder(
				javaBin.absolutePath, "-jar", fart.singleFile.absolutePath,
				"--input", jarFile.absolutePath,
				"--output", tmp.absolutePath,
				"--map", tsrg.absolutePath,
				"--ann-fix", "--ids-fix", "--src-fix", "--record-fix"
			).redirectErrorStream(true).start()
			val output = proc.inputStream.bufferedReader().readText() // drains pipe; also prevents deadlock
			val exit = proc.waitFor()
			if (exit != 0) throw GradleException("FART reobfuscation failed (exit $exit):\n$output")
			logger.info(output)
			jarFile.delete()
			tmp.renameTo(jarFile)
		}
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
