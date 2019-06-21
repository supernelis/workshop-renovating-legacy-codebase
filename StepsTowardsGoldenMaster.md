# The road towards an effective golden master

The goal of the first part of the workshop is for you to build up an effective golden master. There are several important steps to get there.

1. Understand what to observe
2. Capture the output
3. Make the tests reproducible
4. Verify the result with a golden master
5. Check if the golden master is effective
6. Add more variations

## Step: Understand what to observe

The first step in building up an effective golden master is to understand what to observe. So lets start with *running the game*.

JAVA: Run the main in GameRunner. Briefly look to the output.

JS: **TODO** 

Run the game several times. Briefly study the code of the GameRunner.

***Questions to answer:***

* **What do you see that will make it difficult for you to do tests?**
* **Which output needs to be captured?**

## Step: Capture the output

One thing to notice when studying the program is that the only feedback you get is on the console output. So how can you compare the your output with the golden master in this case?

You can try for yourselve, or look to our examples below.

### Example for Java

Java provides a way to capture the console output. The trick is to define your own stream, and overwrite the System.out stream with the one you control. Typically we add a helper method in a test. So setup: 


```java

public class GoldenMasterTest {

	@Test
	public void can_run_a_controlled_game() {
			String result = runGame();

            System.out.println(result)
	}

   	public String runGame() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(outputStream, true);

		PrintStream oldOut = System.out;
		System.setOut(printStream);
		
		GameRunner.runGame();

		System.setOut(oldOut);

		return outputStream.toString();
	}
}

```

In this example, outputStream.toString() contains the output of our test.

### Example for Javascript

**TODO**

## Step: Make the tests reproducible

The GameRunner uses a random nummer generator, causing the output of a run to differ every time. In this way it will be very hard to capture the output and compare it to the golden master, as every output will be different. To make such code testible, it is needed to control the randomness in the tests (so it is only really random in the production code).

### Control randomness in Java

Java provides a way to do this, namely by passing a seed to random. Using this seed will produce the same sequence of random numbers every time. 

```java
    int seed = 1;                       // a seed for the random generator
    Random rand = new Random(seed);
    rand.nextInt();                     // will always produce the exact same result
```

To allow running the game with such controlled randomGenerator we must extract a runGame in the gameRunner with a random number generator as parameter.

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

### Control randomness in Javascript

**TODO**

## Step: Verify the result with a golden master

As we now have a reproducible output to compare, we still need to do the actual comparison. We could write something ourselves, but actually there is a great tool to use in this context called [Approval Tests](http://approvaltests.com/). 

The essense of ApprovalTests is that it will keep track of golden master (called the approved version) and will compare the output of a testrun (called the received version) with the golden master. If you made functional changes that change the output, you are supposed to check it manually and upgrade the received to the approved version.

The simplest way is to add a simple verify step.
In java:
```java
	@Test
	public void can_run_a_controlled_game() {
			String result = runGame(1);

			Approvals.verify(result);
	}
```

In Javascript: 

**TODO**

On the first run the test will still fail, as it lacks an approved version with the correct content. If you are sure this is the version you want to start from you can rename the received file to the approved file and run the tests again.

## Step: Check the quality of your tests

Now we have a golden master test we still need to check if it is effective. For this we are mainly intested in the Game class. This is done in two steps:

* Check the code coverage.
* Use mutation testing

### Check the code coverage 

Run your tests with code coverage and check which parts are covered.

*Java/IntelliJ* users can enable a more advanced tracking of code coverage by enabling tracing. By using tracing it also looks if all possible branches in an if are considered.
 
Click on your build configuration for test -> Edit Configuration -> Code coverage Tab -> Tracing.

*Java/Maven* is another possibility that we configured for you. We use a maven plugin for this. Please execute the following command:

```bash
mvn clean test jacoco:report
```

Next you can open the file `target/site/jacoco/index.html` containing the coverage report.

*Javascript* **TODO**

**Which parts of the code are not coverd yet? Why?** 

### Use mutation testing

The idea behind the mutation testing is that the mutation testing tool produces mutants (i.e. versions of your code with changes) that need to be killed by the tests (i.e. make the tests fail). Every mutant that survives might indicate to a test that insufficiently covers such case.

We already configured maven to run the mutation testing easily. To check if maven is working well:

```bash
mvn clean test -DwithHistory org.pitest:pitest-maven:mutationCoverage
```

If you open the browser and go to: the html report `target/pit-reports/<a date here>/index.html`

**Which mutants survived? Why?**

### Some hints

Often uncovered parts or mutants that survive fall on the following categories:

* Dead code (code that is not used)
* Code that is not covered by tests
* Needs more multiple users and seeds to be covered (more variation)

## Step: Add more variation in the golden master

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
