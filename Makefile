# Makefile for ResumeBuilder Java Application

JAVAC=javac
JAVA=java
JAR_PATH=lib/itextpdf-5.5.10.jar
SRC=src/ResumeBuilder.java
OUT=.
MAIN=ResumeBuilder

all: build

build:
	$(JAVAC) -cp "$(JAR_PATH)" $(SRC) -d $(OUT)

run: build
	$(JAVA) -cp ".:$(JAR_PATH)" $(MAIN)

clean:
	rm -f *.class

rebuild:
	$(MAKE) clean
	$(MAKE) build
	$(MAKE) run
	$(MAKE) clean

.PHONY: all build run clean rebuild 