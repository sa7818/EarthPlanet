// Course Project-Graphics Computer-winter 2015-2016
// Rendering the Earth Model Using Java programming
// Sara Ayubian
// Student Number 201284643

package mypackage;

// This part of the code is responsible to do the Rotation operations in order to transform 3D scene
public class Rotation {

	public static int rotationByPitch = 0;
	// rotation by Yaw which is a rotation along axis x

	public void rotationByYaw(Sphere sphere, int direction, int rotationAngle) {
		//  we need to convert the angle to radians
		double theta = direction * rotationAngle * Math.PI / 180;
		// Calcualte cos  theta
		double cosTheta = Math.cos(theta);
		// Calcualte cos  theta
		double sinTheta = Math.sin(theta);
		// this part will perform rotation
		for (Vertex vertex : sphere.vertices) {
			double vx = vertex.x, vz = vertex.z;
			vertex.x = vx * cosTheta + vz * sinTheta;
			vertex.z = -vx * sinTheta + vz * cosTheta;
		}
	}

	// rotation by Pitch which is a rotation along axis y
	public void rotationByPitch(Sphere sphere, int direction, int rotationAngle) {
		double theta = direction * rotationAngle * Math.PI / 180;
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);
		// perform transformation
		for (Vertex vertex : sphere.vertices) {
			double vy = vertex.y, vz = vertex.z;
			vertex.y = vy * cosTheta - vz * sinTheta;
			vertex.z = vy * sinTheta + vz * cosTheta;
		}
	}

	// Light source rotation 
	public void rotationLightSource(int direction, int rotationAngle) {
		double theta = direction * rotationAngle * Math.PI / 180;
		double cosTheta = Math.cos(theta);
		double sinTheta = Math.sin(theta);
		// This part of the program will Get the light source by Blinnphong shading algorithm
		Vertex vertex = BlinnPhongShading.getLightSource();
		// this part will  rotate that light source coordinate
		double vx = vertex.x, vz = vertex.z;
		vertex.x = vx * cosTheta + vz * sinTheta;
		vertex.z = -vx * sinTheta + vz * cosTheta;
	}

}
//Finished by Sara Ayubian
