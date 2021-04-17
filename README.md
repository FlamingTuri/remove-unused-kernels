# Remove unused kernels

Utility to remove linux kernel images and headers.

## Prerequisites

Java 1.8 or greater.

## Running

By default, the script removes all the kernel images/headers except the one in use and the 2 most recent ones.

```bash
sudo ./gradlew run
```

To keep more/less kernel images/headers, the default value can be overridden as follows:

```bash
sudo ./gradlew clean run --args=${numberOfKernelsToKeep}
```

Examples:

```bash
# The following command only keeps the kernel image/header in use 
sudo ./gradlew clean run --args=0

# The following command keeps the kernel image/header in use and the 5 most recent ones.
sudo ./gradlew clean run --args=5
```

## License:

This project is distributed under the [GNU General Public License v3](LICENSE).

## TODO:

Add the support for other package managers outside of `dpkg`.
