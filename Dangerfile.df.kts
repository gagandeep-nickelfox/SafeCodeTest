@file:Repository("https://repo.maven.apache.org")
@file:DependsOn("org.apache.commons:commons-text:1.6")
@file:DependsOn("com.gianluz:danger-kotlin-android-lint-plugin:0.1.0")
@file:DependsOn("io.github.ackeecz:danger-kotlin-detekt:0.1.4")

import systems.danger.kotlin.*
import com.gianluz.dangerkotlin.androidlint.AndroidLint
import io.github.ackeecz.danger.detekt.DetektPlugin

import java.nio.file.Files
import java.nio.file.Paths
import java.util.function.BiPredicate
import java.util.stream.Collectors

register plugin AndroidLint
register plugin DetektPlugin

danger(args) {

    val allSourceFiles = git.modifiedFiles + git.createdFiles
    val changelogChanged = allSourceFiles.contains("CHANGELOG.md")
    val sourceChanges = allSourceFiles.firstOrNull { it.contains("src") }

    onGitHub {
        val isTrivial = pullRequest.title.contains("#trivial")

        // Changelog
        if (!isTrivial && !changelogChanged && sourceChanges != null) {
            warn("Any changes to library code should be reflected in the Changelog.\n\nPlease consider adding a note there and adhere to the [Changelog Guidelines](https://github.com/Moya/contributors/blob/master/Changelog%20Guidelines.md).")
        }

        // Big PR Check
        if ((pullRequest.additions ?: 0) - (pullRequest.deletions ?: 0) > 300) {
            warn("Big PR, try to keep changes smaller if you can")
        }

        // Work in progress check
        if (pullRequest.title.contains("WIP", false)) {
            warn("PR is classed as Work in Progress")
        }
    }

    AndroidLint.report("app/build/reports/lint-results-debug.xml")

    val detektReports = Files.find(Paths.get(""), 10, BiPredicate { path, attributes ->
        val fileName = path.toFile().name
        fileName.endsWith("detekt.xml")
    }).map { it.toFile() }.collect(Collectors.toList())

    DetektPlugin.parseAndReport(*detektReports.toTypedArray())
}