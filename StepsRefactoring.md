# Renovating the codebase

## Simple refactorings

Start with some simple things to improve readability

### Extract magic numbers or Strings

Like NB_CELLS

Like POP, SCIENCE, SPORT, ROCK to an enum QuestionCategorie

### Simple renames

Rename currentPlayer to currentPlayerIndex

### Remove duplication by extracting methods for logic repeated everywhere

Like players.get(currentPlayer); -> currentPlayer()

Like places[currentPlayer] -> currentPlayerPosition()

## Remove multiline duplication

places[currentPlayerIndex] = currentPlayerPosition() + roll;
if (currentPlayerPosition() >= NB_CELLS) places[currentPlayerIndex] = currentPlayerPosition() - NB_CELLS;

-> Move(int nbPlaces)

<details>
  <summary>Click to expand</summary>
  <p>
  ## A subtitle
  </p>
</details>





