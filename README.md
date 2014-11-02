
# Dependencies

* Java 8
* Maven

The build system is only tested on Linux.

# Compiling

Run `make` from the root directory. This will build the POS tagger along with
the source code provided by this project.

# Modules

## train.sh

Usage: `./train.sh <learning-data> [model]`

Takes the name of a file containing a list of tweets (or other text), separated by newlines, and
constructs a `chain.dat` file containing the Markov model. A model for use with the POS tagger
may optionally be specified. `model.20120919` is used by default; additional models can
be downloaded from the tagger's [official homepage](http://www.ark.cs.cmu.edu/TweetNLP/).

## predict.sh

Usage: `./predict.sh <model>`

A `chain.dat` file must exist prior to running `predict.sh`.

`model` is either `simple` or `grammar`. If `simple` is specified a simple Markov chain on
words is used for prediction. If `grammar` is specified, a Markov chain on POS tags is used
to weight the initial predictions.

Text is read from stdin and predictions are written to stdout.

## getposition.sh

Usage: `./getposition.sh`

A `chain.dat` file must exist prior to running `getposition.sh`.

Reads a line of text on stdin, removes the last word, runs both the `simple` and the `grammar`
model on the remaining text, and prints the index of the removed word in both prediction lists.
Results are written to stdout on the form: [word to predict] [index in `grammar` model]
[index in `simple` model].
