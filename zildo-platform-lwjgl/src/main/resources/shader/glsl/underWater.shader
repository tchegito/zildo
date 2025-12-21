uniform sampler2D tex;
uniform float alpha;

void main (void) {
	vec2 pos = mod(gl_FragCoord.xy, 16);
	
	vec2 texCoord = gl_TexCoord[0].st;
	
	float tileCount = 16.0;
	vec2 variation = vec2(sin(1.6*alpha+pos.y*.09), 0.1+0.7*cos(1.17*alpha-pos.x*0.14)) * 0.05;
	vec2 tileIndex = floor(texCoord * tileCount);
	vec2 tileUV = fract(texCoord * tileCount);
	tileUV = fract(tileUV + variation);
	vec2 distorted = (tileIndex + tileUV) / tileCount;
	gl_FragColor= texture2D(tex, distorted);
}