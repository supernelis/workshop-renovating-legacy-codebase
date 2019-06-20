# The road towards an effective golden master

The goal of the first part of the workshop is for you to build up an effective golden master. There are several important steps to get there.

1. Understand what to observe
2. Capture the output
3. Make the tests reproducible
4. Verify the result with a golden master
5. Check if the golden master is effective
6. Add more variations

## Step: Understand what to observe

The first step in building up an effective golden master is to understand what to observe.

So lets start with running the game by running the main in GameRunner. Briefly look to the output.

Run the game several times. Briefly look to the GameRunner. 

**What do you see that will make it difficult for you to do tests?**

**Which output needs to be captured?**

## Step: Capture the output

The second thing to notice when studying the program is that the only feedback you get is on the console output. So how can you compare the your output with the golden master in this case? 

Again java provides a way to capture the console output. The trick is to define your own stream, and overwrite the System.out stream with the one you control. Example below.  


```java

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(outputStream, true);
    
    PrintStream oldOut = System.out;
    
    System.setOut(printStream);
    
    runGame(rand);
    
    System.setOut(oldOut);
    System.out.print(outputStream.toString());

```

In this example, outputStream.toString() contains the output of our test.

## Step: Make the tests reproducible

The GameRunner uses a random nummer generator, causing the output of a run to differ every time. In this way it will be very hard to capture the output and compare it to the golden master, as every output will be different. 

To make a reproducible test, you need to make the randomness controllable in the tests. 

### Reproducible random

Java provides a way to do this, namely by passing a seed to random. Using this seed will produce the same sequence of random numbers every time. 

```java
    int seed = 1;                       // a seed for the random generator
    Random rand = new Random(seed);
    rand.nextInt();                     // will always produce the exact same result
```

### Use reproducible random

To allow running the game with such controlled randomGenerator we must extract a runGame in the gameRunner with an 
random number generator as parameter

```java
    public static void runGame(Random rand) { ... }
```

Now we can make a new test that uses a random generator with a seed

```java

    public class GoldenMasterTests {
    
        @Test
        public void can_run_with_reproducible_output() {
                Random rand = new Random(1);
    
                runGame(rand);
        } 
    }
    
```

It is not a real test yet (no asserts yet), but at least it allows us to easily run with a reproducible output. 



## Step: Add ApprovalTests to verify

As we now have a reproducible output to compare, we still need to do the actual comparison. We could write something ourselves, but actually there is a great tool to use in this context called [Approval Tests](http://approvaltests.com/). 

The essense of ApprovalTests is that it will keep track of golden master (called the approved version) and will compare the output of a testrun (called the received version) with the golden master. If you made functional changes that change the output, you are supposed to check it manually and upgrade the received to the approved version.

The simplest way is to add a simple verify step:

```java
	@Test
	public void can_run_a_controlled_game() {
			String result = runGame(2);

			Approvals.verify(result);
	}
```

On the first run the test will still fail, as it lacks an approved version with the correct content. If you are sure this is the version you want to start from you can copy the content from the received file to the approved file and run the test again (should produce a green result). 

## Step: Check the quality of your tests

Now we have a golden master test we still need to check if it is effective:

* We can check the code coverage.
* We can use mutation testing to verify if all possibilities are considered.

### Check the code coverage 

Run your tests with code coverage and check which parts are covered.

In IntelliJ you can enable a more advanced tracking of code coverage by enabling tracing. 
Click on your build configuration for test -> Edit Configuration -> Code coverage Tab -> Tracing 

TODO: ensure we can also do this with maven.

TODO: full code coverage is step 1.

### Use mutation testing

The idea behind the mutation testing is that the mutation testing tool produces mutants (i.e. versions of your code with changes) that need to be killed by the tests (i.e. make the tests fail). Every mutant that survives might indicate to a test that insufficiently covers such case. 

We already configured maven to run the mutation testing easily. To check if maven is working well:

```bash
$ mvn clean test
```

Running the mutation tests itself

```bash
$ mvn -DwithHistory org.pitest:pitest-maven:mutationCoverage
```

If you open the browser and go to: the html report `target/pit-reports/<a date here>/index.html`

#### Study the mutants that survived or pieces not covered

Often mutants that survive fall on the following categories:

* Unused code
* Needs more multiple users and seeds to be covered (more variation)
* ...

## Step: Add a test for multiple seeds

```java
	@Test
	public void can_run_controlled_game_for_multiple_seeds() {
		Integer[] seeds = {1,2};

		Approvals.verifyAll(seeds,seed -> runGame(seed));
	}

```

## Step: Control the players and verify combinations of players and seeds

```java
    @Test
	public void can_run_controlled_game_for_multiple_players() throws Exception {
		Integer[] seeds = {3,7};
		String[][] playerCombinations = {
				{"Chet"},
				{"Chet", "Jean"},
				
		};

		CombinationApprovals.verifyAllCombinations(this::runGameForSeedAndPlayers, seeds, playerCombinations);
	}
```
