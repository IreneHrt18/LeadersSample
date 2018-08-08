/*===============================================================================
Copyright (c) 2016 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.Leaders.cugb.AR.app.ImageTargets;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.Leaders.cugb.Application.AppRenderer;
import com.Leaders.cugb.Application.ApplicationSession;
import com.Leaders.cugb.Application.utils.CubeShaders;
import com.Leaders.cugb.Application.utils.LoadingDialogHandler;
import com.Leaders.cugb.Application.utils.SampleApplication3DModel;
import com.Leaders.cugb.Application.utils.SampleUtils;
import com.Leaders.cugb.Application.utils.Teapot;
import com.Leaders.cugb.Application.utils.Texture;
import com.vuforia.Device;
import com.vuforia.Matrix44F;
import com.vuforia.State;
import com.vuforia.Tool;
import com.vuforia.Trackable;
import com.vuforia.TrackableResult;
import com.vuforia.Vuforia;
import com.Leaders.cugb.Application.AppRendererControl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 为摄像机画面进行图形渲染
 */
// The renderer class for the ImageTargets sample. 
public class ImageTargetRenderer implements GLSurfaceView.Renderer, AppRendererControl
{
    private static final String LOGTAG = "ImageTargetRenderer";

    /**
     * AR任务对象
     */
    private ApplicationSession vuforiaAppSession;
    /**
     * 当前Activity
     */
    private ImageTargets mActivity;
    /**
     * 图像处理模块
     */
    private AppRenderer mAppRenderer;

    /**
     * 模型材质纹理数据集
     */
    private Vector<Texture> mTextures;


    private int shaderProgramID;
    private int vertexHandle;
    private int textureCoordHandle;
    private int mvpMatrixHandle;
    private int texSampler2DHandle;

    /**
     * 茶壶模型对象
     */
    private Teapot mTeapot;

    /**
     * 拓展跟踪模型尺寸
     */
    private float kBuildingScale = 0.012f;
    /**
     * 3D模型对象
     */
    private SampleApplication3DModel mBuildingsModel;

    /**活跃状态
     *
     */
    private boolean mIsActive = false;
    /**
     * 模型对象状态
     */
    private boolean mModelIsLoaded = false;

    //adjust the scale of model
    //and we have to keep a proper distance between phone and image or the model will not be seen
    //and the distance should be at most 10*(image'size) witch is none business of the OBJECT_SCALE_FLOAT

    //and the smaller picture size is, the distance is father and the model is seemed to be larger
    private static final float OBJECT_SCALE_FLOAT = 0.01f;

    private ArrayList< MyTrackable> mTrackables;
    
    public ImageTargetRenderer(ImageTargets activity, ApplicationSession session)
    {
        mActivity = activity;
        vuforiaAppSession = session;
        // AppRenderer used to encapsulate the use of RenderingPrimitives setting
        // the device mode AR/VR and stereo mode
        mAppRenderer = new AppRenderer(this, mActivity, Device.MODE.MODE_AR, false, 0.01f , 5f);
        mTrackables=new ArrayList<MyTrackable>();
        setMyTrackable(MyTrackable.defalteResult());
    }

    /**
     * 初始化模型控制器
     * @param arrayList 模型控制数据集
     */
    public void setMyTrackable(ArrayList arrayList){
        mTrackables=arrayList;

    }
    
    // Called to draw the current frame.
    @Override
    public void onDrawFrame(GL10 gl)
    {
        if (!mIsActive)
            return;
        
        // Call our function to render content from AppRenderer class
        mAppRenderer.render();
    }


    /**
     * 开启渲染模块
     * @param active
     */
    public void setActive(boolean active)
    {
        mIsActive = active;

        if(mIsActive)
            mAppRenderer.configureVideoBackground();
    }


    /**
     * 当表面被创建或重新创建时调用。
     * @param gl
     * @param config
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Log.d(LOGTAG, "GLRenderer.onSurfaceCreated");
        
        // Call Leaders function to (re)initialize rendering after first use
        // or after OpenGL ES context was lost (e.g. after onPause/onResume):
        vuforiaAppSession.onSurfaceCreated();

        mAppRenderer.onSurfaceCreated();
    }

    /**
     *当表面改变尺寸时调用。
     * @param gl
     * @param width
     * @param height
     */

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(LOGTAG, "GLRenderer.onSurfaceChanged");
        
        // Call Leaders function to handle render surface size changes:
        vuforiaAppSession.onSurfaceChanged(width, height);

        // RenderingPrimitives to be updated when some rendering change is done
        mAppRenderer.onConfigurationChanged(mIsActive);

        initRendering();
    }


    /**
     * OpenGL初始化
     */
    // Function for initializing the renderer.
    private void initRendering()
    {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
                : 1.0f);
        
        for (Texture t : mTextures)
        {
            GLES20.glGenTextures(1, t.mTextureID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                t.mWidth, t.mHeight, 0, GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE, t.mData);
        }
        
        shaderProgramID = SampleUtils.createProgramFromShaderSrc(
            CubeShaders.CUBE_MESH_VERTEX_SHADER,
            CubeShaders.CUBE_MESH_FRAGMENT_SHADER);

        vertexHandle = GLES20.glGetAttribLocation(shaderProgramID,
            "vertexPosition");
        textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID,
            "vertexTexCoord");
        mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,
            "modelViewProjectionMatrix");
        texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID,
            "texSampler2D");

        if(!mModelIsLoaded) {
            mTeapot = new Teapot();

            try {
                mBuildingsModel = new SampleApplication3DModel();
                mBuildingsModel.loadModel(mActivity.getResources().getAssets(),
                        "ImageTargets/Buildings.txt");
                mModelIsLoaded = true;
            } catch (IOException e) {
                Log.e(LOGTAG, "Unable to load buildings");
            }

            // Hide the Loading Dialog
            mActivity.loadingDialogHandler
                    .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
        }

    }

    /**
     * 更新渲染模块状态
     */
    public void updateConfiguration()
    {
        mAppRenderer.onConfigurationChanged(mIsActive);
    }

    // The render function called from SampleAppRendering by using RenderingPrimitives views.
    // The state is owned by AppRenderer which is controlling it's lifecycle.
    // State should not be cached outside this method.

    /**
     * 向主线程UI发送信息
     */
    private void sendMessage(String messageText){

        Message message;

        Bundle bundle = new Bundle();

        message = ImageTargets.messageHandler.obtainMessage();

        bundle.putString("name", messageText+"");

        message.setData(bundle);

        ImageTargets.messageHandler.sendMessage(message);

    }

    /**
     * 执行图形渲染操作
     * @param state vuForia API
     * @param projectionMatrix
     */
    public void renderFrame(State state, float[] projectionMatrix)
    {
        // Renders video background replacing Renderer.DrawVideoBackground()
        mAppRenderer.renderVideoBackground();

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // handle face culling, we need to detect if we are using reflection
        // to determine the direction of the culling
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);


        for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
            //Do not keep a copy of the pointer!
            TrackableResult result = state.getTrackableResult(tIdx);
            Trackable trackable = result.getTrackable();
            //printUserData(trackable);

            for (int i = 0; i < mTrackables.size(); i++) {
                if (!trackable.getName().equalsIgnoreCase(mTrackables.get(i).getName()))
                    continue;
                else {
            {

                sendMessage(trackable.getName().toString());


                int textureIndex = 0;
                    if(trackable.getName().equalsIgnoreCase(mTrackables.get(mTrackables.size()-1).getName())){

                          //  Toast.makeText(mActivity., "This is the end of the trip", Toast.LENGTH_LONG).show();

                        textureIndex=1;
                        //new ToastSignal().toastSignals(ToastSignal.END,"");
                    }

                    Matrix44F modelViewMatrix_Vuforia = Tool
                            .convertPose2GLMatrix(result.getPose());
                    float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();


           /* textureIndex= trackable.getName().equalsIgnoreCase("stones") ? 0
                    : 1;
            textureIndex = trackable.getName().equalsIgnoreCase("LeadersDB") ? 1
                    : 2;
            textureIndex = trackable.getName().equalsIgnoreCase("LeadersDB") ? 2
                    : textureIndex;
           */
                    // deal with the modelview and projection matrices
                    float[] modelViewProjection = new float[16];

                    if (!mActivity.isExtendedTrackingActive()) {
                        Matrix.translateM(modelViewMatrix, 0, 0.0f, 0.0f,
                                OBJECT_SCALE_FLOAT);
                        Matrix.scaleM(modelViewMatrix, 0, OBJECT_SCALE_FLOAT,
                                OBJECT_SCALE_FLOAT, OBJECT_SCALE_FLOAT);
                        //help teapot to rotate ,a is angle
                        Matrix.rotateM(modelViewMatrix, 0, 90.0f, 0, 0, 1.0f);
                        Matrix.rotateM(modelViewMatrix, 0, mTrackables.get(i).getOrientation()+90.0f, 0, 0, 1.0f);
                    } else {
                        Matrix.rotateM(modelViewMatrix, 0, 90.0f, 1.0f, 0, 0);
                        Matrix.scaleM(modelViewMatrix, 0, kBuildingScale,
                                kBuildingScale, kBuildingScale);
                    }
                    Matrix.multiplyMM(modelViewProjection, 0, projectionMatrix, 0, modelViewMatrix, 0);

                    // activate the shader program and bind the vertex/normal/tex coords
                    GLES20.glUseProgram(shaderProgramID);

                    if (!mActivity.isExtendedTrackingActive()) {
                        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                                false, 0, mTeapot.getVertices());
                        GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                                GLES20.GL_FLOAT, false, 0, mTeapot.getTexCoords());

                        GLES20.glEnableVertexAttribArray(vertexHandle);
                        GLES20.glEnableVertexAttribArray(textureCoordHandle);

                        // activate texture 0, bind it, and pass to shader
                        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                                mTextures.get(textureIndex).mTextureID[0]);
                        GLES20.glUniform1i(texSampler2DHandle, 0);

                        // pass the model view matrix to the shader
                        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                                modelViewProjection, 0);

                        // finally draw the teapot
                        GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                                mTeapot.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
                                mTeapot.getIndices());

                        // disable the enabled arrays
                        GLES20.glDisableVertexAttribArray(vertexHandle);
                        GLES20.glDisableVertexAttribArray(textureCoordHandle);
                    } else {
                        GLES20.glDisable(GLES20.GL_CULL_FACE);
                        GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
                                false, 0, mBuildingsModel.getVertices());
                        GLES20.glVertexAttribPointer(textureCoordHandle, 2,
                                GLES20.GL_FLOAT, false, 0, mBuildingsModel.getTexCoords());

                        GLES20.glEnableVertexAttribArray(vertexHandle);
                        GLES20.glEnableVertexAttribArray(textureCoordHandle);

                        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
                                mTextures.get(3).mTextureID[0]);
                        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
                                modelViewProjection, 0);
                        GLES20.glUniform1i(texSampler2DHandle, 0);
                        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0,
                                mBuildingsModel.getNumObjectVertex());

                        SampleUtils.checkGLError("Renderer DrawBuildings");
                    }

                SampleUtils.checkGLError("Render Frame");
                }
            }
        }
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

    }
    }

    /**
     * 打印跟踪对象信息
     * @param trackable 跟踪对象
     */
    private void printUserData(Trackable trackable)
    {
        String userData = (String) trackable.getUserData();
        Log.d(LOGTAG, "UserData:Retreived User Data	\"" + userData + "\"");
    }

    /**
     * 设置模型材质纹理
     * @param textures
     */
    public void setTextures(Vector<Texture> textures)
    {
        mTextures = textures;
        
    }
    
}
