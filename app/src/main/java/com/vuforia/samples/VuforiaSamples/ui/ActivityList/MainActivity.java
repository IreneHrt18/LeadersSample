package com.vuforia.samples.VuforiaSamples.ui.ActivityList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.vuforia.samples.VuforiaSamples.R;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        initViews1();
        eventsViews1();
        initViews2();
        eventsViews2();

        Button btn1=(Button )findViewById(R.id.start_button);

        btn1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO 自动生成的方法存根
                /*Intent intent = new Intent(MainActivity.this, AboutScreen.class);
                intent.putExtra("ABOUT_TEXT_TITLE", "开始导航");

                intent.putExtra("ACTIVITY_TO_LAUNCH",
                        "app.ImageTargets.ImageTargets");
                intent.putExtra("ABOUT_TEXT", "ImageTargets/IT_about.html");*/
                String mClassToLaunchPackage=getPackageName();
                Intent intent=new Intent();
                intent.setClassName(mClassToLaunchPackage,mClassToLaunchPackage + ".app.ImageTargets.ImageTargets");

                startActivity(intent);
            }
        });

//        TextView tv1=(TextView)findViewById(R.id.textview4);
//        tv1.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                // TODO 自动生成的方法存根
//                Toast.makeText(getApplicationContext(),"开发者信息", Toast.LENGTH_SHORT).show();
//            }
//        });

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
               // slidingmenu.toggle(true);//设置点击菜单按钮产生动画效果。
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    private AutoCompleteTextView acTextView1 = null;
    private String[] res1 = {"location1", "location2", "location3", "KFC", "adidas", "nike", "new balance"};
    private void eventsViews1() {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, res1);
        acTextView1.setAdapter(adapter1);
    }
    private void initViews1() {
        acTextView1 = (AutoCompleteTextView ) findViewById(R.id.autoCompleteTextView1);
    }

    private AutoCompleteTextView acTextView2 = null;
    private String[] res2 = {"location1", "location2", "location3", "KFC", "adidas", "nike", "New Balance shop"};
    private void eventsViews2() {
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, res2);
        acTextView2.setAdapter(adapter2);
    }
    private void initViews2() {
        acTextView2 = (AutoCompleteTextView ) findViewById(R.id.autoCompleteTextView2);
    }

}
