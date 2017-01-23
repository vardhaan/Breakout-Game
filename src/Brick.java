import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/* This class is used to determine the properties of a brick, so that different bricks can exist.
 * This class also automatically assigns an ImageView to the brick.
 * This class should be used in creating another brick for a level.
 * It depends on GameLevel to determine the brick type and brick dimensions.*/

public class Brick {
	
	
	private int numberHitsBreak;
	private int brickType;
	private boolean powerUpsEnabled;
	private boolean weaponsEnabled;
	private boolean fallsDown;
	private ImageView image;
	private int brickWidth;
	private int brickHeight;
	
	public static final int LASER_BRICK = 3;
	public static final int MISSILE_BRICK = 5;
	public static final int HITS_BRICK_1 = 1;
	public static final int HITS_BRICK_2 = 2;
	public static final int HITS_BRICK_3 = 3;
	public static final int HITS_BRICK_BOSS = 20;
	public static final int DAMAGE_PER_HIT = 1;
	public static final double BRICK_WIDTH_RATIO = 0.2;
	public static final double BRICK_HEIGHT_RATIO = 0.15;
	
	public Brick(int typeBrick, GameLevel currentLevel) {
		brickType = typeBrick -1;
		brickWidth = (int) (currentLevel.getLevelWidth()*BRICK_WIDTH_RATIO);
		brickHeight = (int) (currentLevel.getLevelHeight()*BRICK_HEIGHT_RATIO);
		System.out.println(typeBrick);
		switch (brickType) { //SWITCH IS A CODESMELL
		case 0: 
			numberHitsBreak = HITS_BRICK_1;
			break;
		case 1: 
			numberHitsBreak = HITS_BRICK_2;
			fallsDown = true;
			break;
		case 2:
			numberHitsBreak = HITS_BRICK_2;
			powerUpsEnabled = true;
			fallsDown = true;
			break;
		case 3: 
			numberHitsBreak = HITS_BRICK_3;
			weaponsEnabled= true;
			break;
		case 4:
			numberHitsBreak = HITS_BRICK_3;
			break;
		case 5: 
			numberHitsBreak = HITS_BRICK_BOSS;
			weaponsEnabled = true;
			break;
		}
		System.out.println("this is nhb: " + numberHitsBreak);
		setBrickImage("brick"+ typeBrick + ".gif"); //still need to decide which bricks get what img
	}
	
	public void setBrickImage(String brickImg) {
		System.out.println(brickImg);
		Image brickImage = new Image(getClass().getClassLoader().getResourceAsStream(brickImg));
		this.image = new ImageView(brickImage);
		this.image.setFitHeight((double) brickHeight);
		this.image.setFitWidth((double) brickWidth);
	}
	
	public ImageView getImgView() {
		return this.image;
	
	}
	
	public int getNumHitsBreak(){
		return this.numberHitsBreak;
	}
	
	public void setNumHitsBreak(int newNumHits) {
		this.numberHitsBreak = newNumHits;
	}
	
	public void decrementNumHitsBreak() {
		setNumHitsBreak(this.numberHitsBreak - DAMAGE_PER_HIT);
	}
	
	public boolean checkPowerUpsEnabled() {
		return this.powerUpsEnabled;
	}
	
	public boolean checkWeaponsEnabled() {
		return this.weaponsEnabled;
	}
	
	public boolean fallsDown() {
		return this.fallsDown;
	}
	
	public void setBrickX(double brickXLocation) {
		this.image.setX(brickXLocation);
	}
	public void setBrickY(double brickYLocation) {
		this.image.setY(brickYLocation);
	}
	
	public int getBrickHeight() {
		return this.brickHeight;
	}
	public int getBrickWidth() {
		return this.brickWidth;
	}
	public int getBrickType() {
		return this.brickType;
	}
	
}
