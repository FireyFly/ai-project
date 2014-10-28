

.PHONY: all clean

all: class/Main.class class/Learner.class class/GetPredictionPosition.class

clean:
	rm class/*.class
	rmdir class


class:
	mkdir class

ark-tweet-nlp/ark-tweet-nlp/target/bin/ark-tweet-nlp-0.3.2.jar: ark-tweet-nlp
	cd ark-tweet-nlp; mvn package

class/Main.class: src/Main.java class src/Markov.java src/FrequencyList.java \
                  src/Utils.java src/Model.java ark-tweet-nlp/ark-tweet-nlp/target/bin/ark-tweet-nlp-0.3.2.jar
	javac -cp src:ark-tweet-nlp/ark-tweet-nlp/target/bin/* -d class $<

class/Learner.class: src/Learner.java src/Markov.java src/FrequencyList.java src/Utils.java class \
                     ark-tweet-nlp/ark-tweet-nlp/target/bin/ark-tweet-nlp-0.3.2.jar
	javac -Xlint:unchecked -cp src:ark-tweet-nlp/ark-tweet-nlp/target/bin/* -d class $<

class/GetPredictionPosition.class: src/GetPredictionPosition.java class/Main.class
	javac -cp src:ark-tweet-nlp/ark-tweet-nlp/target/bin/* -d class $<
