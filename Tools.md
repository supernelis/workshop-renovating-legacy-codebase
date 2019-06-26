# Tools we used

## Java based

The tools we used in preparation of the workshop:

* Java version 1.8
* IntelliJ (for its supirior refactoring capabilities)
* Maven
* jenv to ensure the version is the same for both maven and IntelliJ

### Troubleshoot

#### The test succeed in maven / IDE but fail in the other

Probably the java version is not the same. Use jenv to get them the same for both maven and your favorite IDE. With `jenv enable-plugin maven` you can control this also for maven. 


## Javascript based

The tools we used in preparation of this workshop:

* npm
* Node
* Visual Studio Code with the following plugins
** [Refactoring JavaScript](https://code.visualstudio.com/docs/editor/refactoring)
** [JS Refactor](https://marketplace.visualstudio.com/items?itemName=cmstead.jsrefactor)
** [Coverage Gutters](https://marketplace.visualstudio.com/items?itemName=ryanluker.vscode-coverage-gutters)
* Alternative: Webstorm
