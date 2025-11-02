uniform vec4 curColor;
uniform sampler2D tex;
uniform vec4 alpha; // (x:angle alpha, y:zoom factor)

void main (void) {

	int uGridSize = 8;
	// Size of one cell in UV space
    float cellSize = 1.0 / float(uGridSize);

	vec2 cellVec = vec2(cellSize / 2.0, cellSize);
	
	vec2 uv = gl_TexCoord[0].st;
    // Find which cell we're in (integer grid position)
    vec2 cellIndex = floor(uv / cellVec);

    // Find the local coordinate within the cell, centered at (0,0)
    vec2 cellOrigin = cellIndex * cellVec;
    vec2 localCoord = (uv - cellOrigin) - vec2(cellSize * 0.5);
    
    localCoord /= alpha.y;
        
	float c = cos(alpha.x);
    float s = sin(alpha.x);
    mat2 rotation = mat2(
        c, -s,
        s,  c
    );
    vec2 rotated = rotation * localCoord + vec2(cellSize * 0.5) + cellOrigin;
	
	vec4 texel=texture2D(tex, rotated);
	gl_FragColor=texel * curColor;
}