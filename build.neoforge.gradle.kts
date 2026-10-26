plugins {
	id("mod-platform")
	id("net.neoforged.moddev")
}

stonecutter {
	replacements.string(current.parsed >= "1.21.11") {
		replace("ResourceLocation", "Identifier")
		replace("location()", "identifier()")
	}
}

platform {
	loader = "neoforge"
	dependencies {
		required("minecraft") {
			forgeLikeVersionRange = prop("deps.minecraft")
		}
		required("neoforge") {
			forgeLikeVersionRange.set("[1,)")
		}
	}
}

neoForge {
	version = prop("deps.neoforge")
	accessTransformers.from(rootProject.file("src/main/resources/aw/${stonecutter.current.version}.cfg"))
	validateAccessTransformers = true

	if (hasProperty("deps.parchment")) parchment {
		val (mc, ver) = prop("deps.parchment").split(':')
		mappingsVersion = ver
		minecraftVersion = mc
	}

	runs {
		val devJvmArgs = propsList("mod", "dev_jvm_args") +
			propsList("mod", "dev_jvm_args_mixin_debug")
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "NeoForge Client (${stonecutter.current.version})"
			programArgument("--username=Dev")
			jvmArguments.addAll(devJvmArgs)
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "NeoForge Server (${stonecutter.current.version})"
			jvmArguments.addAll(devJvmArgs)
		}
	}

	mods {
		register(prop("mod.id")) {
			sourceSet(sourceSets["main"])
		}
	}
	sourceSets["main"].resources.srcDir("${rootDir}/versions/datagen/${sc.current.version.split("-")[0]}/src/main/generated")
}

repositories {
	mavenCentral()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
	maven("https://maven.theillusivec4.top/")  { name = "Illusive Soulworks maven" }
	maven("https://cursemaven.com")
}

dependencies {
	implementation("com.eliotlash.mclib:mclib:20")
	implementation("software.bernie.geckolib:geckolib-neoforge-${prop("deps.minecraft")}:${prop("deps.geckolib")}")
	// Compile against only the API artifact
	compileOnly("top.theillusivec4.curios:curios-neoforge:${prop("deps.curios")}+${prop("deps.minecraft")}:api")
	// Use the full Curios API jar at runtime
	runtimeOnly("top.theillusivec4.curios:curios-neoforge:${prop("deps.curios")}+${prop("deps.minecraft")}")
	implementation("curse.maven:goety-${prop("deps.goety")}")
	runtimeOnly("curse.maven:bagus-lib-${prop("deps.bagus-lib")}")
	implementation("curse.maven:enchant-with-mob-${prop("deps.enchantwithmob")}")
}

tasks.named("createMinecraftArtifacts") {
	dependsOn(tasks.named("stonecutterGenerate"))
}
