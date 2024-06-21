[VERTEX]
attribute vec2 vPosition;        // Vertex position
attribute vec2 TexCoord;

uniform mat4 uMVPMatrix;        // Ortho matrix

varying mediump vec2 vTexCoord;

void main(){
    gl_Position = uMVPMatrix * vec4(vPosition, 0.0, 1.0);
    vTexCoord=TexCoord;
}

[FRAGMENT]
precision mediump float;
uniform vec4 randomColor;
uniform sampler2D sTexture;

varying mediump vec2 vTexCoord;

void main() {
	lowp vec4 texel=texture2D(sTexture, vTexCoord);
	if (texel.w < 0.5)
		discard;
	else {
		gl_FragColor = randomColor - texel / 2.0;
		gl_FragColor.w = 1.0;
	}
}