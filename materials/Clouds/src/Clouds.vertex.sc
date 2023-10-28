$input a_position, a_color0
#if INSTANCING
$input i_data0, i_data1, i_data2
#endif

$output v_color0

#include <bgfx_shader.sh>
#include <MinecraftRenderer.Materials/DynamicUtil.dragonh>
#include <MinecraftRenderer.Materials/FogUtil.dragonh>
#include <MinecraftRenderer.Materials/TAAUtil.dragonh>

uniform vec4 DistanceControl;
uniform vec4 CloudColor;

void main() {
    vec4 position;
    vec3 worldPosition;
#if INSTANCING
    mat4 model;
    model[0] = vec4(i_data0.x, i_data1.x, i_data2.x, 0);
    model[1] = vec4(i_data0.y, i_data1.y, i_data2.y, 0);
    model[2] = vec4(i_data0.z, i_data1.z, i_data2.z, 0);
    model[3] = vec4(i_data0.w, i_data1.w, i_data2.w, 1);
    worldPosition = instMul(model, vec4(a_position, 1.0)).xyz;
#else
    mat4 World = u_model[0];
    worldPosition = mul(World, vec4(a_position, 1.0)).xyz;
#endif
#ifdef DEPTH_ONLY
    v_color0 = vec4(0.0, 0.0, 0.0, 0.0);
    position = mul(u_modelViewProj, vec4(worldPosition, 1.0));
    position.z = clamp(position.z, 0.0, 1.0);
#else
    position = jitterVertexPosition(worldPosition);
    float depth = length(worldPosition) / DistanceControl.x;
    v_color0 = a_color0 * CloudColor;
    const float fogNear = 0.9;
    float fog = max(depth - fogNear, 0.0);
    v_color0.a *= clamp(1.0 - fog, 0.0, 1.0);
#endif
    gl_Position = position;
}
