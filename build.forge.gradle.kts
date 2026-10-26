plugins {
	id("mod-platform")
	id("net.neoforged.moddev.legacyforge")
}

stonecutter {
	replacements.string(current.parsed >= "1.21.11") {
		replace("ResourceLocation", "Identifier")
		replace("location()", "identifier()")
	}
}

platform {
	loader = "forge"
	dependencies {
	    required("minecraft") {
		forgeLikeVersionRange = prop("deps.minecraft")
	    }
	    required("forge") {
		forgeLikeVersionRange.set("[${prop("deps.forge")},)")
	    }
	    required("geckolib") {
		forgeLikeVersionRange.set("[${prop("deps.geckolib")},)")
	    }
	    required("curios") {
		forgeLikeVersionRange.set("[${prop("deps.curios")}+${prop("deps.minecraft")},)")
	    }
	    required("enchantwithmob") {
		forgeLikeVersionRange.set("[${prop("deps.minecraft")}-${prop("deps.enchantwithmob-version")},)")
	    }
	    required("dungeonsmobs") {
		forgeLikeVersionRange.set("[${prop("deps.dungeonsmobs-version")},)")
	    }
	    required("dungeonsgear") {
		forgeLikeVersionRange.set("[${prop("deps.dungeonsgear-version")},)")
	    }
	    required("integrated_api") {
		forgeLikeVersionRange.set("[${prop("deps.integrated_api-version")},)")
	    }
	}
}

legacyForge {
	version = "${prop("deps.minecraft")}-${prop("deps.forge")}"

	validateAccessTransformers = true

	accessTransformers.from(
	    rootProject.file("src/main/resources/aw/${sc.current.version}.cfg")
	)

	val devJvmArgs = propsList("mod", "dev_jvm_args") +
		propsList("mod", "dev_jvm_args_mixin_debug")
	runs {
		register("client") {
			client()
			gameDirectory = file("run/")
			ideName = "Forge Client (${sc.current.version})"
			programArgument("--username=Dev")
			jvmArguments.addAll(devJvmArgs)
		}
		register("server") {
			server()
			gameDirectory = file("run/")
			ideName = "Forge Server (${sc.current.version})"
			jvmArguments.addAll(devJvmArgs)
		}
	}


	mods {
		register(prop("mod.id")) {
			sourceSet(sourceSets["main"])
		}
	}
}

// mixin {
// 	add(sourceSets.main.get(), "${prop("mod.id")}.mixins.refmap.json")
// 	config("${prop("mod.id")}.mixins.json")
// }

repositories {
	mavenCentral()
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
	maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
	maven("https://maven.theillusivec4.top/")  { name = "Illusive Soulworks maven" }
	maven("https://cursemaven.com")
}

dependencies {
	annotationProcessor("org.spongepowered:mixin:${libs.versions.mixin.get()}:processor")

	implementation("com.eliotlash.mclib:mclib:20")
	implementation("software.bernie.geckolib:geckolib-forge-${prop("deps.minecraft")}:${prop("deps.geckolib")}")
	// Compile against only the API artifact
	compileOnly("top.theillusivec4.curios:curios-forge:${prop("deps.curios")}+${prop("deps.minecraft")}:api")
	// Use the full Curios API jar at runtime
	runtimeOnly("top.theillusivec4.curios:curios-forge:${prop("deps.curios")}+${prop("deps.minecraft")}")
	implementation("curse.maven:goety-${prop("deps.goety")}")
	runtimeOnly("curse.maven:bagus-lib-${prop("deps.bagus-lib")}")
	implementation("curse.maven:enchant-with-mob-${prop("deps.enchantwithmob")}")
	implementation("curse.maven:dungeons-libraries-port-${prop("deps.dungeonslibs")}")
	implementation("curse.maven:dungeons-mobs-port-${prop("deps.dungeonsmobs")}")
	implementation("curse.maven:dungeons-gear-port-${prop("deps.dungeonsgear")}")
	implementation("curse.maven:integrated-api-${prop("deps.integrated_api")}")
	// annotationProcessor("io.github.llamalad7:mixinextras-common:${prop("deps.mixinextras")}")
	// implementation("io.github.llamalad7:mixinextras-neoforge:${prop("deps.mixinextras")}")
	// jarJar("io.github.llamalad7:mixinextras-forge:${prop("deps.mixinextras")}")
}

sourceSets {
	main {
		resources.srcDir(
			"${rootDir}/versions/datagen/${sc.current.version.split("-")[0]}/src/main/generated"
		)
	}
}

tasks.named("createMinecraftArtifacts") {
    dependsOn(tasks.named("stonecutterGenerate"))
}
