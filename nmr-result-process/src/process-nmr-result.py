#
# Process NMR calculation lambda results into text and image format.
#
# See README.md for details and for usage examples
#
# author Laszlo Antal
# author Gabor Imre
#

import sys, json
import matplotlib.pyplot as plt

# extract the indexes of the neighbour hydrogens from the 1H NMR result
def get_attached_hydrogens(c_atom_index, hnmr_result):
    hydrogens = []
    for hnmr in hnmr_result:
        if (hnmr["attachedAtomIndex"] == c_atom_index): hydrogens.append(hnmr["atomIndex"])
    return hydrogens

# parse json input from stdin
data = json.load(sys.stdin)

# https://stackoverflow.com/a/522578
for index, d in enumerate(data["results"]):

    print("Structure #{}".format(index))
    print("    13C NMR result:")
    print("    Atom      Shift         Hydrogens")

    cshifts = []
    for r in d["cnmrResult"]:
        cshifts.append(r["shift"])
        print("    {:2}        {:6.2f}        {}".format(r["atomIndex"], r["shift"], get_attached_hydrogens(r["atomIndex"], d["hnmrResult"])))

    print("")

    print("    1H NMR result:")
    print("    Atom      Shift")
    hshifts = []
    for r in d["hnmrResult"]:
        hshifts.append(r["shift"])
        print("    {:2}        {:6.2f}".format(r["atomIndex"], r["shift"]))


    print("")


    # create a simple spectrum from the 13C and 1H shifts
    # constructs two subplots
    fig, (ax1, ax2) = plt.subplots(2, 1, sharex=True)
    fig.suptitle("Strucure #" + str(index), fontweight="bold")

    # add 13C shifts to the first subplot
    ax1.set_yticklabels([])
    ax1.set(title="13C NMR shifts")
    ax1.bar(cshifts, [1] * len(cshifts))

    # add 1H shifts to the first subplot
    ax2.set_yticklabels([])
    ax2.set(title="1H NMR shifts")
    ax2.bar(hshifts, [1] * len(hshifts))
    #plt.show()

    # save to jpg
    plt.savefig("nmr_shifts_" + str(index) + ".jpg")
