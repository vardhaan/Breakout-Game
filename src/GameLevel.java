import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class GameLevel {

	/* Created by Hamsa Pillai
	 * This class holds all the relevant values for a single level. This
	 * class is used to determine the layout of each level, in terms of bricks and images.
	 * This class depends on MainGameLauncher telling it the scene width and height.
	 * It does not depend on other classes.
	 * This class should be used to create a new level for the game.*/
	
	private int numberOfLives;
	private int numberBrickRows;
	private int levelNumber;
	private HashMap<Integer, Integer[]> bricksOnRow;
	private int levelWidth; //central place to store how big scene will be
	private int levelHeight;
	
	public static final int LOSE_A_LIFE = -1;
	public static final int BASE_LIVES = 3;
	
	public GameLevel(int levelNum, int sceneWidth, int sceneHeight) {
		levelHeight = sceneHeight;
		levelWidth = sceneWidth;
		numberOfLives = 3;
		levelNumber = levelNum;
		if (levelNum != 0) {
			String fileChooser = "level" + levelNum + ".txt";
			System.out.println(fileChooser);
			HashMap<Integer, Integer[]> brickConfiguration = findNumBricksPerRow(fileChooser);
			bricksOnRow = brickConfiguration;
		}
	}
	
	
	
	// MIGHT HAVE TO USE SCANNER LIKE U USED FOR IMAGES (GET CLASS.LOADER ETC)!!!!!!!!!!!!!!!!
	public HashMap<Integer, Integer[]> findNumBricksPerRow(String fileWithBrickConfig) {
		HashMap<Integer, Integer[]> configStore = new HashMap<Integer, Integer[]>();
		try {
			Scanner scan = new Scanner(new File(getClass().getClassLoader().getResource(fileWithBrickConfig).getFile()));
			numberBrickRows = scan.nextInt();
			for (int i=0;i<numberBrickRows;i++) {
				int numBricksOnRow = scan.nextInt();
				Integer[] bricksOnRow = new Integer[numBricksOnRow];
				int typeBrickOnRow = scan.nextInt();
				for (int j=0;j<numBricksOnRow;j++) {
					bricksOnRow[j] = typeBrickOnRow;
				}
				configStore.put(i+1, bricksOnRow); //row number starts at 1, not 0
				//config files must have one int saying number of rows, then for each line have 2 numbers
			}
			scan.close();
		} catch (FileNotFoundException e) {
			System.out.println("What;s going on?");
			e.printStackTrace();
		}
		return configStore;
		
	}
	
	public HashMap<Integer, Integer[]> getBricksOnRow() {
		return this.bricksOnRow;
	}
	
	public int getLevelNumber() {
		return this.levelNumber;
	}
	
	public int getNumLives() {
		return this.numberOfLives;
	}
	
	public void changeNumLives(int downOrUp) {
		if (downOrUp == LOSE_A_LIFE) {
			this.numberOfLives-=1;
		} else {
			this.numberOfLives+=1;
		}
	}
	
	public int getLevelWidth() {
		return levelWidth;
	}
	
	public int getLevelHeight() {
		return levelHeight;
	}
	
	public int getNumRows() {
		return this.numberBrickRows;
	}
}
