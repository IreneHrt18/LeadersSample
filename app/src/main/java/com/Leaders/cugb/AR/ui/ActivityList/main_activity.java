package com.Leaders.cugb.AR.ui.ActivityList;


        import android.app.Activity;
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

        import java.util.ArrayList;


public class main_activity extends Activity {

    private SlidingMenu menu;//渚ф粦鑿滃崟
    String city = null, map=null, startID, destination;
    LinearLayout layout2,layout3,layout4,layout5;
    TextView a,b,c;
    Button bt;
    Spinner sp1,sp2;
    AutoCompleteTextView auto1,auto2;
    CheckBox Tips;
    boolean tips=false;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.mainactivity);

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setBehindOffsetRes(R.dimen.offset);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.slidingmenu);
        //闅愯棌

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
        bt.setEnabled(tips);
        a=(TextView)findViewById(R.id.textView8);
        b=(TextView)findViewById(R.id.textView4);
        c=(TextView)findViewById(R.id.textView5);

        //Spinner
        sp1 = (Spinner) findViewById(R.id.spinner);
        sp2 = (Spinner) findViewById(R.id.spinner2);
        sp1.setAlpha((float)0.6);
        sp2.setAlpha((float)0.6);
        sp1.setPrompt("璇烽€夋嫨鍩庡競");
        sp2.setPrompt("璇烽€夋嫨鍦板浘");
        ArrayList citylist=new ArrayList<String>();
        citylist.add(" ");
        citylist.add("鍖椾含");
        citylist.add("鍏朵粬");
        ArrayAdapter<String> sp1adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,citylist);
        sp1adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(sp1adapter);
        ArrayList maplist=new ArrayList<String>();
        maplist.add(" ");
        maplist.add("鍟嗗満");
        maplist.add("鍏朵粬");
        ArrayAdapter<String> sp2adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,maplist);
        sp2adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(sp2adapter);
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city=sp1.getSelectedItem().toString();
                if(city!=" "&&map!=" ")
                    visible();
                else
                    hidden();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                map=sp2.getSelectedItem().toString();
                if(city!=" "&&map!=" ")
                    visible();
                else
                    hidden();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //TIPS
        Tips=(CheckBox)findViewById(R.id.checkBox);
        Tips.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tips=Tips.isChecked();
            }
        });
        //AutoCompleteTextView
        auto1=(AutoCompleteTextView)findViewById(R.id.auto1);
        auto2=(AutoCompleteTextView)findViewById(R.id.auto2);
        auto1.setAlpha((float)0.6);
        auto2.setAlpha((float)0.6);
        auto1.setThreshold(2);
        auto2.setThreshold(1);
        auto1.setSingleLine(true);
        auto2.setSingleLine(true);

    }
    public void visible(){
        layout2.setVisibility(View.VISIBLE);
        layout3.setVisibility(View.VISIBLE);
        layout4.setVisibility(View.VISIBLE);
        layout5.setVisibility(View.VISIBLE);
        bt.setVisibility(View.VISIBLE);
        a.setVisibility(View.GONE);
        b.setText(city);
        c.setText(map);
    }
    public void hidden(){
        layout2.setVisibility(View.INVISIBLE);
        layout3.setVisibility(View.INVISIBLE);
        layout4.setVisibility(View.INVISIBLE);
        layout5.setVisibility(View.INVISIBLE);
        bt.setVisibility(View.INVISIBLE);
        a.setVisibility(View.VISIBLE);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                menu.toggle(true);//璁剧疆鐐瑰嚮鑿滃崟鎸夐挳浜х敓鍔ㄧ敾鏁堟灉銆?
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
