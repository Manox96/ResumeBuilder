# Resume Builder

A Java application for creating professional resumes using iText PDF library.

## Prerequisites

- Java Development Kit (JDK)
- iText PDF library

## Setup

1. Clone the repository
2. Make sure you have JDK installed
3. Download the iText PDF library:
   - Download iText PDF 5.5.10 from [Maven Central](https://repo1.maven.org/maven2/com/itextpdf/itextpdf/5.5.10/itextpdf-5.5.10.jar)
   - Create a `lib` directory in the project root if it doesn't exist
   - Place the downloaded JAR file in the `lib` directory

## Building and Running

Use the provided Makefile to build and run the application:

```bash
# Build the project
make build

# Run the application
make run

# Clean compiled files
make clean

# Rebuild and run
make rebuild
```

## Project Structure

- `src/` - Source code directory
- `lib/` - Library dependencies (place iText PDF JAR here)
- `Makefile` - Build automation 