
JAVA=java
JAVAC=javac
JFLEX=jflex
CLASSPATH=-classpath /usr/share/java/jfreechart-1.0.19.jar:.

all: SignalAnalyzer.class

SignalAnalyzer.class: SignalAnalyzer.java
	javac $(CLASSPATH) SignalAnalyzer.java

%.class: %.java
	javac $(CLASSPATH) $^ 

run: SignalAnalyzer.class
	java $(CLASSPATH) SignalAnalyzer

clean:
	rm -f *.class
