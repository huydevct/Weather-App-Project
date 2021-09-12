package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KinhDoanh extends AppCompatActivity {
    ListView lvTinTuc;
    ImageView imgBack;
    TinTucAdapter adapter;
    ArrayList<DocBao> docBaoArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kinh_doanh);
        overridePendingTransition(R.anim.side_in_right, R.anim.side_out_left);

        lvTinTuc = findViewById(R.id.listViewKinhDoanh);
        imgBack  = findViewById(R.id.ImageViewbackNewsKinhDoanh);
        docBaoArrayList = new ArrayList<DocBao>();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new KinhDoanh.ReadData().execute("https://vnexpress.net/rss/kinh-doanh.rss");
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(KinhDoanh.this, TinTucTongHop.class));
            }
        });

        lvTinTuc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(KinhDoanh.this, DetailTinTuc.class);
                intent.putExtra("linktintuc", docBaoArrayList.get(i).getLink());
                intent.putExtra("title", docBaoArrayList.get(i).getTitle());
                startActivity(intent);
            }
        });
    }

    class ReadData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            return TinTuc.docNoiDung_Tu_URL(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            XMLDOMParser parser = new XMLDOMParser();
            Document document = parser.getDocument(s);
            NodeList nodeList = document.getElementsByTagName("item");
            NodeList nodeListDescrip = document.getElementsByTagName("description");
            String image = "";
            String title = "";
            String link = "";

            for (int i=0; i<nodeList.getLength(); i++){
                String cdata = nodeListDescrip.item(i + 1).getTextContent();
                Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");

                Matcher matcher = p.matcher(cdata);
                if (matcher.find()){
                    image = matcher.group(1);
                }

                Element element = (Element) nodeList.item(i);
                title = parser.getValue(element, "title");
                link = parser.getValue(element, "link");
                docBaoArrayList.add(new DocBao(title, link, image));
            }
            adapter = new TinTucAdapter(KinhDoanh.this, android.R.layout.simple_list_item_1, docBaoArrayList);
            lvTinTuc.setAdapter(adapter);

            super.onPostExecute(s);
        }
    }
}