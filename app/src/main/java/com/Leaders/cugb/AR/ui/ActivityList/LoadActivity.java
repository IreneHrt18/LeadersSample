package com.Leaders.cugb.AR.ui.ActivityList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.os.StatFs;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Leaders.cugb.AR.R;
import com.Leaders.cugb.AR.app.ImageTargets.ImageTargets;
import com.Leaders.cugb.AR.app.ImageTargets.MyTrackable;
import com.Leaders.cugb.AR.app.ImageTargets.TrackableManager;
import com.Leaders.cugb.Application.dijkstra.Dij_Main;
import com.Leaders.cugb.Application.dijkstra.Dijkstra;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class LoadActivity extends Activity
{

    private ProgressBar progressBar;

    private String datasetString1;
    private String datasetString2;
    private Dij_Main mDijkstra;
    private Document resultDocument;
    private Element tracking;
    private TextView load_text;
    private static final String XMLNS_XSI = "xmlns:xsi";
    private static final String XSI_SCHEMA_LOCATION = "xsi:noNamespaceSchemaLocation";
    private  static final  String SCHEMA_LOCATION="qcar_config.xsd";
    private  static final String STORAGE_PATH= Environment.getExternalStorageDirectory().getPath();

    private ArrayList<String> idStrings;
    public static TrackableManager trackableManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_activity);

        progressBar= ( ProgressBar ) findViewById(R.id.progressBar);
        load_text= ( TextView ) findViewById(R.id.load_text);

        increaseProgressBar();

        mDijkstra=new Dij_Main(LoadActivity.this);

        trackableManager=new TrackableManager();

        Intent intent=getIntent();

        String startID=intent.getStringExtra("startID");
        String destination=intent.getStringExtra("destination");

        Log.i("intentMes", "startID"+startID+" destination:"+destination);


        try {
            trackableManager.analyseDijkstra(mDijkstra,startID,destination);

            datasetString1="LC"+mDijkstra.getStartLayer();
            datasetString2="LC"+mDijkstra.getEndLayer();

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            //initialize the id we needed
            initList();

            //initialize the xml title witch is all the same at the beginning
            initXml();

            //now begin to read the original  xml
            readXml(datasetString1+".xml");

            if(!datasetString1.equals(datasetString2))
            readXml(datasetString2+".xml");

            //end of read then transform the document to xml
            transformXml();

            Intent intent2=new Intent(LoadActivity.this, ImageTargets.class);

            startActivity(intent2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int MAXPROGRESS=100;

    public void increaseProgressBar() {

        progressBar.setMax(MAXPROGRESS);

        int progress=progressBar.getProgress();

        while(progress<progressBar.getMax())
        {
            progressBar.incrementProgressBy(1);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progress=progressBar.getProgress();

            setLoadText(progress);
        }

    }

    private void checkSDCard()
    {
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)) {

            File sdCardDir = Environment.getExternalStorageDirectory();

            StatFs sf = new StatFs(sdCardDir.getPath());

            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            long availCount = sf.getAvailableBlocks();

            Log.d("sdCardSize", "总大小:"+blockSize*blockCount/1024+"KB");
            Log.d("sdCardSize", "剩余空间:"+ availCount*blockSize/1024+"KB");

            if(availCount*blockSize/1024/1024<1)
                showALtDialog();

        }

    }

    private void showALtDialog()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        builder.setTitle("内存空间不足!");
        builder.setIcon(R.drawable.logo2);
        builder.setMessage("请检查内存空间！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoadActivity.this.finish();
            }
        });

        AlertDialog dialog=builder.create();
        dialog.show();

    }


    private String LOADTEXTSTRING=getString(R.string.loading_text);

    public void setLoadText(int progress)
    {
        String percentage=progress+"%";

        load_text.setText(LOADTEXTSTRING+percentage);
    }


    public void  initList(){

        idStrings=new ArrayList<String>();

        for (int i=0;i<trackableManager.trackableNumber;i++)
        {
            MyTrackable trackable=trackableManager.getDijikstraResult().get(i);
            idStrings.add(trackable.getName());
        }
    }

    public boolean isMatch (String nameInXml)
    {

        for(String name:idStrings){

            if(name.equals(nameInXml)){

                return true;
            }
        }

        return false;
    }

    public void readXml(String datasetXml) throws IOException
    {
        // ready for read xml
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = null;

        try {

            db = dbf.newDocumentBuilder();

        } catch (ParserConfigurationException pce) {

            System.err.println(pce); // 出异常时输出异常信息，然后退出，下同

            System.exit(1);
        }

        Document doc = null;

        try {
            //doc = db.parse(getAssets().open("LeadersDB.xml"));
            doc = db.parse(getAssets().open(datasetXml));

        } catch (DOMException dom) {

            System.err.println(dom.getMessage());

            System.exit(1);

        } catch (IOException ioe) {

            System.err.println(ioe);

            System.exit(1);

        } catch (SAXException e) {
            e.printStackTrace();
        }

        // read root element
        Element root = doc.getDocumentElement();

        // get "ImageTarget" elements to list
        NodeList targets = root.getElementsByTagName("ImageTarget");

        for (int i = 0; i < targets.getLength(); i++) {

            // go through every "ImageTarget"
            Element target = (Element) targets.item(i);

            NamedNodeMap namedNodeMap = target.getAttributes();

            //get attributes of "ImageTarget"
            String name = namedNodeMap.getNamedItem("name").getTextContent();

            System.out.print("name:"+name);

            if(isMatch(name))
            {
                String size = namedNodeMap.getNamedItem("size").getTextContent();

                System.out.println("size:"+size);

                //appendImageTarget(name,size);

                tracking.appendChild(appendImageTarget(name,size));
            }


        }

    }
    public void initXml()
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;

        // textView.setText(path);
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            System.err.println(pce); // 出异常时输出异常信息，然后退出，下同
            System.exit(1);
        }

        resultDocument=db.newDocument();
        resultDocument.setXmlStandalone(true);

        resultDocument.appendChild(appendQcar().appendChild(tracking=appendTracking()));



    }

    public void transformXml(){
        //Transform document to xml
        TransformerFactory transformerFactory= TransformerFactory.newInstance();
        try {
            Transformer tf=transformerFactory.newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING,"UTF8");
            tf.transform(new DOMSource(resultDocument),new StreamResult(new File(STORAGE_PATH+"/LeadersDB3.xml")));
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public Element appendImageTarget(String name,String size){

        Element imageTarget=resultDocument.createElement("ImageTarget");

        imageTarget.setAttribute("name",name);
        imageTarget.setAttribute("size",size);

        return imageTarget;
    }

    public Element appendTracking(){

        Element trackingTitle=resultDocument.createElement("Tracking");

        return trackingTitle;

    }
    public Element appendQcar(){

        Element qcarTitle=resultDocument.createElement("QCARConfig");
        qcarTitle.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
                XSI_SCHEMA_LOCATION, SCHEMA_LOCATION);
        qcarTitle.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLNS_XSI,
                XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);

        return qcarTitle;
    }


}
