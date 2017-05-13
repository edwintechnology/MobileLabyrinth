uniform mat4 uModelMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uProjectionMatrix;
uniform mat4 uNormalMatrix;

attribute vec4 a_vertex;
attribute vec3 a_normal;
attribute vec2 a_texture;

varying vec3 v_vertex;
varying vec3 v_normal;
varying vec2 v_texture;

void main() {
	mat4 MV = uViewMatrix * uModelMatrix;
    mat4 MVP =  uProjectionMatrix * MV;
        
	v_vertex = vec3(MV * a_vertex);            		
	v_texture = a_texture;                                      
    v_normal = normalize(vec3(uNormalMatrix * vec4(a_normal, 0.0)));

	gl_Position = MVP * a_vertex;
}