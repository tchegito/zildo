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
uniform sampler2D sTexture;

varying mediump vec2 vTexCoord;

void main() {
	lowp vec2 pos = mod(gl_FragCoord.xy, 16);
	
	lowp vec2 texCoord = vTexCoord;
	
	float tileCount = 16.0;
	lowp vec2 variation = vec2(sin(1.6*alpha+pos.y*.09), 0.1+0.7*cos(1.17*alpha-pos.x*0.14)) * 0.05;
	lowp vec2 tileIndex = floor(texCoord * tileCount);
	lowp vec2 tileUV = fract(texCoord * tileCount);
	tileUV = fract(tileUV + variation);
	lowp vec2 distorted = (tileIndex + tileUV) / tileCount;
	gl_FragColor= texture2D(sTexture, distorted);
}