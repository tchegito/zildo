[VERTEX]
attribute lowp vec4 vPosition;		// Vertex position
attribute mediump vec2 TexCoord;

uniform highp mat4 uMVPMatrix;		// Ortho matrix
uniform lowp vec2 vTranslate;		// Translation

varying mediump vec2 vTexCoord;

highp vec4 translated;
void main(){
	translated = vec4(vPosition.x + vTranslate.x, vPosition.y + vTranslate.y, vPosition.z, vPosition.w);
	gl_Position = uMVPMatrix * translated;
	vTexCoord=TexCoord;
}

[FRAGMENT]
precision highp float;
uniform sampler2D sTexture;
uniform lowp vec4 CurColor;		// Ortho matrix
varying mediump vec2 vTexCoord;
void main(){
	gl_FragColor = texture2D(sTexture, vTexCoord) * CurColor;
}