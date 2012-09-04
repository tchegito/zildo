[VERTEX]
attribute vec2 vPosition;        // Vertex position
attribute mediump vec2 TexCoord;

uniform mat4 uMVPMatrix;        // Ortho matrix

varying mediump vec2 vTexCoord;

void main(){
    gl_Position = uMVPMatrix * vPosition;
    vTexCoord=TexCoord;
}

[FRAGMENT]
uniform lowp vec4 randomColor;
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