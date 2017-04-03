package pika.c.gradle.alcortoolchain

import org.gradle.internal.os.OperatingSystem
import org.gradle.api.*
import org.gradle.model.*
import org.gradle.platform.base.*
import org.gradle.language.base.plugins.ComponentModelBasePlugin

import org.gradle.api.internal.file.FileResolver
import org.gradle.process.internal.ExecActionFactory
import org.gradle.internal.reflect.Instantiator
import org.gradle.internal.service.ServiceRegistry
import org.gradle.nativeplatform.toolchain.*
import org.gradle.nativeplatform.platform.NativePlatform
import org.gradle.nativeplatform.plugins.NativeComponentPlugin
import org.gradle.internal.operations.BuildOperationProcessor
import org.gradle.nativeplatform.toolchain.internal.gcc.version.CompilerMetaDataProviderFactory

interface AlcorToolchainBase {
    boolean canApply(OperatingSystem os)
    Task apply(Project project)
    // WARNING!!!!
    // Do not trust that GradleRIO owns the file given by get_toolchain_root.
    // On Windows and Mac systems, it is installed under ~/.gradle/gradlerioc/toolchain
    // by default, HOWEVER, on Linux systems, it is installed under /usr as packages
    // are installed by apt. DO NOT remove files from this directory.
    // For this reason, no 'clean_frc_toolchain' task is provided.
    // File get_toolchain_root()
}

class AlcorToolchainPlugin implements Plugin<Project> {
    //static def alcortoolchains = new ArrayList<AlcorToolchainBase>()

    void apply(Project project) {
        project.getPluginManager().apply(NativeComponentPlugin.class)
        project.getPluginManager().apply(ComponentModelBasePlugin.class)
    }

    static AlcorToolchainBase getActiveToolchain() {
        /*alcortoolchains.find {
            it.canApply(OperatingSystem.current())
        }*/
    }

    static class Rules extends RuleSource {
        @Model("alcor")
        void createAlcorModel(AlcorSpec spec) {}

        @Defaults 
        void defaultAlcorModel(AlcorSpec spec) {
            spec.setCppVersion("c++1y") // C++1Y = (roughly) C++14. The RoboRIO supports C++1Y (not C++14 ISO standard)
            spec.setDebugInfo(true) // This adds -g (gcc) and /Zi,/FS,/DEBUG (msvc). This is used for debugging and symbol info in a crash
        }

        // TODO Platform Compilers for ARM (Rasp Pi, Pine64)?
        @Mutate
        void addPlatform(PlatformContainer platforms) {
            // RoboRIO ARM Cross Compilation (XToolchain GCC)
            NativePlatform nds32 = platforms.maybeCreate("nds32", NativePlatform.class)
            nds32.architecture("nds32")
            nds32.operatingSystem("rtos")
        }

        @Defaults
        void addToolchain(NativeToolChainRegistry toolChainRegistry, ServiceRegistry serviceRegistry) {
            final FileResolver fileResolver = serviceRegistry.get(FileResolver.class);
            final ExecActionFactory execActionFactory = serviceRegistry.get(ExecActionFactory.class);
            final Instantiator instantiator = serviceRegistry.get(Instantiator.class);
            final BuildOperationProcessor buildOperationProcessor = serviceRegistry.get(BuildOperationProcessor.class);
            final CompilerMetaDataProviderFactory metaDataProviderFactory = serviceRegistry.get(CompilerMetaDataProviderFactory.class);
            println "fuck"
            toolChainRegistry.registerFactory(AlcorToolchainGCC.class, { String name ->
                return instantiator.newInstance(AlcorToolchainGCC.class, instantiator, name, buildOperationProcessor, OperatingSystem.current(), fileResolver, execActionFactory, metaDataProviderFactory)
            })
            println "shit"
            toolChainRegistry.registerDefaultToolChain("alcorGcc", AlcorToolchainGCC.class)
            println "asshole"
        }

        /*@Mutate
        void configureToolchains(NativeToolChainRegistry toolChains, @Path("alcor") AlcorSpec alcorSpec) {
        }*/

        /*@Mutate
        void configureAlcorBuildable(BinaryContainer binaries) {
            binaries.withType(NativeBinarySpec) {
                *//*if (it.toolChain in XToolchainGCC && !it.toolChain.isCrossCompilerPresent()) {
                    it.buildable = false
                }*//*
            }
        }*/
    }
}

/*
class XToolchain {
    static def url_base = "http://first.wpi.edu/FRC/roborio/toolchains/"

    static File download_file(Project project, String platform, String filename) {
        def dest = new File(GradleRIO_C.getGlobalDirectory(), "cache/${platform}")
        dest.mkdirs()
        return new File(dest, filename)
    }

    static File get_toolchain_extraction_dir(String platform) {
        return new File(GradleRIO_C.getGlobalDirectory(), "toolchain/${platform}").absoluteFile
    }

    static void download_xtoolchain_file(Project project, String platform, String filename) {
        def dlfile = download_file(project, platform, filename)
        if (!dlfile.exists()) {
            new URL(url_base + filename).withInputStream{ i -> dlfile.withOutputStream{ it << i }}
        }
    }
}
*/