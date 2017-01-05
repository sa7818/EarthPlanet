// Course Project-Graphics Computer-winter 2015-2016
// Rendering the Earth Model Using Java programming
// Sara Ayubian
// Student Number 201284643

package mypackage;

import java.awt.Image;
import java.awt.image.PixelGrabber;
//This part of the code is responsible generate earth and stars rendering
public class RenderEarthModel {

	private View3DCanvas display = null;
	private double[][] vectorsNormalize = null;
	private int pixels[] = null;
	private double alpha[] = null;

	public RenderEarthModel(View3DCanvas canvas) {
		this.display = canvas;
		this.vectorsNormalize = new Bumping().findBumpNormals(canvas.earthBumpMap);
	}
// by Z-buffer algorithm we can get the screen by rendered image
	public void calculateImageToBeRendered() {
		pixels = new int[display.width * display.height];
		Image universe = display.universe.getScaledInstance(display.width, display.height, Image.SCALE_DEFAULT);
		// get universe pixels. Backgorund of starfield
		PixelGrabber pg = new PixelGrabber(universe, 0, 0, display.width, display.height, pixels, 0, display.width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			throw new IllegalStateException("Failure Notice for Intterupted Pixels");
		}

		// get 2D scene using Zbuffer algorithm
		ZBuffer zbuffer = new ZBuffer(display, display.theearthsphere.triangles,
				display.theearthsphere.vertices, vectorsNormalize, display.earth, display.darkEarth,
				display.earthSpecularMap, null, true, pixels);
		//we need to calculate rendered image
		zbuffer.calculateImageToBeRendered();
		alpha = zbuffer.getAlpha();
	}

	public int[] getPixels() {
		return pixels;
	}

	public double[] getAlpha() {
		return alpha;
	}
}
//Finished by Sara Ayubian
