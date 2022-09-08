[VERTEX]
attribute vec2 vPosition;		// Vertex position
attribute vec2 TexCoord;

uniform mat4 uMVPMatrix;		// Ortho matrix

varying mediump vec2 vTexCoord;

void main(){
	gl_Position = uMVPMatrix * vec4(vPosition.x, vPosition.y, 0.0, 1.0);
	vTexCoord=TexCoord;
}

[FRAGMENT]
precision mediump float;
uniform sampler2D sTexture;
uniform float squareSize;
uniform vec4 CurColor;		// Current color

varying mediump vec2 vTexCoord;

void main(){
	float dx = floor(vTexCoord.x * 256.0 / squareSize);
	float dy = floor(vTexCoord.y * 256.0 / (squareSize*2.0));
	float tx = dx * squareSize / 256.0;
	float ty = (dy * squareSize * 2.0) / 256.0;
	gl_FragColor = texture2D(sTexture, vec2(tx, ty) ) * CurColor;
}