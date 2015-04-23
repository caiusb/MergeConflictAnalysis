function readTextFile(file)
{
    var request = new XMLHttpRequest();
    request.open("GET", file, false);
    request.send(null);
    return request.responseText;
}

function diffLOC(a, b) {
    // get the baseText and newText values from the two textboxes, and split them into lines
    var base = difflib.stringAsLines(readTextFile(a));
    var newtxt = difflib.stringAsLines(readTextFile(b));

    // create a SequenceMatcher instance that diffs the two sets of lines
    var sm = new difflib.SequenceMatcher(base, newtxt);

    // get the opcodes from the SequenceMatcher instance
    // opcodes is a list of 3-tuples describing what changes should be made to the base text
    // in order to yield the new text
    var opcodes = sm.get_opcodes();
    var diffoutputdiv = document.getElementById('diffoutput');
    while (diffoutputdiv.firstChild) diffoutputdiv.removeChild(diffoutputdiv.firstChild);
    // var contextSize = 0;
    // contextSize = contextSize ? contextSize : null;

    // build the diff view and add it to the current DOM
    diffoutputdiv.appendChild(diffview.buildView({
        baseTextLines: base,
        newTextLines: newtxt,
        opcodes: opcodes,
        // set the display titles for each resource
        baseTextName: "Source for A",
        newTextName: "Source for B",
        contextSize: null,
        viewType: 0
    }));

    // scroll down to the diff view window.
    location = url + "#diff";
}