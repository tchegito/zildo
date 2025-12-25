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
uniform sampler2D sTexture;
varying mediump vec2 vTexCoord;

const float PI = 3.1415926535897932;

//speed
const float speed = 0.2;
const float speed_x = 0.3;
const float speed_y = 0.3;

// refraction
const float emboss = 0.50;
const float intensity = 2.4;
const int steps = 8;
const float frequency = 6.0;
const int angle = 7; // better when a prime

// reflection
const float delta = 60.;
const float gain = 700.;
const float reflectionCutOff = 0.012;
const float reflectionIntensity = 200000.;
 
  float col(vec2 coord,float time)
  {
    float delta_theta = 2.0 * PI / float(angle);
    float col = 0.0;
    float theta = 0.0;
    for (int i = 0; i < steps; i++)
    {
      lowp vec2 adjc = coord;
      theta = delta_theta*float(i);
      adjc.x += cos(theta)*time*speed + time * speed_x;
      adjc.y -= sin(theta)*time*speed - time * speed_y;
      col = col + cos( (adjc.x*cos(theta) - adjc.y*sin(theta))*frequency)*intensity;
    }

    return cos(col);
  }
  
void main (void) {
	lowp vec2 pos = gl_FragCoord.xy + camera;
	
	float time = alpha*0.1;
	lowp vec2 iResolution = vec2(320, 240);

	lowp vec2 p = pos/iResolution.xy, c1 = p, c2 = p;
	float cc1 = col(c1*4.0,time);
	
	c2.x += iResolution.x/delta;
	float dx = emboss*(cc1-col(c2,time))/delta;
	
	c2.x = p.x;
	c2.y += iResolution.y/delta;
	float dy = emboss*(cc1-col(c2,time))/delta;

	float alpha = 1.+dot(dx,dy)*gain;
		
	float ddx = dx - reflectionCutOff;
	float ddy = dy - reflectionCutOff;
	if (ddx > 0. && ddy > 0.)
		alpha = pow(alpha, ddx*ddy*reflectionIntensity);
		
	lowp vec4 texel1 = texture2D(sTexture, vTexCoord);

	gl_FragColor= texel1*alpha;
}