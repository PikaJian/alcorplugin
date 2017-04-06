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
import org.gradle.nativeplatform.toolchain.GccCompatibleToolChain

public interface AlcorToolChain extends GccCompatibleToolChain { }


public class AlcorToolchainGCC extends GccToolChain implements AlcorToolChain {
    public static final String DEFAULT_NAME = "Alcor";
    public AlcorToolchainGCC(Instantiator instantiator, String name, BuildOperationProcessor buildOperationProcessor, OperatingSystem operatingSystem, FileResolver fileResolver, ExecActionFactory execActionFactory, CompilerOutputFileNamingSchemeFactory compilerOutputFileNamingSchemeFactory, CompilerMetaDataProviderFactory metaDataProviderFactory) {
        super(instantiator, name, buildOperationProcessor, operatingSystem, fileResolver, execActionFactory, compilerOutputFileNamingSchemeFactory, metaDataProviderFactory)


        
        target("nds32", new Action<GccPlatformToolChain>() {
            @Override
            public void execute(GccPlatformToolChain target) {
                String gccPrefix = "nds32le-elf-";
                String gccSuffix = OperatingSystem.current().isWindows() ? ".exe" : ""
                def common_flag = ['-O1', '-Wall', '-fmessage-length=0']
                def common_flag_d = ['-g', '-O1', '-g3', '-Wall', '-fmessage-length=0']


                def link_flags = ['-O0', '-fno-builtin', '-mrelax', '-Wl,--gc-sections',
                            '-nostartfiles', '-static', '-TAU3510_LD_V12_ExCode_20120530.x']

                target.cCompiler.executable =           gccPrefix + "gcc" + gccSuffix;
                target.cppCompiler.executable =         gccPrefix + "g++" + gccSuffix;
                target.linker.executable =              gccPrefix + "gcc" + gccSuffix;
                target.assembler.executable =           gccPrefix + "as"  + gccSuffix;
                target.staticLibArchiver.executable =   gccPrefix + "ar"  + gccSuffix;
                target.cCompiler.withArguments      { a -> a << "-DALCOR" }   // Define the 'ROBORIO' macro
                target.cppCompiler.withArguments    { a -> a << "-DALCOR" }   // Define the 'ROBORIO' macro
                target.cCompiler.withArguments { args ->
                    args.addAll(common_flag)
                    args.addAll("-c")
                    args.remove "-c"
                    args.remove "-x"
                    args.remove "c"
                }
                target.assembler.withArguments { args ->
                    args.addAll(common_flag)
                }
                target.staticLibArchiver.withArguments { args ->
                    args.add(0, "-qcs")
                    args.remove "-rcs"
                }
                target.linker.withArguments { args ->
                    args.addAll(link_flags)
                }
            }
        })
        

        //def bindir = ".gradle/nds32le-elf-newlib-v2/bin"
        //path(bindir)
    }

/*
    public boolean isCrossCompilerPresent() {
        String gccSuffix = OperatingSystem.current().isWindows() ? ".exe" : ""
        return new File(AlcorToolchainPlugin.getActiveToolchain().get_toolchain_root(), "bin/nds32le-elf-gcc${gccSuffix}").exists()
    }
*/

    @Override
    protected String getTypeName() {
        return "Alcor ToolChain";
    }

    @Override
    protected void initForImplementation(DefaultGccPlatformToolChain platformToolChain, GccVersionResult versionResult) {
        platformToolChain.setCanUseCommandFile(false);
    }
}
