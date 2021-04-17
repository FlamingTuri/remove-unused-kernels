package it

import java.util.regex.Matcher

class Dpkg {

    private Matcher matcher

    Dpkg(String dpkgOutputLine) {
        matcher = dpkgOutputLine =~ /ii\s\s(linux-(image|headers)-((\S*?)(-generic)?))(\s+.*)/
    }

    boolean matches() {
        matcher.matches()
    }

    String getPackageName() {
        matches() ? matcher[0][1] : null
    }

    String getVersion() {
        matcher[0][4]
    }

    String getRemoveCommand() {
        matches() ? "sudo apt-get -y purge ${getPackageName()}" : ''
    }

    static String getKernelInUse() {
        "uname -r".execute().text.trim()
    }

    static String getKernelInUseVersion(String kernelInUse = getKernelInUse()) {
        def matcher = kernelInUse =~ /^(\S*?)(-generic)?$/
        matcher.matches() ? matcher[0][1] : null
    }

    static List<String> getInstalledKernels() {
        def out = 'dpkg --list'.execute() | 'egrep -i linux-(image|headers)'.execute()
        out.waitFor()
        out.text.split('\n').toList()
    }
}
