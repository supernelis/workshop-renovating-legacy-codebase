# Renovating the codebase

In this part of the workshop we will perform refactorings. This section will give an example of several types of refactorings. The goal is to get through the examples, and then further refactor allong the same line to extract a more domain concepts. 

## Reveal intent

When looking to a new codebase the number of problem you find in there can be overwelming. Our advice is to start with micro-improvements to clarify the intent and make the code more simple and readable. This allows you to remove clutter and get to know the codebase better before starting the heavy lifting of restructuring it. 

Some typical quick wins:

* Extract magic numbers or Strings
* Simple renames
* Remove micro duplications
* Remove multiline duplication

### Extract magic values

A magic value is used in the code: 1) without clear context or meaning; 2) Used in several places. By extracting it as a constant you give this magic value a meaning through its name, and you prevent errors possible errors that come from giving the values every time again.  

**Can you identify such magic values? Please extract them!**

Magic values can be extracted as constants or enums. 

<details>
  <summary>Click to see an example with a constant</summary>
  <p>
  
  A good example can be found in `if (places[currentPlayer] > 11) places[currentPlayer] = places[currentPlayer] - 12;`. It is not immediatly obvious what the 11 and 12 stand for in this statement. 

  Studying the code in more details will reveal that they are connected to the number of cells on your gameboard which is 12. The `> 11` has the same meaning and can be easily replaced by `>= 12` to simplify the extraction of a constant. This leads to:

*Java* 
```java

public class Game {
    public static final int NUMBER_OF_CELLS = 12;

...

```

And the if construct itself changes to: 

```java

if (places[currentPlayer] >= NUMBER_OF_CELLS) places[currentPlayer] = places[currentPlayer] - NUMBER_OF_CELLS;

```

Be sure to run your tests at this point, and to commit the code of they succeed!  

*Javascript* 


  </p>
</details>

<details>
  <summary>Click to see an example with an enum</summary>
  <p>

  A good example to extract in an enum are `"Pop", "Science", "Sports", "Rock"`. These values are possible values for the question category.

  *Java*

  In IntelliJ you can extract these values as constants first (using the IDE provided constant extraction) leading to:

  ```java
public static final String POP = "Pop";
public static final String SCIENCE = "Science";
public static final String SPORTS = "Sports";
public static final String ROCK = "Rock";
  ```

  Then you can extract a delegate (using the IDE provided delegate extraction), selecting the four constants and ticking extract as enum.

```java

public enum Category {
    POP("Pop"), SCIENCE("Science"), SPORTS("Sports"), ROCK("Rock");
    private String value;

    public String getValue() {
        return value;
    }

    Category(String value) {
        this.value = value;
    }
}

```

And an example of the usage then becomes
```java
		if (places[currentPlayer] == 0) return Category.POP;
```

**With this the tests still fail. Can you spot why?**

  *javascript*

  ```javascript
  module.exports = Object.freeze({
    "POP": "Pop",
    "SCIENCE": "Science",
    "SPORTS": "Sports",
    "ROCK": "Rock"
  });
  ```
  
  </p>
</details>


### Simple renames

Rename currentPlayer to currentPlayerIndex

### Remove micro duplications

Like 

*java*
```java
players.get(currentPlayer); -> currentPlayerName();
```

*javascript*

```javascript
players[currentPlayerIndex]; -> currentPlayerName();
```

Like

*java* and *javascript*

```
places[currentPlayer] -> currentPlayerPosition()
```

### Remove multiline duplication

places[currentPlayerIndex] = currentPlayerPosition() + roll;
if (currentPlayerPosition() >= NB_CELLS) places[currentPlayerIndex] = currentPlayerPosition() - NB_CELLS;

-> Move(int nbPlaces)

## Extract 

After focussing on revealing intent it is time for the next step.

The guide for the next step is to realise that the Game class tries to cover to many concerns at once. By extracing domain concepts and collaborators covering a specific aspect we can make the code a lot simpler. 

**Can you identify some good candidates for extraction?**

### Extract collaborators 

<details>
  <summary>Click to see an example of a technical concern. </summary>
  <p>
    
  Everywhere in the code the output is written directly to the console (System.out in Java). This makes it hard in our test (remember the setOut in the Golden Master), but also makes it pretty to use this in the context of a website or mobile application. Extracting a reporter that reports about what happens in the game would be a big step forward. 
  
  The first step towards a reporter is to extract the System.out in a method. 
  
  *java*

  ```java
    private void report(String message) {
        System.out.println(message);
    }
  ```

  *javascript*

  ```javascript
  function report(message) {
		console.log(message);
	}
  ```
  
  The next step is to create a Reporter class and move the method there. 
  
*Java*

  ```java
    public class Reporter {
      public Reporter() {
      }

      void report(String message) {
          System.out.println(message);
      }
    }
  ```

*javascript*

```javascript
module.exports = {
    report: function(message) {
        console.log(message);
    }
}
```
  
  And everywhere in the code you will find: 
  
  ```java
    reporter.report(...);
  ```
  
  Next we ensure the dependencies are properly injected: 
  
  ```java 
    public class Reporter {

      private PrintStream stream;

      public Reporter(PrintStream stream) {
          this.stream = stream;
      }
    ...
    
    }
  ```  
  
  And in the game class.
  
 *java* 
  ```java  
    public class Game {

    ...

    private final Reporter reporter;

   ...

    public Game(Reporter reporter) {
        ...
        this.reporter = reporter;
    }
    ...
    
    }
  ```
  
 *javascript*

 ```javascript
 module.exports = function (reporter) {
   ....

  function report(message) {
		reporter.report(message);
	}
 }
 ```
  
  </p>
</details>

### Extract domain objects

#### Player

*javascript*

```javascript
module.exports = function(name) {
    this.name = name;
    this.coins = 0;

    this.addCoin = function(){
        this.coins += 1;
    }

    this.hasWon = function() {
        return this.coins == 6;
    }
}
```

use in `Game``

```javascript
  const Player = require('./player');

  module.exports = function (reporter) {

    ...

    function currentPlayer() {
      return players[currentPlayerIndex];
    }

    function currentPlayerName() {
      return currentPlayer().name;
    }

    function currentPlayerCoins() {
      return currentPlayer().coins;
    }

    function addCointToCurrentPlayer() {
      currentPlayer().addCoin();
    }

    function hasCurrentPlayerWon() {
      return currentPlayer().hasWon();
    }
  }
```

## If to MAP

<details>
  <summary>Click to see an example of a technical concern. </summary>
  <p>

*javascript*

```javascript
    const categoryForPosition = new Map();
    categoryForPosition.set(0, Category.POP);
    categoryForPosition.set(1, Category.SCIENCE);
    categoryForPosition.set(2, Category.SPORTS);
    categoryForPosition.set(3, Category.ROCK);

    var currentCategory = function () {
        const nbCategories = Object.keys(Category).length;
        const scaledPosition = places[currentPlayerIndex] % nbCategories;
        return categoryForPosition.get(scaledPosition);
    };
```

you can also replace the Map with an algorithm.

```javascritp
  var currentCategory = function () {
		const nbCategories = Object.keys(Category).length;
		const scaledPosition = places[currentPlayerIndex] % nbCategories
		return Category[Object.keys(Category)[scaledPosition]];
	};
```

  </p>
</details>


## Javascript plugin VSCode

- [Refactoring JavaScript](https://code.visualstudio.com/docs/editor/refactoring)
- [JS Refactor](https://marketplace.visualstudio.com/items?itemName=cmstead.jsrefactor)








