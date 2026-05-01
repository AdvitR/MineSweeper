.PHONY: all compile run test test-stack-trace clean

all:	

compile:
	mvn compile

run: compile
	mvn exec:java

zip: $(SUBMIT)
	zip -j '$(ZIPNAME)' $(SUBMIT)

test: 
	mvn test

test-stack-trace:
	mvn -DtrimStackTrace=false test

clean:
	mvn clean
