Together

Things we wrote in the initial proposal but didn't do:
- We wrote that we wanted to make the camera move smoothly, but we realized that this was not necessary at all since the movements of the players are already smooth.

Known bugs:
- The y positions of the hitboxes of the buttons are not aligned with the locations of the buttons when the window is stretched vertically; however, this is not an issue when playing with a normal aspect ratio.
- When drawing text, there are sometimes artifacts at the edges of each letter. This is because to render text, we first had to use awt to draw every character of a font onto an image, then render the characters from this font atlas.

Other notes:
- The game has a lot of code related to drawing things with OpenGL. This is what we spent most of our time on. Specifically, rendering text and rendering 3D models each took a few full days of time.
- We wrote a lot of the code in a very modular and extendable fashion. For example, although we only have 1 3D model (the player model), we wrote the code in a way where we could easily add other 3D models into the game with only a few more lines of code.
- A lot of the responsibilities we wrote in the proposals got swapped. Most notably, Ryan ended up doing the physics stuff and Lucas did the UI stuff.