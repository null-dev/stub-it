# StubIt!

## About
StubIt! is a simple program to make stubbed versions of Java source code.
This is useful for testing and generating APIs.

### Example
INPUT: *This simple file with nested classes.*
```
class DateManager {
    public int CURRENT_MONTH = getMonth();
    
    public String SOME_CONSTANT = "constant value";
    
    public int getMonth() {
        return new Date().month;
    }
    
    class TimeManager {
        public int getSecond() {
            return new Time().second;
        }
    }
}
```
OUTPUT: *The file with all executable code removed.*
```
class DateManager {

    public int CURRENT_MONTH;

    public String SOME_CONSTANT = "constant value";

    public int getMonth() {
        throw new RuntimeException("Stub!");
    }

    class TimeManager {

        public int getSecond() {
            throw new RuntimeException("Stub!");
        }
    }
}
```

## Usage
Using StubIt! is very simple, just pass it a bunch of files to stub:
```
java -jar StubIt.jar Class1.java Class2.java Class3.java...
```
StubIt! will save the stubbed versions of the files as: `[FILENAME].stub.java`

### Advanced Usage
```
java -jar StubIt.jar [OPTIONS] [FILES]

Options:
    --print: Prints the stubbed files to stdout.
    --stdin: Reads the input from stdin, implies --print.
    --extension: Changes the extensions of the output files.
                 Default: stub.java
```