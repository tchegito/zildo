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
uniform float factor;		// Gold factor
varying mediump vec2 vTexCoord;
void main(){
	vec4 texel = texture2D(sTexture, vTexCoord);
	float gray = dot(vec3(texel),vec3(0.3, 0.59, 0.11));
	gray = clamp(gray * (factor * 4.0), 0.0, 1.0);
	gl_FragColor = vec4(gray, gray, 0, texel.w * factor);
}