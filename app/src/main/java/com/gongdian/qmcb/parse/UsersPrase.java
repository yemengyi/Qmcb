package com.gongdian.qmcb.parse;

import com.gongdian.qmcb.model.Users;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qian-pc on 12/19/15.
 */
public class UsersPrase {

    public static List<Users> parser(String xml){
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        List<Users> info = null;
        Users mf = null;
        try {
            XmlPullParserFactory factory=XmlPullParserFactory.newInstance();
            XmlPullParser xmlParser=factory.newPullParser();
            xmlParser.setInput(is, "UTF-8");
            int eventType=xmlParser.getEventType();
            while(eventType!=XmlPullParser.END_DOCUMENT){
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        info = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        if(xmlParser.getName().equals("d_users_row")){
                            mf =new Users();
                        }else if(xmlParser.getName().equals("id")){
                            mf.setId(xmlParser.nextText());
                        }else if(xmlParser.getName().equals("uids")){
                            mf.setUids(xmlParser.nextText());
                        }else if(xmlParser.getName().equals("uname")){
                            mf.setUname(xmlParser.nextText());
                        }else if(xmlParser.getName().equals("urole")){
                            mf.setUrole(xmlParser.nextText());
                        }else if(xmlParser.getName().equals("pid")){
                            mf.setPid(xmlParser.nextText());
                        }else if(xmlParser.getName().equals("pcode")){
                            mf.setPcode(xmlParser.nextText());
                        }else if(xmlParser.getName().equals("imei")){
                            mf.setImei(xmlParser.nextText());
                        }else if(xmlParser.getName().equals("pname")){
                            mf.setPname(xmlParser.nextText());
                        }else if(xmlParser.getName().equals("version")){
                            mf.setVersion(xmlParser.nextText());
                        }else if(xmlParser.getName().equals("headurl")){
                            mf.setHeadurl(xmlParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (xmlParser.getName().equals("d_users_row")){
                            info.add(mf);
                            mf = null;
                        }
                        break;
                }
                eventType=xmlParser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return info;
    }
}