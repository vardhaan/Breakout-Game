
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


/* Created by Hamsa Pillai, start method taken from ExampleBounce by Robert Duvall
 * This is the Class within which the Game actually launches
 * It depends on the Ball, Brick, GameLevel, and Paddle Classes.
 * This contains the bulk of the code for the game and should be used to create a new game.*/

public class MainGameLauncher extends Application {
		public static final int BASIC_WINDOW_WIDTH= 600;
		public static final int BASIC_WINDOW_HEIGHT = 600;
		public static final Paint BACKGROUND_COLOR = Color.BLACK;
		public static final String TITLE = "BREAKOUT FIGHTS BACK"; 
		public static final int FRAMES_PER_SECOND = 60;
		public static final double MILLISECOND_DELAY = 1000.0/FRAMES_PER_SECOND;
		public static final double SECOND_DELAY = 1.0/FRAMES_PER_SECOND;
		public static final int MOVE_LEFT = 1;
		public static final int MOVE_RIGHT = -1;
		public static final double MARGIN_OF_ERROR_FOR_PADDLE_HIT = 10;
		public static final int NUMBER_POWER_UPS = 3;
		public static final String LASER_IMG = "brick9.gif";
		public static final String MISSILE_IMG = "laserpower.gif";
		public static final double WEAPON_WIDTH_RATIO = 0.05;
		public static final double WEAPON_HEIGHT_RATIO = 0.09;
		public static final double OVERALL_MISSILE_VELO = 4;
		public static final String HEART_IMG = "sizepower.gif";
		
		
		private ArrayList<Brick> brickCollection;
		private ArrayList<Ball> ballCollection;
		private ArrayList<Paddle> paddleCollection;
		private Group root;
		private Scene gameScene;
		private ArrayList<Ball> ballToRemove;
		private ArrayList<Paddle> paddleToRemove;
		private Timeline animation;
		private boolean ballIsIndestructible;
		private boolean powerUpInEffect;
		private boolean ballHitBrickSincePowerUp;
		private boolean laserFromEnemyExists;
		private ArrayList<ImageView> missileCollection;
		private double missileXVelocity;
		private double missileYVelocity;
		private ArrayList<ImageView> missilesToDestroy;
		private int numBricksRemaining;
		private ArrayList<ImageView> hearts;
		private ArrayList<ImageView> heartsToRemove;
	
		
		
		public static final Paint TEXT_COLOR = Color.WHITE;
		public static final Font TEXT_FONT = Font.font("verdana");
		public static final String SPLASH_SCREEN_TEXT = "Welcome to Breakout Strikes Back. In this game, "
				+ "you will need to use a paddle to hit a ball to break bricks. However, the bricks FIGHT BACK. "
				+ "You will have three lives per level and there are 4 levels. There are also powerups for your paddle "
				+ "and cheatcodes that will be revealed after you beat the game. Good luck! Press ENTER to begin.";
		public static final double POSITION_Y_MAIN_SPLASH_TEXT = 0.2;
		public static final String GAME_WON_TEXT = "CONGRATULATIONS! You beat the game! As promised, here are the cheatcodes: \n"
				+ "Press R to reset the ball and paddle to their original positions \n" + "Press C to skip the level \n" 
						+ "Press X to make the paddle the length of the screen \n"
						+ "Press Z to make the ball destroy every block until it hits the paddle again \n"
						+ "Press V to make the paddle move 1.5x faster!";
		public static final String GAME_LOST_TEXT = "YOU LOST! QUIT AND TRY AGAIN!";
		
		
		
	@Override
	public void start(Stage primaryStage) {
		  	Scene scene = loadSplashScreen(); //need to still make scene
	        primaryStage.setScene(scene);
	        primaryStage.setTitle(TITLE);
	        gameScene = scene;
	        primaryStage.show();
	        animation = new Timeline();
	      
	        
	}
	
	/* This method starts the timeline animation for each level */

	public void KeyframePlay(GameLevel currentLvl) {
		  KeyFrame frame = new KeyFrame(Duration.millis(MILLISECOND_DELAY),
                  e -> step(SECOND_DELAY, currentLvl));
		  if (animation!=null) {
		  animation.stop();
		  }
		  animation = new Timeline();
		  animation.setCycleCount(Timeline.INDEFINITE);
		  animation.getKeyFrames().add(frame);
		  animation.play();
	}

	
	/* This method initializes a new game level */
	public void loadPlayingLevel(int levelNumber) {
		//System.out.println("This was triggered");
		root.getChildren().clear();
		if(levelNumber == 5) {
			showEndScreen();
			
		}
		heartsToRemove = new ArrayList<ImageView>();
		hearts = new ArrayList<ImageView>();
		Image heartImage = new Image(getClass().getClassLoader().getResourceAsStream(HEART_IMG));
		for (int heartCounter = 1;heartCounter<=GameLevel.BASE_LIVES;heartCounter++) {
			ImageView newHeart = new ImageView(heartImage);
			newHeart.setX(BASIC_WINDOW_WIDTH - (newHeart.getBoundsInLocal().getWidth()*heartCounter));
			newHeart.setY(BASIC_WINDOW_HEIGHT - newHeart.getBoundsInLocal().getHeight());
			root.getChildren().add(newHeart);
			hearts.add(newHeart);
			}
		numBricksRemaining = 0;
		missileCollection = new ArrayList<ImageView>();
		missilesToDestroy = new ArrayList<ImageView>();
		GameLevel newLevel = new GameLevel(levelNumber, BASIC_WINDOW_WIDTH, BASIC_WINDOW_HEIGHT);
		gameScene.setOnKeyPressed(e -> handleGameKeyInput(e.getCode(), newLevel));
		extractInfoAndAddBricks(newLevel);
		initializePaddleAndBall(newLevel);
		KeyframePlay(newLevel);
		powerUpInEffect = false;
		ballHitBrickSincePowerUp = false;
	}
	
	
	/* This method assigns actions to user-input*/
	public void handleGameKeyInput(KeyCode code, GameLevel currentLevel) {
		handlePaddleInputs(code);
		handleCheatCodeInputs(code, currentLevel);
	}
	
	/* Starts the cheat code corresponding to the user input */
	public void handleCheatCodeInputs(KeyCode code, GameLevel currentLevel) {
		if (code == KeyCode.R) {
			removePaddleAndBall();
			removePaddleAndBallII(currentLevel);
		}
		if (code == KeyCode.C) {
			if (currentLevel.getLevelNumber() == 4) {
				showEndScreen();
			} else {
			loadPlayingLevel(currentLevel.getLevelNumber() + 1);
			}
		}
		if (code == KeyCode.X) {
			powerUpInitialize(0);
		}
		if (code == KeyCode.Z) {
			powerUpInitialize(1);
		}
		if (code == KeyCode.V) {
			powerUpInitialize(2);
		}
	}
	
	
	/* Moves the paddle depending on the paddle location and user input */
	public void handlePaddleInputs(KeyCode code) {
		if (code == KeyCode.A || code == KeyCode.LEFT) {
			setNewPaddleLocation(MOVE_LEFT);
		}
		if (code == KeyCode.D || code == KeyCode.RIGHT) {
			setNewPaddleLocation(MOVE_RIGHT);
		}
	}
	
	
	/* Determines how much to move the paddle by depending on proximity to edges */
	public void setNewPaddleLocation(int leftOrRight) {
		for (Paddle p : paddleCollection) {
			
				//System.out.println(p.getPaddleView().getX() + p.getPaddleView().getFitWidth());
				if (p.getPaddleView().getX()==0 && p.getPaddleView().getX() + p.getPaddleView().getFitWidth() == BASIC_WINDOW_WIDTH) {
					//System.out.println("THIS IS HAPPENING");
				}
				else if (leftOrRight == MOVE_LEFT && (p.getPaddleView().getX() < p.getSpeed())) {
					p.setPaddleX(0);
					
				}
				else if (leftOrRight == MOVE_RIGHT && (p.getPaddleView().getX() + p.getPaddleWidth() >= BASIC_WINDOW_WIDTH - p.getSpeed())) {
					p.setPaddleX(BASIC_WINDOW_WIDTH - p.getPaddleWidth());
				}
				else if ((p.getPaddleView().getX() == 0) && leftOrRight == MOVE_RIGHT) {
					p.setPaddleX(p.getPaddleView().getX() + p.getSpeed());
				}
				else if ((p.getPaddleView().getX() + p.getPaddleWidth() == BASIC_WINDOW_WIDTH) && leftOrRight == MOVE_LEFT) {
					p.setPaddleX(p.getPaddleView().getX() - p.getSpeed());
				}
				else if ((p.getPaddleView().getX() + p.getPaddleWidth() < BASIC_WINDOW_WIDTH) && (p.getPaddleView().getX() > 0) ) {
						p.setPaddleX(p.getPaddleView().getX() - (leftOrRight)*p.getSpeed());
				}
				
			
			
		}
	}
	
	/* Loads the intro screen that gives the instructions */
	public Scene loadSplashScreen() {
		root = new Group();
		Text splashText = new Text("BREAKOUT STRIKES BACK");
		splashText.setFill(TEXT_COLOR);
		splashText.setFont(TEXT_FONT);
		int textWidth = (int) splashText.getBoundsInLocal().getWidth();
		splashText.setX((double) ((BASIC_WINDOW_WIDTH/2.0) - (textWidth/2.0)));
		splashText.setY((splashText.getBoundsInLocal().getHeight()));
		root.getChildren().add(splashText);
		Text mainSplashText = createMainSplashText();
		root.getChildren().add(mainSplashText);
		Scene theSplashScene = new Scene(root, BASIC_WINDOW_WIDTH, BASIC_WINDOW_HEIGHT, BACKGROUND_COLOR);
		theSplashScene.setOnKeyPressed(e -> handleSplashKeyInput(e.getCode()));
		return theSplashScene;
	}
	
	/* Loads the first level when user presses Enter */
	public void handleSplashKeyInput(KeyCode code) {
		if (code == KeyCode.ENTER) {
			//System.out.println("This was triggered first");
			loadPlayingLevel(1);
		}
	}
	
	/* Initializes the text to be displayed on the intro screen */
	public Text createMainSplashText() {
		Text splashMain = new Text(SPLASH_SCREEN_TEXT);
		splashMain.setWrappingWidth(BASIC_WINDOW_WIDTH*0.96);
		splashMain.setFill(TEXT_COLOR);
		splashMain.setX(0.02*BASIC_WINDOW_WIDTH);
		splashMain.setY(POSITION_Y_MAIN_SPLASH_TEXT*BASIC_WINDOW_HEIGHT);
		return splashMain;
	}
	
	
	/* Main method that handles interactions between objects and moves/removes objects. Runs 60 times a second */
	public void step(double elapsedTime, GameLevel currentLevel) {
		
		
		ArrayList<Brick> bricksToRemove = new ArrayList<Brick>();
		for (Ball ballToCheck : ballCollection) {
			for (Brick brickToCheck : brickCollection) {
				if (checkAnyCollision(ballToCheck, brickToCheck)) {
					ballHitBrickSincePowerUp = true;
					handleBallBrickCollision(ballToCheck, brickToCheck);
					bricksToRemove.add(brickToCheck);
					
				}
			}
			for (Paddle paddle : paddleCollection) {
				if (checkPaddleCollision(ballToCheck, paddle)) {
					//System.out.println("A paddle collision occurred");
					if (ballIsIndestructible == true && ballHitBrickSincePowerUp == true) {
						ballIsIndestructible = false;
					}
					handlePaddleCollision(ballToCheck, paddle);
				}
			}
			
			if (checkWallCollision(ballToCheck)) {
				handleWallCollision(ballToCheck, currentLevel);
			}
			
			
		}
		
		for (ImageView heart : heartsToRemove) {
			hearts.remove(heart);
			root.getChildren().remove(heart);
		}
		
		
		removePaddleAndBallII(currentLevel);
		
		
		for (Brick brickRemove : bricksToRemove) {
			
			brickRemove.decrementNumHitsBreak();
			if (ballIsIndestructible) {
				brickRemove.setNumHitsBreak(0);
			}
			if (brickRemove.getNumHitsBreak() == 0) {
				root.getChildren().remove(brickRemove.getImgView());
				numBricksRemaining--;
				System.out.println("This is nbr: " + numBricksRemaining);
				brickCollection.remove(brickRemove);
				if (!powerUpInEffect) {
					generateRandomPowerUp();
				}
			}
		}
		
		
		
		
		
		checkLevelWon(currentLevel);
		
		
		for (ImageView missile : missileCollection) {
			for (Paddle p : paddleCollection) {
				if (p.getPaddleView().intersects(missile.getBoundsInLocal())) {
					currentLevel.changeNumLives(GameLevel.LOSE_A_LIFE);
					if (hearts.size() > 0) {
						heartsToRemove.add(hearts.get(0));
					}
					missilesToDestroy.add(missile);
					laserFromEnemyExists = false;
				}
			}
			if (missile.getY() >= BASIC_WINDOW_HEIGHT) {
				missilesToDestroy.add(missile);
				laserFromEnemyExists = false;
			}
		}
		
		for (ImageView heart : heartsToRemove) {
			hearts.remove(heart);
			root.getChildren().remove(heart);
		}
		
		

		checkGameLost(currentLevel);
			
		
		if(!missilesToDestroy.isEmpty()) {
			for (ImageView missileDestroy : missilesToDestroy) {
				missileCollection.remove(missileDestroy);
				root.getChildren().remove(missileDestroy);
			}
		}
		
		for (Brick b : brickCollection) {
			if (laserFromEnemyExists == false) {
				int brickType = b.getBrickType();
				if (brickType == Brick.LASER_BRICK) {
					Image laser = new Image(getClass().getClassLoader().getResourceAsStream(LASER_IMG));
					ImageView laserImageView = new ImageView(laser);
					laserImageView.setFitWidth(BASIC_WINDOW_WIDTH*WEAPON_WIDTH_RATIO);
					laserImageView.setFitHeight(BASIC_WINDOW_HEIGHT*WEAPON_HEIGHT_RATIO);
					laserImageView.setX(b.getImgView().getX() + b.getBrickWidth() - (laserImageView.getBoundsInLocal().getWidth()/2.0));
					laserImageView.setY(b.getImgView().getY() + b.getImgView().getBoundsInLocal().getHeight());
					missileXVelocity = 0;
					missileYVelocity = Math.sqrt(OVERALL_MISSILE_VELO*OVERALL_MISSILE_VELO - (missileXVelocity*missileXVelocity));
					missileCollection.add(laserImageView);
					root.getChildren().add(laserImageView);
					laserFromEnemyExists = true;
		
					
				}
				
				if (brickType == Brick.MISSILE_BRICK) {
					Image laser = new Image(getClass().getClassLoader().getResourceAsStream(MISSILE_IMG));
					ImageView laserImageView = new ImageView(laser);
					laserImageView.setFitWidth(BASIC_WINDOW_WIDTH*WEAPON_WIDTH_RATIO);
					laserImageView.setFitHeight(BASIC_WINDOW_HEIGHT*WEAPON_HEIGHT_RATIO);
					laserImageView.setX(b.getImgView().getX() + b.getBrickWidth() - (laserImageView.getBoundsInLocal().getWidth()/2.0));
					laserImageView.setY(b.getImgView().getY() + b.getImgView().getBoundsInLocal().getHeight());
					missileXVelocity = determineMissileXVelo(brickType, b);
					missileYVelocity = Math.sqrt(OVERALL_MISSILE_VELO*OVERALL_MISSILE_VELO - (missileXVelocity*missileXVelocity));
					missileCollection.add(laserImageView);
					root.getChildren().add(laserImageView);
					laserFromEnemyExists = true;
				}
			
			}
			
		}
		
		
		
		for (Ball ballNewSpeed : ballCollection) {
			
			ballNewSpeed.setBallX(ballNewSpeed.getBallImgView().getX() + ballNewSpeed.getBallXVelo());
			ballNewSpeed.setBallY(ballNewSpeed.getBallImgView().getY() + ballNewSpeed.getBallYVelo());
			
		}
		
		for (ImageView missile : missileCollection) {
			missile.setX(missile.getX() + missileXVelocity);
			missile.setY(missile.getY() + missileYVelocity);
		}
		
		
		
	}
	
	
	/* Determines the x-direction velocity of the missile coming from a brick */
	public double determineMissileXVelo(int brickType, Brick brickShootingMissile) {
		if(brickType == Brick.LASER_BRICK) {
			return 0;
		} 
		Paddle p = paddleCollection.get(0); 
		double xDistance = (p.getPaddleView().getX() + (p.getPaddleView().getBoundsInLocal().getWidth()/2.0)) - (brickShootingMissile.getImgView().getX() + (brickShootingMissile.getImgView().getBoundsInLocal().getWidth()/2.0));
		double yDistance = (p.getPaddleView().getY() + (p.getPaddleView().getBoundsInLocal().getHeight()/2.0) - (brickShootingMissile.getImgView().getY() + (brickShootingMissile.getImgView().getBoundsInLocal().getWidth())));
		double totalDistance = Math.sqrt(yDistance*yDistance + (xDistance*xDistance));
		double xVelo = (OVERALL_MISSILE_VELO/totalDistance)*(xDistance);
		return xVelo;
		
		
		
	}
	
	/* Checks if a player has beaten a level and loads the next level if so */
	public void checkLevelWon(GameLevel currentLevel) {
		if (brickCollection.isEmpty()) {
			if (currentLevel.getLevelNumber()==4) {
				showEndScreen();
			} else {
			loadPlayingLevel(currentLevel.getLevelNumber()+1);
			}
		}
	}
	
	
	/* Shows the "YOU WON" screen */
	public void showEndScreen() {
		root.getChildren().clear();
		Text winText = new Text(GAME_WON_TEXT);
		winText.setWrappingWidth(BASIC_WINDOW_WIDTH*0.96);
		winText.setFill(TEXT_COLOR);
		winText.setX(0.02*BASIC_WINDOW_WIDTH);
		winText.setY(POSITION_Y_MAIN_SPLASH_TEXT*BASIC_WINDOW_HEIGHT);
		root.getChildren().add(winText);
		
	}
	
	/* Randomly determines if the user will get a power-up */
	public void generateRandomPowerUp() {
		Random chance = new Random();
		int rand = chance.nextInt(10);
		if (rand < 2 ) { //20% chance of powerup
			int powerUpChooser = chance.nextInt(NUMBER_POWER_UPS);
			powerUpInitialize(powerUpChooser);
		}
	}
	
	/* Implements the random power-up in the level */
	public void powerUpInitialize(int typeOfPowerUp) {
		powerUpInEffect = true;
		switch (typeOfPowerUp) {
			case 0:
				for (Paddle p : paddleCollection) {
					p.setPaddleX(0);
					p.getPaddleView().setFitWidth(BASIC_WINDOW_WIDTH);
				}
				break;
			case 1:
				ballIsIndestructible = true;
				break;
			case 2:
				for (Paddle p : paddleCollection) {
					p.setPaddleSpeed(p.getSpeed()*1.5);
				}
		}
	}
	
	/* Checks if the ball hit any walls */
	public boolean checkWallCollision(Ball toCheck) {
		return (toCheck.getBallImgView().getX() +toCheck.getBallImgView().getBoundsInLocal().getWidth() >= BASIC_WINDOW_WIDTH  || toCheck.getBallImgView().getX()<=0 ||
				toCheck.getBallImgView().getY() + toCheck.getBallImgView().getBoundsInLocal().getHeight() >= BASIC_WINDOW_HEIGHT || toCheck.getBallImgView().getY()<=0);
	}
	
	
	/* Determines what happens to the ball depending on the wall it hits */
	public void handleWallCollision(Ball toCheck, GameLevel currentLvl) {
		if (toCheck.getBallImgView().getX() +toCheck.getBallImgView().getBoundsInLocal().getWidth() >=BASIC_WINDOW_WIDTH || toCheck.getBallImgView().getX()<=0 ) {
			toCheck.setXVelo(toCheck.getBallXVelo()*-1);
		}
		if (toCheck.getBallImgView().getY()<=0 ) {
			toCheck.setYVelo(toCheck.getBallYVelo()*-1);
		}
		if (toCheck.getBallImgView().getY() + toCheck.getBallImgView().getBoundsInLocal().getHeight() >= BASIC_WINDOW_HEIGHT) {
			currentLvl.changeNumLives(GameLevel.LOSE_A_LIFE);
			if (hearts.size()>0) {
			heartsToRemove.add(hearts.get(0));
			}
			checkGameLost(currentLvl);
			removePaddleAndBall();
			
		}
	}
	
	
	/* Removes the paddle and ball, to be re-initialized */
	public void removePaddleAndBall() {
		ballToRemove = new ArrayList<Ball>();
		paddleToRemove = new ArrayList<Paddle>();
		for (Ball toRemove : ballCollection) {
			ballToRemove.add(toRemove);
			
			
		}
		for (Paddle toRemove : paddleCollection) {
			paddleToRemove.add(toRemove);
			
		}
	}
	
	/* Complestes the removal of the paddle and ball */
	public void removePaddleAndBallII(GameLevel currentLevel) {
		if (ballToRemove != null && !ballToRemove.isEmpty()) {
			for (Ball toRemove : ballToRemove) {
				root.getChildren().remove(toRemove.getBallImgView());
			}
			
			for (Paddle toRemove : paddleToRemove) {
				root.getChildren().remove(toRemove.getPaddleView());
			}
			paddleCollection.clear();
			ballCollection.clear();
			ballToRemove.clear();
			paddleToRemove.clear();
			initializePaddleAndBall(currentLevel);
		}
	}
	
	/* Checks if the user has lost all his lives */
	public void checkGameLost(GameLevel currentLevel) {
		if (hearts.isEmpty()) {
			root.getChildren().clear();
			Text winText = new Text(GAME_LOST_TEXT);
			winText.setWrappingWidth(BASIC_WINDOW_WIDTH*0.96);
			winText.setFill(TEXT_COLOR);
			winText.setX(0.02*BASIC_WINDOW_WIDTH);
			winText.setY(POSITION_Y_MAIN_SPLASH_TEXT*BASIC_WINDOW_HEIGHT);
			root.getChildren().add(winText);
		}
	}
	
	/* Determines new velocities of the ball when it hits the paddle */
	public void handlePaddleCollision(Ball ball, Paddle paddle) {
		double xCoordinateCollision = ball.getBallImgView().getX() + (ball.getBallImgView().getBoundsInLocal().getWidth()/2.0);
		//System.out.println("this is x coordinate: " + xCoordinateCollision);
		double newBallXSpeed = ballXSpeedDeterminer(xCoordinateCollision, paddle);
		//System.out.println("this is new ballX speed: " + newBallXSpeed);
		double newBallYSpeed = -1*(Math.sqrt((Ball.BASE_BALL_SPEED*Ball.BASE_BALL_SPEED) - (newBallXSpeed*newBallXSpeed)));
		//System.out.println("this is new ballY speed: " + newBallYSpeed);
		
		ball.setXVelo(newBallXSpeed);
		ball.setYVelo(newBallYSpeed);
		
	}
	
	/* Determines x-direction velocity of ball depending on where on paddle it hits */
	public double ballXSpeedDeterminer(double xCoordinate, Paddle paddle) {
		double paddleMiddle = paddle.getPaddleView().getX() + (paddle.getPaddleView().getBoundsInLocal().getWidth()/2.0);
		//System.out.println(paddle.getPaddleView().getBoundsInLocal().getWidth());
		double difference = xCoordinate - (paddleMiddle);
		double ratio = difference/(paddle.getPaddleView().getBoundsInLocal().getWidth()/2.0);
		//System.out.println("this is ratio: " + ratio);
		double newXSpeed = ratio*Ball.PADDLE_EDGE_HIT_SPEED_X;
		return newXSpeed;
		
		
		
	}
	
	/* Checks to see if the paddle and ball hit */
	public boolean checkPaddleCollision(Ball ballToCheck, Paddle paddleToCheck) {
		return paddleToCheck.getPaddleView().contains(ballToCheck.getBallImgView().getX() + (ballToCheck.getBallImgView().getBoundsInLocal().getWidth()/2.0), 
				ballToCheck.getBallImgView().getY()+ballToCheck.getBallImgView().getBoundsInLocal().getHeight());
	}
	
	/* Sets new velocity for ball and determines if a brick loses a life or gets destroyed */
	public void handleBallBrickCollision(Ball ballCollision, Brick brickCollision) {
		System.out.println("Brick colision occurred here");
		System.out.println("Brick location: " + brickCollision.getImgView().getX() + " " + brickCollision.getImgView().getY());
		if (checkBottomCollision(ballCollision, brickCollision) || checkTopCollision(ballCollision, brickCollision)) {
			//System.out.println("Top or Bottom occurred");
			ballCollision.setYVelo(-1*ballCollision.getBallYVelo());
		} else {
			ballCollision.setXVelo(-1*ballCollision.getBallXVelo());
		}
		
	}
	
	/* Checks if ball hit bottom of a brick */
	public boolean checkBottomCollision(Ball ballCollision, Brick brickCollision) {
		Line bottomLine = new Line();
		bottomLine.setStartX(brickCollision.getImgView().getX());
		bottomLine.setStartY(brickCollision.getImgView().getY() + brickCollision.getImgView().getBoundsInLocal().getHeight());
		bottomLine.setEndX(brickCollision.getImgView().getX() + brickCollision.getImgView().getBoundsInLocal().getWidth());
		bottomLine.setStartY(brickCollision.getImgView().getY() + brickCollision.getImgView().getBoundsInLocal().getHeight());
		return bottomLine.intersects(ballCollision.getBallImgView().getBoundsInLocal());
	}
	
	/* Checks if ball hit top of a brick */
	public boolean checkTopCollision(Ball ballCollision, Brick brickCollision) {
		Line topLine = new Line();
		topLine.setStartX(brickCollision.getImgView().getX());
		topLine.setStartY(brickCollision.getImgView().getY());
		topLine.setEndX(brickCollision.getImgView().getX() + brickCollision.getImgView().getBoundsInLocal().getWidth());
		topLine.setEndY(brickCollision.getImgView().getY());
		return topLine.intersects(ballCollision.getBallImgView().getBoundsInLocal());	
		}
	
	/* Returns the middle x-coordinate of a ball */
	public double getMiddleXOfBall(Ball ballMiddle) {
		return ballMiddle.getBallImgView().getX() + (ballMiddle.getBallImgView().getFitWidth()/2.0);
	}
	
	
	/* Updates ball location */
	public void updateBallSpeed(Ball ballToUpdate) {
		ballToUpdate.setBallX((double) (ballToUpdate.getBallImgView().getX() + ballToUpdate.getBallXVelo()));
		ballToUpdate.setBallY((double) (ballToUpdate.getBallImgView().getY() + ballToUpdate.getBallYVelo())); 
	}
	
	/* Checks if a ball hits a brick */
	public boolean checkAnyCollision(Ball ballCheck, Brick brickCheck) {
		return brickCheck.getImgView().getBoundsInLocal().intersects(ballCheck.getBallImgView().getBoundsInLocal());
	}
	
	/* Extracts the level layout and initializes the necessary bricks in this layout */
	public void extractInfoAndAddBricks(GameLevel levelContainingBrickInfo) {
		HashMap<Integer, Integer[]> brickInfo = levelContainingBrickInfo.getBricksOnRow();
		Brick example = new Brick(1, levelContainingBrickInfo);
		int brickHeight = example.getBrickHeight();
		int brickWidth = example.getBrickWidth();
		double startingY = 0;
		int numBrickRows = levelContainingBrickInfo.getNumRows();
		//System.out.println("Hello");
		//System.out.println(numBrickRows);
		brickCollection = new ArrayList<Brick>();
		for (int rowCounter = 1;rowCounter<=numBrickRows;rowCounter++) {
			//System.out.println("Hello2");
			double startingX = 0; //each row starts at the same x coordinate
			int bricksOnRow = brickInfo.get(rowCounter).length;
			for (int brickCounter = 0; brickCounter<bricksOnRow;brickCounter++) {
				//System.out.println("This is it:" +brickInfo.get(rowCounter)[0]);
				Brick toBePlaced = new Brick(brickInfo.get(rowCounter)[0], levelContainingBrickInfo);
				numBricksRemaining++;
				toBePlaced.setBrickX(startingX);
				toBePlaced.setBrickY(startingY);
				root.getChildren().add(toBePlaced.getImgView()); //only imageview can be added to roots
				brickCollection.add(toBePlaced);
				startingX+= brickWidth; 
				
			}
			startingY += brickHeight; //new row of bricks is one brick length below the last row
		}
		
	}
	
	/* Creates a new paddle and ball for the level */
	public void initializePaddleAndBall(GameLevel currentGameLevel) {
		
		paddleCollection = new ArrayList<Paddle>();
		if (!paddleCollection.isEmpty()) {
			paddleCollection.clear();
		}
		if (!missileCollection.isEmpty()) {
			missilesToDestroy = new ArrayList<ImageView>();
			for (ImageView m : missileCollection) {
				missilesToDestroy.add(m);
			}
			for (ImageView missileDestroy : missilesToDestroy) {
				root.getChildren().remove(missileDestroy);
			
			}
			missileCollection.clear();
		}
		Paddle levelPaddle = new Paddle(currentGameLevel);
		levelPaddle.setPaddleX((currentGameLevel.getLevelWidth()/2.0) - levelPaddle.getPaddleWidth()/2.0);
		levelPaddle.setPaddleY(currentGameLevel.getLevelHeight()-(levelPaddle.getPaddleHeight()));
		root.getChildren().add(levelPaddle.getPaddleView());
		paddleCollection.add(levelPaddle);
		Ball gameBall = new Ball(false);
		ballIsIndestructible = false;
		powerUpInEffect = false;
		laserFromEnemyExists = false;
		gameBall.setBallX(BASIC_WINDOW_WIDTH/2.0);
		gameBall.setBallY((BASIC_WINDOW_HEIGHT*(currentGameLevel.getNumRows()*Brick.BRICK_HEIGHT_RATIO)) + 1);
		root.getChildren().add(gameBall.getBallImgView());
		ballCollection = new ArrayList<Ball>();
		ballCollection.add(0, gameBall);
		
	}
	 /* The method that starts it all. */
	 public static void main (String[] args) {
	        launch(args);
	    }

}
