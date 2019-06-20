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
  
  </p>
</details>


### Simple renames

Rename currentPlayer to currentPlayerIndex

### Remove micro duplications

Like players.get(currentPlayer); -> currentPlayer()

Like places[currentPlayer] -> currentPlayerPosition()

### Remove multiline duplication

places[currentPlayerIndex] = currentPlayerPosition() + roll;
if (currentPlayerPosition() >= NB_CELLS) places[currentPlayerIndex] = currentPlayerPosition() - NB_CELLS;

-> Move(int nbPlaces)

## Extract collaborators

After focussing on revealing intent it is time for the next step.

The guide for the next step is to realise that the Game class tries to cover to many concerns at once. By extracing domain concepts and collaborators covering a specific aspect we can make the code a lot simpler. 

**Can you identify some good candidates for extraction?**

<details>
  <summary>Click to see an example of a technical concern. </summary>
  <p>
    
  Everywhere in the code the output is written directly to the console (System.out in Java). This makes it hard in our test (remember the setOut in the Golden Master), but also makes it pretty to use this in the context of a website or mobile application. Extracting a reporter that reports about what happens in the game would be a big step forward. 
  
  The first step towards a reporter is to extract the System.out in a method. 
  
  ```java
    private void report(String message) {
        System.out.println(message);
    }
  ```
  
  The next step is to create a Reporter class and move the method there. 
  
  ```java
    public class Reporter {
      public Reporter() {
      }

      void report(String message) {
          System.out.println(message);
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
  
  
  
  </p>
</details>






Inject your dependencies



## Extract domain object: Player?
---
## If to MAP









