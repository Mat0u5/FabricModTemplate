# Stonecutter Multi-Platform Mod Template

A multi-platform Minecraft mod template for **Fabric**, **Forge** and **NeoForge**,
using [Stonecutter](https://stonecutter.kikugie.dev/) for
multiversion and multiloader code.

## **Configure your mod**

1. Edit `gradle.properties` to set your mod's metadata.
2. 

Dependencies/Properties that are specific to a version/loader 
are defined in `gradle.properties` as `[VERSIONED]` then set in `versions/{version}-{loader}/gradle.properties`.

#### 5. **Rename package structure**

Rename the `net.mat0u5.modid` package in
`src/main/java/` to match your `mod.group` and `mod.id`.

#### 6. **Update resource files**

Rename these files to match your `mod.id`:

* `src/main/resources/modtemplate.accesswidener`
* `src/main/resources/modtemplate.mixins.json`

Replace and `src/main/resources/assets/icon.png` and `.idea/icon.png` with the mods icon.