[VERTEX]
attribute lowp vec4 vPosition;		// Vertex position
attribute mediump vec2 TexCoord;

uniform highp mat4 uMVPMatrix;		// Ortho matrix
uniform int radius;
uniform lowp vec2 center;

varying bool inside = false;
varying mediump vec2 vTexCoord;

void main(){
	if (dist(vPosition, center) < radius) 
		inside = true;	// Maybe following lines are useless if inside is false
	gl_Position = uMVPMatrix * vPosition;
	vTexCoord=TexCoord;
}

[FRAGMENT]
precision highp float;
uniform sampler2D sTexture;

varying mediump vec2 vTexCoord;
varying bool inside;

void main(){
	if (inside)
		gl_FragColor = texture2D(sTexture, vTexCoord);
	else
		discard;	// Keep the default black color
}