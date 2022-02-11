Microspecies distribution calculation as AWS Lambda
============================================


This subproject defines a lightweight wrapper for ChemAxon microspecies distribution calculator to be used in AWS Lambda. Apart from the implementation of
interface `com.amazonaws.services.lambda.runtime.RequestHandler` there is no further dependency to AWS specific services, so the wrapper can be used
from simple Java SE projects.

See [`README.md`](../README.md) in root project for notes on dependency management and prerequisites.


Create deployment package
-------------------------

Invoke with your ChemAxon Hub credentials and with the directory containing `license.cxl` file:

``` bash
./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> -PcxnLicenseDir=<LICENSE_DIR> :msdistr-lambda-example:deploymentPackage
```

Make sure the referenced `license.cxl` has proper read permissions (call `chmod 664 <LICENSE_DIR>/license.cxl`).

Deployment package is created in `msdistr-lambda-example/build/distributions/msdistr-lambda-example-<VERSION>-deployment-package.zip`. Deployment
package will contain only the cherry picked dependencies specified in file `includes-msdistr.txt`. Deployment package size is around 8.1 MB.

Please note that when the license file expires, the deployed function will stop working with a `chemaxon.license.LicenseException`. In this case a 
fresh deployment built with a valid license file is required.


Deployment
----------

  - Use `Java 8` runtime
  - Use `com.chemaxon.calculations.lambda.MsDistrCalculator::handleRequest` handler
  - Use `512 MB` memory (note that memory usage can be decreased, needs further profiling)

No further settings, environmental variables, etc required.

### Using `aws` CLI

See <https://awscli.amazonaws.com/v2/documentation/api/latest/reference/lambda/create-function.html> for details.
See <https://docs.aws.amazon.com/lambda/latest/dg/lambda-intro-execution-role.html> on details on role to be used. Default recommended 
`AWSLambdaBasicExecutionRole` (AWS managed policy) is sufficient for the role below.


``` bash
# Refer your appropriate role below, see details above
export LAMBDAROLE=<ROLE_TO_BE_USED_ARN>

aws lambda create-function \
  --function-name calc-msdistr-example \
  --role "${LAMBDAROLE}" \
  --runtime java8 \
  --handler "com.chemaxon.calculations.lambda.MsDistrCalculator::handleRequest" \
  --timeout 60 \
  --memory-size 512 \
  --publish \
  --zip-file fileb://msdistr-lambda-example/build/distributions/msdistr-lambda-example-0.0.3-deployment-package.zip
```

To delete the created function use (see <https://awscli.amazonaws.com/v2/documentation/api/latest/reference/lambda/delete-function.html>):

``` bash
aws lambda delete-function \
  --function-name calc-msdistr-example
```

Test
----


See 

 - [`com.chemaxon.calculations.lambda.MsDistrRequest`](src/main/java/com/chemaxon/calculations/lambda/MsDistrRequest.java) for details on request 
   format.
 - [`com.chemaxon.calculations.lambda.MsDistrResponse`](src/main/java/com/chemaxon/calculations/lambda/MsDistrResponse.java) for details on response 
   format.

As an example you can use the following test data from Lambda console:

``` json
{
    "smiles": [
        "CC(=O)OC1=CC=CC=C1C(O)=O",
        "CC(=O)NC1=CC=C(O)C=C1"
    ],
    "pH": 7.4,
    "tautomerize": false,
    "temperature": 298.0
}
```

### Using `aws` CLI

See <https://awscli.amazonaws.com/v2/documentation/api/latest/reference/lambda/invoke.html> for details.

``` bash
aws lambda invoke \
   --function-name calc-msdistr-example \
   --payload '{"smiles":["CC(=O)OC1=CC=CC=C1C(O)=O","CC(=O)NC1=CC=C(O)C=C1"],"pH":[7.4],"tautomerize":false,"temperature":298.0}' \
   --cli-binary-format raw-in-base64-out \
   response.json

# Pretty print response
cat response.json | python -m json.tool

```

The response printed:

``` json
{
    "results": [
        {
            "input": "CC(=O)OC1=CC=CC=C1C(O)=O",
            "pH": 7.4,
            "tautomerize": false,
            "microspecies": [
                {
                    "distribution": 0.01034556647539921,
                    "ms": "CC(=O)Oc1ccccc1C(O)=O"
                },
                {
                    "distribution": 99.98965443352152,
                    "ms": "CC(=O)Oc1ccccc1C([O-])=O"
                }
            ]
        },
        {
            "input": "CC(=O)NC1=CC=C(O)C=C1",
            "pH": 7.4,
            "tautomerize": false,
            "microspecies": [
                {
                    "distribution": 99.14512041830184,
                    "ms": "CC(=O)Nc1ccc(O)cc1"
                },
                {
                    "distribution": 0.8548772059523501,
                    "ms": "CC(=O)Nc1ccc([O-])cc1"
                }
            ]
        }
    ]
}
```


Notes on capacity planning
--------------------------

Single threaded execution on an Intel(R) Xeon(R) X5677 CPU for a single molecule takes around 10-15ms with 128-256 MB of 
memory. Execution for a 1M molecules costs (according to <https://aws.amazon.com/lambda/pricing/>) 0.0208 $ to 0.0625 $ if CPU speeds matches.

Since 1M requests cost 0.2 $ it is recommended to amortize request cost between multiple calculations.

See [Lambda quotas](https://docs.aws.amazon.com/lambda/latest/dg/gettingstarted-limits.html) for further limits:
    
Network bandwidth should be investigated. Even a ~20mbit measured network bandwith was mentioned. If we consider 10ms execution time per structure
this translates to 200kbit cap per structure which is well suitable for the typical smiles (<100 byte per structrue) sizes.


