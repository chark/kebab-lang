<p align="center">
  <img src="https://raw.githubusercontent.com/Chark/kebab-lang/master/kebab_lang.png"/>
</p>
<hr/>
`kebab-lang` is an awesome scripting language that overuses snake_case convention for naming.

### Setup
JDK 1.8 and gradle installation is required. After cloning the project navigate yourself to the root directory and initiate this command:
```
gradle build
```

This should download all dependencies and create a `.jar` file which can be used for executing `kebab_lang` scripts. The jar file will be located at `build/libs` directory.

### Usage
First grab the `.jar` file created in the build directory, create a source file and run it, for example having such source file `test.keb`:
```
_func hello(message: 'Hello World!') {
  showl(message)
}

hello()
```

You can run it like this:
```
java -jar kebab-lang.jar test.keb
```

### Examples
You can find a list of complete example files [here](src/main/resources).
