[VERTEX]
attribute lowp vec4 vPosition;		// Vertex position
attribute mediump vec2 TexCoord;

uniform highp mat4 uMVPMatrix;		// Ortho matrix

varying mediump vec2 vTexCoord;

void main(){
	gl_Position = uMVPMatrix * vPosition;
	vTexCoord=TexCoord;
}

[FRAGMENT]
precision highp float;
uniform sampler2D sTexture;
uniform int squareSize;

varying mediump vec2 vTexCoord;

void main(){
	lowp ivec2 vNormalizedTexcoord = vTexCoord / squareSize;
	gl_FragColor = texture2D(sTexture, vNormalizedTexcoord * squareSize);
}