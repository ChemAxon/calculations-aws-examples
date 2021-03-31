ChemAxon Calculators AWS code examples
======================================


This repository contains a **proof of concept** example of using ChemAxon calculators as AWS Lambda functions.

Notes:

 - The current ChemAxon Java API distribution is a monolithic; no subset / module selection    is supported. This project demonstrates a way to cherry
   pick the required jars for a limited set of functionalities. Further minimization of the cherry picked jars is not implemented.

 - ChemAxon proprietary functionalities require a license file (`license.cxl`), usually placed in the executing    user home or accessed through a 
   network. The license file can also be read from the classpath, baking it into the AWS lambda distribution package is also demonstrated. Contact us
   to acquire evaluation / production license.
   
 - The current examples provide batch processing capability but no granular error handling: in case of an error the whole batch is rejected. Finer
   and customizable error handling can be expected later.

**NOTE**: If you have any question, suggestion please feel free to contact us at 
[`calculators-support@chemaxon.com`](mailto:calculators-support@chemaxon.com)


Prerequisites
-------------
 
  * Make sure that valid ChemAxon licenses (evaluation or production) for the calculators you intend to use are available and 
    [installed](https://docs.chemaxon.com/Installing+Licenses).
  * Java 8
  * Linux shell or equivalent (WSL, Cygwin)
  * Make sure that you have a Chemaxon Pass account (register [here](https://accounts.chemaxon.com/register)) and you acquire your ChemAxon Hub public
    repository API key (visit <https://accounts.chemaxon.com/settings>). 


Dependencies
------------

ChemAxon proprietary dependencies are available from [ChemAxon public repository (hub)](https://docs.chemaxon.com/display/docs/Public+Repository). 
Make sure your [ChemAxon pass](https://pass.chemaxon.com/login) email address is available and you acquire Public Repository API key from 
<https://accounts.chemaxon.com/settings>. You can register a Pass acount [here](https://accounts.chemaxon.com/register). Use the following way
to specify your Pass credentials for the build scripts:

 ``` bash
 ./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> <FURTHER OPTIONS/TASKS>
 ```

Note that dependency resolution for ChemAxon internal environment (Gluon) is also available in the build scripts; it is for ChemAxon employees only.
Note that dependency resolution from a downloaded Jchem distribution is **not** available in this example project.

Version of ChemAxon proprietary dependencies (for both environments) are specified in file `gradle.properties`.


Example projects
----------------

- [Major microspecies calculation as AWS Lambda](./majorms-lambda-example/README.md) Creates cherry picked deployment package for Major Microspecies
- [NMR calculation as AWS Lambda](./nmr-lambda-example/README.md) Creates cherry picked deployment package for NMR
- [Command line interfaces](./cli/README.md) Launch exposed calculations locally, determine jars to be cherry picked for AWS Lambda deployment 
  packages



Create AWS Lambda deployment package
------------------------------------

Invoke with your ChemAxon Hub credentials and with the directory containing `license.cxl` file:

``` bash
./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> -PcxnLicenseDir=<LICENSE_DIR> :majorms-lambda-example:deploymentPackage
```

Make sure the referenced `license.cxl` has proper read permissions (call `chmod 664 <LICENSE_DIR>/license.cxl`).

Deployment package is created in `majorms-lambda-example/build/distributions/majorms-lambda-example-<VERSION>-deployment-package.zip`. Deployment
package will contain only the cherry picked dependencies specified in file `includes.txt`. Deployment package size is around 8.1 MB.

See `majorms-lambda-example/README.md` for details.


Create launcher scripts for local execution
-------------------------------------------

Invoke with your ChemAxon Hub credentials:

``` bash
./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> :majorms-cli:createScripts
```

Launcher scripts are generated into `majorms-cli/build/scripts/`. Launch them:

``` bash
# print command line help
./majorms-cli/build/scripts/run-majorms -h

# launch calculation for SMILES input
./majorms-cli/build/scripts/run-majorms -in molecules.smi.gz -write-times true -out out.txt
```

See `majorms-cli/README.md` for details on further use cases and on creating dependency cherry picking list.


Licensing
---------

The content of this project (this git repository) is distributed under the Apache License 2.0. Some dependencies of this
project are **ChemAxon proprietary products** which are **not** covered by this license.
Please note that unauthorized redistribution of ChemAxon proprietary products and your license file (`license.cxl`) is not allowed.
