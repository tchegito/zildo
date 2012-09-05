[VERTEX]
attribute vec2 vPosition;		// Vertex position
attribute mediump vec2 TexCoord;

uniform mat4 uMVPMatrix;		// Ortho matrix

varying mediump vec2 vTexCoord;

void main(){
	gl_Position = uMVPMatrix * vec4(vPosition, 0.0, 1.0);
	vTexCoord=TexCoord;
}

[FRAGMENT]
uniform sampler2D sTexture;
uniform lowp int squareSize;

varying mediump vec2 vTexCoord;

void main(){
	int dx = int(vTexCoord.x * 256.0 / float(squareSize));
	int dy = int(vTexCoord.y * 256.0 / float(squareSize*2));
	mediump float tx = float(dx * squareSize) / 256.0;
	mediump float ty = float(dy * squareSize*2) / 256.0;
	gl_FragColor = texture2D(sTexture, vec2(tx, ty) );
}