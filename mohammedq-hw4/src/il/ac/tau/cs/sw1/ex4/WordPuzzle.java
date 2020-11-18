package il.ac.tau.cs.sw1.ex4;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class WordPuzzle {
	public static final char HIDDEN_CHAR = '_';
	public static final int MAX_VOCABULARY_SIZE = 3000;
	

	public static String[] scanVocabulary(Scanner scanner) { // Q - 1
		String [] st = new String[MAX_VOCABULARY_SIZE];
		int cnt = 0;
		while (scanner.hasNext() && cnt < (MAX_VOCABULARY_SIZE)){
			String LetterCaseWord = scanner.next().toLowerCase();
			if (isLegalWord(LetterCaseWord) && (!ContainsStr(st, LetterCaseWord))){
				st[cnt] = LetterCaseWord;
				cnt++;
			}
		}
		String [] help = new String[cnt];
		if(help.length > 0) System.arraycopy(st, 0, help,0, cnt);
		Arrays.sort(help);
		return help;
	}

	public static int countHiddenInPuzzle(char[] puzzle) { // Q - 2
		int cnt = 0;
		for (char ch : puzzle){
			if (ch == HIDDEN_CHAR){
				cnt++;
			}
		}
		return cnt;
	}

	public static String getRandomWord(String[] vocabulary, Random generator) { // Q - 3
		return vocabulary[generator.nextInt(vocabulary.length)];
	}

	public static boolean checkLegal(String word, char[] puzzle) { // Q - 4
		boolean b1 = false, b2 = false;
		for (int i = 0; i < word.length(); i++){
			char ch = word.charAt(i), chPz = puzzle[i];

			if (chPz == HIDDEN_CHAR && (!b1)){
				b1 = true;
			}
			if (chPz != HIDDEN_CHAR && (!b2)){
				b2 = true;
			}
			if (chPz == HIDDEN_CHAR){
				if (String.valueOf(puzzle).contains(Character.toString(ch))) {
					return false;
				}
			}
			if (chPz != HIDDEN_CHAR){
				for (int j = i+1; j < word.length(); j++){
					if (word.charAt(j) == chPz){
						if (puzzle[j] == HIDDEN_CHAR){
							return false;
						}
					}
				}
			}

		}

		return b1 && b2;
	}

	public static char[] getRandomPuzzleCandidate(String word, double prob, Random generator) { // Q - 5
		char [] puzzle = new char[word.length()];
		for (int i = 0; i < word.length(); i++){
			if (generator.nextFloat() < prob){
				puzzle[i] = HIDDEN_CHAR;
			}
			else {
				puzzle[i] = word.charAt(i);
			}
		}
		return puzzle;
	}

	public static char[] getRandomPuzzle(String word, double prob, Random generator) { // Q - 6
		for (int i = 0; i < 1000; i++){
			char [] puzzle = getRandomPuzzleCandidate(word, prob, generator);
			if(checkLegal(word, puzzle)){
				return puzzle;
			}
		}
		throwPuzzleGenerationException();
		return null;
	}

	public static int applyGuess(char guess, String solution, char[] puzzle) { // Q - 7
		int cnt = 0;
		for (int i = 0; i < puzzle.length; i++){
			if(puzzle[i] == HIDDEN_CHAR && solution.charAt(i) == guess){
				puzzle[i] = guess;
				cnt++;
			}
		}
		return cnt;
	}

	public static char[] getHelp(String solution, char[] puzzle) { // Q - 8
		for (int i = 0; i < puzzle.length; i++){
			if(puzzle[i] == HIDDEN_CHAR){
				puzzle[i] = solution.charAt(i);
				for (int j = i+1; j < puzzle.length; j++){
					if(solution.charAt(i) == solution.charAt(j)){
						puzzle[j] = solution.charAt(i);
					}
				}
				break;
			}
		}
		return puzzle;
	}

	private static boolean isLegalWord(String word){
		if (word.length() < 2){
			return false;
		}
		for (int i = 0; i < word.length(); i++){
			int x  = word.charAt(i);
			if (!(96 < x && x < 123)){
				return false;
			}
		}
		return true;
	}

	private static boolean ContainsStr(String[] st, String word){
		for (String str: st){
			if (str == null){
				break;
			}
			if (str.equals(word)){
				return true;
			}
		}
		return false;
	}

	private static int HiddenCharCount(char[] puzzle){
		int cnt = 0;
		for (char ch: puzzle){
			if (ch == HIDDEN_CHAR){
				cnt++;
			}
		}
		return cnt;
	}

	private static char[][] getRandomizersFunc(String [] scanVocabulary, double prob, Random generator){
		String randomWord = getRandomWord(scanVocabulary, generator);
		char[] chArray = new char[randomWord.length()];
		for(int i=0; i<randomWord.length(); i++){
			chArray[i] = randomWord.charAt(i);
		}
		return new char[][]{getRandomPuzzle(randomWord, prob, generator), chArray};
	}

	private static float settingStage(){
		printSettingsMessage();
		printEnterHidingProbability();
		Scanner scan1 = new Scanner(System.in);
		return scan1.nextFloat();
	}

	private static String[] VocabularyInitializer(String path) throws FileNotFoundException {
		//System.out.println("Enter a Pathname: ");
		//Scanner scan_path = new Scanner(System.in);
		//String path = scan_path.next();
		File file = new File(path);
		Scanner scanner = new Scanner(file);
		String [] scanVocabulary = scanVocabulary(scanner);
		scanner.close();
		printReadVocabulary(path, scanVocabulary.length);
		return scanVocabulary;
	}

	private static String userResponse(){
		Scanner scan_Response = new Scanner(System.in);
		return scan_Response.next();
	}

	private static String stage_b3(){
		printReplacePuzzleMessage();
		return userResponse();
	}

	private static char[][] newPuzzle(String [] scanVocabulary, double prob, Random generator, char[] puzzle){
		char [][] new_puzzle_and_solution = getRandomizersFunc(scanVocabulary, prob, generator);
		while (Arrays.equals(new_puzzle_and_solution[0], puzzle)){
			new_puzzle_and_solution = getRandomizersFunc(scanVocabulary, prob, generator);
		}
		return new_puzzle_and_solution;
	}

	private static boolean didWin(char[] puzzle, String solution){
		String st_puzzle = new String(puzzle);
		if (HiddenCharCount(puzzle) == 0 && st_puzzle.equals(solution)){
			printWinMessage();
			return true;
		}
		return false;
	}

	private static void gameStage(char[][] puzzle_and_solution){
		printGameStageMessage();
		boolean win = false;
		int tries = countHiddenInPuzzle(puzzle_and_solution[0]) + 3;
		char [] puzzle = puzzle_and_solution[0];
		String solution = new String(puzzle_and_solution[1]);
		while (tries > 0){
			printPuzzle(puzzle);
			printEnterYourGuessMessage();
			String response = userResponse();
			char guess = response.charAt(0);
			int cnt = applyGuess(guess, solution, puzzle);
			tries--;
			if (cnt > 0){
				if (didWin(puzzle, solution)){
					win = true;
					break;
				}
				else {
					printCorrectGuess(tries);
				}
			}
			else {
				if (guess != 'H'){
				printWrongGuess(tries);
				}
				if (guess == 'H'){
					getHelp(solution, puzzle);
					if(didWin(puzzle, solution)){
						win = true;
						break;
					}
				}
			}

		}

		if (!win){ printGameOver(); }

	}

	public static void main(String[] args) throws Exception { // Q - 9

		String [] scanVocabulary = VocabularyInitializer(args[0]); // initializing new vocabulary
		int vocabularySize = scanVocabulary.length;

		float prob = settingStage(); // getting probability

		// Uncomment only one of the generators:
		// Random generator = new MyRandom(new int[]{0,1,2,3,4,5},new float[]{0.0f,0.1f,0.2f,0.3f,0.4f,0.5f,0.6f,0.7f,0.8f,0.9f,1.0f});
		Random generator = new MyRandom(getRrandomIntArr(vocabularySize), getRandomFloatArr());

		// Random generator = new Random ();

		// puzzle stage
		char [][] puzzle_and_solution = getRandomizersFunc(scanVocabulary, prob, generator);

		printPuzzle(puzzle_and_solution[0]);

		String response = stage_b3();


		while (!response.equals("no")){
			if (response.equals("yes")){
				puzzle_and_solution = newPuzzle(scanVocabulary, prob, generator, puzzle_and_solution[0]); // TODO check if we can get the samewordtwice
				printPuzzle(puzzle_and_solution[0]);
			}
			response = stage_b3();
		}

		gameStage(puzzle_and_solution);

	}

	/*************************************************************/
	/********************* Don't change this ********************/
	/*************************************************************/
	private static float[] getRandomFloatArr() {
		Double[] doubleArr = new Double[] { 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 };
		List<Double> doubleList = Arrays.asList(doubleArr);
		Collections.shuffle(doubleList);
		double[] unboxed = doubleList.stream().mapToDouble(Double::doubleValue).toArray();

		// cast double array to float array
		float[] floatArr = new float[unboxed.length];
		for (int i = 0; i < unboxed.length; i++) {
			floatArr[i] = (float) unboxed[i];
		}
		return floatArr;
	}

	private static int[] getRrandomIntArr(int vocabularySize) {
		
		if(vocabularySize<0) {
			throw new RuntimeException("Wrong use of getRandomIntArr(int vocabularySize)");
		}
		
		int i = 0;
		Integer[] intArr = new Integer[vocabularySize];
		while (i < vocabularySize) {
			intArr[i] = i;
			i++;
		}
		List<Integer> doubleList = Arrays.asList(intArr);
		Collections.shuffle(doubleList);
		int[] unboxed = doubleList.stream().mapToInt(Integer::intValue).toArray();
		return unboxed;
	}

	public static void throwPuzzleGenerationException() {
		throw new RuntimeException("Failed creating a legal puzzle after 1000 attempts!");
	}

	public static void printReadVocabulary(String vocabularyFileName, int numOfWords) {
		System.out.println("Read " + numOfWords + " words from " + vocabularyFileName);
	}

	public static void printSettingsMessage() {
		System.out.println("--- Settings stage ---");
	}

	public static void printEnterHidingProbability() {
		System.out.println("Enter your hiding probability:");
	}

	public static void printPuzzle(char[] puzzle) {
		System.out.println(puzzle);
	}

	public static void printReplacePuzzleMessage() {
		System.out.println("Replace puzzle?");
	}

	public static void printGameStageMessage() {
		System.out.println("--- Game stage ---");
	}

	public static void printEnterYourGuessMessage() {
		System.out.println("Enter your guess:");
	}

	public static void printCorrectGuess(int attemptsNum) {
		System.out.println("Correct Guess, " + attemptsNum + " guesses left");
	}

	public static void printWrongGuess(int attemptsNum) {
		System.out.println("Wrong Guess, " + attemptsNum + " guesses left");
	}

	public static void printWinMessage() {
		System.out.println("Congratulations! You solved the puzzle");
	}

	public static void printGameOver() {
		System.out.println("Game over!");
	}

}
