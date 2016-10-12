uniform sampler2D tex;
uniform vec4 iFrame;

const float nHome = 3.0;
const float coeff = 0.2;

float nearest(float a) {
    
    float midHome = 1.0/nHome/2.0;

    int numberHome = int(floor(a*nHome));
    float pos = float(numberHome) / nHome;
    return a > (pos + midHome) ? (pos+2.0*midHome) : pos;
}

void main(void) {
	vec2 uv = gl_TexCoord[0].st;
    
    vec2 nearestHome = vec2(nearest(uv.x),
                            nearest(uv.y));
    
    vec2 d = abs(nearestHome - uv);
    
    float scale = clamp(float(iFrame), 0.0, 256.0) / 256.0;

    vec2 interp = mix(uv, nearestHome, scale);
    float weight = scale * 1.4;
    
    vec4 trueCol = texture2D(tex, uv).xyzw;
    vec4 col = trueCol * (1.0-scale*2.0);
    
    vec2 dir1 = vec2(0, -1);
    vec2 dir2 = vec2(1, -1);
    vec2 dir3 = vec2(0, 1);
    vec2 dir4 = vec2(-1, 0);
    
    // Take 4 points
    vec4 sumCol;
    sumCol = weight * texture2D(tex, uv + dir1 * scale * coeff*d).xyzw;
    sumCol += weight * texture2D(tex, uv + dir2 * scale * coeff*d).xyzw;
    sumCol += weight * texture2D(tex, uv + dir3 * scale * coeff*d).xyzw;
    sumCol += weight * texture2D(tex, uv + dir4 * scale * coeff*d).xyzw;
                 
    col += clamp((0.5-scale*0.4)*sumCol, 0.0, 0.7) - 0.02*scale*scale;
    
    gl_FragColor = col;
}