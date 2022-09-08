[VERTEX]

uniform mat4 uMVPMatrix; 	// Ortho matrix
attribute vec2 vPosition;	// Vertex position
void main(){
	gl_Position = uMVPMatrix * vec4(vPosition, 0.0, 1.0);
}

[FRAGMENT]

precision mediump float;
uniform vec4 CurColor;
void main(){
	gl_FragColor = CurColor;
}
