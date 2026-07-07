# Stonecutter Multi-Platform Mod Template

A multi-platform Minecraft mod template for **Fabric**, **Forge** and **NeoForge**,
using [Stonecutter](https://stonecutter.kikugie.dev/) for
multiversion and multiloader code.

## **Configure your mod**

1. Run setup_template.bat to automatically rename your packages and Mod ID and similar.

2. Change gradle.properties as necessary

3. Replace and `src/main/resources/assets/icon.png` and `.idea/icon.png` with the mods icon.

### Java and Gradle settings
Project Java version: 25
Gradle JVM: JetBrains Runtime 25

### Info
Forge < 1.15 does not support mixins by default.

### Issues
Forge client launch currently does not work in <= 1.15

### Fabric AW/CT Loader versions
`accessWidener v2` - Fabric Loader 0.12.0+
`classTweaker v1` - Fabric Loader 0.18.0+

## Compatibility Table

| Fabric         | Forge            | NeoForge         |
|----------------|------------------|------------------|
| 1.14 - 1.21.11 | 1.15 - 1.19.4    | 1.20.4 - 1.21.11 |
| 26.1+          | 1.20 - 1.20.1    | 26.1+            |
|                | 1.20.5 - 1.21.11 |                  |
|                | 26.1+            |                  |