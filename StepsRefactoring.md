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









