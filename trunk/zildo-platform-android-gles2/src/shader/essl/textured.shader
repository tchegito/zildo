[VERTEX]
attribute vec2 vPosition;		// Vertex position
attribute mediump vec2 TexCoord;

uniform mat4 uMVPMatrix;		// Ortho matrix
uniform lowp vec2 vTranslate;		// Translation

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
void main(){
	gl_FragColor = texture2D(sTexture, vTexCoord) * CurColor;
}