@file:DependsOn("org.apache.commons:commons-text:1.6")
@file:DependsOn("com.gianluz:danger-kotlin-android-lint-plugin:0.1.0")

import systems.danger.kotlin.*
import org.apache.commons.text.WordUtils
import com.gianluz.dangerkotlin.androidlint.AndroidLint

register plugin AndroidLint

danger(args) {

    onGitHub {

        // Big PR Check
        if ((pullRequest.additions ?: 0) - (pullRequest.deletions ?: 0) > 10) {
            warn("Big PR, try to keep changes smaller if you can")
        }

    }
    AndroidLint.report("app/build/reports/lint-results-debug.xml")

}

