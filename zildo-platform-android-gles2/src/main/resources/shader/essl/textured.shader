[VERTEX]
attribute vec2 vPosition;		// Vertex position
attribute mediump vec2 TexCoord;

uniform mat4 uMVPMatrix;		// Ortho matrix
uniform vec2 vTranslate;		// Translation

varying mediump vec2 vTexCoord;

highp vec4 translated;
void main(){
	translated = vec4(vPosition.x + vTranslate.x, vPosition.y + vTranslate.y, 0.0, 1.0);
	gl_Position = uMVPMatrix * translated;
	vTexCoord=TexCoord;
}

[FRAGMENT]
precision mediump float;
uniform sampler2D sTexture;
uniform lowp vec4 CurColor;		// Current color
varying mediump vec2 vTexCoord;
uniform int clip;

float clipFunction(in float y) { 
	return clamp( (208.0 - abs(y*2.0-240.0)) / 24.0, 0.0, 1.0);
}

void main(){
	vec4 pix = texture2D(sTexture, vTexCoord) * CurColor;
	if (clip == 1)
		pix = pix * clipFunction(gl_FragCoord.y);
	gl_FragColor = pix;
}