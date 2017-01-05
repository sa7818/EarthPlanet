// Course Project-Graphics Computer-winter 2015-2016
// Rendering the Earth Model Using Java programming
// Sara Ayubian
// Student Number 201284643

package mypackage;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;

// This part of the code is responsible to calculate bump mapping from distance map
public class Bumping {
	private double[][] normalbump = null;

	public double[][] findBumpNormals(BufferedImage bumpMap) {
		normalbump = new double[bumpMap.getWidth() * bumpMap.getHeight()][3];
		PixelGrabber pixelGraber = null;
		int bumpMapWidth = bumpMap.getWidth();
		int bumpMapHeight = bumpMap.getHeight();
		int[] bumpPixeld = new int[bumpMapWidth * bumpMapHeight];
		// Get all pixels of distance map
		pixelGraber = new PixelGrabber(bumpMap, 0, 0, bumpMapWidth, bumpMapHeight, bumpPixeld, 0, bumpMapWidth);
		try {
			pixelGraber.grabPixels();
		} catch (InterruptedException e) {
			System.err.println("Error:Not able to hold pixels");
		}

		for (int x = 1; x < bumpMapWidth - 1; x++) {
			for (int y = 1; y < bumpMapHeight - 1; y++) {
				// Calculate finite difference
				Color X0 = getPixelColorFromBumpMap(bumpPixeld, x + 1, y, bumpMapWidth);
				Color X1 = getPixelColorFromBumpMap(bumpPixeld, x - 1, y, bumpMapWidth);
				Color Y0 = getPixelColorFromBumpMap(bumpPixeld, x, y + 1, bumpMapWidth);
				Color Y1 = getPixelColorFromBumpMap(bumpPixeld, x, y - 1, bumpMapWidth);
				// calculate normal along X and Y direction
				double normalX = (X0.getRed() - X1.getRed()) + (X0.getGreen() - X1.getGreen())
						+ (X0.getBlue() - X1.getBlue());
				double normalY = (Y0.getRed() - Y1.getRed()) + (Y0.getGreen() - Y1.getGreen())
						+ (Y0.getBlue() - Y1.getBlue());
				normalX /= (double) (255);
				normalY /= (double) (255);
				// Calculate normal along Z direction
				double normalZ = (double) (1 - Math.sqrt((normalX * normalX) + (normalY * normalY)));
				if (normalZ < 0.0f)
					normalZ = 0.0f;
				// add to bump map normals
				normalbump[y * bumpMapWidth + x][0] = normalX;
				normalbump[y * bumpMapWidth + x][1] = normalY;
				normalbump[y * bumpMapWidth + x][2] = normalZ;
			}
		}
		return normalbump;
	}
	// this part of the code will calculate pixel color
	private Color getPixelColorFromBumpMap(int[] bumpPixels, int x, int y, int width) {
		int pixel = bumpPixels[y * width + x];
		int red = (pixel >> 16) & 0xff;
		int green = (pixel >> 8) & 0xff;
		int blue = (pixel) & 0xff;
		return (new Color(red, green, blue));
	}

}
//Finished by Sara Ayubian