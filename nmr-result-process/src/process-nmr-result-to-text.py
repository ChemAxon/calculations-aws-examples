#
# Process NMR calculation lambda results into simple text format.
#
# See README.md for details and for usage examples
#
# author Laszlo Antal
# author Gabor Imre
#

import sys
import json

# extract the indexes of the neighbour hydrogens from the 1H NMR result
def get_attached_hydrogens(c_atom_index, hnmr_result):
    hydrogens = []
    for hnmr in hnmr_result:
        if (hnmr["attachedAtomIndex"] == c_atom_index): hydrogens.append(hnmr["atomIndex"])
    return hydrogens


# Convert an nmr response to a textual representation
def response_to_text_lines(data):
    ret = []

    # https://stackoverflow.com/a/522578
    for index, d in enumerate(data["results"]):

        ret.append("Structure #{}".format(index))
        ret.append("    13C NMR result:")
        ret.append("    Atom      Shift         Hydrogens")

        cshifts = []
        for r in d["cnmrResult"]:
            cshifts.append(r["shift"])
            ret.append("    {:2}        {:6.2f}        {}".format(r["atomIndex"], r["shift"], get_attached_hydrogens(r["atomIndex"], d["hnmrResult"])))

        ret.append("")

        ret.append("    1H NMR result:")
        ret.append("    Atom      Shift")
        hshifts = []
        for r in d["hnmrResult"]:
            hshifts.append(r["shift"])
            ret.append("    {:2}        {:6.2f}".format(r["atomIndex"], r["shift"]))


        ret.append("");
    
    
    return ret


# Lambda handler for lambda deployment
#
# event is an NMR lambda result
# returns textual lines in a list
# See https://docs.aws.amazon.com/lambda/latest/dg/python-handler.html
def lambda_handler(event, context): 
    return response_to_text_lines(event)


# When executed as a script read from stdin and write to stdout
if __name__ == "__main__":
    # parse json input from stdin
    data = json.load(sys.stdin)

    lines=response_to_text_lines(data)

    print('\n'.join(lines))

