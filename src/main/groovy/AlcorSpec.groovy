import org.gradle.model.Managed

@Managed
public interface AlcorSpec {
    String getCppVersion()
    void setCppVersion(String cppVersion)

    boolean getDebugInfo()
    void setDebugInfo(boolean debug)
}
