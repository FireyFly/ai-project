

.PHONY: all clean

all: class/Main.class

clean:
	rm class/*.class
	rmdir class


class:
	mkdir class

class/Main.class: src/Main.java class
	javac -cp src -d class $<
