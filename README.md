# Autocomplete Engine

## Running the program:
1. Create a copy of this repository on your machine by running `git clone` followed by the URL for this repository.
2. Navigate to the repository on your machine by running `cd Autocomplete-Engine`.
    - You can confirm that this worked by running `git status` and seeing the output:
    ```
    On branch master
    Your branch is up to date with 'origin/master'.
    
    nothing to commit, working tree clean
    ```
3. Compile the program by running `javac ac_test.java`.
4. Finally, run the program by running `java ac_test`.

## How the program works
* The program will accept single letters followed by `Enter` when you are inputting your word.
	* Inputting more than a single letter at a time will result in the program only recording the first letter of the input and nothing afterwards.
* To denote that a prediction by the program is correct, enter the number that appears to the left of the prediction followed by `Enter` and the next word will start.
	* Do NOT include the parenthesis around the number.
	* Do NOT enter a number if it does not represent a valid prediction as it will crash the program.
* If you have finished entering your word and it was not predicted by the program, you can enter `$` followed by `Enter` and the next word will start.
* If at any point you wish to exit the program, you can enter `!` followed by `Enter`.

An example run of the program would proceed as follows:

```
Enter your first character: t

(0.000251 s)
Predictions:
(1) t    (2) ta    (3) tab    (4) tab's    (5) tabbed    

Enter the next character:  h               

(0.000159 s)
Predictions:
(1) thalami    (2) thalamus    (3) thalamus's    (4) thalidomide    (5) thalidomide's    

Enter the next character:  e

(0.000052 s)
Predictions:
(1) the    (2) theater    (3) theater's    (4) theatergoer    (5) theatergoer's    

Enter the next character:  r

(0.000225 s)
Predictions:
(1) therapeutic    (2) therapeutically    (3) therapeutics    (4) therapeutics's    (5) therapies    

Enter the next character:  e

(0.000182 s)
Predictions:
(1) there    (2) there's    (3) thereabout    (4) thereabouts    (5) thereafter    

Enter the next character:  3


 WORD COMPLETED:  thereabout

Enter first character of the next word: t

(0.000128 s)
Predictions:
(1) thereabout    (2) t    (3) ta    (4) tab    (5) tab's        

Enter the next character:  !


Average time:  0.000145 s
Bye!
```

### Notes
* Every time you finish a word, whether by denoting that a prediction was correct or using `$`, that word is added to your history file and taken into consideration for future predictions.
	* Your history file persists across different runs of the program.
* Inputs are case-sensitive.
* It is assumed that no spelling errors are made when you are entering words.
