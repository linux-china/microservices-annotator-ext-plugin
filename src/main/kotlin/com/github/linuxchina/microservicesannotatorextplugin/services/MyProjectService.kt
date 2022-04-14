package com.github.linuxchina.microservicesannotatorextplugin.services

import com.intellij.openapi.project.Project
import com.github.linuxchina.microservicesannotatorextplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
