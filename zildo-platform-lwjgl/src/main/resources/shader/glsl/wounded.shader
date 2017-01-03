uniform vec4 randomColor;
uniform sampler2D tex;

void main (void) {
	vec4 texel=texture2D(tex, gl_TexCoord[0].st);
	if (texel.w != 0.0) {
		gl_FragColor = randomColor- texel / 2.0;
		gl_FragColor.w = 1.0;
	}
}