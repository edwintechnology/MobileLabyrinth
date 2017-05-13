precision mediump float;
precision mediump int;

uniform vec3 u_LightPos1;
uniform vec3 u_LightPos2;
uniform vec3 u_LightPos3;
uniform vec3 u_LightPos4;

uniform sampler2D u_Texture;

varying vec3 v_vertex;
varying vec3 v_normal;
varying vec2 v_texture;

struct lightSource
{
  vec3 position;
  vec4 diffuse;
  vec4 specular;
  float constantAttenuation, linearAttenuation, quadraticAttenuation;
  float spotCutoff, spotExponent;
  vec3 spotDirection;
};
const int numberOfLights = 3;
lightSource lights[numberOfLights];

lightSource light0 = lightSource(
  vec3(-0.01,  0.11,  -3.58),
  vec4(1.0,  1.0,  1.0, 1.0),
  vec4(1.0,  1.0,  1.0, 1.0),
  0.0, 1.0, 0.0,
  180.0, 0.0,
  vec3(0.0, 0.0, 0.0)
);
lightSource light1 = lightSource(
  vec3(-0.01,  0.11,  -3.58),
  vec4(1.0,  1.0,  1.0, 1.0),
  vec4(1.0,  1.0,  1.0, 1.0),
  0.0, 1.0, 0.0,
  180.0, 0.0,
  vec3(0.0, 0.0, 0.0)
);
lightSource light2 = lightSource(
  vec3(-0.01,  0.11,  -3.58),
  vec4(1.0,  1.0,  1.0, 1.0),
  vec4(1.0,  1.0,  1.0, 1.0),
  0.0, 1.0, 0.0,
  180.0, 0.0,
  vec3(0.0, 0.0, 0.0)
);
lightSource light3 = lightSource(
  vec3(-0.01,  0.11,  -3.58),
  vec4(1.0,  1.0,  1.0, 1.0),
  vec4(1.0,  1.0,  1.0, 1.0),
  0.0, 1.0, 0.0,
  180.0, 0.0,
  vec3(0.0, 0.0, 0.0)
);
vec4 scene_ambient = vec4(0.2, 0.2, 0.2, 1.0);
struct material
{
  vec4 ambient;
  vec4 diffuse;
  vec4 specular;
  float shininess;
};

material objMtl = material(
  vec4(0.0, 0.0, 0.0, 1.0),
  vec4(0.64, 0.64, 0.64, 1.0),
  vec4(0.5, 0.5, 0.5, 1.0),
  5.0
);

void main()
{  	
	light0.position = u_LightPos1;
	light1.position = u_LightPos2;
	light2.position = u_LightPos3;
	light3.position = u_LightPos4;
	
	//lights[0] = light0;
	//lights[1] = light1;
	//lights[2] = light2;

	vec4 color = scene_ambient;
	vec3 normal = normalize(v_normal);
	vec3 eyeVector = normalize(-v_vertex);
	
	vec3 lightVector;
	vec3 reflectVector;
	
	
	// FIRST LIGHT
	lightVector = normalize(light0.position - v_vertex);
	reflectVector = normalize(-reflect(lightVector, normal));
	
	// ambient term
	vec4 Iamb = objMtl.ambient;
	
	// diffuse term
	vec4 Idiff = objMtl.diffuse * max(dot(normal, lightVector), 0.0);
	Idiff = clamp(Idiff, 0.0, 1.0);
	
	// specular term
	vec4 Ispec = objMtl.specular * pow(max(dot(reflectVector, eyeVector), 0.0), 0.3 * objMtl.shininess);
	Ispec = clamp(Ispec, 0.0, 1.0);
	
	color = color + Iamb + Idiff + Ispec;
	
	// SECOND LIGHT
	lightVector = normalize(light1.position - v_vertex);
	reflectVector = normalize(-reflect(lightVector, normal));
	
	Iamb = objMtl.ambient;
	Idiff = objMtl.diffuse * max(dot(normal, lightVector), 0.0);
	Idiff = clamp(Idiff, 0.0, 1.0);
	Ispec = objMtl.specular * pow(max(dot(reflectVector, eyeVector), 0.0), 0.3 * objMtl.shininess);
	Ispec = clamp(Ispec, 0.0, 1.0);
	
	color = color + Iamb + Idiff + Ispec;
	
	// THIRD LIGHT
	lightVector = normalize(light2.position - v_vertex);
	reflectVector = normalize(-reflect(lightVector, normal));
	
	Iamb = objMtl.ambient;
	Idiff = objMtl.diffuse * max(dot(normal, lightVector), 0.0);
	Idiff = clamp(Idiff, 0.0, 1.0);
	Ispec = objMtl.specular * pow(max(dot(reflectVector, eyeVector), 0.0), 0.3 * objMtl.shininess);
	Ispec = clamp(Ispec, 0.0, 1.0);
	
	color = color + Iamb + Idiff + Ispec;
	
	// FOURTH LIGHT
	lightVector = normalize(light3.position - v_vertex);
	reflectVector = normalize(-reflect(lightVector, normal));
	
	Iamb = objMtl.ambient;
	Idiff = objMtl.diffuse * max(dot(normal, lightVector), 0.0);
	Idiff = clamp(Idiff, 0.0, 1.0);
	Ispec = objMtl.specular * pow(max(dot(reflectVector, eyeVector), 0.0), 0.3 * objMtl.shininess);
	Ispec = clamp(Ispec, 0.0, 1.0);
	
	color = color + Iamb + Idiff + Ispec;
	
	gl_FragColor = color * texture2D(u_Texture, v_texture);
	
	//vec3 view = normalize(vec3(0.0, 0.0, 1.0));
	//vec3 lightVector;
	//float attenuation;
	
	//vec3 totalLighting = ambient();
	
	//for(int i = 0; i < numberOfLights; i++)
	//{
	//	if(0.0 == lights[i].position.w){
	///		lightVector = normalize(vec3(lights[i].position));
	//		attenuation = 1.0;
	//	}
	//	else
	//	{
	//		vec3 posToLightSource = vec3(lights[i].position - v_vertex);
	//		float distance = length(posToLightSource);
	//		lightVector = normalize(posToLightSource);
	//		attenuation = 1.0 / (lights[i].constantAttenuation
	//		       + lights[i].linearAttenuation * distance
	//		       + lights[i].quadraticAttenuation * distance * distance);
			       
	//		if(lights[i].spotCutoff <= 90.0)
	//		{
	//			float clampedCosine =  max(0.0, dot(-lightVector, normalize(lights[i].spotDirection)));
	//			if (clampedCosine < cos(radians(lights[i].spotCutoff))) // outside of spotlight cone?
	//			{
	//	 			attenuation = 0.0;
	//			}
	//      		else
	//			{
	//	  			attenuation = attenuation * pow(clampedCosine, lights[i].spotExponent);   
	//			}
	//		}
	//	}
		
	//	vec3 diffuseReflection = vec3(attenuation 
	//			* lights[i].diffuse * texture2D(u_Texture, v_texture)
	//			* max(0.0, dot(vec4(normal, 1.0), vec4(lightVector, 1.0))));
				
	//	vec3 specularReflection;
    //  	if (dot(normal, lightVector) < 0.0) // light source on the wrong side?
	//	{
	//  		specularReflection = vec3(0.0, 0.0, 0.0); // no specular reflection
	//	}
     // 	else // light source on the right side
	//	{
	//  		specularReflection = attenuation * vec3(lights[i].specular) * vec3(objMtl.specular) 
	//    	* pow(max(0.0, dot(reflect(-lightVector, normal), view)), objMtl.shininess);
	//	}
	//	totalLighting = totalLighting + diffuseReflection + specularReflection;
	//}
	
	//gl_FragColor = vec4(totalLighting, 1.0); //* texture2D(u_Texture, v_texture);
	
	//color = phong(lights[2], phong(lights[1], phong(lights[0], color, normal, view), normal, view), normal, view);
	//color[0] = min(1.0, color[0]);
	//color[1] = min(1.0, color[1]);
	//color[2] = min(1.0, color[2]);
	
	//gl_FragColor = vec4(color, 1.0) * texture2D(u_Texture, v_texture); 
	
	//float distance = length(u_LightPos1 - v_vertex);                  
	//lightVector = normalize(u_LightPos1 - v_vertex);              	
	//float diffuse1 = max(dot(v_normal, lightVector), 0.0);               	  		  													  
	///diffuse1 = diffuse1 * (1.0 / (1.0 + (0.10 * distance)));
   // diffuse1 = diffuse1 + 0.2;  

	//distance = length(u_LightPos2 - v_vertex);                  
	//lightVector = normalize(u_LightPos2 - v_vertex);              	
	//float diffuse2 = max(dot(v_normal, lightVector), 0.0);               	  		  													  
	//diffuse2 = diffuse2 * (1.0 / (1.0 + (0.10 * distance)));
    //diffuse2 = diffuse2 + 0.2; 
    
   // distance = length(u_LightPos3 - v_vertex);                  
	//lightVector = normalize(u_LightPos3 - v_vertex);              	
	//float diffuse3 = max(dot(v_normal, lightVector), 0.0);               	  		  													  
	//diffuse3 = diffuse3 * (1.0 / (1.0 + (0.10 * distance)));
   // diffuse3 = diffuse3 + 0.2; 
    
   // float diffuse = diffuse1+diffuse2+diffuse3;
    
	//gl_FragColor = (diffuse1 * texture2D(u_Texture, v_texture));
	      
}