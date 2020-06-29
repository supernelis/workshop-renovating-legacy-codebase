# Longer String

## First test

```java
@Test
public void longerGoldenMaster() throws Exception {
    verifyAllCombinations(
        new String[]{
                "Nelis", "Matteo"
        }
     );
}
```
run your test, they should fail, approve your golden master.
run your test with full coverage. You should have 100% of test coverage.

## Run mutation testing

Check that you are running java version 8.

```bash
$ mvn clean test -DwithHistory org.pitest:pitest-maven:mutationCoverage
```

open the report under `target/pit-reports/<date-time>/index.html`

You should have one mutant that survives. (what happens when two strings have the same size?)

## Add second test case

```java
@Test
public void longerGoldenMaster() throws Exception {
    verifyAllCombinations(
        new String[]{
                "Nelis", "Matteo",
                "Tim", "Tom"
        }
     );
}
```

rerun all your tests and re-approve the golden master.
Rerun the mutation testing, you should have 0 surviving mutants.

