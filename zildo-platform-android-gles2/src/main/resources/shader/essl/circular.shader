[VERTEX]
attribute vec2 vPosition;        // Vertex position
attribute mediump vec2 TexCoord;

uniform mat4 uMVPMatrix;        // Ortho matrix

varying mediump vec2 vTexCoord;

void main(){
    gl_Position = uMVPMatrix * vec4(vPosition, 0.0, 1.0);
    vTexCoord=TexCoord;
}

[FRAGMENT]
precision mediump float;

uniform sampler2D sTexture;
uniform vec4 CurColor;		// Current color
uniform vec4 alpha; // (x:angle alpha, y:zoom factor)

varying mediump vec2 vTexCoord;

void main (void) {
	int uGridSize = 8;
	// Size of one cell in UV space
    float cellSize = 1.0 / float(uGridSize);

	vec2 cellVec = vec2(cellSize / 2.0, cellSize);
	
	vec2 uv = vTexCoord;
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
		
    lowp vec4 texel=texture2D(sTexture, rotated);
    gl_FragColor=texel * CurColor;
}