import java.util.Random;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/* This class is used to create a Ball with different properties. 
 * It does not depend on other classes. Use this when creating a new ball in the game.*/

public class Ball {

	private ImageView ballImageView;
	private boolean bounceOffAllWalls;
	private double xVelocity;
	private double yVelocity;
	private double overallVelo;
	
	
	public static final String BALL_IMG = "ball.gif";
	public static final int BASE_BALL_SPEED = 3;
	public static final double PADDLE_EDGE_HIT_SPEED_X = 2.5;
	
	
	public Ball(boolean bounceAll) {
		Image ballImg = new Image(getClass().getClassLoader().getResourceAsStream(BALL_IMG));
		this.ballImageView = new ImageView(ballImg);
		this.bounceOffAllWalls = bounceAll;
		overallVelo = BASE_BALL_SPEED;
		this.setInitialXandYVelocity();
	}
	
		
	public void setInitialXandYVelocity() {
		Random initialXVelocityDeterminer = new Random();
		double initialXVelocity = (double) initialXVelocityDeterminer.nextInt((int) (2*overallVelo));
		if (initialXVelocity >= 5) {
			initialXVelocity = 5;
		}
		if (initialXVelocity == 0) {
			initialXVelocity = 1;
		}
		initialXVelocity = initialXVelocity - overallVelo; //sets range between -10 and 10 for initial X
		double initialYVelocity = Math.sqrt((double) ((overallVelo*overallVelo) - (initialXVelocity*initialXVelocity)));
		if (initialXVelocityDeterminer.nextInt(10) < 5) {
			initialYVelocity *= -1;
		}
		this.xVelocity = initialXVelocity;
		this.yVelocity = initialYVelocity;
	}
		
	
	public boolean getBallBounceAllWalls() {
		return this.bounceOffAllWalls;
	}
	
	public ImageView getBallImgView() {
		return this.ballImageView;
	}
	
	public void setBallX(double newX) {
		this.ballImageView.setX(newX);
	}
	
	public void setBallY(double newY) {
		this.ballImageView.setY(newY);
	}
	
	public double getBallXVelo() {
		return this.xVelocity;
	}
	
	public double getBallYVelo() {
		return this.yVelocity;
	}
	public void setOverallVelo(double newVelo) {
		this.overallVelo = newVelo;
	}
	
	public double getOverallVelo() {
		return this.overallVelo;
	}
	
	public void setXVelo(double newXVelo) {
		this.xVelocity = newXVelo;
	}
	
	public void setYVelo(double newYVelo) {
		this.yVelocity = newYVelo;
	}
	
}
