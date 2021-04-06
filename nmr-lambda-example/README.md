NMR as AWS Lambda
=================


This subproject defines a lightweight wrapper for ChemAxon major NMR calculator to be used in AWS Lambda. Apart from the implementation of
interface `com.amazonaws.services.lambda.runtime.RequestHandler` there is no further dependency to AWS specific services, so the wrapper can be used
from simple Java SE projects.

See [`README.md`](../README.md) in root project for notes on dependency management and prerequisites.


Create deployment package
-------------------------

Invoke with your ChemAxon Hub credentials and with the directory containing `license.cxl` file:

``` bash
./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> -PcxnLicenseDir=<LICENSE_DIR> :nmr-lambda-example:deploymentPackage
```

Make sure the referenced `license.cxl` has proper read permissions (call `chmod 664 <LICENSE_DIR>/license.cxl`).

Deployment package is created in `nmr-lambda-example/build/distributions/nmr-lambda-example-<VERSION>-deployment-package.zip`. Deployment
package will contain only the cherry picked dependencies specified in file `includes-nmr.txt`. Deployment package size is around 27 MB.

Please note that when the license file expires, the deployed function will stop working with a `chemaxon.license.LicenseException`. In this case a 
fresh deployment built with a valid license file is required.


Deployment
----------

  - Use `Java 8` runtime
  - Use `com.chemaxon.calculations.lambda.NmrCalculator::handleRequest` handler
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
  --function-name calc-nmr-example \
  --role "${LAMBDAROLE}" \
  --runtime java8 \
  --handler "com.chemaxon.calculations.lambda.NmrCalculator::handleRequest" \
  --timeout 60 \
  --memory-size 512 \
  --publish \
  --zip-file fileb://nmr-lambda-example/build/distributions/nmr-lambda-example-0.0.1-deployment-package.zip
```

To delete the created function use (see <https://awscli.amazonaws.com/v2/documentation/api/latest/reference/lambda/delete-function.html>):

``` bash
aws lambda delete-function \
  --function-name calc-nmr-example
```

Test
----


See 

 - [`com.chemaxon.calculations.lambda.NmrRequest`](src/main/java/com/chemaxon/calculations/lambda/NmrRequest.java) for details on request format. 
 - [`com.chemaxon.calculations.lambda.NmrResponse`](src/main/java/com/chemaxon/calculations/lambda/NmrResponse.java) and 
   [`com.chemaxon.calculations.lambda.NmrResult`](src/main/java/com/chemaxon/calculations/lambda/NmrResult.java) for details on response format. 


As an example you can use the following test data from Lambda console:

``` json
{
  "structureSources": [
    "CC(=O)OC1=CC=CC=C1C(O)=O"
  ],
  "format" : "smiles"
}
```

### Using `aws` CLI

See <https://awscli.amazonaws.com/v2/documentation/api/latest/reference/lambda/invoke.html> for details.

``` bash
aws lambda invoke \
   --function-name calc-nmr-example \
   --payload '{"structureSources":["CC(=O)OC1=CC=CC=C1C(O)=O"],"format":"smiles"}' \
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
            "cnmrResult": [
                {
                    "atomIndex": 0,
                    "bondCount": 4,
                    "hCount": 3,
                    "shift": 20.947499999999998
                },
                {
                    "atomIndex": 1,
                    "bondCount": 3,
                    "hCount": 0,
                    "shift": 168.35333333333332
                },
                {
                    "atomIndex": 4,
                    "bondCount": 3,
                    "hCount": 0,
                    "shift": 150.99333333333334
                },
                {
                    "atomIndex": 5,
                    "bondCount": 3,
                    "hCount": 1,
                    "shift": 123.27
                },
                {
                    "atomIndex": 6,
                    "bondCount": 3,
                    "hCount": 1,
                    "shift": 134.33333333333334
                },
                {
                    "atomIndex": 7,
                    "bondCount": 3,
                    "hCount": 1,
                    "shift": 125.95666666666666
                },
                {
                    "atomIndex": 8,
                    "bondCount": 3,
                    "hCount": 1,
                    "shift": 132.13666666666666
                },
                {
                    "atomIndex": 9,
                    "bondCount": 3,
                    "hCount": 0,
                    "shift": 122.85333333333334
                },
                {
                    "atomIndex": 10,
                    "bondCount": 3,
                    "hCount": 0,
                    "shift": 169.70000000000002
                }
            ],
            "format": "sdf",
            "hnmrResult": [
                {
                    "atomIndex": 13,
                    "attachedAtomIndex": 0,
                    "attachedAtomNumber": 6,
                    "shift": 2.3310000000000004
                },
                {
                    "atomIndex": 14,
                    "attachedAtomIndex": 0,
                    "attachedAtomNumber": 6,
                    "shift": 2.3310000000000004
                },
                {
                    "atomIndex": 15,
                    "attachedAtomIndex": 0,
                    "attachedAtomNumber": 6,
                    "shift": 2.3310000000000004
                },
                {
                    "atomIndex": 16,
                    "attachedAtomIndex": 5,
                    "attachedAtomNumber": 6,
                    "shift": 7.099
                },
                {
                    "atomIndex": 17,
                    "attachedAtomIndex": 6,
                    "attachedAtomNumber": 6,
                    "shift": 7.532
                },
                {
                    "atomIndex": 18,
                    "attachedAtomIndex": 7,
                    "attachedAtomNumber": 6,
                    "shift": 7.19
                },
                {
                    "atomIndex": 19,
                    "attachedAtomIndex": 8,
                    "attachedAtomNumber": 6,
                    "shift": 8.133
                },
                {
                    "atomIndex": 20,
                    "attachedAtomIndex": 11,
                    "attachedAtomNumber": 8,
                    "shift": 11.0
                }
            ],
            "molecule": "\n  Mrv2021 04062121042D          \n\n 21 21  0  0  0  0            999 V2000\n    1.4289    3.3000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    1.4289    2.4750    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    2.1434    2.0625    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n    0.7145    2.0625    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n    0.7145    1.2375    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    1.4289    0.8250    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    1.4289   -0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    0.7145   -0.4125    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    0.0000    0.0000    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    0.0000    0.8250    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   -0.7145    1.2375    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   -0.7145    2.0625    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   -1.4289    0.8250    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n    2.2539    3.3000    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n    1.4289    4.1250    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n    0.6039    3.3000    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n    2.1434    1.2375    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n    2.1434   -0.4125    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n    0.7145   -1.2375    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n   -0.7145   -0.4125    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n    0.0000    2.4750    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n  1  2  1  0  0  0  0\n  2  3  2  0  0  0  0\n  2  4  1  0  0  0  0\n  4  5  1  0  0  0  0\n  5  6  4  0  0  0  0\n  6  7  4  0  0  0  0\n  7  8  4  0  0  0  0\n  8  9  4  0  0  0  0\n  9 10  4  0  0  0  0\n  5 10  4  0  0  0  0\n 10 11  1  0  0  0  0\n 11 12  1  0  0  0  0\n 11 13  2  0  0  0  0\n  1 14  1  0  0  0  0\n  1 15  1  0  0  0  0\n  1 16  1  0  0  0  0\n  6 17  1  0  0  0  0\n  7 18  1  0  0  0  0\n  8 19  1  0  0  0  0\n  9 20  1  0  0  0  0\n 12 21  1  0  0  0  0\nM  END\n$$$$\n"
        }
    ]
}
```


Notes on capacity planning
--------------------------

Single threaded execution on an Intel(R) Xeon(R) X5677 CPU for a single molecule takes around 3000ms with 128-256 MB of 
memory. 

Since 1M requests cost 0.2 $ it is recommended to amortize request cost between multiple calculations (1 request costs the same as ~ 12 GB ms
or ~ 100 ms execution time with 128 MB memory).

See [Lambda quotas](https://docs.aws.amazon.com/lambda/latest/dg/gettingstarted-limits.html) for further limits:
    
Network bandwidth should be investigated. Even a ~20mbit measured network bandwith was mentioned. If we consider 3000ms execution time per structure
this translates to 60Mbit cap per structure which is well suitable for the typical SDF (few kbyte per structrue) sizes.



