// Course Project-Graphics Computer-winter 2015-2016
// Rendering the Earth Model Using Java programming
// Sara Ayubian
// Student Number 201284643

package mypackage;

import java.util.ArrayList;
import java.util.List;

//Tessellate octahedron to generate sphere
public class TessellationAlgorithm {

	// tesellate the sphere
	public void tessellateSphere(Sphere sphere, int viewerLevel) {
		for (int level = 0; level < viewerLevel; level++) {
			List<Triangle> createdTriangles = new ArrayList<Triangle>();
			for (Triangle triangle : sphere.triangles) {
				// get vertex of triangle
				Vertex[] vertices = new Vertex[3];
				Vertex[] midPoints = new Vertex[3];
				for (int i = 0; i < 3; i++)
					vertices[i] = sphere.vertices.get(triangle.vertex[i]);
				// Calculate new midpoints
				int indexMdpt1 = sphere.vertices.size();
				for (int i = 0; i < 3; i++) {
					midPoints[i] = Vertex.midpoint(vertices[i], vertices[(i + 1) % 3]);
					// Normalize and push back
					midPoints[i].normalizeAndScale(sphere.radius);
					// calculate latitude and longitude
					midPoints[i].calculateAngles(sphere.radius);
					sphere.vertices.add(midPoints[i]);
				}

				// create new triangles
				createdTriangles.add(new Triangle(triangle.vertex[0], indexMdpt1, indexMdpt1 + 2));
				createdTriangles.add(new Triangle(indexMdpt1, triangle.vertex[1], indexMdpt1 + 1));
				createdTriangles.add(new Triangle(indexMdpt1 + 2, indexMdpt1 + 1, triangle.vertex[2]));
				createdTriangles.add(new Triangle(indexMdpt1, indexMdpt1 + 1, indexMdpt1 + 2));
			}
			sphere.triangles = createdTriangles;
		}

	}

}
//Finished by Sara Ayubian