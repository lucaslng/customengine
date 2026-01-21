# Together - Ryan C and Lucas

A 3D platformer game where you make your way through teamwork-based levels with a friend!

## Things we wrote in the initial proposal but didn't do

- We wrote that we wanted to make the camera move smoothly, but we realized that this was not necessary at all since the movements of the players are already smooth.

## Known bugs / issues

- The y positions of the hitboxes of the buttons are not aligned with the locations of the buttons when the window is stretched vertically; however, this is not an issue when playing with a normal aspect ratio (16:9).
- We can't center align drawn text onto a position. As we mentioned in the showcase, this is because it is a bit difficult to calculate the width of the drawn text. We probably could have done this with a bit more time.
- When first loading into a level, the timer is often a few seconds short because of the loading time. Perhaps we could fix this by creating objects beforehand and just cutting down the number of objects we are creating.
- Our lighting system doesn't yet take into account the normals of the textures that we have downloaded. This means that all materials are "shiny", including grass or bricks.

## Other notes

- The game has a lot of code related to drawing things with OpenGL. This is what we spent most of our time on. Specifically, rendering text and rendering 3D models each took a few full days of time.
- We wrote a lot of the code in a very modular and extendable fashion. For example, although we only have 1 3D model (the player model), we wrote the code in a way where we could easily add other 3D models into the game with only a few more lines of code.
- We wrote code to track player deaths but never got a chance to display them.
- A lot of the responsibilities we wrote in the proposals got swapped. Most notably, Ryan ended up doing the physics stuff and Lucas did the UI stuff.
- Most of any physics-related bugs could have been fixed by just using an already established Java physics engine, but we unfortunately we realized too late and did not want to recode everything.

## Libraries used

- [LWJGL](https://www.lwjgl.org/) - Lightweight Java Game Library
- [OpenGL](https://www.opengl.org/) and [GLFW](https://github.com/glfw/glfw) - for 3D stuff
- [JOML](https://github.com/JOML-CI/JOML) - for useful vector and matrix objects, boring math stuff
- We could have used libraries for physics, text rendering, and reading and rendering 3D models but we did not (maybe in grade 12!)

## File structure

```text
.
├── App -> entry point of the program, creates the game engine and game states
├── GameStates -> Enum for the different game states
├── MainMenuState, EndScreenState, OptionsState, LevelSelectionState -> game states for each corresponding menu
├── PlayingState -> Main game logic. Here you will find the very high level code which calls all the other stuff.
├── ColorList -> A few colors we defined
├── engine
│   ├── Engine -> Acts like an API to the Game so it can access user inputs, rendering, camera etc.
│   ├── EngineSettings -> Values used by the game that can be adjusted
│   ├── EntityManager -> Object that manages all the entities living in a gamestate
│   ├── GameState -> Abstract class which is implemented by each game state, handles the looping logic and calculates delta time.
│   ├── GameStateSwitch -> Object that gets returned when one game state wants to switch to another game state. Also allows the game state to send the next game state a payload object which we used to create the level picker.
│   ├── InputHandler -> Tracks some mouse and key inputs. Uses a HashSet to store held keys when keys are pressed or released. Events are read by hooking into glfw callbacks
│   ├── KeyBind -> A keybind which can be set and has a default value
│   ├── MouseMotionToRotationListener -> Not used in this game, but this was code that could be used for first-person games
│   ├── SoundHandler -> lets us easily play sounds
│   ├── components -> classes which contain pieces of data that entities could have, eg Position, Velocity, Mesh, etc
│   ├── entities
│   │   ├── AbstractEntityFactory -> allows game to create entities with multiple components easily
│   │   ├── Entity -> only stores the id of an entity, not any data. Uses an Atomic Integer to automatically increment entity ids when new entities are created.
│   ├── systems ->  The majority of actual game logic. The names of the files are self explanatory: Deaths, Exits, Levels, LevelTransition, Physics, etc. Accessed by Game. Levels also contains code to parse the level files.
│   ├── utils
│   │   ├── FileReader -> code to read files easily using the class path thing since we are packaging our program into a jar
│   │   ├── OsFetcher -> needed because a special flag needs to be enabled on MacOS.
│   │   ├── ColorUtils -> Only contains a helper method to convert awt Color objects into Vector objects
│   ├── renderer -> Everything related to drawing stuff on screen
│   │   ├── Buffer, BufferLayout, FloatBuffer, Integer, ShaderProgram, Texture, VertexArray, VertexBufferElement, SubMesh -> stuff related to abstracting away OpenGL calls because working with OpenGL directly is a pain :(
│   │   ├── Material -> A material can either be a Texture or a color (Vector4f)
│   │   ├── Materials -> HashMap of materials used in the game
│   │   ├── Camera -> Allows the engine to easily set the position and rotation of the camera, and allows the renderer to easily access the view matrix
│   │   ├── Renderer -> Handles everything related to drawing the game. The method render() is called every frame, it finds every entity with a position, rotation, and mesh and renders it.
│   │   ├── Window -> initializes the glfw window, allows us to add multiple callbacks to one glfw callback
│   │   ├── ModelParser -> Code to parse .obj files and .mtl files. Also automatically centers the vertex coordinates within 3D models.
│   │   ├── ParsedObj, SubParsedObj -> types returned by the .obj parser. SubParsedObj is necessary because OpenGL requires us to split up a 3D model into multiple meshes if there are multiple materials in the model.
│   │   ├── FontAtlas, TextRenderer -> Stuff related to drawing text
├── entities -> The actual types of entities in our game which all implement AbstractEntityFactory. Eg. Player, Box, Camera, etc.
```

## How levels are defined

```text
timer (0 = no time)
player1x player1y player2x, player2y
ropeEnabled (true/false)
entitycount
entitytype entityarg1 entityarg2 entityarg3 …
…

platform posx posy width height [blink onseconds offseconds]
moving_platform posx posy width height moveX moveY speed flag flagnum coins requiredcoins
lava posx posy width height
coin posx posy
moving_lava posx posy width height moveX moveY speed pauseDuration
button posx posy toggle/latch flag flagnum

…
```
