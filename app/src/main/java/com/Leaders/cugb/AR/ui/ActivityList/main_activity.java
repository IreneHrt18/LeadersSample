package com.Leaders.cugb.AR.ui.ActivityList;


import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.Leaders.cugb.AR.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *系统主界面，在此类中获取地图编号，起点，终点
 */
public class Main_activity extends Activity {

    /**
     *侧滑菜单
     */
    private SlidingMenu menu;

    /**
     *地图信息
     */
    String city = null, map = null, startID, destination;

    LinearLayout layout2, layout3, layout4, layout5;
    TextView a, b, c;
    Button bt;
    Spinner sp1, sp2;
    AutoCompleteTextView auto1, auto2;
    CheckBox Tips;
    boolean tips = false;
    int k[] = {545, 484, 566, 797, 619, 354};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main_activity);

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setBehindOffsetRes(R.dimen.offset);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.slidingmenu);
        /*
         *隐藏信息
         */

        layout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        layout2.setVisibility(View.INVISIBLE);
        layout3 = (LinearLayout) findViewById(R.id.linearLayout3);
        layout3.setVisibility(View.INVISIBLE);
        layout4 = (LinearLayout) findViewById(R.id.linearLayout4);
        layout4.setVisibility(View.INVISIBLE);
        layout5 = (LinearLayout) findViewById(R.id.linearLayout5);
        layout5.setVisibility(View.INVISIBLE);
        bt = (Button) findViewById(R.id.button);
        bt.setVisibility(View.INVISIBLE);
        a = (TextView) findViewById(R.id.textView8);
        b = (TextView) findViewById(R.id.textView4);
        c = (TextView) findViewById(R.id.textView5);

        /*
         *搜索框
         */
        sp1 = (Spinner) findViewById(R.id.spinner);
        sp2 = (Spinner) findViewById(R.id.spinner2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            sp1.setAlpha((float) 0.6);
            sp2.setAlpha((float) 0.6);
        }
        sp1.setPrompt("请选择城市");
        sp2.setPrompt("请选择地图");
        ArrayList citylist = new ArrayList<String>();
        citylist.add(" ");
        citylist.add("北京");
        citylist.add("其他");
        ArrayAdapter<String> sp1adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, citylist);
        sp1adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(sp1adapter);
        ArrayList maplist = new ArrayList<String>();
        maplist.add(" ");
        maplist.add("爱琴海购物中心");
        maplist.add("其他");
        ArrayAdapter<String> sp2adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, maplist);
        sp2adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(sp2adapter);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = sp1.getSelectedItem().toString();
                if (city != " " && map != " ")
                    visible();
                else
                    hidden();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            /*
             *当且仅当城市及地图被选择后信息可见
             */
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                map = sp2.getSelectedItem().toString();
                if (city != " " && map != " ")
                    visible();
                else
                    hidden();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //TIPS
        Tips = (CheckBox) findViewById(R.id.checkBox);
        Tips.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tips = Tips.isChecked();
            }
        });
        //AutoCompleteTextView
        auto1 = (AutoCompleteTextView) findViewById(R.id.auto1);
        auto2 = (AutoCompleteTextView) findViewById(R.id.auto2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            auto1.setAlpha((float) 0.6);
            auto2.setAlpha((float) 0.6);
        }
        auto1.setThreshold(2);
        auto2.setThreshold(1);
        auto1.setSingleLine(true);
        auto2.setSingleLine(true);
        ArrayList startIDlist = new ArrayList<String>();
        for (int i = 1; i < 7; i++)
            for (int j = 1; j <= k[i - 1]; j++) {
                int z = 1000 * i + j;
                startIDlist.add(z + "");
            }
        ArrayAdapter<String> startadapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, startIDlist);
        auto1.setAdapter(startadapter);
        ArrayList deslist = new ArrayList<String>();
        String[] res1 = {"1"};
        AssetManager ass = getAssets();

            try {
                BufferedReader bf=new BufferedReader(new InputStreamReader(ass.open( "name.txt"))); //InputStream des = ass.open("F" + i + "name.txt");

                String buffer;
                while((buffer=bf.readLine())!=null){
                String result = new String(buffer.split(" ")[0]);

                    deslist.add(result);};
            } catch (Exception e) {
                e.printStackTrace();
            }


        ArrayAdapter<String> desadapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, deslist);
        auto2.setAdapter(desadapter);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startID = auto1.getText().toString();
                destination = auto2.getText().toString();
                String mClassToLaunchPackage=getPackageName();
                Intent intent=new Intent();
                intent.setClassName(mClassToLaunchPackage,mClassToLaunchPackage + ".app.ImageTargets.ImageTargets");

//                intent.putExtra("ACTIVITY_TO_LAUNCH",
//                        "app.ImageTargets.ImageTargets");
                startActivity(intent);
            }
        });
    }

    public void visible() {
        layout2.setVisibility(View.VISIBLE);
        layout3.setVisibility(View.VISIBLE);
        layout4.setVisibility(View.VISIBLE);
        layout5.setVisibility(View.VISIBLE);
        bt.setVisibility(View.VISIBLE);
        a.setVisibility(View.GONE);
        b.setText(city);
        c.setText(map);
    }

    public void hidden() {
        layout2.setVisibility(View.INVISIBLE);
        layout3.setVisibility(View.INVISIBLE);
        layout4.setVisibility(View.INVISIBLE);
        layout5.setVisibility(View.INVISIBLE);
        bt.setVisibility(View.INVISIBLE);
        a.setVisibility(View.VISIBLE);
    }
    /*
     *菜单动画效果
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                menu.toggle(true);//设置点击菜单按钮产生动画效果。
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
