package it

import java.util.stream.Collectors

class App {

    static String getKernelInUse() {
        "uname -r".execute().text.trim()
    }

    static String getKernelInUseVersion() {
        def matcher = getKernelInUse() =~ /^(\S*?)(-generic)?$/
        matcher.matches() ? matcher[0][1] : null
    }

    static void main(String[] args) {
        def kernelInUseVersion = getKernelInUseVersion()
        println "current kernel version in use $kernelInUseVersion"

        def skipList = ['linux-headers-virtual',
                        'linux-image-virtual',
                        'linux-headers-generic-hwe-',
                        'linux-image-generic-hwe-',
                        'linux-image-generic',
                        'linux-headers-generic']

        def out = 'dpkg --list'.execute() | 'egrep -i linux-(image|headers)'.execute()
        out.waitFor()

        List<Dpkg> packages = out.text.split('\n').toList().stream()
                .map { new Dpkg(it) }
                .filter {
                    it.matches() && !skipList.contains(it.getPackageName()) && it.getVersion() != kernelInUseVersion
                }
                .collect(Collectors.toList())

        List<Dpkg> versionsToKeep = packages.stream()
                .map { it.getVersion() }
                .distinct()
                .sorted(Comparator.reverseOrder())
                .limit(2)
                .collect(Collectors.toList())

        def getText = { String text -> versionsToKeep.isEmpty() ? '' : text }

        println "kernel version${getText('s')} to keep " +
                "$kernelInUseVersion${getText(',')} ${versionsToKeep.join(', ')}"

        packages.stream()
                .filter { !versionsToKeep.contains(it.getVersion()) }
                .map { it.getRemoveCommand() }
                .map { it.execute() }
                .forEach { it.waitForProcessOutput(System.out, System.err) }
    }
}
