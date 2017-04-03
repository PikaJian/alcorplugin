package pika.c.gradle.alcortoolchain

import org.gradle.api.*
import org.gradle.language.base.plugins.ComponentModelBasePlugin

class ALCOR_C implements Plugin<Project> {

    void apply(Project project) {
        project.getPluginManager().apply(ComponentModelBasePlugin.class)
        project.getPluginManager().apply(AlcorToolchainPlugin.class)
    }
    // ~/.gradle
    static File getGlobalDirectory() {
        return new File("${System.getProperty('user.home')}/.gradle", "gradlerioc")
    }

}