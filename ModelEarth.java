// Course Project-Graphics Computer-winter 2015-2016
// Rendering the Earth Model Using Java programming
// Sara Ayubian
// Student Number 201284643


package mypackage;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.Timer;

////////////////////////////////***********************Main Program***********************************//////////////////////////////////


@SuppressWarnings("serial")

//This is the main part of the implementation to display the earth model 
//using  seven images which is prepared for this aim
public class ModelEarth extends Frame {
	//This code is for  viewing scene viewer
	private View3DCanvas viewer;

	// The following constructor will add canvas and load texture images and maximize the window for display
	public ModelEarth(Vertex center, double radius) {
		super("Earth Model");
		// This part is the initialization of the earth and cloud sphere
		viewer = new View3DCanvas(new Sphere(center, radius), new Sphere(center, radius + 0.03));
		add("Center", viewer);
		// load texture images
		viewer.universe = loadImage("stars.png");
		viewer.earth = loadImage("earth-map.jpg");
		viewer.earthBumpMap = loadImage("earth-bump.jpg");
		viewer.darkEarth = loadImage("dark-earth.jpg");
		viewer.earthSpecularMap = loadImage("specular.jpg");
		viewer.earthCloudMap = loadImage("earth-cloud-map.jpg");
		viewer.earthCloudSpecMap = loadImage("cloud-map-rotation.jpg");
		setExtendedState(MAXIMIZED_BOTH);
		addWindowListener(new ExitListener());
	}

	// Image loading will happen in this part
	public BufferedImage loadImage(String name) {
		Image image = Toolkit.getDefaultToolkit().getImage(name);
		MediaTracker mt = new MediaTracker(this);
		try {
			mt.addImage(image, 0);
			mt.waitForID(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		BufferedImage buff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		buff.createGraphics().drawImage(image, 0, 0, null);
		image.flush();
		return buff;
	}

	
	class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}

		public void windowOpened(WindowEvent e) {
			//This part will show a dialog for user to wait for calculation of this implementation
			JOptionPane.showMessageDialog(((ModelEarth) e.getSource()),
					"Welcome to the Earth Planet ");
		}
	}

	public static void main(String[] args) {
		Vertex center = new Vertex(0, 0, 0);
		double radius = 2;
		if (args.length >= 3)
			center = new Vertex(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]));
		if (args.length == 4) {
			radius = Double.parseDouble(args[3]);
			if (radius > 2.5)
				radius = 2.5;
		}
		ModelEarth window = new ModelEarth(center, radius + 0.1);
		window.setSize(400, 460);
		window.setVisible(true);
	}
}

@SuppressWarnings("serial")
class View3DCanvas extends Canvas implements ActionListener {
	// this part shows how to define image buffers
	Image image;
	BufferedImage earthSpecularMap = null;
	BlendingImages blender = null;
	BufferedImage universe = null;
	BufferedImage earth = null;
	BufferedImage earthBumpMap = null;
	BufferedImage darkEarth = null;
	BufferedImage earthCloudMap = null;
	BufferedImage earthCloudSpecMap = null;
	
	
	// canvas size for screen to raster transform
		int width, height, scale;
		// tessellation level
		int level = 6;
		
	// The following code is responsible for displaying earth sphere and cloud sphere 
	// we need to define pitch and paw angles for world to camera transformation
	//also we need to define focal length and image size in order to transform camera to screen
	
	Sphere theearthsphere;
	Sphere thecloudSphere;
	int pitch, yaw;
	int focal, size;
	
	
	// we need timer for the rotation of the earth in this implementation
	Timer earthtimer = null;
	Rotation tranform = new Rotation();

	// This part of the code is responsible for initializing the 3d viewing canvas and fix the focal length
	// we make the initialization of the earth size which will be 3 which is a good fit for our display
	
	public View3DCanvas(Sphere earth, Sphere clouds) {
		theearthsphere = earth;
		thecloudSphere = clouds;
		size = 3;
		focal = 20;
		
		// listeners configurations
		DragListener drag = new DragListener();
		addMouseListener(drag);
		addMouseMotionListener(drag);
		addKeyListener(new ArrowListener());
		addComponentListener(new ResizeListener());
		// Initialize timer for earth rotation
		earthtimer = new Timer(1000, this);
		earthtimer.setInitialDelay(1000);
		earthtimer.start();
	}
// world coordinates will change to camera coordinates
	public Vertex world2Camera(Vertex from) {
		return new Vertex(from.x, from.y, from.z);
	}
// camera coordinates will change to screen coordinates
	public Vertex camera2Screen(Vertex from) {
		double weight = from.z / focal + 1;
		return new Vertex(from.x / (size * weight), from.y / (size * weight), 0);
	}
// screen coordinates transform to raster coordinates
	public Vertex screen2Raster(Vertex from) {
		return new Vertex(-from.x * scale + width / 2, from.y * scale + height / 2, 0);
	}

	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, this);
	}

	class ResizeListener extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			((View3DCanvas) e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			width = getWidth();
			height = getHeight();
			//scale reconfiguration
			scale = Math.min(width / 2, height / 2);
			theearthsphere = new Sphere(theearthsphere.center, theearthsphere.radius);
			thecloudSphere = new Sphere(thecloudSphere.center, thecloudSphere.radius);
			// Tessellate earth and cloud sphere
			TessellationAlgorithm tessellate = new TessellationAlgorithm();
			tessellate.tessellateSphere(theearthsphere, level);
			tessellate.tessellateSphere(thecloudSphere, level);
		}

	}

	
	class DragListener extends MouseAdapter implements MouseMotionListener {
		int lastX, lastY;
		Rotation tranform = new Rotation();

		public void mousePressed(MouseEvent e) {
			lastX = e.getX();
			lastY = e.getY();
			// this part of the code is responsible to stop the timer to perform rotation operations
			((View3DCanvas) e.getSource()).earthtimer.stop();
		}
// every time that you drag your mouse pitch and yaw will be updated 
		public void mouseDragged(MouseEvent e) {
			if (lastX != e.getX()) {
				// Rotate earth and clouds along x axis by 10 degrees
				tranform.rotationByYaw(theearthsphere, (lastX - e.getX()) / Math.abs(e.getX() - lastX),10);
				tranform.rotationByYaw(thecloudSphere, (lastX - e.getX()) / Math.abs(e.getX() - lastX), 10);
			}
			if (lastY != e.getY()) {
				// Rotate earth and clouds along y axis by 10 degrees
				tranform.rotationByPitch(theearthsphere, (lastY - e.getY()) / Math.abs(e.getY() - lastY), 10);
				tranform.rotationByPitch(thecloudSphere, (lastY - e.getY()) / Math.abs(e.getY() - lastY), 10);
				if (lastY - e.getY() < 0)
					Rotation.rotationByPitch++;
				else
					Rotation.rotationByPitch--;
			}

			View3DCanvas canvas = (View3DCanvas) e.getSource();
			// this part will Generate earth and universe using blender algorithm
			blender.generateEarthAlongWithUniverse();
			// Generate clouds along other object using blender algorithm
			blender.generateClouds();
			// by using alpha blending function earth and clouds will blend
			int[] screen = blender.blendEarthAndClouds();
			// This part of the code displays the image on the screen
			canvas.image = canvas.createImage(new MemoryImageSource(canvas.getWidth(), canvas.getHeight(), screen, 0,
					canvas.getWidth()));
			repaint();
		}

		public void mouseReleased(MouseEvent e) {
			lastX = e.getX();
			lastY = e.getY();
			// we need to start the timer in this step
			((View3DCanvas) e.getSource()).earthtimer.start();
		}
	}

	// Action listener for keyboard for rotation and resizing
	class ArrowListener extends KeyAdapter {
		Rotation rotate = new Rotation();

		@SuppressWarnings("static-access")
		public void keyPressed(KeyEvent e) {
			//this code will stop the timer
			((View3DCanvas) e.getSource()).earthtimer.stop();
			if (e.getKeyCode() == e.VK_UP && size > 3)
				size--; //zoom out
			else if (e.getKeyCode() == e.VK_DOWN && size < 20)
				size++; // zoom in
			else if (e.getKeyCode() == e.VK_LEFT) {
				// view direction will change anti-clockwise
				rotate.rotationByYaw(theearthsphere, 1, 5);
				rotate.rotationByYaw(thecloudSphere, 1, 5);
				rotate.rotationLightSource(1, 5);
			} else if (e.getKeyCode() == e.VK_RIGHT) {
				// view direction will change clockwise
				rotate.rotationByYaw(theearthsphere, -1, 5);
				rotate.rotationByYaw(thecloudSphere, -1, 5);
				rotate.rotationLightSource(-1, 5);
			}

			View3DCanvas canvas = (View3DCanvas) e.getSource();
			// again we need to generate earth,universe and clouds and blend those two in order to paint the image on the screen
			blender.generateEarthAlongWithUniverse();
			blender.generateClouds();
			int[] screen = blender.blendEarthAndClouds();
			canvas.image = canvas.createImage(new MemoryImageSource(canvas.getWidth(), canvas.getHeight(), screen, 0,
					canvas.getWidth()));
			repaint();
		}

		public void keyReleased(KeyEvent e) {
			// timer will start again
			((View3DCanvas) e.getSource()).earthtimer.start();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Translating the axis back to its original place by draggin the mouse by user
		for (int i = 0; i < Math.abs(Rotation.rotationByPitch); i++) {
			tranform.rotationByPitch(theearthsphere,
					Rotation.rotationByPitch / Math.abs(Rotation.rotationByPitch), 10);
			tranform.rotationByPitch(thecloudSphere,
					Rotation.rotationByPitch / Math.abs(Rotation.rotationByPitch), 10);
		}
		tranform.rotationByYaw(theearthsphere, -1, 1);
		tranform.rotationByYaw(thecloudSphere, -1, 3);
		for (int i = 0; i < Math.abs(Rotation.rotationByPitch); i++) {
			tranform.rotationByPitch(theearthsphere,
					-Rotation.rotationByPitch / Math.abs(Rotation.rotationByPitch), 10);
			tranform.rotationByPitch(thecloudSphere,
					-Rotation.rotationByPitch / Math.abs(Rotation.rotationByPitch), 10);
		}

		View3DCanvas canvas = this;
		if (blender == null)
			blender = new BlendingImages(canvas);
		blender.generateEarthAlongWithUniverse();
		blender.generateClouds();
		int[] screen = blender.blendEarthAndClouds();
		canvas.image = canvas.createImage(new MemoryImageSource(canvas.getWidth(), canvas.getHeight(), screen, 0,
				canvas.getWidth()));
		repaint();
		// Setting cursor to default version
		setCursor(Cursor.getDefaultCursor());
	}

}
// in this part of the implementation we need to define a vertex by its coordinate in a 3d space
class Vertex {
	double x, y, z;	double theta, beta;	double u, v;

	public Vertex(double a, double b, double c) {
		x = a;y = b;z = c;
	}

	public Vertex(Vertex v) {
		x = v.x;y = v.y;z = v.z;
	}

	// latitude and longitude calculations
	public void calculateAngles(double r) {
		beta = Math.asin(y / r);
		theta = Math.atan2(z, x);
		u = (theta / (2 * Math.PI)) + 0.5;
		v = (beta / Math.PI) + 0.5;
	}

	public void normalise() {
		double d = magnitude();
		x = x / d;
		y = y / d;
		z = z / d;
	}
	public double magnitude() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public void scale(double wt) {
		x = x * wt;
		y = y * wt;
		z = z * wt;
	}

	public void normalizeAndScale(double wt) {
		normalise();
		scale(wt);
	}
	
	public static Vertex midpoint(Vertex v1, Vertex v2) {
		return new Vertex((v1.x + v2.x) / 2, (v1.y + v2.y) / 2, (v1.z + v2.z) / 2);
	}

	public static Vertex subtract(Vertex v1, Vertex v2) {
		return new Vertex(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}

	public static Vertex add(Vertex v1, Vertex v2) {
		return new Vertex(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}

	public Vertex add(Vertex v1) {
		x = x + v1.x;
		y = y + v1.y;
		z = z + v1.z;
		return this;
	}

	public Vertex addAndScale(Vertex v1) {
		v1.scale(1.35);
		x = x + v1.x;
		y = y + v1.y;
		z = z + v1.z;
		return this;
	}

	public Vertex subtract(Vertex v1) {
		x = x - v1.x;
		y = y - v1.y;
		z = z - v1.z;
		return this;
	}

	public double dotProduct(Vertex v) {
		return (x * v.x) + (y * v.y) + (z * v.z);
	}

	public static Vertex crossProduct(Vertex v1, Vertex v2) {
		double vx = v1.y * v2.z - v1.z * v2.y;
		double vy = v1.z * v2.x - v1.x * v2.z;
		double vz = v1.x * v2.y - v1.y * v2.x;
		return new Vertex(vx, vy, vz);
	}
}

// in this part of the implementation we need to define Triangle 
class Triangle {
	int[] vertex = new int[3];
	boolean render = true;

	// constructor
	public Triangle(int u, int v, int w) {
		vertex[0] = u;
		vertex[1] = v;
		vertex[2] = w;
	}

	public void setRender(boolean render) {
		this.render = render;
	}

}

// in this part of the implementation we need to define Sphere 
class Sphere {
	double radius;
	Vertex center;
	List<Vertex> vertices;
	List<Triangle> triangles;

	public Sphere(Vertex c, double r) {
		center = c;
		radius = r;

		// Generating triangle mesh for Octahedron - it will be the start point for tessellation 
		vertices = new ArrayList<Vertex>();
		vertices.add(new Vertex(center.x, center.y, center.z + radius));
		vertices.add(new Vertex(center.x + radius, center.y, center.z));
		vertices.add(new Vertex(center.x, center.y + radius, center.z));
		vertices.add(new Vertex(center.x - radius, center.y, center.z));
		vertices.add(new Vertex(center.x, center.y - radius, center.z));
		vertices.add(new Vertex(center.x, center.y, center.z - radius));
		triangles = new ArrayList<Triangle>();
		triangles.add(new Triangle(0, 1, 2));
		triangles.add(new Triangle(0, 2, 3));
		triangles.add(new Triangle(0, 3, 4));
		triangles.add(new Triangle(0, 4, 1));
		triangles.add(new Triangle(5, 2, 1));
		triangles.add(new Triangle(5, 3, 2));
		triangles.add(new Triangle(5, 4, 3));
		triangles.add(new Triangle(5, 1, 4));
	}
}
//Finished by Sara Ayubian
