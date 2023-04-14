uniform vec4 noise;
uniform vec4 curColor;

void main (void) {
	vec2 delta = vec2(gl_TexCoord[0].xy - vec2(0.5, 0.5));
	float intensity = pow(delta.x, 2.0) + pow(delta.y, 2.0);
	intensity = sqrt(intensity);
	
	// calculate angle-dependant factor
	float cosAlpha = delta.x / length(delta);
	float angle = atan(delta.y / delta.x);
	float trigAlpha1 = (0.8-(noise.y*0.12)) * cos((angle + noise.x*0.6) * 4.0);
	float trigAlpha2 = 0.6 * sin((angle - noise.x/1.2) * 2.0);
	float alpha = max(abs(trigAlpha1), abs(trigAlpha2));
	alpha = 0.11+ 0.6*alpha;
	alpha += noise.y * 0.01;
	intensity = intensity / clamp(alpha, 0.1, 0.7);
	intensity = clamp(intensity, 0.0, 1.0);
	
	vec4 starTex = vec4(1.0, 1.0, 1.0, 1.0-intensity) * curColor;
	
	gl_FragColor = starTex;
}