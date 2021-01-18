CLI for majorms lambda
======================


Command line interface to invoke majorms lambda calculation locally. This project is used to collect dependencies to be included in the
deployment package.


Launcher scripts
----------------

Launcher scripts provide a quick, convenient command line entry point for the functionality exposed by subproject `majorms-lambda-example`. Scripts 
use all declared dependencies with **no** cherry picking, referring them directly from Gradle cache. The created scripts are **not** portable.

Invoke with your ChemAxon Hub credentials and with the directory containing `license.cxl` file:

``` bash
./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> :majorms-cli:createScripts
```

Launcher scripts will be generated into `majorms-cli/build/scripts`. Invocation examples:

``` bash
# print command line help
./majorms-cli/build/scripts/run-majorms -h

# launch calculation for SMILES input
# note that gzipped input is recognized
./majorms-cli/build/scripts/run-majorms -in molecules.smi.gz -write-times true -out out.txt

# launch calculation for SMILES input
# This script passes JVM option `-verbose:class` to print loaded classes to standard output
./majorms-cli/build/scripts/run-majorms-vc -in molecules.smi.gz -write-times true -out out.txt

```


Create cherry pick list
-----------------------

To create cherry pick list for dependencies we instruct the JVM to print all loaded classes and collect the affected jar files. System libraries
(`rt.jar`, `jsse.jar`) and the library created by `majorms-lambda-example` subproject are excluded.

``` bash
./majorms-cli/build/scripts/run-majorms-vc \
    -in molecules.smi.gz \
    -write-times true \
    -out out.txt | \
    \
    # Keep only interested jars \
    grep "\\.jar" | \
    grep -v "rt\\.jar" | \
    grep -v "jsse\\.jar" | \
    grep -v "majorms-lambda-example-.*\\.jar" | \
    \
    # extract jar names and make unique list
    sed -e "s/^.*\///" | sed -e "s/]$//" | sort -u > new-includes.txt
```

The above command chain will create a `new-includes.txt` file. To use it by the build tools overwrite the contents of `includes.txt`.


Distribution task
-----------------

A simple Gradle `distribution` plugin based task is present which collects all dependencies and compiled jars into a `lib/` folder and creates a
pathing har `classpath.jar`. This task use no cherry picking. 

``` bash
./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> :majorms-cli:installDist
```

The resulting `majorms-cli/build/install/majorms-cli/lib/` is portable. To launch the command line entry point use:

``` bash
java -cp majorms-cli/build/install/majorms-cli/lib/classpath.jar com.chemaxon.calculations.cli.MajorMsCli -h
```


