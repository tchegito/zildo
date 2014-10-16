[VERTEX]
attribute vec2 vPosition;		// Vertex position
attribute vec2 TexCoord;

uniform mat4 uMVPMatrix;		// Ortho matrix

varying mediump vec2 vTexCoord;
varying mediump vec2 interpPosition;

void main(){
	gl_Position = uMVPMatrix * vec4(vPosition, 0.0, 1.0);
	vTexCoord=TexCoord;
	interpPosition = vPosition.xy;
}

[FRAGMENT]
precision mediump float;
uniform sampler2D sTexture;
uniform int radius;
uniform vec2 center;
uniform vec4 CurColor;		// Current color

varying mediump vec2 vTexCoord;
varying mediump vec2 interpPosition;

void main(){
	if (int(distance(interpPosition.xy, center)) < radius) 
		gl_FragColor = texture2D(sTexture, vTexCoord) * CurColor;
	else
		discard;
}