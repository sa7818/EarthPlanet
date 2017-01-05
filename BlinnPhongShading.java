// Course Project-Graphics Computer-winter 2015-2016
// Rendering the Earth Model Using Java programming
// Sara Ayubian
// Student Number 201284643

package mypackage;

import java.awt.Color;

//This part of the program is responsible for Implementing Blinn-Phong shading algorithm for lighting calculation
public class BlinnPhongShading {

	// Default light source position
	private static Vertex Lv = new Vertex(0.2, 0, 1);
	private Vertex V = new Vertex(0, 0, 1);

	public Color calculateIllumination(Vertex N, Color diffuseColor, Color nightColor, Color specularColor) {
		// light vector will be Normalized here
		Lv.normalise();
		// Calculate diffuse light
		double diffusedLight = Math.max(N.dotProduct(Lv), 0);
		// get diffuse color
		int duffRed = (int) (diffuseColor.getRed() * diffusedLight * diff);
		int duffGreen = (int) (diffuseColor.getGreen() * diffusedLight * diff);
		int duffBlue = (int) (diffuseColor.getBlue() * diffusedLight * diff);
		Color dayDiffusedColor = new Color(checkColorRange(duffRed), checkColorRange(duffGreen),
				checkColorRange(duffBlue));
		Color diffusedColor = dayDiffusedColor;
		// this part will calculate night light color
		if (nightColor != null) {
			// Night light diffused color formula
			duffRed = (int) (nightColor.getRed() * (1 - diffusedLight) * lightdiff);
			duffGreen = (int) (nightColor.getGreen() * (1 - diffusedLight) * lightdiff);
			duffBlue = (int) (nightColor.getBlue() * (1 - diffusedLight) * lightdiff);
			Color nightDiffusedColor = new Color(checkColorRange(duffRed), checkColorRange(duffGreen),
					checkColorRange(duffBlue));
			//combining day and night diffused color
			diffusedColor = new Color(dayDiffusedColor.getRed() + nightDiffusedColor.getRed() / 2,
					dayDiffusedColor.getGreen() + nightDiffusedColor.getGreen() / 2, dayDiffusedColor.getBlue()
							+ nightDiffusedColor.getBlue() / 2);
		}
		Color specColor = new Color(0, 0, 0);
		if (diffusedLight != 0) {
			V.normalise();
			Vertex L = new Vertex(Lv);
			Vertex H = L.add(V);
			H.normalise();
			// normalize normal vector
			N.normalise();
			// this part of the code calculate specular light effect
			double specularLight = Math.pow(Math.max(H.dotProduct(N), 0), n);
			duffRed = (int) (specularColor.getRed() * specularLight * spec);
			duffGreen = (int) (specularColor.getGreen() * specularLight * spec);
			duffBlue = (int) (specularColor.getBlue() * specularLight * spec);
			specColor = new Color(checkColorRange(duffRed), checkColorRange(duffGreen), checkColorRange(duffBlue));
		}
		int finalRed = diffusedColor.getRed() + specColor.getRed();
		int finalGreen = diffusedColor.getGreen() + specColor.getGreen();
		int finalBlue = diffusedColor.getBlue() + specColor.getBlue();
		return new Color(checkColorRange(finalRed), checkColorRange(finalGreen), checkColorRange(finalBlue));
	}
	private int checkColorRange(int color) {
		if (color > 255)
			color = 255;
		else if (color < 0)
			color = 0;
		return color;
	}

	private double diff = 0.6, lightdiff = 0.6, spec = 0.45, n = 80;

	public BlinnPhongShading() {
	}

	public BlinnPhongShading(double kd, double lightdiff, double ks, double n) {
		this.diff = kd;
		this.lightdiff = lightdiff;
		this.spec = ks;
		this.n = n;
	}

	//this part will  update light source for rendering object
	public static void setLightSource(Vertex light) {
		Lv = light;
	}

	public static Vertex getLightSource() {
		return Lv;
	}
}
//Finished by Sara Ayubian
