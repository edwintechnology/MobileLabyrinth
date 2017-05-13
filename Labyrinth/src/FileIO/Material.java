package FileIO;

public class Material {
	private float[] ambient;
	private float[] diffuse;
	private float[] specular;
	private float shininess;
	private float  specularCoefficient;
	private float indexOfRefraction;
	private float transparency;
	private float illum;
	private String Name;
	public Material()
	{
		ambient = new float[4];
		diffuse = new float[4];
		specular  = new float[4];
		shininess = 0.0f;
	}
	public Material(float[] a, float[] d, float[] s, float sh)
	{
		ambient = a;
		diffuse = d;
		specular = s;
		shininess = sh;
	}
	public String getName(){
		return this.Name;
	}
	public void setName(String name){
		this.Name = name;
	}
	public float[] getAmbient() {
		return ambient;
	}
	public void setAmbient(float[] ambient) {
		this.ambient = ambient;
	}
	public float[] getDiffuse() {
		return diffuse;
	}
	public void setDiffuse(float[] diffuse) {
		this.diffuse = diffuse;
	}
	public float[] getSpecular() {
		return specular;
	}
	public void setSpecular(float[] specular) {
		this.specular = specular;
	}
	public float getShininess() {
		return shininess;
	}
	public void setShininess(float shininess) {
		this.shininess = shininess;
	}

	public float getSpecularCoefficient() {
		return specularCoefficient;
	}
	public void setSpecularCoefficient(float specularCoefficient) {
		this.specularCoefficient = specularCoefficient;
	}
	public float getIndexOfRefraction() {
		return indexOfRefraction;
	}
	public void setIndexOfRefraction(float indexOfRefraction) {
		this.indexOfRefraction = indexOfRefraction;
	}
	public float getTransparency() {
		return transparency;
	}
	public void setTransparency(float transparency) {
		this.transparency = transparency;
	}
	public float getIllum() {
		return illum;
	}
	public void setIllum(float illum) {
		this.illum = illum;
	}
}
