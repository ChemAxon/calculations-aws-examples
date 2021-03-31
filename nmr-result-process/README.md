Process NMR results
===================

This is a proof of concept lambda written in Python to process NMR calculation results. The main goal of this
exercise is to demonstrate the interoperability of lamdas using different platforms.

Requirements
------------

Python3 with `Matplotlib` is required. On Ubuntu follow <https://phoenixnap.com/kb/how-to-install-python-3-ubuntu> and 
<https://matplotlib.org/3.3.3/users/installing.html>.

Launch locally
--------------

Follow script creation instructions of the [`cli`](../cli/README.md) subproject, then invoke

``` bash
./cli/build/scripts/run-nmr \
    -in molecules.smi.gz \
    -in-format smiles \
    -write-times true \
    -out /dev/null \
    -jsonl-out - \
    -max-count 1 | python3 nmr-result-process/src/process-nmr-result-to-text.py
```