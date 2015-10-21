package com.example.testjsoup;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class MainActivity extends Activity {

    //String url = "https://accounts.google.com/ServiceLoginAuth";//"http://www.wikipedia.org";
    String url = "https://www.facebook.com/login.php";
    String url2 = "https://www.facebook.com/login.php?login_attempt=1";
    String url3 = "https://www.facebook.com/";
    //String url2 = "https://www.google.co.jp";
    Document doc = null;
    TextView textView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView1);
        Button button = (Button) findViewById(R.id.button1);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                textView.setText("WORKING"); //just to show button has been pressed
                new DataGrabber().execute(); //execute the asynctask below
            }
        });

    }
    //New class for the Asynctask, where the data will be fetched in the background
    private class DataGrabber extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                String userAgent = "Mozilla/5.0";
                // 使えるかも？ 
             //   Authenticator.setDefault(new Authenticator() {
               //     @Override
                 //   protected PasswordAuthentication getPasswordAuthentication(){

                   //     return new PasswordAuthentication("xxx@gmail.com","mypass".toCharArray());
                    //}
                //});
                //doc = Jsoup.connect(url).get();

                String u1="https://accounts.google.com/ServiceLoginAuth";
                String u2="https://accounts.google.com/AccountChooser";
                String u3="https://play.google.com/store/account";
                String u4="https://accounts.google.com/ServiceLogin?service=googleplay&continue=https://play.google.com/store&followup=https://play.google.com/store";
                Connection.Response response = Jsoup.connect(u1).userAgent(userAgent)
                        .method(Connection.Method.GET)
                        .execute();

                response = Jsoup.connect(u4)
                        .cookies(response.cookies())
                        .data("action", "login")
                        .data("Email", "xxx@gmail.com")
                        .data("password", "mypass")
                        .data("auto_login", "1")
                        .userAgent(userAgent)
                        .method(Connection.Method.POST)
                        .followRedirects(true)
                        .execute();

                doc = Jsoup.connect(u3)
                        .cookies(response.cookies())
                        .userAgent(userAgent)
                        .get();
                System.out.println(response);
               // System.out.println(doc);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //This is where we update the UI with the acquired data
            String title=null;
            if (doc != null){
                title =doc.getElementsByTag("title").text();
                textView.setText(title.toString());
            }else{
                textView.setText("FAILURE");
            }
        }
    }
}
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv=(TextView)findViewById(R.id.textview);
        tv.setText("test");

        try {
            main();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
  //  public class ProvaLoginFB {

        private List<String> cookies;
        private HttpsURLConnection conn;

        private final String USER_AGENT = "Mozilla/5.0";

        public void main() throws Exception {

}
*/

    /*
    TextView tv;
 String url = "https://accounts.google.com/ServiceLoginAuth";
        String gmail = "https://mail.google.com/mail/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=(TextView)findViewById(R.id.textview);
        HttpLoader loader=new HttpLoader(tv);
        loader.execute("http://www.google.co.jp");
        //loader.execute("https://play.google.com/store/apps/collection/topgrossing?hl=ja");
        //loader.execute("https://play.google.com/store/account?hl=ja");

    }
}*/