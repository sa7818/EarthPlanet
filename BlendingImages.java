
// Course Project-Graphics Computer-winter 2015-2016
// Rendering the Earth Model Using Java programming
// Sara Ayubian
// Student Number 201284643

package mypackage;

import java.awt.Color;
// This part of the program will responsible for blending two images using blending function
public class BlendingImages {

	private View3DCanvas canvas = null;
	private int[] pixelsWorld = null;
	private int[] pixelsClouds = null;
	private RenderEarthModel renderEarth = null;
	private RenderClouds renderCloud = null;

	// Constructor
	public BlendingImages(View3DCanvas canvas) {
		this.canvas = canvas;
		// initialize the pixels of world and cloud 
		pixelsWorld = new int[canvas.width * canvas.height];
		pixelsClouds = new int[canvas.width * canvas.height];
		// we need a renderer for earth and cloud rendering
		renderEarth = new RenderEarthModel(canvas);
		renderCloud = new RenderClouds(canvas);
	}

	public void generateEarthAlongWithUniverse() {
		renderEarth.calculateImageToBeRendered();
		pixelsWorld = renderEarth.getPixels();
	}

	// this part will generate clouds
	public void generateClouds() {
		renderCloud.calculateImageToBeRendered();
		pixelsClouds = renderCloud.getPixels();
	}

	//blending function
	public int[] blendEarthAndClouds() {
		int[] screen = new int[canvas.width * canvas.height];
		double[] alpha = renderCloud.getAlpha();
		for (int i = 0; i < canvas.getHeight() * canvas.getWidth(); i++) {
			double alphaChannel = 0;
			if (i < alpha.length)
				alphaChannel = alpha[i];
			Color world = Color.BLACK;
			if (i < pixelsWorld.length)
				world = new Color(pixelsWorld[i]);
			Color cloudTextureColor = Color.BLACK;
			if (i < pixelsClouds.length)
				cloudTextureColor = new Color(pixelsClouds[i]);
			double worldRed = world.getRed() * (1 - alphaChannel);
			double worldGreen = world.getGreen() * (1 - alphaChannel);
			double worldBlue = world.getBlue() * (1 - alphaChannel);
			//  transparency calculation
			double red = worldRed + (alphaChannel * cloudTextureColor.getRed());
			double green = worldGreen + (alphaChannel * cloudTextureColor.getGreen());
			double blue = worldBlue + (alphaChannel * cloudTextureColor.getBlue());
			if (i < screen.length)
				screen[i] = new Color((int) red, (int) green, (int) blue).getRGB();
		}
		// return scene
		return screen;
	}

}
// Finished by Sara Ayubian