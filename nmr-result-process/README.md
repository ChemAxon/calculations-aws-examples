Process NMR results
===================

This is a proof of concept lambda written in Python to process NMR calculation results. The main goal of this
exercise is to demonstrate the interoperability of lamdas using different platforms.

Requirements
------------

Python3 with `Matplotlib` is required. On Ubuntu follow <https://phoenixnap.com/kb/how-to-install-python-3-ubuntu> and 
<https://matplotlib.org/3.3.3/users/installing.html>.

Fucntionality
-------------

 - `src/process-nmr-result-to-text.py`: convert NMR spectrum result to custom text 

Local execution from result JSON and AWS Lambda deployment (using Python 3.8 runtime) are supported.



Launch locally
--------------

Follow script creation instructions of the [`cli`](../cli/README.md) subproject,


``` bash
./gradlew -PcxnHubUser=<YOUR_PASS_EMAIL> -PcxnHubPass=<YOUR_HUB_API_KEY> :cli:createScripts
```

then invoke

``` bash
./cli/build/scripts/run-nmr \
    -in molecules.smi.gz \
    -in-format smiles \
    -write-times true \
    -out /dev/null \
    -jsonl-out - \
    -max-count 1 | python3 nmr-result-process/src/process-nmr-result-to-text.py
```

Deploy as AWS lambda manually
-----------------------------

You can deploy file `nmr-result-process/src/process-nmr-result-to-text.py` manually as code for Python runtime. You can generate valid test data
using

```
./cli/build/scripts/run-nmr \
    -in molecules.smi.gz \
    -in-format smiles \
    -write-times true \
    -out /dev/null \
    -jsonl-out - \
    -max-count 1 > data.json
```

Deploy as AWS lambda with AWS CLI
---------------------------------

See <https://awscli.amazonaws.com/v2/documentation/api/latest/reference/lambda/create-function.html> for details.
See <https://docs.aws.amazon.com/lambda/latest/dg/lambda-intro-execution-role.html> on details on role to be used. Default recommended 
`AWSLambdaBasicExecutionRole` (AWS managed policy) is sufficient for the role below. See also
<https://docs.aws.amazon.com/lambda/latest/dg/python-package-create.html>.


``` bash
# Refer your appropriate role below, see details above
export LAMBDAROLE=<ROLE_TO_BE_USED_ARN>

# Create deployment package
# Python sources are packaged to the archive root
rm -f python-deployment-package.zip 
zip -j python-deployment-package.zip nmr-result-process/src/process-nmr-result-to-text.py

aws lambda create-function \
  --function-name nmr-result-to-text \
  --role "${LAMBDAROLE}" \
  --runtime python3.8 \
  --handler "process-nmr-result-to-text.lambda_handler" \
  --timeout 60 \
  --memory-size 512 \
  --publish \
  --zip-file fileb://python-deployment-package.zip
```

To delete the created function use (see <https://awscli.amazonaws.com/v2/documentation/api/latest/reference/lambda/delete-function.html>):

``` bash
aws lambda delete-function \
  --function-name nmr-result-to-text
```

### Test using `aws` CLI


See `com.chemaxon.calculators.NmrResponse` for details on request format. 
See <https://awscli.amazonaws.com/v2/documentation/api/latest/reference/lambda/invoke.html> for details on lambda invocation from `aws` CLI.


``` bash
# Generate test data
./cli/build/scripts/run-nmr \
    -in molecules.smi.gz \
    -in-format smiles \
    -write-times true \
    -out /dev/null \
    -jsonl-out - \
    -max-count 1 > data.json

# Invoke with test data
aws lambda invoke \
   --function-name nmr-result-to-text \
   --payload file://data.json \
   --cli-binary-format raw-in-base64-out \
   response.json

# Pretty print response
cat response.json | python -m json.tool
```

The response printed:

``` json
[
    Structure #0
    13C NMR result:
    Atom      Shift     Shift Error    Hydrogens
     1         59.15       0.67        [34, 35]
     2        126.97      16.16        [21]
     3        135.00       0.00        []
     4        135.90       0.00        [22]
     5        125.80       0.00        [23]
     6        130.00       0.00        [24]
     7        137.12       1.40        []
     8        137.32       0.68        [25]
     9        127.35       0.48        [26]
    10        137.55       0.70        []
    11        130.06       1.81        []
    12         32.78       0.79        [27, 28]
    13         18.70       0.92        [29, 30]
    14         39.35       0.65        [31, 32]
    15         33.95       0.70        []
    16         28.67       0.79        [36, 37, 38]
    17         28.67       0.79        [39, 40, 41]
    18         21.38       0.77        [42, 43, 44]
    19         12.42       0.79        [45, 46, 47]
    20         12.70       0.00        [48, 49, 50]

    1H NMR result:
    Atom      Shift      Shift Error
    21          5.52        0.15
    22          6.27        0.00
    23          6.69        0.09
    24          6.19        0.06
    25          6.18        0.15
    26          6.21        0.04
    27          2.04        0.02
    28          2.04        0.02
    29          1.64        0.03
    30          1.64        0.03
    31          1.48        0.01
    32          1.48        0.01
    33          2.16        0.00
    34          4.20        0.13
    35          4.20        0.13
    36          1.03        0.01
    37          1.03        0.01
    38          1.03        0.01
    39          1.03        0.01
    40          1.03        0.01
    41          1.03        0.01
    42          1.70        0.07
    43          1.70        0.07
    44          1.70        0.07
    45          1.97        0.03
    46          1.97        0.03
    47          1.97        0.03
    48          1.88        0.00
    49          1.88        0.00
    50          1.88        0.00
    ""
]
```