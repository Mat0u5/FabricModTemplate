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
			systemProperty("mixin.env.disableRefMap", "true")
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
	if (stonecutter.eval(stonecutter.current.version, "<1.13")) {
		maven("https://maven.minecraftforge.net") {
			metadataSources {
				mavenPom()
				artifact()
			}
		}
	}
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
	if (stonecutter.eval(stonecutter.current.version, "<=1.14.4")) {
		compileOnly("org.spongepowered:mixin:${libs.versions.mixin.get()}")
	}
}

if (legacyForge) {
	val mappingsRepoBase = rootProject.file(".gradle/mavenizer/repo/net/minecraft")
	val mappingsChannel = if (usesOfficialMappings) "official" else prop("deps.mappings_channel")
	val mappingsVersion = if (usesOfficialMappings) prop("deps.minecraft") else prop("deps.mappings_version")

	val extractMcpToSrg by tasks.registering {
		val outputFile = layout.buildDirectory.file("mappings/map2srg.tsrg")
		outputs.file(outputFile)
		doLast {
			val mcVersion = prop("deps.minecraft")
			val groupDir = File(mappingsRepoBase, "mappings_$mappingsChannel")

			val candidates = groupDir.listFiles { d -> d.isDirectory }
				?.filter { it.name.startsWith(mcVersion) || it.name.contains(mappingsVersion) }
				?.mapNotNull { d -> d.listFiles { f -> f.name.endsWith("-map2srg.tsrg.gz") }?.firstOrNull() }
				?: emptyList()

			val gzFile = candidates.firstOrNull { it.parentFile.name.contains(mappingsVersion) }
				?: candidates.firstOrNull()
				?: throw GradleException(
					"No map2srg.tsrg.gz for channel '$mappingsChannel', mappings '$mappingsVersion', mc '$mcVersion' in $groupDir.\n" +
							"Available: ${groupDir.listFiles()?.joinToString { it.name } ?: "(group dir missing)"}"
				)

			logger.lifecycle("extractMcpToSrg[$mcVersion]: using ${gzFile.absolutePath}")
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

if (legacyForge) {
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
			val args = mutableListOf(
				javaBin.absolutePath, "-jar", fart.singleFile.absolutePath,
				"--input", jarFile.absolutePath,
				"--output", tmp.absolutePath,
				"--map", tsrg.absolutePath,
				"--ann-fix", "--ids-fix", "--src-fix", "--record-fix"
			)
			sourceSets["main"].compileClasspath.files
				.filter { it.exists() && it.extension == "jar" }
				.forEach { args += listOf("--lib", it.absolutePath) }

			val proc = ProcessBuilder(args).redirectErrorStream(true).start()
			val output = proc.inputStream.bufferedReader().readText()
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
