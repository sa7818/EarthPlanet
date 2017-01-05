// Course Project-Graphics Computer-winter 2015-2016
// Rendering the Earth Model Using Java programming
// Sara Ayubian
// Student Number 201284643

package mypackage;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

//Zbuffer algorithm implementation for visibility determination
public class ZBuffer {

	private double[][] normalVectors = null;
	private BlinnPhongShading shading = null;
	private double distance[] = null;
	private List<Triangle> triangles = null;
	private List<Vertex> vertices = null;
	private int canvasWidth = 0;
	private int canvasHeight = 0;
	private View3DCanvas canvas = null;
	private BufferedImage textureImage = null;
	private BufferedImage nightImage = null;
	private BufferedImage specularImage = null;
	private BufferedImage transparencyImage = null;
	private boolean lightingEffect = false;
	private double alpha[] = null;
	private int[] pixels = null;

	// Constructor
	public ZBuffer(View3DCanvas canvas, List<Triangle> triangles, List<Vertex> vertices,
			double[][] normalVectors, BufferedImage textureImage, BufferedImage nightImage,
			BufferedImage specularImage, BufferedImage transparencyImage, boolean lightingEffect, int[] pixels) {
		this.canvas = canvas;
		this.canvasWidth = canvas.getWidth();
		this.canvasHeight = canvas.getHeight();
		this.normalVectors = normalVectors;
		this.triangles = triangles;
		this.vertices = vertices;
		this.textureImage = textureImage;
		this.nightImage = nightImage;
		this.specularImage = specularImage;
		this.lightingEffect = lightingEffect;
		this.transparencyImage = transparencyImage;
		this.pixels = pixels;
		// For lighting effect -- earth
		if (lightingEffect)
			shading = new BlinnPhongShading();
		else
			shading = new BlinnPhongShading(1, 0.2, 0.3, 80);
		distance = new double[canvasWidth * canvasHeight];
		for (int i = 0; i < canvasWidth * canvasHeight; i++) {
			distance[i] = 1 - Double.MAX_VALUE;
		}
		// init alpha channel
		alpha = new double[canvasWidth * canvasHeight];
		for (int i = 0; i < canvasWidth * canvasHeight; i++) {
			alpha[i] = 0;
		}
	}

	// Calculate scene image
	public void calculateImageToBeRendered() {
		for (int i = 0; i < triangles.size(); i++)
			applyZbuffer(triangles.get(i));
	}

	// Apply zuffer algorithm
	private void applyZbuffer(Triangle triangle) {
		Point[] points = new Point[3];
		Vertex[] verts = new Vertex[3];
		Vertex[] uvVertex = new Vertex[3];
		Vertex[] normals = new Vertex[3];
		// Get vertex, UV coord and raster coordinates
		for (int n = 0; n < 3; n++) {
			verts[n] = vertices.get(triangle.vertex[n]);
			uvVertex[n] = new Vertex(verts[n].u, verts[n].v, 0);
			Vertex vertex = canvas.screen2Raster(canvas.camera2Screen(canvas.world2Camera(verts[n])));
			points[n] = new Point((int) (vertex.x), (int) (vertex.y));
		}
		// Calculate normal at each vertex. Normal will vary due to depth change
		for (int n = 0; n < 3; n++) {
			Vertex BA = Vertex.subtract(verts[(n + 1) % 3], verts[n]);
			Vertex CA = Vertex.subtract(verts[(n + 2) % 3], verts[n]);
			Vertex N = Vertex.crossProduct(BA, CA);
			N.normalise();
			normals[n] = N;
		}
		// Use bounding volume to restrict calculations
		int x_ = canvasWidth, xU = 0, y_ = canvasHeight, yU = 0;
		for (int n = 0; n < 3; n++) {
			if (points[n].x < x_)
				x_ = points[n].x;
			if (points[n].x > xU)
				xU = points[n].x;
			if (points[n].y < y_)
				y_ = points[n].y;
			if (points[n].y > yU)
				yU = points[n].y;
		}
		x_ = checkBounds(x_, canvasWidth);
		y_ = checkBounds(y_, canvasHeight);
		for (int y = y_; y <= yU; y++) {
			for (int x = x_; x <= xU; x++) {
				// Calculate barycentric cordinates
				double denominator = (points[1].y - points[2].y) * (points[0].x - points[2].x)
						+ (points[2].x - points[1].x) * (points[0].y - points[2].y);
				// get alpha, beta and gamma
				double alpha = ((points[1].y - points[2].y) * (x - points[2].x) + (points[2].x - points[1].x)
						* (y - points[2].y))
						/ denominator;
				double beta = ((points[2].y - points[0].y) * (x - points[2].x) + (points[0].x - points[2].x)
						* (y - points[2].y))
						/ denominator;
				double gamma = 1 - alpha - beta;
				// check if point is inside the triangle
				if (alpha >= 0 && alpha <= 1 && beta >= 0 && beta <= 1 && gamma >= 0 && gamma <= 1) {
					double depth = alpha * verts[0].z + beta * verts[1].z + gamma * verts[2].z;
					if (depth >= 0 && depth > distance[y * canvasWidth + x]) {
						// interpolate UV coordinate
						Vertex UVA = new Vertex(uvVertex[0]);
						Vertex UVB = new Vertex(uvVertex[1]);
						Vertex UVC = new Vertex(uvVertex[2]);
						UVA.scale(alpha);
						UVB.scale(beta);
						UVC.scale(gamma);
						Vertex UV = Vertex.add(Vertex.add(UVA, UVB), UVC);

						// interpolate normal
						Vertex NA = new Vertex(normals[0]);
						Vertex NB = new Vertex(normals[1]);
						Vertex NC = new Vertex(normals[2]);
						NA.scale(alpha);
						NB.scale(beta);
						NC.scale(gamma);
						Vertex N = Vertex.add(Vertex.add(NA, NB), NC);
						N.normalise();

						// Get texture
						int textureWidth = textureImage.getWidth();
						int textureHeight = textureImage.getHeight();
						int textureX = (int) (UV.x * (textureWidth - 1));
						int textureY = (int) (UV.y * (textureHeight - 1));

						Color finalColor = Color.BLACK;
						// calculate lighting for earth
						if (lightingEffect) {
							// get texture color from texture map
							Color textureColor = new Color(textureImage.getRGB(textureX, textureY));
							// get specular color from specular map
							Color specularColor = new Color(specularImage.getRGB(textureX, textureY));
							// get bump normals and add them to surface
							// geometric normals
							double Nx = normalVectors[textureY * textureWidth + textureX][0];
							double Ny = normalVectors[textureY * textureWidth + textureX][1];
							double Nz = normalVectors[textureY * textureWidth + textureX][2];
							Vertex normalMapVertex = new Vertex(Nx, Ny, Nz);
							N.addAndScale(normalMapVertex);
							// Adjust displacement of night map -- given night
							// map is 5pixels displaced compare to daylight
							// earth map
							textureX = (int) (((UV.x + 0.018) % 1) * (textureWidth - 1));
							textureY = (int) (((UV.y + 0.015) % 1) * (textureHeight - 1));
							// get night color from night map
							Color nightColor = new Color(nightImage.getRGB(textureX, textureY));
							// calculate blinn-phong shading
							Color earthColor = shading
									.calculateIllumination(N, textureColor, nightColor, specularColor);
							finalColor = earthColor;
							// define alpha
							this.alpha[y * canvasWidth + x] = 1;
						} else if (transparencyImage != null) {
							// for cloud map
							N.scale(1.5);
							// start cloud map with displacement
							UV.x = (UV.x + 0.7) % 1;
							textureX = (int) (UV.x * (textureWidth - 1));
							textureY = (int) (UV.y * (textureHeight - 1));
							// get cloud texture color from cloud map
							Color textureColor = new Color(textureImage.getRGB(textureX, textureY));
							// get cloud transparency from transparency map
							Color transColor = new Color(transparencyImage.getRGB(textureX, textureY));
							// calculate alpha channel for cloud
							double alphaChannel = ((double) (transColor.getRed() + transColor.getGreen() + transColor
									.getBlue()) / (double) (3 * 255));
							// calculate blinn-phong for light effect on clouds
							Color cloudColor = shading.calculateIllumination(N, textureColor, transColor, Color.BLACK);
							finalColor = cloudColor;
							// set alpha channel -- how much transparent
							this.alpha[y * canvasWidth + x] = (1 - alphaChannel);
						}
						// get the final color calculated
						if ((y * canvasWidth + x) < pixels.length)
							pixels[y * canvasWidth + x] = finalColor.getRGB();
					}
				}
			}
		}
	}

	// check pixel bounds
	private int checkBounds(int x, int limit) {
		if (x > limit)
			x = limit - 1;
		else if (x < 0)
			x = 0;
		return x;
	}

	// get alpha channel map
	public double[] getAlpha() {
		return alpha;
	}
}
//Finished by Sara Ayubian