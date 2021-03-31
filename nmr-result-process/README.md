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
