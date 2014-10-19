

.PHONY: all clean

all: class/Main.class class/Learner.class

clean:
	rm class/*.class
	rmdir class


class:
	mkdir class

class/Main.class: src/Main.java class src/Markov.java src/FrequencyList.java src/Utils.java src/Model.java
	javac -cp src -d class $<

ark-tweet-nlp/ark-tweet-nlp/target/bin/ark-tweet-nlp-0.3.2.jar: ark-tweet-nlp
	cd ark-tweet-nlp; mvn package

class/Learner.class: src/Learner.java src/Markov.java class ark-tweet-nlp/ark-tweet-nlp/target/bin/ark-tweet-nlp-0.3.2.jar
	javac -Xlint:unchecked -cp src:ark-tweet-nlp/ark-tweet-nlp/target/bin/* -d class $<
