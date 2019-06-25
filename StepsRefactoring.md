# Renovating the codebase

In this part of the workshop we will perform refactorings. This section will give an example of several types of refactorings. The goal is to get through the examples, and then further refactor allong the same line to extract a more domain concepts.

We will move through examples of the following types of refactorings:
* Reveal intent
* Extract functionality (from the game class)
* Replace case selection with maps

## Reveal intent

When looking to a new codebase the number of problem you find in there can be overwelming. Our advice is to start with micro-improvements to clarify the intent and make the code more simple and readable. This allows you to remove clutter and get to know the codebase better before starting the heavy lifting of restructuring it. 

Some typical quick wins:

* Extract magic numbers or Strings
* Simple renames
* Remove micro duplications
* Remove multiline duplication

### Simple renames

Renaming variables is one of the most simple ways to reveal intent. It is best to use your IDE refactoring capabilities to rename variables consistently.

An example of a confusiong name is `currentPlayer`. It seems to suggest it contains the currentplayer, but actually it only contains the index of the current player. If you want to know anything about this player you have to access some lists. To ensure this is clear, we can rename this to `currentPlayerIndex`.

### Remove micro duplications in functions

With micro duplications we mean duplications less then a line. An example of such statement is `players.get(currentPlayerIndex)` in java or `players[currentPlayerIndex];` in javascript, repeated 7 times. These types of duplication clutter the code, and it is not always obvious what this is. By extracting it in a function and give this a name, the code becomes more readable. The example statement represents the name of the current player.

*java*
```java
private String currentPlayerName() {
  return players.get(currentPlayer);
}
```

To make this work, you will also have to assign a type to the list of players, like `ArrayList<String> players = new ArrayList();`.

*javascript*

```javascript
function currentPlayerName() {
  return players[currentPlayerIndex];
}
```

**Another example to look is `places[currentPlayerIndex]`. Please extract it in a similar way**

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

```javascript
module.exports = function () {
    const NB_CELLS=12;

...
```

And the if construct itself changes to:

```javascript
if (currentPlayerPosition() >= NB_CELLS) {
  places[currentPlayerIndex] = currentPlayerPosition() - NB_CELLS;
}
```

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
  if (currentPlayerPosition() == 0) return Category.POP;
```

**With this the tests still fail, lukily we have the golden master. Can you spot why?**

  *javascript*

In javascript we start by defining the enum in a new file called `category.js`

```javascript
module.exports = Object.freeze({
  "POP": "Pop",
  "SCIENCE": "Science",
  "SPORTS": "Sports",
  "ROCK": "Rock"
});
```

Next we must import the new module in the game module:

```javascript
var Category = require('./category.js')

module.exports = function () {
  ...

```

Finally we can use it like this:

```javascript 
if (currentPlayerPosition() == 0)
  return Category.POP;
```

  </p>
</details>

### Remove multiline duplication by extracting a function

Next it is time to search for multiline duplications and extract them in functions. An example of this below. There are several other similar types of duplication, feel free to extract them.

*Java*

```java

places[currentPlayerIndex] = currentPlayerPosition() + roll;
if (currentPlayerPosition() >= NB_CELLS) places[currentPlayerIndex] = currentPlayerPosition() - NB_CELLS;

```

This can be extracted in

```java
private void move(int roll) {
  places[currentPlayerIndex] = places[currentPlayerIndex] + roll;
  if (places[currentPlayerIndex] >= NUMBER_OF_CELLS)
    places[currentPlayerIndex] = places[currentPlayerIndex] - NUMBER_OF_CELLS;
}

```

*Javascript*

```javascript
places[currentPlayerIndex] = currentPlayerPosition() + roll;
if (currentPlayerPosition() >= NB_CELLS) {
  places[currentPlayerIndex] = currentPlayerPosition() - NB_CELLS;
}
```

This can be extracted in 

```javascript

function move(roll) {
  places[currentPlayerIndex] = currentPlayerPosition() + roll;
  if (currentPlayerPosition() >= NB_CELLS) {
    places[currentPlayerIndex] = currentPlayerPosition() - NB_CELLS;
  }
}

```

### Removing multiline duplication by reordening an IF construct

A more advance removal of duplication is to remove duplication by restructuring a complex IF construct.

For example, looking to the roll function you will see the following lines twice:

*Java*

```java
move(roll);

System.out.println(currentPlayerName()
    + "'s new location is "
    + places[currentPlayerIndex]);
System.out.println("The category is " + currentCategory());
askQuestion();
```

By inverting the nested if condition and adding a return you can remove the duplication. *Ask one of the facilitators to help you with this one*.

The end result will be a simplified roll.

```java
public void roll(int roll) {
  System.out.println(currentPlayerName() + " is the current player");
  System.out.println("They have rolled a " + roll);

  if (inPenaltyBox[currentPlayerIndex]) {
    if (roll % 2 == 0) {
      System.out.println(currentPlayerName() + " is not getting out of the penalty box");
      isGettingOutOfPenaltyBox = false;
      return;
    }
    isGettingOutOfPenaltyBox = true;

    System.out.println(currentPlayerName() + " is getting out of the penalty box");
  }

  move(roll);

  System.out.println(currentPlayerName()
      + "'s new location is "
      + places[currentPlayerIndex]);
  System.out.println("The category is " + currentCategory());
  askQuestion();
}
```

*Javascript*

```javascript
move(roll);

console.log(currentPlayerName() + "'s new location is " + currentPlayerPosition());
console.log("The category is " + currentCategory());
askQuestion();
```

By inverting the nested if condition and adding a return you can remove the duplication. *Ask one of the facilitators to help you with this one*.

The end result will be a simplified roll.

```javascript
this.roll = function (roll) {
  console.log(currentPlayerName() + " is the current player");
  console.log("They have rolled a " + roll);

  function move(roll) {
    places[currentPlayerIndex] = currentPlayerPosition() + roll;
    if (currentPlayerPosition() >= NB_CELLS) {
      places[currentPlayerIndex] = currentPlayerPosition() - NB_CELLS;
    }
  }

  if (inPenaltyBox[currentPlayerIndex]) {
    if (roll % 2 == 0) {
      console.log(currentPlayerName() + " is not getting out of the penalty box");
      isGettingOutOfPenaltyBox = false;
      return;
    }
    isGettingOutOfPenaltyBox = true;
    console.log(currentPlayerName() + " is getting out of the penalty box");
  }

  move(roll);

  console.log(currentPlayerName() + "'s new location is " + currentPlayerPosition());
  console.log("The category is " + currentCategory());
  askQuestion();

};
```

## Extract functionality (from Game class)

After focussing on revealing intent it is time for the next step. The next step is to split off certain functionality from the Game class. We do this because the Game class tries to cover to many concerns. By extract collaborators and domain concepts we can make the game class simpler and more readable.

**Can you identify some good candidates for extraction?**

### Extract collaborators
  
Everywhere in the code the output is written directly to the console (System.out in Java). This makes it hard in our test (remember the setOut in the Golden Master), but also makes it pretty to use this in the context of a website or mobile application. Extracting a reporter that reports about what happens in the game would be a big step forward. We call this refactor a 'collaborator' as it extract a part of the functionality in an object (Reporter) where the original object (Game) will collaborate with.

The first step towards a reporter is to extract the output in a method.

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

Make a new module in the file `reporter.js`.

```javascript
module.exports = function() {
    this.report = function(message) {
        console.log(message);
    }
}
```

Be sure to import it correctly:

```javascript
var Reporter = require('./reporter.js');

...

reporter = new Reporter();
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

public class GameRunner {

  private static boolean notAWinner;

  public static void main(String[] args) {
    Random rand = new Random();
    runGame(rand, new Reporter(System.out));
  }

  public static void runGame(Random rand, Reporter reporter){
    runGame(rand, reporter, "Chet", "Pat", "Sue");
  }

  public static void runGame(Random rand, Reporter reporter, String... players) {
    Game aGame = new Game(reporter);

  ...
}

  ```
  
Now we can also remove the hack to capture the console output, as it became an injectable dependency.

```java
public String runGameForSeedAndPlayers(Integer seed, Players players) {
  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  PrintStream printStream = new PrintStream(outputStream, true);

  Random rand = new Random(seed);
  GameRunner.runGame(rand, new Reporter(printStream), players.players);

  return outputStream.toString();
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
  
#### Meaninfull reports

You can go even one step further and make meaningfull methods to report about stuff, seperating the way you report from the gamelogic. An example of extracing one message for reporting about the number of players below.

```java
public class Reporter {

...

    void reportNbPlayers(int nbPlayers) {
        report("They are player number " + nbPlayers);
    }
}
```

**Suggestion: extract a few more of these messages.**

### Extract domain concept

In the previous section we extracted a collaborator based on functionality. In this section we extract a more specific collaborator, namely one that encapsulates a domain concept.

#### Player

The first domain concept we can extract is the player. 

The first step is to extract to extract all things you want to move in methods, this will make it a lot easier to ensure a correct move. 

*Java*

```java 
Usage in the game class:

```java

private Player currentPlayer() {
  return players.get(currentPlayerIndex);
}

private String currentPlayerName() {
  return players.get(currentPlayerIndex);
}

private int currentPlayerCoins() {
  return purses[currentPlayerIndex];
}

private void addCoinForCurrentPlayer() {
  purses[currentPlayerIndex]++;
}

private boolean hasCurrentPlayerWon() {
  return (currentPlayerCoins() == NB_COINS_TO_WIN);
}

```

*Javascript*

**TODO**

Then we can extract the player. We typically still do this in two steps: 1) only extract player and its player name (run tests, commit; 2) add the coins (run tests, commit). The end results can be found below. 

*Java*

The player class.
```java

public class Player {

    private final String name;
    private int coins = 0;

    Player(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public int getCoins(){
        return coins;
    }

    public void addCoin(){
        coins++;
    }

    public boolean hasWon() {
        return getCoins() == Game.NB_COINS_TO_WIN;
    }
}
```

The resulting usage in the Game class.

```java
private Player currentPlayer() {
  return players.get(currentPlayerIndex);
}

private String currentPlayerName() {
  return currentPlayer().getName();
}

private int currentPlayerCoins() {
  return currentPlayer().getCoins();
}

private void addCoinForCurrentPlayer() {
  currentPlayer().addCoin();
}

private boolean hasCurrentPlayerWon() {
  return !currentPlayer().hasWon();
}
```


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

## Replace case selection with maps

The next refactor is to attempt to remove switch cases or if constructs for selecting a specific case. The example is the `currentCategory` method. 

Before starting it is best to reorder the statements a bit, to reveal that 0, 4 and 8 lead to POP, 1, 5 and 9 lead to SCIENCE, ... This means there is a repetition and we can use the module operator to select from a map with four entries.

The result will look something like this:

*Java*

```java

private Map<Integer, Category> categoryForPosition = new HashMap();

public  Game(Reporter reporter){

  ...

  buildCategories();
  
  ...
}

private void buildCategories() {
  categoryForPosition.put(0, Category.POP);
  categoryForPosition.put(1, Category.SCIENCE);
  categoryForPosition.put(2, Category.SPORTS);
  categoryForPosition.put(3, Category.ROCK);
}

private Category currentCategory() {
  int scaledPosition = currentPlayerPosition() % categoryForPosition.size();
  return categoryForPosition.get(scaledPosition);
}

```

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

In javascript you can also replace the Map with an algorithm.

```javascript
var currentCategory = function () {
  const nbCategories = Object.keys(Category).length;
  const scaledPosition = places[currentPlayerIndex] % nbCategories
  return Category[Object.keys(Category)[scaledPosition]];
};
```







