plugins {
	alias(libs.plugins.stonecutter)
	alias(libs.plugins.dotenv)
	alias(libs.plugins.fabric.loom).apply(false)
	alias(libs.plugins.fabric.loom.remap).apply(false)
	alias(libs.plugins.neoforged.moddev).apply(false)
	alias(libs.plugins.minecraftforge.gradle).apply(false)
	alias(libs.plugins.minecraftforge.jarjar).apply(false)
	alias(libs.plugins.jsonlang.postprocess).apply(false)
	alias(libs.plugins.mod.publish.plugin).apply(false)
	alias(libs.plugins.kotlin.jvm).apply(false)
	alias(libs.plugins.devtools.ksp).apply(false)
	alias(libs.plugins.fletching.table).apply(false)
	alias(libs.plugins.legacyforge.moddev).apply(false)
}

stonecutter active file(".sc_active_version")

for (version in stonecutter.versions.map { it.version }.distinct()) tasks.register("publish$version") {
	group = "publishing"
	dependsOn(stonecutter.tasks.named("publishMods") { metadata.version == version })
}

gradle.taskGraph.whenReady {
	val taskNames = listOf("publishModrinth", "publishCurseforge")

	taskNames.forEach { targetName ->
		val allTasksInBuild = allTasks.filter { it.name == targetName }

		val sortedTasks = allTasksInBuild.sortedWith(
			compareBy<Task> { task ->
				val pName = task.project.name.lowercase()
				when {
					pName.contains("fabric") -> 2
					pName.contains("neoforge") -> 1
					else -> 0
				}
			}.thenBy { it.project.name }
		)

		for (i in 1 until sortedTasks.size) {
			val previous = sortedTasks[i - 1]
			val current = sortedTasks[i]

			current.mustRunAfter(previous)
		}

		sortedTasks.forEachIndexed { index, task ->
			if (index > 0) {
				task.doFirst {
					logger.lifecycle("\n>>> [WAITING] 10s delay: Uploading ${task.project.name} after ${sortedTasks[index-1].project.name}...")
					Thread.sleep(10000)
				}
			}
		}
	}
}

tasks.register("runActiveClient") {
	group = "stonecutter"
	description = "Run client of the active Stonecutter version (always up-to-date)"

	dependsOn(stonecutter.current!!.project + ":processResources")
	dependsOn(stonecutter.current!!.project + ":classes")

	finalizedBy(stonecutter.current!!.project + ":runClient")
}

stonecutter parameters {
	constants.match(node.metadata.project.substringAfterLast('-'), "fabric", "neoforge", "forge")
	filters.include("**/*.fsh", "**/*.vsh")
	swaps["mod_version"] = "\"" + property("mod.version") + "\";"
	swaps["mod_id"] = "\"" + property("mod.id") + "\";"
	swaps["mod_name"] = "\"" + property("mod.name") + "\";"
	swaps["mod_group"] = "\"" + property("mod.group") + "\";"
	swaps["minecraft"] = "\"" + node.metadata.version + "\";"
	constants["release"] = property("mod.id") != "modtemplate"
}
