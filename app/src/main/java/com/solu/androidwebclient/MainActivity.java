package com.solu.androidwebclient;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String TAG;
    EditText txt_url;
    TextView txt_content;

    /*응용 프로그램 기반의 어플리케이션이 웹서버측에 요청 및 응답 결과를
        가져오려면 자바아에서는 아래의 객체를 사용한다.
        즉 웹프로그램이 아니더라도, 웹서버에 요청이 가능하다..
    */
    URL url;
    HttpURLConnection con;
    Thread connectThread;/*안드로이드는 네트워크 작업은 절대
    메인스레드에서 하면 안된다*/
    BufferedReader buffr;
    Handler handler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG=this.getClass().getName();
        setContentView(R.layout.activity_main);

        txt_url = (EditText)findViewById(R.id.txt_url);
        txt_content=(TextView)findViewById(R.id.txt_content);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                String content=msg.getData().getString("content");
                txt_content.setText(content);
            }
        };
    }

    /* 지정한  url의 데이터를 디바이스로 가져오기 !!*/
    public void requestURL(){
        String path=txt_url.getText().toString();

        try {
            url = new URL(path);
            con=(HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            InputStream is=con.getInputStream();
            buffr = new BufferedReader(new InputStreamReader(is,"utf-8"));

            String data=null;
            StringBuffer sb = new StringBuffer();

            while(true){
                data=buffr.readLine();
                if(data==null)break;
                sb.append(data);
            }
            //핸들러에게 부탁!!
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("content", sb.toString());
            message.setData(bundle);

            handler.sendMessage(message);

            Log.d(TAG, "is  는"+is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(buffr!=null){
                try {
                    buffr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void btnClick(View view){

        connectThread = new Thread(){
            public void run() {
                requestURL();
            }
        };
        connectThread.start();

    }

}









