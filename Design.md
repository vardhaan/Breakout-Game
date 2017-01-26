
Hamsa Pillai

# Breakout Design

* High-level design goals
> The main goal of the project was to create a functional Breakout variant. Some subsidiary design goals were to create code that could be modified easily in the future to create new levels, enemies, powerups, and other features. The question of whether or not I successfully achieved this is murkier.
* Adding new features
> Adding new features is possible yet a little convoluted. The problem is that it would require modification of existing code. For example, I definitely did not design my code to ensure that current features could be easily removed from the game. Additionally, to add new features would mean adding code to the JavaFX `step` function that controls the behavior of all sprites in the game. It would also require modifying my `loadLevel` method to initialize the features. Futhermore, other functions would need to be checked to ensure that they don't intefere with the new feature. For example, if you wanted to make the paddle wrap around on the screen, the `setNewPaddleLocation` method would need to be modified. Some modifications, such as adding a new type of missile (say, one that homes in on the paddle or ball) could be easily implemented because I purposefully left those code segments vague so as to handle various cases that could occur. Changing the images of the paddle, ball, bricks, or missiles is also as easy as changing the public static final string that holds the name of the image source. So the ease of new features depends on the size of the new feature being added.
* Major design choices
> My biggest design choice was to separate the all the elements of the game into separate classes. It was either that or have one gigantic class that formed the bricks and balls and paddles __AND__ ran the game loop. Instead I decided to run the game loop in one class and deal with the initialization of the game and the specialization of different objects in the game by using other classes. Another design choice I made was to use ArrayLists to hold the balls and paddles. This allows for modifications in the future that can allow a player to have multiple balls or paddles at a time and the code is designed to deal with this just as it is designed to deal with a singular ball and paddle. This was just a better overall choice than keeping the ball and paddle singular entities because it's increased flexibility for virtually no extra cost.

* Assumptions resolving ambiguity
> One of my major problems was figuring out which side of the brick the ball hit. I never actually solved it properly; if you play my game and get the ball to hit the left or right side of a brick, you will see it bounce in a way that defies physics. This is because my code registers this as hitting the top or bottom. But to even get to this point, I had to assume that the ball only comes in contact with other objects at 4 points: the top, bottom, left, or right points. 


