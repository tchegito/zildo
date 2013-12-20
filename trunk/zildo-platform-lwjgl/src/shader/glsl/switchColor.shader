uniform vec4 Color1;
uniform vec4 Color2;
uniform vec4 Color3;
uniform vec4 Color4;
uniform vec4 curColor;
uniform sampler2D tex;

void main (void) {
	vec4 texel=texture2D(tex, gl_TexCoord[0].st);
	if (ceil(texel.w*2.0) == 2.0*Color1.w && texel.xyz==Color1.xyz)
		gl_FragColor = Color3;
	else if (ceil(texel.w*2.0) == 2.0*Color2.w && texel.xyz==Color2.xyz)
		gl_FragColor = Color4;
	else gl_FragColor=texel * curColor;
}