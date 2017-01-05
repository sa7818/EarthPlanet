# EarthPlanet

-----------Rendering the Earth Model Using Java programming------------
----------Sara Ayubian-------------------------------------------------
                                                                                    
The present report discusses the algorithms which are used in rendering of earth model in java programming and demonstrates the steps that we need in order to display an earth model on the screen. This implementation render the earth model by using both color and depth textures. It models the earth by a parametric sphere and applies a color texture map. The other algorithm which used is bump mapping in order to bring depth texture for the model of height changes on the earth and Z-buffer algorithm for visibility determination, and then implementation brings further enhances for the rendering results such as reflection map and nigh light map and it allows the user to rotate earth by using mouse and re-render the image. The algorithm considered a parametric sphere of radius more than earth’s radius in order to render the cloud which will be appear in night and user can change the view direction using arrow keys. The final result would be rendering the surface of the earth including clouds and galaxy as the background.

Main : ModelEarth.java
-------------------------------------------------------------
            Classes for Earth Rendering Model
-------------------------------------------------------------
To generate sphere: TessellationAlgorithm.java 
To generate bump features: Bumping.java 
To have Visibility determination: ZBuffer.java
To have Lighting effect: BlinnPhongShading.java
To have rotation operation:Rotation.java 
To render clouds: RenderClouds.java
To render earth and background stars: RenderEarthModel.java
----------------------------------------------
                        USER CONTROL
----------------------------------------------
--1--Zoom in/out
Use  keyboard bottom UP/DOWN to resize the earth
DOWN : make it smaller
UP : enlarge the earth 
-----------------------------------------------------
--2-- Earth Rotation
Drag the mouse along X axis or Y axis and release the mouse button
------------------------------------------------------
--3-- Changing View Direction
Use  keyboard bottom  LEFT/RIGHT to change the view direction and see the dark side of the earth
 Click the mouse one time because the rotation of earth is automatic
