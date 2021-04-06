CLI for majorms lambda / nmr lambda
===================================


Command line interface to invoke majorms / nmr lambda calculation locally. This project is used to collect dependencies
to be included in the deployment package.


Launcher scripts
----------------

Launcher scripts provide a quick, convenient command line entry point for the functionality exposed by subprojects `majorms-lambda-example`
and `nmr-lambda-examples`. Scripts 
use all declared dependencies with **no** cherry picking, referring them directly from Gradle cache. The created scripts are **not** portable.

Invoke with your ChemAxon Hub credentials:

``` bash
./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> :cli:createScripts
```

Launcher scripts will be generated into `cli/build/scripts`. Invocation examples:

``` bash
# print command line help
./cli/build/scripts/run-majorms -h
./cli/build/scripts/run-nmr -h

# launch calculation for SMILES input
# note that gzipped input is recognized
./cli/build/scripts/run-majorms \
    -in molecules.smi.gz \
    -write-times true \
    -out out.txt \
    -jsonl-out out.jsonl

./cli/build/scripts/run-nmr \
    -in molecules.smi.gz \
    -in-format smiles \
    -write-times true \
    -out out.txt \
    -sdf-out out.sdf \
    -jsonl-out out.jsonl \
    -max-count 5

# launch calculation for SMILES input
# This script passes JVM option `-verbose:class` to print loaded classes to standard output
./cli/build/scripts/run-majorms-vc -in molecules.smi.gz -write-times true -out out.txt
./cli/build/scripts/run-nmr-vc -in molecules.smi.gz -in-format smiles -write-times true -out out.txt -sdf-out out.sdf

```


Create cherry pick list
-----------------------

The AWS Lambda deployment package creators use 
To create cherry pick list for dependencies we instruct the JVM to print all loaded classes and collect the affected jar files. System libraries
(`rt.jar`, `jsse.jar`) and the libraries created by `majorms-lambda-example` and `nmr-lambda-example` subprojects are excluded.

``` bash
# Collect cherry pick list for majorms calculation
# Note that we do not write JSONL output in order to avoid GSON library to be in the result list
./cli/build/scripts/run-majorms-vc \
    -in molecules.smi.gz \
    -write-times true \
    -out out.txt | \
    \
    `# Keep only interested jars, exclude system and jars created by this project` \
    grep "\\.jar" | \
    grep -v "rt\\.jar" | \
    grep -v "jsse\\.jar" | \
    grep -v "majorms-lambda-example-.*\\.jar" | \
    grep -v "nmr-lambda-example-.*\\.jar" | \
    \
    `# extract jar names and make unique list` \
    sed -e "s/^.*\///" | sed -e "s/]$//" | sort -u > new-includes-majorms.txt


# Collect cherry pick list for nmr calculation
# Note that we exercise SMILES importer (and ensure to be in the result list) by using SMILES input format
# Note that we do not write JSONL output in order to avoid GSON library to be in the result list
./cli/build/scripts/run-nmr-vc \
    -in molecules.smi.gz \
    -in-format smiles \
    -write-times true \
    -out out.txt \
    -sdf-out out.sdf \
    -max-count 5 | \
    \
    `# Keep only interested jars, exclude system and jars created by this project` \
    grep "\\.jar" | \
    grep -v "rt\\.jar" | \
    grep -v "jsse\\.jar" | \
    grep -v "majorms-lambda-example-.*\\.jar" | \
    grep -v "nmr-lambda-example-.*\\.jar" | \
    \
    `# extract jar names and make unique list` \
    sed -e "s/^.*\///" | sed -e "s/]$//" | sort -u > new-includes-nmr.txt

```

The above command chain will create a `new-includes-majorms.txt` file. To use it by the build tools overwrite the contents of `includes-majorms.txt`. 
Note that 
inline comments in the multiline command above use Bash's backtick command substitution (see details of this technique 
[here](https://stackoverflow.com/a/12797512)).


Distribution task
-----------------

A simple Gradle `distribution` plugin based task is present which collects all dependencies and compiled jars into a `lib/` folder and creates a
pathing har `classpath.jar`. This task use no cherry picking. 

``` bash
./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> :cli:installDist
```

The resulting `cli/build/install/cli/lib/` is portable. To launch the command line entry points use:

``` bash
java -cp cli/build/install/cli/lib/classpath.jar com.chemaxon.calculations.cli.MajorMsCli -h
java -cp cli/build/install/cli/lib/classpath.jar com.chemaxon.calculations.cli.NmrCli -h
```


