/*===============================================================================
Copyright (c) 2016-2017 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.Leaders.cugb.AR.app.ImageTargets;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.Leaders.cugb.AR.ui.SampleAppMenu.AppMenu;
import com.vuforia.CameraDevice;
import com.vuforia.DataSet;
import com.vuforia.HINT;
import com.vuforia.ObjectTracker;
import com.vuforia.STORAGE_TYPE;
import com.vuforia.State;
import com.vuforia.Trackable;
import com.vuforia.Tracker;
import com.vuforia.TrackerManager;
import com.vuforia.Vuforia;
import com.Leaders.cugb.Application.ApplicationControl;
import com.Leaders.cugb.Application.ApplicationException;
import com.Leaders.cugb.Application.ApplicationSession;
import com.Leaders.cugb.Application.utils.SampleApplicationGLView;
import com.Leaders.cugb.Application.utils.LoadingDialogHandler;
import com.Leaders.cugb.Application.utils.Texture;
import com.Leaders.cugb.AR.R;
import com.Leaders.cugb.AR.ui.SampleAppMenu.AppMenuGroup;
import com.Leaders.cugb.AR.ui.SampleAppMenu.AppMenuInterface;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

/**
 * 在此类中执行AR任务的Activity
 */
public class ImageTargets extends Activity implements ApplicationControl,
        AppMenuInterface,DatasetInitializer
{
    private static final String LOGTAG = "ImageTargets";
    /**
     * AR任务对象
     */
    ApplicationSession AppSession;

    private int mDatasetsNumber=0;
    //private DataSet mCurrentDataset;
    /**
     * 起点终点所在数据集
     */
    private DataSet mStartDataset;
    private DataSet mEndDataset;

    /**
     * 存放数据集名称的集合
     */
    private ArrayList<String> mDatasetStrings = new ArrayList<String>();
    

    /**
     * OpenGL视图
     */
    private SampleApplicationGLView mGlView;

    /**
     * 返回信息视图
     */
    private TextView messageView;

    /**
     * AR 界面布局
     */
    private RelativeLayout mainLayout;

    /**
     * 响应信息模块
     */
    public static MessageHandler messageHandler;

    /**
     * 渲染模块
     */
    private ImageTargetRenderer mRenderer;
    
    private GestureDetector mGestureDetector;

    /**
     * 模型纹理材质集合
     */
    private Vector<Texture> mTextures;
    
    //private boolean mSwitchDatasetAsap = false;
    private boolean mFlash = false;
    private boolean mContAutofocus = true;
    /**
     * 拓展跟踪（保留）
     */
    private boolean mExtendedTracking = false;


    private View mFocusOptionView;
    private View mFlashOptionView;
    
    private RelativeLayout mUILayout;
    
    private AppMenu mAppMenu;

    LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
    

    private AlertDialog mErrorDialog;
    
    boolean mIsDroidDevice = false;
    
    
    // Called when the activity first starts or the user navigates back to an
    // activity.

    /**
     * 初始化AR识别任务
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {


        Log.d(LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);

        messageHandler=new MessageHandler();

        AppSession = new ApplicationSession(this);
        
        startLoadingAnimation();
        mDatasetStrings.add("LeadersDB.xml");
        mDatasetStrings.add("LeadersDB2.xml");
        
        AppSession
            .initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        mGestureDetector = new GestureDetector(this, new GestureListener());
        
        // Load any sample specific textures:
        mTextures = new Vector<Texture>();
        loadTextures();
        
        mIsDroidDevice = Build.MODEL.toLowerCase().startsWith(
            "droid");
        
    }

    private void initUI(){
        //不能用findViewbyId，因为不是在此activity的父窗体中找的
        //messageView= (TextView ) findViewById(R.id.message);

        mainLayout=new RelativeLayout(this);

        RelativeLayout.LayoutParams messageViewParams= new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,300);

        LayoutParams glViewParams= new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);

        RelativeLayout.LayoutParams mainLayoutParam=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

        messageViewParams.addRule(RelativeLayout.ALIGN_TOP);

        messageView =new TextView(this);

            messageView.setText("This is textview!");
            messageView.setBackgroundColor(Color.TRANSPARENT);
            messageView.setTextSize(30);
            messageView.setTextColor(Color.BLACK);
            messageView.setGravity(Gravity.CENTER);
            messageView.setLayoutParams(messageViewParams);

        mainLayout.setLayoutParams(mainLayoutParam);

        mainLayout.addView(mGlView,glViewParams);

        //将messageView放在摄像机前
        mainLayout.addView(messageView);

        this.setContentView(mainLayout);

    }

    // Process Single Tap event to trigger autofocus
    private class GestureListener extends
        GestureDetector.SimpleOnGestureListener
    {
        // Used to set autofocus one second after a manual focus is triggered
        private final Handler autofocusHandler = new Handler();
        
        
        @Override
        public boolean onDown(MotionEvent e)
        {
            return true;
        }
        
        
        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            boolean result = CameraDevice.getInstance().setFocusMode(
                    CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);
            if (!result)
                Log.e("SingleTapUp", "Unable to trigger focus");

            // 生成一个处理程序来触发连续的自动焦点
            // 1秒后
            autofocusHandler.postDelayed(new Runnable()
            {
                public void run()
                {
                    if (mContAutofocus)
                    {
                        final boolean autofocusResult = CameraDevice.getInstance().setFocusMode(
                                CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

                        if (!autofocusResult)
                            Log.e("SingleTapUp", "Unable to re-enable continuous auto-focus");
                    }
                }
            }, 1000L);

            return true;
        }
    }
    

    /**
     * 加载模型纹理材质
     */
    private void loadTextures()
    {
        mTextures.add(Texture.loadTextureFromApk("TextureTeapotBrass.png",
            getAssets()));
        mTextures.add(Texture.loadTextureFromApk("TextureTeapotBlue.png",
            getAssets()));
        mTextures.add(Texture.loadTextureFromApk("TextureTeapotRed.png",
            getAssets()));
        mTextures.add(Texture.loadTextureFromApk("ImageTargets/Buildings.jpeg",
            getAssets()));
    }


    /**
     * 当活动开始与用户交互时调用
     */
    @Override
    protected void onResume()
    {
        Log.d(LOGTAG, "onResume");
        super.onResume();

        showProgressIndicator(true);
        
        // 这对于一些Droid设备来说是必需的
        if (mIsDroidDevice)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        AppSession.onResume();
    }


    /**
     * 用于配置的回调将更改活动本身
     * @param config
     */
    @Override
    public void onConfigurationChanged(Configuration config)
    {
        Log.d(LOGTAG, "onConfigurationChanged");
        super.onConfigurationChanged(config);
        
        AppSession.onConfigurationChanged();
    }



    @Override
    protected void onPause()
    {
        Log.d(LOGTAG, "onPause");
        super.onPause();
        
        if (mGlView != null)
        {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }
        
        // 关闭手电筒
        if (mFlashOptionView != null && mFlash)
        {
            // OnCheckedChangeListener在更改已检查的状态时调用
            setMenuToggle(mFlashOptionView, false);
        }
        
        try
        {
            AppSession.pauseAR();
        } catch (ApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }
    }
    

    @Override
    protected void onDestroy()
    {
        Log.d(LOGTAG, "onDestroy");
        super.onDestroy();
        
        try
        {
            AppSession.stopAR();
        } catch (ApplicationException e)
        {
            Log.e(LOGTAG, e.getString());
        }
        
        // 卸载
        mTextures.clear();
        mTextures = null;
        
        System.gc();
    }


    /**
     * 初始化应用程序组件。
     */
    private void initApplicationAR()
    {
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 3);

        // 初始化 OpenGL ES 视图
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();
        
        mGlView = new SampleApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);

        mRenderer = new ImageTargetRenderer(this, AppSession);
        mRenderer.setTextures(mTextures);
        mGlView.setRenderer(mRenderer);
    }
    
    
    private void startLoadingAnimation()
    {
        mUILayout = (RelativeLayout) View.inflate(this, R.layout.camera_overlay,
            null);
        
        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);
        
        // 获取加载对话框的引用
        loadingDialogHandler.mLoadingDialogContainer = mUILayout
            .findViewById(R.id.loading_indicator);
        

        loadingDialogHandler
            .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
        

        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT));
        
    }
    
    
    // Methods to load and destroy tracking data.
    @Override
    public boolean doLoadTrackersData()
    {
        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
            .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;


        copy("LeadersDB.xml");
       copy("LeadersDB2.xml");

        copy("LeadersDB.dat");
        copy("LeadersDB2.dat");

        if(!doInitDataset(objectTracker,mStartDataset,"LeadersDB.xml"))
            return false;
        if(!doInitDataset(objectTracker,mEndDataset,"LeadersDB2.xml"))
            return false;

        return true;
    }

    /**
     *初始化开始层的“数据集”和结束层的“数据集”
     */
    @Override
    public boolean doInitDataset(ObjectTracker mObjTracker, DataSet mDataset, String mDatasetString){
        if (mDataset == null)
            mDataset = mObjTracker.createDataSet();
        if (mDataset == null) {
            Log.d("DBload", "doLoadTrackersData: failed to create Dataset");
            return false;
        }


        //copy(mDatasetString);
        /*if (!mDataset.load(mDatasetString,
                STORAGE_TYPE.STORAGE_APPRESOURCE)){
            Log.d("DBload", "doLoadTrackersData: failed to load Dataset");
            return false;}*/
        if (!mDataset.load("storage/sdcard0/"+mDatasetString,
                STORAGE_TYPE.STORAGE_ABSOLUTE)){
            Log.d("DBload", "doLoadTrackersData: failed to load Dataset");
            return false;}

        if (!mObjTracker.activateDataSet(mDataset)){
            Log.d("DBload", "doLoadTrackersData: failed to active Dataset");
            return false;
        }
        //numbers of imagetargets
        int numTrackables = mDataset.getNumTrackables();

        //start extend tracking and set user's data
        for (int count = 0; count < numTrackables; count+=2) {
            Trackable trackable = mDataset.getTrackable(count);
            if (isExtendedTrackingActive()) {
                trackable.startExtendedTracking();
            }

            String name = "Current Dataset : " + trackable.getName();
            trackable.setUserData(name);
            Log.d(LOGTAG, "UserData:Set the following user data "
                    + ( String ) trackable.getUserData());
        }

        return true;

    }

    public void copy(String fileName){
        try {
            String xml=null;
            InputStream inputStream = getAssets().open(fileName);
            String dir= "storage/sdcard0/";//Environment.getExternalStorageDirectory().toString();
            FileOutputStream openFileOutput = new FileOutputStream(dir+fileName);
            // openFileOutput = openFileOutput("LeadersDB.xml", Context.MODE_PRIVATE);
            byte[] bytes = new byte[1024];
            int length;
            ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
            while ((length=inputStream.read(bytes)) != -1) {
                openFileOutput.write(bytes, 0, length);

            }
//            arrayOutputStream.close();
//            xml = new String(arrayOutputStream.toByteArray());
//
//            openFileOutput.write(xml.getBytes());
            openFileOutput.flush();
            inputStream.close();
            openFileOutput.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public boolean doLoadDataset(ObjectTracker mObjTracker, DataSet mDataset, String mDatasetString) {
        return false;
    }

    @Override
    public boolean doUnLoadDataset(ObjectTracker mObjTracker, DataSet mDataset) {
        return false;
    }

    @Override
    public boolean doCheckDataset(ObjectTracker mObjTracker, DataSet mDataset) {
        return false;
    }

    /**
     *指示跟踪器是否正确卸载。
     * @return
     */
    @Override
    public boolean doUnloadTrackersData()
    {
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) tManager
            .getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;
            if (mStartDataset != null && mStartDataset.isActive()) {
                if (objectTracker.getActiveDataSet(0).equals(mStartDataset)
                        && !objectTracker.deactivateDataSet(mStartDataset)) {
                } else if (!objectTracker.destroyDataSet(mStartDataset)) {
                    result = false;
                }

                mStartDataset = null;
            }
        if (mEndDataset != null && mEndDataset.isActive()) {
            if (objectTracker.getActiveDataSet(0).equals(mEndDataset)
                    && !objectTracker.deactivateDataSet(mEndDataset)) {
            } else if (!objectTracker.destroyDataSet(mEndDataset)) {
                result = false;
            }

            mEndDataset = null;
        }

        return result;
    }

    @Override
    public void onVuforiaResumed()
    {
        if (mGlView != null)
        {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }
    }

    @Override
    public void onVuforiaStarted()
    {
        mRenderer.updateConfiguration();

        if (mContAutofocus)
        {
            // 设置相机的焦点模式
            if(!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO))
            {
                // 如果连续自动对焦模式失败，尝试设置为不同的模式
                if(!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO))
                {
                    CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);
                }

                // 更新切换状态
                setMenuToggle(mFocusOptionView, false);
            }
            else
            {
                // 更新切换状态
                setMenuToggle(mFocusOptionView, true);
            }
        }
        else
        {
            setMenuToggle(mFocusOptionView, false);
        }

        showProgressIndicator(false);
    }


    public void showProgressIndicator(boolean show)
    {
        if (loadingDialogHandler != null)
        {
            if (show)
            {
                loadingDialogHandler
                        .sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
            }
            else
            {
                loadingDialogHandler
                        .sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
            }
        }
    }
    
    
    @Override
    public void onInitARDone(ApplicationException exception)
    {
        
        if (exception == null)
        {
            initApplicationAR();
            
            mRenderer.setActive(true);

            /**
             * 现在添加GL surface视图。
              */
            initUI();
//
            // 设置要在摄像机前绘制的UILayout
            mUILayout.bringToFront();
            
            // 将布局背景设置为透明
            mUILayout.setBackgroundColor(Color.TRANSPARENT);
            
            AppSession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);
            
            mAppMenu = new AppMenu(this, this, "摄像机选项",
                mGlView, mUILayout, null);
            setSampleAppMenuSettings();
            
        } else
        {
            Log.e(LOGTAG, exception.getString());
            showInitializationErrorMessage(exception.getString());
        }
    }
    
    
    // Shows initialization error messages as System dialogs
    public void showInitializationErrorMessage(String message)
    {
        final String errorMessage = message;
        runOnUiThread(new Runnable()
        {


            public void run()
            {

                if (mErrorDialog != null)
                {
                    mErrorDialog.dismiss();

                }
                
                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(
                    ImageTargets.this);
                builder
                    .setMessage(errorMessage)
                    .setTitle(getString(R.string.INIT_ERROR))
                    .setCancelable(false)
                    .setIcon(0)
                    .setPositiveButton(getString(R.string.button_OK),
                        new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                finish();
                            }
                        });
                
                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }
    

    @Override
    public void onVuforiaUpdate(State state)
    {
        /*if (mSwitchDatasetAsap)
        {
            mSwitchDatasetAsap = false;
            TrackerManager tm = TrackerManager.getInstance();
            ObjectTracker ot = (ObjectTracker) tm.getTracker(ObjectTracker
                .getClassType());
            if (ot == null || mStartDataset == null|| mEndDataset == null
                || ot.getActiveDataSet(0) == null)
            {
                Log.d(LOGTAG, "Failed to swap datasets");
                return;
            }
            
            doUnloadTrackersData();
            doLoadTrackersData();
        }*/
    }
    
    
    @Override
    public boolean doInitTrackers()
    {
        // 指示跟踪器是否初始化正确
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        Tracker tracker;
        
        // 初始化图像跟踪器。
        tracker = tManager.initTracker(ObjectTracker.getClassType());
        if (tracker == null)
        {
            Log.e(
                LOGTAG,
                "Tracker not initialized. Tracker already initialized or the camera is already started");
            result = false;
        } else
        {
            Log.i(LOGTAG, "Tracker successfully initialized");
        }
        return result;
    }
    
    
    @Override
    public boolean doStartTrackers()
    {
        // 指示追踪器是否启动正确
        boolean result = true;
        
        Tracker objectTracker = TrackerManager.getInstance().getTracker(
            ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.start();
        
        return result;
    }
    
    
    @Override
    public boolean doStopTrackers()
    {
        // Indicate if the trackers were stopped correctly
        boolean result = true;
        
        Tracker objectTracker = TrackerManager.getInstance().getTracker(
            ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.stop();
        
        return result;
    }
    
    
    @Override
    public boolean doDeinitTrackers()
    {
        // Indicate if the trackers were deinitialized correctly
        boolean result = true;
        
        TrackerManager tManager = TrackerManager.getInstance();
        tManager.deinitTracker(ObjectTracker.getClassType());
        
        return result;
    }
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // Process the Gestures
        if (mAppMenu != null && mAppMenu.processEvent(event))
            return true;
        
        return mGestureDetector.onTouchEvent(event);
    }
    
    
    boolean isExtendedTrackingActive()
    {
        return mExtendedTracking;
    }
    
    final public static int CMD_BACK = -1;
    final public static int CMD_EXTENDED_TRACKING = 1;
    final public static int CMD_AUTOFOCUS = 2;
    final public static int CMD_FLASH = 3;
    final public static int CMD_CAMERA_FRONT = 4;
    final public static int CMD_CAMERA_REAR = 5;
    final public static int CMD_DATASET_START_INDEX = 6;
    
    
    // This method sets the menu's settings
    private void setSampleAppMenuSettings()
    {
        AppMenuGroup group;
        
        group = mAppMenu.addGroup("", false);
        group.addTextItem(getString(R.string.menu_back), -1);

        group = mAppMenu.addGroup("", true);
        group.addSelectionItem(getString(R.string.menu_extended_tracking),
            CMD_EXTENDED_TRACKING, false);
        mFocusOptionView = group.addSelectionItem(getString(R.string.menu_contAutofocus),
            CMD_AUTOFOCUS, mContAutofocus);
        mFlashOptionView = group.addSelectionItem(
            getString(R.string.menu_flash), CMD_FLASH, false);
        
        CameraInfo ci = new CameraInfo();
        boolean deviceHasFrontCamera = false;
        boolean deviceHasBackCamera = false;
        for (int i = 0; i < Camera.getNumberOfCameras(); i++)
        {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == CameraInfo.CAMERA_FACING_FRONT)
                deviceHasFrontCamera = true;
            else if (ci.facing == CameraInfo.CAMERA_FACING_BACK)
                deviceHasBackCamera = true;
        }
        
        if (deviceHasBackCamera && deviceHasFrontCamera)
        {
            group = mAppMenu.addGroup(getString(R.string.menu_camera),
                true);
            group.addRadioItem(getString(R.string.menu_camera_front),
                CMD_CAMERA_FRONT, false);
            group.addRadioItem(getString(R.string.menu_camera_back),
                CMD_CAMERA_REAR, true);
        }
        
       /* group = mAppMenu
            .addGroup(getString(R.string.menu_datasets), true);*/
       // mStartDatasetsIndex = CMD_DATASET_START_INDEX;
        mDatasetsNumber = mDatasetStrings.size();
        
        //group.addRadioItem("Stones & Chips", mStartDatasetsIndex, true);
        //group.addRadioItem("LeadersDB", mStartDatasetsIndex, true);
        //group.addRadioItem("Tarmac", mStartDatasetsIndex + 1, false);
        mAppMenu.attachMenu();
    }


    private void setMenuToggle(View view, boolean value)
    {
        // OnCheckedChangeListener is called upon changing the checked state
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        {
            ((Switch) view).setChecked(value);
        } else
        {
            ((CheckBox) view).setChecked(value);
        }
    }
    
    
    @Override
    public boolean menuProcess(int command)
    {
        
        boolean result = true;
        
        switch (command)
        {
            case CMD_BACK:
                finish();
                break;
            
            case CMD_FLASH:
                result = CameraDevice.getInstance().setFlashTorchMode(!mFlash);
                
                if (result)
                {
                    mFlash = !mFlash;
                } else
                {
                    showToast(getString(mFlash ? R.string.menu_flash_error_off
                        : R.string.menu_flash_error_on));
                    Log.e(LOGTAG,
                        getString(mFlash ? R.string.menu_flash_error_off
                            : R.string.menu_flash_error_on));
                }
                break;
            
            case CMD_AUTOFOCUS:
                
                if (mContAutofocus)
                {
                    result = CameraDevice.getInstance().setFocusMode(
                        CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);
                    
                    if (result)
                    {
                        mContAutofocus = false;
                    } else
                    {
                        showToast(getString(R.string.menu_contAutofocus_error_off));
                        Log.e(LOGTAG,
                            getString(R.string.menu_contAutofocus_error_off));
                    }
                } else
                {
                    result = CameraDevice.getInstance().setFocusMode(
                        CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
                    
                    if (result)
                    {
                        mContAutofocus = true;
                    } else
                    {
                        showToast(getString(R.string.menu_contAutofocus_error_on));
                        Log.e(LOGTAG,
                            getString(R.string.menu_contAutofocus_error_on));
                    }
                }
                
                break;
            
            case CMD_CAMERA_FRONT:
            case CMD_CAMERA_REAR:
                
                // Turn off the flash
                if (mFlashOptionView != null && mFlash)
                {
                    setMenuToggle(mFlashOptionView, false);
                }
                
                AppSession.stopCamera();

                AppSession
                    .startAR(command == CMD_CAMERA_FRONT ? CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_FRONT
                        : CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_BACK);

                break;


        }
        
        return result;
    }

    class MessageHandler extends Handler{


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle=msg.getData();
            String i=bundle.getString("name");
            ImageTargets.this.messageView.setText(i);

        }
    }
    
    
    private void showToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
