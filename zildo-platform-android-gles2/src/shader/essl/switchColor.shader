[VERTEX]
attribute vec2 vPosition;        // Vertex position
attribute mediump vec2 TexCoord;

uniform mat4 uMVPMatrix;        // Ortho matrix

varying mediump vec2 vTexCoord;

void main(){
    gl_Position = uMVPMatrix * vPosition;
    vTexCoord=TexCoord;
}

[FRAGMENT]
uniform lowp vec4 Color1;
uniform lowp vec4 Color2;
uniform lowp vec4 Color3;
uniform lowp vec4 Color4;
uniform sampler2D sTexture;
uniform lowp vec4 CurColor;		// Current color

varying mediump vec2 vTexCoord;

void main (void) {
    lowp vec4 texel=texture2D(sTexture, vTexCoord);
    if (ceil(texel.w*2.0) == 2.0*Color1.w && texel.xyz==Color1.xyz)
         gl_FragColor = Color3;
    else if (ceil(texel.w*2.0) == 2.0*Color2.w && texel.xyz==Color2.xyz)
        gl_FragColor = Color4;
    else
        gl_FragColor=texel * CurColor;
}