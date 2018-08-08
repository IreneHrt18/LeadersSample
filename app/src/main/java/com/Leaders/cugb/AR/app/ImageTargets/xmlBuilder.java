package com.Leaders.cugb.AR.app.ImageTargets;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

import com.Leaders.cugb.AR.R;
import com.Leaders.cugb.Application.dijkstra.dijkstra;
import com.Leaders.cugb.Application.dijkstra.point;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

/**
 * Created by pepper on 2018/7/9.
 */

public class xmlBuilder {


    private Context mContext;
    private dijkstra mDijkstra;
    private Document resultDocument;
    private TextView textView;
    private static final String XMLNS_XSI = "xmlns:xsi";
    private static final String XSI_SCHEMA_LOCATION = "xsi:noNamespaceSchemaLocation";
    private  static final  String SCHEMA_LOCATION="qcar_config.xsd";
    private  static final String STORAGE_PATH= Environment.getExternalStorageDirectory().getPath();

    private ArrayList<String> idStrings;

    public  xmlBuilder(Context context,String datasetString) {

        this.mContext=context;
        try {
            //initialize the id we needed
            initList();

            //initialize the xml title witch is all the same at the beginning
            initXml();

            //now begin to read the original  xml
            readXml(datasetString);

            //end of read then transform the document to xml
            transformXml();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void  initList(){

        idStrings=new ArrayList<String>();
        int i=0;

        for (com.Leaders.cugb.Application.dijkstra.point mPoint:mDijkstra.getMyPoints()
             ) {
            idStrings.add(mPoint.name);
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
            doc = db.parse(mContext.getAssets().open(datasetXml));

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

            String size = namedNodeMap.getNamedItem("size").getTextContent();

            System.out.println("size:"+size);

            if(isMatch(name))
            {
                appendImageTarget(name,size);
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


        Element tracking;
        resultDocument.appendChild(appendQcar().appendChild(tracking=appendTracking()));

        tracking.appendChild(appendImageTarget("",""));



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
