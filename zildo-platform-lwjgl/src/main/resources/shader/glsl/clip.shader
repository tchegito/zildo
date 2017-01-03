uniform vec4 curColor;
uniform sampler2D tex;

// Math function so as alpha clips a given zone between y=20 and y=220
float clipFunction(in float y) {
	return clamp( (208.0 - abs(y*2.0-240.0)) / 24.0, 0.0, 1.0);
}

void main (void) {
	vec4 texel=texture2D(tex, gl_TexCoord[0].st);
	
	float factor = clipFunction(gl_FragCoord.y);
	gl_FragColor=texel * (factor) * curColor;
}