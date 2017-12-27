package com.bip.recyclerviewdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bip.recyclerviewdemo.database.MySqliteHelper;
import com.bip.recyclerviewdemo.model.DataResult;
import com.bip.recyclerviewdemo.model.Student;
import com.bip.recyclerviewdemo.model.VnExpressXmlObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stanfy.gsonxml.GsonXml;
import com.stanfy.gsonxml.GsonXmlBuilder;
import com.stanfy.gsonxml.XmlParserCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cac buoc tao 1 Recyclerview
 * 1. Them Thu vien
 * 2. Tao giao dien Activity co RecyclerView
 * 3. Anh xa RecyclerView vao code Activity
 * 4. Tao ViewHolder va giao dien cho ViewHolder
 * 5. Tao Adapter
 * 6. Overrite 3 ham trong Adapter: OncreateViewHolder,
 * OnBindViewHolder, getitemCount
 * 7. Set LayoutManager cho Recyclerview de chi dinh
 * kieu hien thi cho recyclerview(list/grid/ngang/doc)
 * 8. Tao du lieu cho Adapter va khoi tao Adapter
 * 9. Set Adapter cho RecyclerView
 * 10. Chay chuong trinh
 */
public class MainActivity extends AppCompatActivity {
    public static final String STUDENT_ID ="STUDENT_ID";

    private RecyclerView recyclerView;
    private StudentAdapter myAdapter;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
//        RequestHttpAsyntask requestHttpAsyntask = new RequestHttpAsyntask("https://api.github.com/search/users?q=tom");
//        requestHttpAsyntask.execute();
//        httpRequestUsingVolley("https://api.github.com/search/users?q=tom");
        Log.e("Log", "1");
        loadStudentFromDb();
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateOrEditActivity.class);
                intent.putExtra(STUDENT_ID, 0);
                startActivity(intent);
            }
        });
        getVnExpressRss("https://vnexpress.net/rss/tin-moi-nhat.rss");
    }


    class RequestHttpAsyntask extends AsyncTask<Void, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;
        private String url;

        public RequestHttpAsyntask(String url) {
            this.url = url;
        }

        //Chay vao truoc khi goi tac vu thuoc uithread
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "Loading", "Đang tải dữ liệu", true);
        }

        //Chay ngam khi thuc hien tac vui nam o thread khac tach biet
        @Override
        protected String doInBackground(Void... voids) {
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(url);
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
                result = null;
            }
            return result;

        }

        //Khi thuc hien xong tac vu goi den ham nay de tra ket qua ve cho UI
        @Override
        protected void onPostExecute(String json) {
            super.onPostExecute(json);
            progressDialog.dismiss();
            //txtContent.setText(s);
            //disPlayData(json);
            disPlayDataUseGson(json);
        }

    }

    private void disPlayData(String json) {
        List<Student> studentList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            //Lay ra mang Json
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                //Lay ra Jsonobject tai vi tri i
                JSONObject item = jsonArray.getJSONObject(i);
                Student studenti = new Student();
                studenti.setId(item.getInt("id"));
                studenti.setAvatar(item.getString("avatar_url"));
                studenti.setName(item.getString("login"));
                studentList.add(studenti);
            }
            myAdapter = new StudentAdapter(studentList);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(myAdapter);
            //txtContent.setText(content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void disPlayDataUseGson(String json) {
        Gson gson = new Gson();

        //Type listType = new TypeToken<List<Student>>() {}.getType();
        //ArrayList<Student> list= gson.fromJson(json, listType);

        DataResult dataResult = gson.fromJson(json, DataResult.class);
        ArrayList<Student> studentArrayList = dataResult.getStudentArrayList();
        myAdapter = new StudentAdapter(studentArrayList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(myAdapter);
    }
    //Okhttp:
    //Retrofit: 1
    //Volley: 2

    private void httpRequestUsingVolley(String url) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        disPlayDataUseGson(response);
                        Log.e("Log", "2");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Co loi xay ra", Toast.LENGTH_LONG).show();
                        Log.e("Log", "3");
                    }


        }){// truyen tham so
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("list_id", "1_20_36_99");

                return params;
            }
        };
        Log.e("Log", "4");
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        Log.e("Log", "5");

    }
    private void getVnExpressRss(String url){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        displayRss(response);
                        Log.e("Log", "2");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Co loi xay ra", Toast.LENGTH_LONG).show();
                        Log.e("Log", "3");
                    }


                });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        Log.e("Log", "5");
    }
    //https://github.com/stanfy/gson-xml
    private void displayRss(String xml){
        XmlParserCreator parserCreator = new XmlParserCreator() {
            @Override
            public XmlPullParser createParser() {
                try {
                    return XmlPullParserFactory.newInstance().newPullParser();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };

        GsonXml gsonXml = new GsonXmlBuilder()
                .setXmlParserCreator(parserCreator)
                .create();
        String xml1 = "<rss xmlns:slash=\"http://purl.org/rss/1.0/modules/slash/\" version=\"2.0\"><name>my name</name><description>my description</description></rss>";

        VnExpressXmlObject vnExpressXmlObject = gsonXml.fromXml(xml,
                VnExpressXmlObject.class);
        Log.e("getvnexpress", vnExpressXmlObject.getChannel().getTitle());
    }
    private void loadStudentFromDb(){
        MySqliteHelper dbHelper = new MySqliteHelper(this);
        List<Student> studentArrayList = dbHelper.getAllStudent1();
        myAdapter = new StudentAdapter(studentArrayList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(myAdapter);
    }
}
