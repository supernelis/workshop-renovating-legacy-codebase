# Renovating the codebase

## Reveal intent

When renovating a horrible codebase, it is often a good first step to do a series of smaller refactorings to get to know the codebase better, to better reveal the intent as you improve your understanding and to remove some clutter. This is a warmup before starting the heavy lifting.

Some typical quick wins:

* Extract magic numbers or Strings
* Simple renames
* Remove micro duplications
* Remove multiline duplication

### Extract magic numbers or Strings

A magic number or string is a value that is used in the code: 1) without clear context or meaning; 2) Used in several places.

**Can you identify such magic numbers and strings?**

Magic numbers or Strings can be extracted as constants or enums. 

<details>
  <summary>Click to see the example with a constant</summary>
  <p>
    
  Like NB_CELLS
  
  </p>
</details>

<details>
  <summary>Click to see the example with an enum </summary>
  <p>
    
  Like POP, SCIENCE, SPORT, ROCK to an enum QuestionCategorie

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








