uniform vec4 factor;
uniform sampler2D tex;

const float blurSize = 1.0/256.0;

void main (void) {
	vec4 texel=texture2D(tex, gl_TexCoord[0].st);
	float gray = dot(vec3(texel),vec3(0.3, 0.59, 0.11));
	gray = clamp(gray * (factor.x * 4.0), 0.0, 1.0);
	gl_FragColor = vec4(gray ,gray, 0, texel.w);
}