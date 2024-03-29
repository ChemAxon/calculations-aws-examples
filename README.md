Chemaxon Calculators AWS code examples
======================================

This repository contains a **proof-of-concept** example of using Chemaxon calculators as AWS Lambda functions.

Notes:

- The current Chemaxon Java API distribution is monolithic; no subset / module selection is supported. This project
  demonstrates a way to cherry-pick
  the required jars for a limited set of functionalities. Further minimization of the cherry-picked jars is not
  implemented.

- Chemaxon proprietary functionalities require a license file (`license.cxl`), usually placed in the executing user home
  or accessed through
  network. The license file can also be read from the classpath, baking it into the AWS lambda distribution package is
  also demonstrated. Contact us
  to acquire evaluation / production license.

- The current examples provide batch processing capability but no granular error handling: in case of an error the whole
  batch is rejected. Finer
  and customizable error handling can be expected later.

**NOTE**: If you have any question, suggestion please feel free to contact us at
[`calculators-support@chemaxon.com`](mailto:calculators-support@chemaxon.com)


Prerequisites
-------------

- Chemaxon licenses for the used functionalities are available and installed. For details,
  see <https://docs.chemaxon.com/display/docs/license-installation.md>
- Access to the [Chemaxon Public Repository](https://hub.chemaxon.com). For details,
  see <https://docs.chemaxon.com/display/docs/public-repository.md>
* Java 17
* Linux shell or equivalent (WSL, Cygwin)
* Make sure that you have a Chemaxon Pass account (register [here](https://accounts.chemaxon.com/register)) and you
  acquire your Chemaxon Hub public repository API key (visit <https://accounts.chemaxon.com/settings>).
* For pretty printing JSON responses of lambda invocations Python.
* For local execution of NMR spectrum post process demo (see `nmr-result-process/`) Python3 and optionally `matplotlib`.

Use the following way to specify your Pass credentials for the build scripts:

 ``` bash
 ./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPassword=<YOUR_HUB_API_KEY> <FURTHER OPTIONS/TASKS>
 ```

Note that dependency resolution from a downloaded Jchem distribution is **not** available in this example project.


Example projects
----------------

- [Major microspecies calculation as AWS Lambda](./majorms-lambda-example/README.md) Creates cherry-picked deployment
  package for Major Microspecies.
- [Microspecies distribution calculation as AWS Lambda](./msdistr-lambda-example/README.md) Creates cherry-picked deployment
  package for Microspecies Distribution.
- [NMR calculation as AWS Lambda](./nmr-lambda-example/README.md) Creates cherry-picked deployment package for NMR.
- [Process NMR results](./nmr-result-process/README.md) Python-based example lambda to further post-process NMR
  prediction results.
- [Command line interfaces](./cli/README.md) Launch exposed calculations locally, determine jars to be cherry-picked for
  AWS Lambda deployment packages.

See subprojects for detailed lambda deployment, invocation and input/output descriptions.

TODO

Create AWS Lambda deployment package
------------------------------------

Invoke with your ChemAxon Hub credentials and with the directory containing `license.cxl` file:

``` bash
./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPassword=<YOUR_HUB_API_KEY> -PcxnLicenseDir=<LICENSE_DIR> :majorms-lambda-example:deploymentPackage
./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPassword=<YOUR_HUB_API_KEY> -PcxnLicenseDir=<LICENSE_DIR> :nmr-lambda-example:deploymentPackage
```

Make sure the referenced `license.cxl` has proper read permissions (call `chmod 664 <LICENSE_DIR>/license.cxl`).

Deployment packages are created
in `majorms-lambda-example/build/distributions/majorms-lambda-example-<VERSION>-deployment-package.zip` and
in `nmr-lambda-example/build/distributions/nmr-lambda-example-<VERSION>-deployment-package.zip`. Deployment
packages will contain only the cherry picked dependencies specified in file `includes-majorms.txt`
and `includes-nmr.txt`. Deployment package size are
around 11 MB and 30 MB.

See [`majorms-lambda-example/README.md`](majorms-lambda-example/README.md)
and [`nmr-lambda-example/README.md`](nmr-lambda-example/README.md)
for details.


Create launcher scripts for local execution
-------------------------------------------

Invoke with your ChemAxon Hub credentials:

``` bash
./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> :cli:createScripts
```

Launcher scripts are generated into `cli/build/scripts/`. Launch them:

``` bash
# print command line help
./cli/build/scripts/run-majorms -h
./cli/build/scripts/run-nmr -h

# launch calculations for SMILES input
# note that gzipped input is recognized
./cli/build/scripts/run-majorms \
    -in molecules.smi.gz \
    -write-times true \
    -out out.txt \
    -jsonl-out out.jsonl

./cli/build/scripts/run-nmr \
    -in molecules.smi.gz \
    -write-times true \
    -out out.txt \
    -sdf-out out.sdf \
    -jsonl-out out.jsonl \
    -max-count 5
```

See [`cli/README.md`](cli/README.md) for details on further use cases and on creating dependency cherry picking lists.



Licensing
---------

The content of this project (this git repository) is distributed under the Apache License 2.0. Some dependencies of this
project are **ChemAxon proprietary products** which are **not** covered by this license.
Please note that unauthorized redistribution of ChemAxon proprietary products and your license file (`license.cxl`) is
not allowed.
