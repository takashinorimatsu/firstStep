package com.example.testjsoup;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by takashi_norimatsu on 2015/10/17.
 */
public class HttpLoader
        extends AsyncTask<String, Void, String> {
    TextView tv;

    public HttpLoader(TextView tv) {
        this.tv=tv;
    }

    @Override
    protected String doInBackground(String... url) {
       String title="";
        Authenticator.setDefault(new Authenticator() {

    //        @Override
     //   protected PasswordAuthentication getPasswordAuthentication(){
     //           return new PasswordAuthentication("xxxx@gmail.com","mypass".toCharArray());
     //       }

        });

       // StringBuilder buf = new StringBuilder();
        try {
            // Connectionを作成
            Connection conn = Jsoup.connect(url[0]);
            HashMap<String, String> param = new HashMap<String, String>();

            Connection.Response res = conn.data(param).method(Connection.Method.POST).execute();
            Map<String, String> cookies = res.cookies();
          //  Document document=Jsoup.connect(url[0]).get();
           // Document document= Jsoup.connect(url[0]).get();
         //   Elements links =document.getElementsByTag("div");
            title =res.parse().text();

            }
        //title= String.valueOf(buf);
            // Log.i("length", String.valueOf(title.length()));
           // title = document.getAllElements().text();
         catch (IOException e) {
            e.printStackTrace();
        }

        //return buf;
        return title;
    }

    @Override
   // protected void onPostExecute(StringBuilder result) {
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        tv.setText(result);
        //System.out.println(result);
        //Log.i("result", result);
    }

}