package com.qycx.base;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public abstract class SampleBase {

    private String TAG = "SampleBase";

    private String[] vertexShaderSource =
            {
                    "precision mediump float;\n" +
                            "attribute vec3 aPosition;\n" +

                            "void main() {\n" +
                            "	gl_Position = vec4(aPosition, 1.0);\n" +
                            "}\n",

                    "precision mediump float;\n" +
                            "attribute vec3 aPosition;\n" +
                            "uniform mat4 uMatrix;\n" +
                            "void main() {\n" +
                            "	gl_Position = uMatrix * vec4(aPosition, 1.0);\n" +
                            "}\n",

                    "precision mediump float;\n" +
                            "attribute vec3 aPosition;\n" +
                            "uniform mat4 uProjMatrix;\n" +
                            "uniform mat4 uMVMatrix;\n" +
                            "void main() {\n" +
                            "	gl_Position = uProjMatrix * uMVMatrix * vec4(aPosition, 1.0);\n" +
                            "}\n",

                    "precision mediump float;\n" +
                            "attribute vec3 aPosition;\n" +
                            "attribute vec3 aNormal;\n" +
                            "uniform mat4 uProjMatrix;\n" +
                            "uniform mat4 uViewMatrix;\n" +
                            "uniform mat4 uModelMatrix;\n" +
                            "uniform vec3 uLightColor;\n" +
                            "uniform vec3 uLightDir;\n" +
                            "uniform vec3 uMaterialDiffuse;\n" +
                            "varying vec3 vColor;\n" +
                            "void main() {\n" +
                            "	gl_Position = uProjMatrix * uViewMatrix * uModelMatrix * vec4(aPosition, 1.0);\n" +
                            "   vec3 normal_cameraspace = (uViewMatrix * uModelMatrix * vec4(aNormal, 1.0)).xyz;\n" +
                            "   vec3 lightdir_cameraspace = (uViewMatrix * vec4(uLightDir, 1.0)).xyz;" +
                            "   vec3 n = normalize(normal_cameraspace);\n" +
                            "   vec3 l = normalize(lightdir_cameraspace);\n" +
                            "   float cosTheta = clamp( dot(n, l), 0.0, 1.0);" +
                            "   vColor = uMaterialDiffuse * uLightColor * cosTheta;" +
                            "}\n",

                    "precision mediump float;\n" +
                            "attribute vec3 aPosition;\n" +
                            "attribute vec3 aNormal;\n" +
                            "attribute vec2 aTexCoord;\n" +
                            "uniform mat4 uProjMatrix;\n" +
                            "uniform mat4 uViewMatrix;\n" +
                            "uniform mat4 uModelMatrix;\n" +
                            "uniform vec3 uLightColor;\n" +
                            "uniform vec3 uLightDir;\n" +
                            "uniform vec3 uMaterialDiffuse;\n" +
                            "varying vec3 vColor;\n" +
                            "varying vec2 vTexCoord;\n" +
                            "void main() {\n" +
                            "   vTexCoord = aTexCoord;\n" +
                            "	gl_Position = uProjMatrix * uViewMatrix * uModelMatrix * vec4(aPosition, 1.0);\n" +
                            "   vec3 normal_cameraspace = (uViewMatrix * uModelMatrix * vec4(aNormal, 1.0)).xyz;\n" +
                            "   vec3 lightdir_cameraspace = (uViewMatrix * vec4(uLightDir, 1.0)).xyz;" +
                            "   vec3 n = normalize(normal_cameraspace);\n" +
                            "   vec3 l = normalize(lightdir_cameraspace);\n" +
                            "   float cosTheta = clamp( dot(n, l), 0.0, 1.0);" +
                            "   vColor = uMaterialDiffuse * uLightColor * cosTheta;" +
                            "}\n",

                    // test
                    "precision mediump float;\n" +
                            "uniform mat4 uProjMatrix;\n" +
                            "uniform mat4 uViewMatrix;\n" +
                            "uniform mat4 uModelMatrix;\n" +
                            "attribute vec3 aPosition;\n" +
                            "attribute vec2 aTexCoord;\n" +
                            "varying vec2 vTexCoord;\n" +
                            "void main() {\n" +
                            "   vTexCoord = aTexCoord;\n" +
                            "	gl_Position = uProjMatrix * uViewMatrix * uModelMatrix * vec4(aPosition, 1.0);\n" +
                            "}\n"


            };

    private String[] fragmentShaderSource =
            {
                    "precision mediump float;\n" +
                            "uniform vec3 uColor;\n" +
                            "void main() {\n" +
                            "   gl_FragColor = vec4(uColor, 1.0);\n" +
                            "}\n",

                    "precision mediump float;\n" +
                            "uniform vec3 uColor;\n" +
                            "void main() {\n" +
                            "   gl_FragColor = vec4(uColor, 1.0);\n" +
                            "}\n",

                    "precision mediump float;\n" +
                            "uniform vec3 uColor;\n" +
                            "void main() {\n" +
                            "   gl_FragColor = vec4(uColor, 1.0);\n" +
                            "}\n",

                    "precision mediump float;\n" +
                            "varying vec3 vColor;\n" +
                            "void main() {\n" +
                            "   gl_FragColor = vec4(vColor, 1.0);\n" +
                            "}\n",

                    "precision mediump float;\n" +
                            "uniform sampler2D sTexture0;\n" +
                            "varying vec3 vColor;\n" +
                            "varying vec2 vTexCoord;\n" +
                            "void main() {\n" +
                            "   vec4 texColor = texture2D(sTexture0, vTexCoord);\n" +
                            "   gl_FragColor = vec4(vColor, 1.0) * texColor;\n" +
                            "}\n",

                    // test
                    "precision mediump float;\n" +
                            "uniform sampler2D sTexture0;\n" +
                            "varying vec2 vTexCoord;\n" +
                            "void main() {\n" +
                            "   vec4 texColor = texture2D(sTexture0, vTexCoord);\n" +
                            "   gl_FragColor = texColor;\n" +
                            "}\n"

            };

    protected int SHADER_TYPE_DEFAULT = 0;
    protected int SHADER_TYPE_MATRIX = 1;
    protected int SHADER_TYPE_PROJECTION = 2;
    protected int SHADER_TYPE_LIGHTING = 3;
    protected int SHADER_TYPE_TEXTURE = 4;
    protected int SHADER_TYPE_TEST = 5;

    protected int createProgram(int shaderType, int vertexShader_o, int fragmentShader_o) {
        int program = GLES20.glCreateProgram();

        int vertexShader = getShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource[shaderType]);
        int fragmentShader = getShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource[shaderType]);

        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);

        //
        vertexShader_o = vertexShader;
        fragmentShader_o = fragmentShader;


        //
        return program;
    }

    private int getShader(int type, String shaderSource) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderSource);
        GLES20.glCompileShader(shader);

        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        if (compiled[0] == 0) {
            Log.e(TAG, "Could not compile shader");
            Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
            Log.e(TAG, shaderSource);
        } else {
            Log.d(TAG, "Shader = " + shader);
        }

        return shader;
    }

    protected FloatBuffer createFloatBuffer(float[] values) {
        int Float_SIZE = 4;
        FloatBuffer buffer = ByteBuffer.allocateDirect(values.length * Float_SIZE).
                order(ByteOrder.nativeOrder()).asFloatBuffer();

        buffer.put(values);
        buffer.position(0);

        return buffer;
    }


    public abstract void resize(int width, int height, float angle_z_unused);

    public abstract void draw(float angle_z);

    public abstract void destroy();

}
