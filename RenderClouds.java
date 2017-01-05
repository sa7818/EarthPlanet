// Course Project-Graphics Computer-winter 2015-2016
// Rendering the Earth Model Using Java programming
// Sara Ayubian
// Student Number 201284643

package mypackage;

//Cloud renderer will generate scene with clouds
public class RenderClouds {

	private View3DCanvas display = null;
	private int pixels[] = null;
	private double alpha[] = null;

	public RenderClouds(View3DCanvas canvas) {
		this.display = canvas;
	}
// this part of the code will get cloud scene to be rendered using zbuffer algorithm
	public void calculateImageToBeRendered() {
		pixels = new int[display.width * display.height];
		ZBuffer zbuffer = new ZBuffer(display, display.thecloudSphere.triangles,
				display.thecloudSphere.vertices, null, display.earthCloudMap, null, null, display.earthCloudSpecMap, false,
				pixels);
		zbuffer.calculateImageToBeRendered();
		alpha = zbuffer.getAlpha();
	}

	// getter for pixels
	public int[] getPixels() {
		return pixels;
	}

	// getter for alpha
	public double[] getAlpha() {
		return alpha;
	}
}
//Finished by Sara Ayubian