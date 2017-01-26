import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;

/**
 * Masterpiece file
 * @author Vardhaan
 *
 *This is my masterpiece class. For this, I refactored out part of my
 *very long step function to another class. I used the DRY principle 
 *significantly cut down on repetition in the code. I also broke up
 *big chunks of code into calls to other methods. I believe both
 *improve the design and understandability of the class. The
 *application of the DRY principle also allows the game to be more
 *easily modified to implement new features. I also generally
 *followed the 20-line heuristic and I found that it definitely
 *makes code easier to read and understand.
 *Also, making this a class, allows someone to create a new
 *physics engine for the game and implement that in one simple step
 *in MainGameLauncher.java.
 *
 */

public class Physics {

	public class BrickBreakerPhysics extends Physics {
		
		
		private ArrayList<Brick> brickCollection;
		private ArrayList<Ball> ballCollection;
		private ArrayList<Paddle> paddleCollection;
		private ArrayList<ImageView> heartCollection;
		private ArrayList<Ball> ballToRemove;
		private ArrayList<Paddle> paddleToRemove;
		
		private Group root;
		private boolean ballIsIndestructible;

		public static final int BASIC_WINDOW_WIDTH= 600;
		public static final int BASIC_WINDOW_HEIGHT = 600;
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
		public static final int TOP_WALL = 0;
		public static final int RIGHT_WALL = 1;
		public static final int BOTTOM_WALL = 2;
		public static final int LEFT_WALL = 3;
		public static final int NO_WALL_HIT = -1;
		
		public void step(double elapsedTime, GameLevel currentLevel) {
			
			ArrayList<Brick> bricksToRemove = new ArrayList<Brick>();
			
			for (Ball ballToCheck : ballCollection) {
				
				checkAndHandleBallBrickCollisions(ballToCheck, bricksToRemove);
				
				checkAndHandleBallPaddleCollisions(ballToCheck);
				
				checkAndHandleWallCollision(ballToCheck, currentLevel);
				
			}
			
			removePaddlesAndBalls(currentLevel); 
			
			removeBricks(bricksToRemove);
			
		} 
		
		public boolean checkCollision(ImageView objectA, ImageView objectB) {
			return objectA.getBoundsInLocal().intersects(objectB.getBoundsInLocal());
	}
		
		public void removeBricks(ArrayList<Brick> bricksToRemove) {
			ArrayList<Brick> bricksToDelete = new ArrayList<Brick>();
			ArrayList<ImageView> brickImages = new ArrayList<ImageView>();
			for (Brick brickRemove : bricksToRemove) {
				brickRemove.decrementNumHitsBreak();
				if (ballIsIndestructible) {
					brickRemove.setNumHitsBreak(0);
				}
				if (brickRemove.getNumHitsBreak() == 0) {
					bricksToDelete.add(brickRemove);
					brickImages.add(brickRemove.getImgView());
				}
			}
			removeImageViews(brickImages);
			removeItems(bricksToRemove, brickCollection);
		}
		
		public void checkAndHandleBallPaddleCollisions(Ball ballToCheck) {
			for (Paddle paddle : paddleCollection) {
				if (checkCollision(ballToCheck.getBallImgView(), paddle.getPaddleView())) {
					handlePaddleCollision(ballToCheck, paddle);
				}
			}
		}
		
		public void checkAndHandleBallBrickCollisions(Ball ballToCheck, ArrayList<Brick> bricksToRemove) {
			for (Brick brickToCheck : brickCollection) {
				if (checkCollision(ballToCheck.getBallImgView(), brickToCheck.getImgView())) {
					handleBallBrickCollision(ballToCheck, brickToCheck);
					bricksToRemove.add(brickToCheck);
				}
			}
		}
		
		public void handleBallBrickCollision(Ball ballCollision, Brick brickCollision) {
			int whichSideBallHits = whichSideBrickCollision(ballCollision, brickCollision);
			if (whichSideBallHits == TOP_WALL || whichSideBallHits == BOTTOM_WALL) {
				ballCollision.reverseBallYVelo();
			} else {
				ballCollision.reverseBallXVelo();
			}
		}
		
		public int whichSideBrickCollision(Ball ballCollision, Brick brickCollision) {
			ImageView brkImg = brickCollision.getImgView();
			Line[] brickBoundaries = initializeWallBounds(brkImg.getX(), brkImg.getY(), brkImg.getFitWidth(), brkImg.getFitHeight());
			return checkWallCollision(ballCollision, brickBoundaries);
		}
		
		public void removeImageViews(ArrayList<ImageView> toRemove) {
			for (ImageView remove : toRemove) {
				root.getChildren().remove(remove);
			}
		}
		
		public void checkAndHandleWallCollision(Ball ballToCheck, GameLevel currentLevel) {
			Line[] lineCollection = initializeWallBounds(0.0, 0.0, BASIC_WINDOW_WIDTH, BASIC_WINDOW_HEIGHT );
			int ballCollidesWith = checkWallCollision(ballToCheck, lineCollection);
			if (ballCollidesWith != NO_WALL_HIT) {
				if (ballCollidesWith == LEFT_WALL || ballCollidesWith == RIGHT_WALL) {
					ballToCheck.reverseBallXVelo();
				}
				if (ballCollidesWith == TOP_WALL) {
					ballToCheck.reverseBallYVelo();
				}
				if (ballCollidesWith == BOTTOM_WALL) {
					removeALife(currentLevel);
				}
			}
			
		}
		
		public void removeALife(GameLevel currentLevel) {
			currentLevel.changeNumLives(GameLevel.LOSE_A_LIFE);
			if(!heartCollection.isEmpty()) {
				heartCollection.remove(0);
				addPaddleAndBallToRemove();
			}
		}
		
		public int checkWallCollision(Ball ballToCheck, Line[] lines) {
			ImageView ballView = ballToCheck.getBallImgView();
			for (int counter=0;counter<lines.length;counter++) {
				if(ballView.intersects(lines[counter].getBoundsInLocal())) {
					return counter;
				}
			}
			return NO_WALL_HIT;
		}
		
		public void addPaddleAndBallToRemove() {
			ballToRemove = new ArrayList<Ball>();
			paddleToRemove = new ArrayList<Paddle>();
			for (Ball toRemove : ballCollection) {
				ballToRemove.add(toRemove);
			}
			for (Paddle toRemove : paddleCollection) {
				paddleToRemove.add(toRemove);
			}
		}
		
		public void removePaddlesAndBalls(GameLevel currentLevel) {
			ArrayList<ImageView> paddleImagesRemove = new ArrayList<ImageView>();
			ArrayList<ImageView> ballImagesRemove = new ArrayList<ImageView>();
			for (Paddle rem : paddleToRemove) {
				paddleImagesRemove.add(rem.getPaddleView());
			}
			for (Ball rem : ballToRemove) {
				ballImagesRemove.add(rem.getBallImgView());
			}
			removeImageViews(paddleImagesRemove);
			removeImageViews(ballImagesRemove);
			removeItems(paddleToRemove, paddleCollection);
			removeItems(ballToRemove, ballCollection);
			paddleToRemove.clear(); 
			paddleCollection.clear();
			ballToRemove.clear();
			ballCollection.clear();
		}
		
		public void removeItems(ArrayList<?> toRemove, ArrayList<?> toRemoveFrom) {
			if ((!toRemove.isEmpty() && !(toRemove == null))) {
				for (Object remove : toRemove) {
					toRemoveFrom.remove(remove);
				}
				toRemove.clear();
			}
		}
		public Line[] initializeWallBounds(double topLeftX, double topLeftY, double width, double height) {
			Line[] lines = new Line[4];
			lines[TOP_WALL] = new Line(topLeftX, topLeftY, topLeftX+width, topLeftY);
			lines[RIGHT_WALL] = new Line(topLeftX+width, topLeftY, topLeftX+width, topLeftY+height);
			lines[BOTTOM_WALL] = new Line(topLeftX+width, topLeftY+height, topLeftX, topLeftY+height);
			lines[LEFT_WALL] = new Line(topLeftX, topLeftY+height, topLeftX,topLeftY);
			return lines;
		}
		
		public void handlePaddleCollision(Ball ball, Paddle paddle) {
			double xCoordinateCollision = ball.getBallImgView().getX() + (ball.getBallImgView().getBoundsInLocal().getWidth()/2.0);
			double newBallXSpeed = ballXSpeedDeterminer(xCoordinateCollision, paddle);
			double newBallYSpeed = -1*(Math.sqrt((Ball.BASE_BALL_SPEED*Ball.BASE_BALL_SPEED) - (newBallXSpeed*newBallXSpeed)));
			ball.setXVelo(newBallXSpeed);
			ball.setYVelo(newBallYSpeed);
		}
		
		public double ballXSpeedDeterminer(double xCoordinate, Paddle paddle) {
			double paddleMiddle = paddle.getPaddleView().getX() + (paddle.getPaddleView().getBoundsInLocal().getWidth()/2.0);
			double difference = xCoordinate - (paddleMiddle);
			double ratio = difference/(paddle.getPaddleView().getBoundsInLocal().getWidth()/2.0);
			double newXSpeed = ratio*Ball.PADDLE_EDGE_HIT_SPEED_X;
			return newXSpeed;
		}
}
}
