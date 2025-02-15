# Bounding Box Application

This application processes a single input file and generates the largest non-overlapping minimum bounding boxes based on
the given data.

The input file data must conform to the following properties:

- Input is split into lines delimited by newline characters.
- Every line has the same length.
- Every line consists of an arbitrary sequence of hyphens ("-") and asterisks ("\*").
- The final line of input is terminated by a newline character.

## Requirements

- **Java 21**: Ensure that Java 21 is installed on your machine to run this application.

## Gradle Commands

### Run the Application

To run the application, use the following command:

```bash
./gradlew run --args=[path to input file]
```

Example:

```bash
./gradlew run --args="groups.txt"
```

### Run Tests

To execute the tests for this application, use the following command:

```bash
./gradlew test
```

## Application Binary

### Build the Application Binary

To build the application binary, use the following command:

```bash
./gradlew installDist
```

This will create the binary in the `build/install/bounding-box/bin` directory.

### Running the Binary

To run the application using the generated binary, follow these steps:

1. Navigate to the binary directory:

```bash
cd build/install/bounding-box/bin
```

2. Run the application with your input file:

```bash
./bounding-box [path to input file]
```

Example:

```bash
./bounding-box groups.txt
```
