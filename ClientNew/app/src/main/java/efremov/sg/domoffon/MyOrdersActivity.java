package efremov.sg.domoffon;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {
    private String secret;
    private int user_id;
    private String addr;
    private String phone;
    private String mSurnameView;
    private String mNameView;
    private String mPatronymicView;
    private ListView lv;
    private OrderListAdapter adapter;
    private List<Product> mProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        Intent intent = getIntent();
        intent.getIntExtra("id", user_id);
        secret = intent.getStringExtra("secret");
        //mOldPasswordView.setText(intent.getStringExtra("secret"));
        mNameView = intent.getStringExtra("name");
        mSurnameView = intent.getStringExtra("surname");
        mPatronymicView = intent.getStringExtra("patronymic");
        //cdek = intent.getStringExtra("cdek");
        addr = intent.getStringExtra("addr");
        phone = intent.getStringExtra("phone");
        UserRegTask task =  new UserRegTask();
        task.execute();
    }

    public void SetAdapter(List<Order> order){
        lv = (ListView)findViewById(R.id.lvview);
        adapter = new OrderListAdapter(getApplicationContext(), order);
        lv.setAdapter(adapter);
    }

    public class UserRegTask extends AsyncTask<Void, Void, Boolean> {
        private String response;
        private List<Order> mOrderList;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                HttpURLConnection urlConnection = null;
                Thread.sleep(2000);
                URL myURL = new URL(getString(R.string.server_ip) + "getorder?secret=" + secret + "&id=" + String.valueOf(user_id));

                HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");

                urlConnection = (HttpURLConnection) myURL
                        .openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader isw = new InputStreamReader(in);


                int data = isw.read();
                while (data != -1) {
                    char current = (char) data;
                    data = isw.read();
                    System.out.print(current);
                    response += current;
                }
                ;
                response = response.replace("null", "");
            } catch (InterruptedException e) {
                return false;
            } catch (MalformedURLException e) {
                response = e.getMessage();
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                response = e.getMessage();
                e.printStackTrace();
            } catch (ProtocolException e) {
                response = e.getMessage();
                e.printStackTrace();
            } catch (IOException e) {
                response = e.getMessage();
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                int count;
                //TODO: append check response
                try {
                    mOrderList = new ArrayList<>();
                    JSONObject dataJsonObj = new JSONObject(response);
                    int code = dataJsonObj.getInt("Code");
                    if (code == 200) {
                        JSONArray array = dataJsonObj.getJSONArray("Orders");
                        String output = "";
                            for (int i = 0; i < array.length(); i++){
                                JSONObject obj = array.getJSONObject(i);
                                mOrderList.add(new Order(i, obj.getString("Name"), obj.getString("Addr"), 1, obj.getInt("Price"), obj.getString("Status")));
                            }
                            SetAdapter(mOrderList);
                        //TextView txt = (TextView) findViewById(R.id.infotxt);
                        //txt.setText(output);
                        return;
                        //answer = dataJsonObj.getString("UserName");
                        //data = dataJsonObj.getJSONArray("ProductI");
                        //count = dataJsonObj.getInt("Count");
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                dataJsonObj.getString("Error"),
                                Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                } catch (JSONException e) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Ошибка сервера: некорректный JSON",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
            }
        }
    }
}
