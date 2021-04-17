package it

import java.util.stream.Collectors

class App {

    static void main(String[] args) {
        def printErr = System.err.&println
        int numberOfVersionsToKeep = 2
        if (args.size() > 0) {
            try {
                numberOfVersionsToKeep = args[0].toInteger()
            } catch (NumberFormatException ignore) {
                printErr("the number of kernel versions to keep outside of the one in use must be an int number")
                System.exit(-1)
            }
            if (numberOfVersionsToKeep < 0) {
                printErr("the number of kernel versions to keep outside of the one in use must be >= 0")
                System.exit(-1)
            }
        }

        def kernelInUseVersion = Dpkg.getKernelInUseVersion()
        println "current kernel version in use $kernelInUseVersion"

        def skipList = ['linux-headers-virtual',
                        'linux-image-virtual',
                        'linux-headers-generic-hwe-',
                        'linux-image-generic-hwe-',
                        'linux-image-generic',
                        'linux-headers-generic']

        List<Dpkg> packages = Dpkg.getInstalledKernels().stream()
                .map { new Dpkg(it) }
                .filter {
                    it.matches() && !skipList.contains(it.getPackageName()) && it.getVersion() != kernelInUseVersion
                }
                .collect(Collectors.toList())

        List<Dpkg> versionsToKeep = packages.stream()
                .map { it.getVersion() }
                .distinct()
                .sorted(Comparator.reverseOrder())
                .limit(numberOfVersionsToKeep)
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
