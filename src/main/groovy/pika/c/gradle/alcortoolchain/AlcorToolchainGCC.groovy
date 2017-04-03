package pika.c.gradle.alcortoolchain

import org.gradle.api.*
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.os.OperatingSystem
import org.gradle.nativeplatform.toolchain.internal.gcc.*
import org.gradle.nativeplatform.toolchain.internal.*
import org.gradle.process.internal.ExecActionFactory
import org.gradle.nativeplatform.toolchain.internal.gcc.version.CompilerMetaDataProviderFactory
import org.gradle.internal.operations.BuildOperationProcessor
import org.gradle.internal.reflect.Instantiator
import org.gradle.nativeplatform.toolchain.GccPlatformToolChain
import org.gradle.nativeplatform.toolchain.internal.gcc.version.GccVersionResult;
import org.gradle.nativeplatform.internal.CompilerOutputFileNamingSchemeFactory;

public class AlcorToolchainGCC extends GccToolChain {
    public static final String DEFAULT_NAME = "Gcc";
    public AlcorToolchainGCC(Instantiator instantiator, String name, BuildOperationProcessor buildOperationProcessor, OperatingSystem operatingSystem, FileResolver fileResolver, ExecActionFactory execActionFactory, CompilerOutputFileNamingSchemeFactory compilerOutputFileNamingSchemeFactory, CompilerMetaDataProviderFactory metaDataProviderFactory) {
        super(instantiator, name, buildOperationProcessor, operatingSystem, fileResolver, execActionFactory, compilerOutputFileNamingSchemeFactory, metaDataProviderFactory)

        target("nds32", new Action<GccPlatformToolChain>() {
            @Override
            public void execute(GccPlatformToolChain target) {
                String gccPrefix = "nds32le-elf-";
                String gccSuffix = OperatingSystem.current().isWindows() ? ".exe" : ""
                target.cCompiler.executable =           gccPrefix + "gcc" + gccSuffix;
                target.cCompiler.withArguments      { a -> a << "-DALCOR" }   // Define the 'ROBORIO' macro
                target.cppCompiler.executable =         gccPrefix + "g++" + gccSuffix;
                target.cppCompiler.withArguments    { a -> a << "-DALCOR" }   // Define the 'ROBORIO' macro
                target.linker.executable =              gccPrefix + "gcc" + gccSuffix;
                target.assembler.executable =           gccPrefix + "as"  + gccSuffix;
                target.staticLibArchiver.executable =   gccPrefix + "ar"  + gccSuffix;
            }
        })

        def bindir = ".gradle/nds32le-elf-newlib-v2/bin"
        path(bindir)
    }

/*
    public boolean isCrossCompilerPresent() {
        String gccSuffix = OperatingSystem.current().isWindows() ? ".exe" : ""
        return new File(AlcorToolchainPlugin.getActiveToolchain().get_toolchain_root(), "bin/nds32le-elf-gcc${gccSuffix}").exists()
    }
*/

    @Override
    protected String getTypeName() {
        return "AlcorNds32Gcc";
    }

    @Override
    protected void initForImplementation(DefaultGccPlatformToolChain platformToolChain, GccVersionResult versionResult) {
        platformToolChain.setCanUseCommandFile(false);
    }
}