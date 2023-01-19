# Example of usage jep-distro
In this example project we include [jep-distro](https://github.com/icemachined/jep-distro)
as a dependency, unpack it to target folder in maven install phase,
and then setup jep for running in JepInitializer file.
This example demonstrate how we can pass java objects to python code, call python procedures from python module which call java code, 
and then return result back to java code.

## How to run 
1. mvn clean install
2. Run main function