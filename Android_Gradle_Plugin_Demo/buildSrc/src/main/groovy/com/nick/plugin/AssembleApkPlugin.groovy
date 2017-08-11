package com.nick.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 来自:
 * http://blog.csdn.net/jo__yang/article/details/51746484#
 *
 * // 运行gradlew testPlugin 会看到Hello gradle plugin 打印信息
 */
class AssembleApkPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        //create 方法的第一个参数就是你在 build.gradle 文件中的进行参数配置的 dsl 的名字，必须一致；第二个参数，就是参数类的名字。
        project.extensions.create('pluginArgs', AssembleApkExtension);
        project.task('testPlugin') << {
            // println "Hello gradle plugin"
            println project.pluginArgs.message;
        }
    }
}