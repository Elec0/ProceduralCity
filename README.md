ProceduralCity
==============
My final, which was written entirely in Java with LWJGL (Lightweight Java Game Library), is a 3D procedural city nightscape generation program. The entire thing has no pre-made assets whatsoever, everything is generated in realtime whenever a new city is generated. 
I chose a nightscape because I could have black buildings with lit windows and give the illusion of more detail than there actually was, to save on rendering and generating power.

I have attached the compiled Java file for both Windows and Linux. The Windows version has been extensively tested on Windows 7. The Linux version hasn't been tested whatsoever. Both versions require that Java be installed, preferably a more recent version. The controls to the programs are listed at the bottom of the email. 
Here is a screenshot of the program in action: http://imgur.com/bCOvLwA
The source code is packed in the .jar files as well, if either of you would like to view that. Java files can be opened just like zip files.

What follows is a fairly detailed write-up of the different features I wrote, and some of my thinking behind them.


When a new city is generated, it first generates a large texture for the buildings to each use part of, which is only looping through the texture and randomly selecting if a window is lit or not, if it is the square for the window is brighter, with a range of brightness it can be. If the window isn't lit, it has a much dimmer range it can be, to simulate ambient light, so not all unlit windows are completely black.

After the main texture is generated, the horizon texture is made, which starts with a warm color--which I defined as a starting color that has a range of red it can be, and whatever green is picked it must be less than the red. If a red of 150 is picked the green can be 0-149. Blue isn't used.
Once both textures are generated, the development level of every tile is calculated. Two points are picked on the grid, one 'industrial' point, and one 'residential' point. The industrial point has a higher development level, but is smaller, and the residential point has a lower level but is larger. From the two origin points the development level degrades in a linear falloff. (The development levels are represented by certain colors in the program when viewing dev colors, teal is level 4, blue is 3, green is 2, and red is 1.)

Now that the program know what buildings can be placed where, it starts generating the buildings, which is also done in realtime. There are three different kinds of building types that can be generated. They all have a range of height, width, and complexity, with the higher dev levels having taller and more complicated buildings.

The first, and most simple, is a simple square. It only requires dev level 1.
Second is a more complicated building that requires dev level 2. It starts with a tall rectangle, and iterates down a number of times based on the height of the initial rectangle. At each iteration a shorter rectangle is generated, with the constraint that it must be extruding out farther than the previous rectangle. So it looks like a building with multiple rectangles at differing heights.
The third requires dev level 4. It is a circular building of varying height, and there is a chance that it will skip 90 degrees and leave a flat edge to be more aesthetically pleasing.

After that, during rendering, the program checks if each building is within a 2D top-down view cone for optimization. It uses the dot product of the vectors between each building and the camera, determining if it can be seen on the x and z axis. 3D view checking would have been better, but much more complicated, so I opted for a simpler option. Because of that you can look down and see the cone of buildings that is not rendered.


Unfortunately, after I had finished most of the project, I learned about bus limits and realized that I had hit one. Therefore the FPS is limited to around 20 FPS because I'm trying to put too much data through the bus. That can be solved with vertex buffer objects, but it was too late in the project to switch my entire rendering and generating pipeline to a VBO, so I decided to leave it as it was and deal with the still mostly acceptable frame rate.


The controls of the program are as follows:
WASD: Move
Mouse: Look
Left Shift: Speed up movement
Space: Ascend
C: Toggle dev colors
N: Generate new city
P: Toggle wireframe
Esc: Exit
