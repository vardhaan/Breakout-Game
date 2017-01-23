import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/* Created by Hamsa Pillai
 * This class is used to create a new paddle with different properties in a game level.
 * It depends on GameLevel to determine its dimensions.*/

public class Paddle {
	
	private ImageView paddleImageView;
	private boolean wrapAround;
	private double paddleSpeed;
	private double paddleWidth;
	private double paddleHeight;
	
	//might need to do paddleHeight too?
	
	
	public static final String PADDLE_IMG = "paddle.gif";
	public static final double BOSS_LEVEL_SPEED_UP = 1.3; //130%
	public static final double BASE_PADDLE_SPEED_RATIO = 0.1; //10% of screen-size
	public static final double BASE_PADDLE_WIDTH_RATIO = 0.3;
	public static final double BASE_PADDLE_HEIGHT_RATIO = 0.05;
	
	
	public Paddle(GameLevel currentLvl) {
		paddleSpeed = BASE_PADDLE_SPEED_RATIO*currentLvl.getLevelWidth();
		int currGameLvl = currentLvl.getLevelNumber();
		if (currGameLvl >= 2) {
			wrapAround = true;
		}
		if (currGameLvl == 4) {
			paddleSpeed = (int) BOSS_LEVEL_SPEED_UP*paddleSpeed;
		}
		Image paddleImg = new Image(getClass().getClassLoader().getResourceAsStream(PADDLE_IMG));
		paddleImageView = new ImageView(paddleImg);
		paddleWidth =  (currentLvl.getLevelWidth()*BASE_PADDLE_WIDTH_RATIO);
		this.setPaddleWidth((double) paddleWidth);
		paddleHeight =  (currentLvl.getLevelHeight()*BASE_PADDLE_HEIGHT_RATIO);
		this.setPaddleHeight((double) paddleHeight);
		System.out.println(this.getPaddleView().getX());
		System.out.println(this.getPaddleView().getBoundsInLocal().getMinX());
		
	}
	
	public ImageView getPaddleView() {
		return this.paddleImageView;
	}
	
	public double getSpeed() {
		return this.paddleSpeed;
	}
	
	public boolean getWrapAround() {
		return this.wrapAround;
	}
	
	public void setPaddleWidth(double newPaddleWidth) {
		paddleImageView.setFitWidth(newPaddleWidth);
		this.paddleWidth = newPaddleWidth;
	}
	
	public void setPaddleHeight(double newPaddleHeight) {
		paddleImageView.setFitHeight(newPaddleHeight);
		this.paddleHeight = newPaddleHeight;
	}
	
	public void setPaddleX(double xLocation) {
		this.paddleImageView.setX(xLocation);
	}
	
	public void setPaddleY(double yLocation) {
		this.paddleImageView.setY(yLocation);
	}
	
	public double getPaddleWidth() {
		return this.paddleWidth;
	}
	
	public void setPaddleSpeed(double newSpeed) {
		this.paddleSpeed = newSpeed;
	}
	
	public double getPaddleHeight() {
		return this.paddleHeight;
	}
	
}
