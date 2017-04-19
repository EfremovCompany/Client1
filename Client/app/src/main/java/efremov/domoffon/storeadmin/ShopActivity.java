package efremov.domoffon.storeadmin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Id to identity READ_CONTACTS permission request.

    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
*/
    private UserRegTask mAuthTask = null;
    private View mProgressView;
    private View mLoginFormView;
    public String secret;
    public int user_id = 0;
    public TextView txtView;
    final String LOG_TAG = "myLogs";

    // имена атрибутов для Map
    final String ATTRIBUTE_NAME_TEXT = "text";
    final String ATTRIBUTE_NAME_THIS = "thisPeriod";
    final String ATTRIBUTE_NAME_PREVIOUS = "previouse";

    ListView lvSimple;

    void btnClickInfo() {
        Intent intent = new Intent(this, InfoActivity.class);
        intent.putExtra("response", user_id);
        intent.putExtra("secret", secret);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shop);
        mLoginFormView = findViewById(R.id.reg_form);
        mProgressView = findViewById(R.id.reg_progress);
        TextView text = (TextView) findViewById(R.id.text1);
        txtView = (TextView)findViewById(R.id.sbtxt);

        Intent intent = getIntent();
        intent.getIntExtra("response", user_id);
        secret = intent.getStringExtra("secret");
        //text.setText("Привет " + response);
        showProgress(true);
        mAuthTask = new UserRegTask();
        mAuthTask.execute((Void) null);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                /*MySimpleAdapter adapter = null;
                try {
                    adapter = (MySimpleAdapter) parent.getAdapter();
                }
                catch(Exception e){
                    e.getMessage();
                }

                for (int i = 0; i < adapter.getCount(); i++) {
                    //Мы можем получить доступ к каждому значению и изменить его
                    Map m = (Map) adapter.getItem(i);

                    //Для тех кто не знает при вставке данных в Map с существующим ключём значение заменяется
                    //m.put(ATTRIBUTE_NAME_THIS, "123");
                }*/
                //После всех манипуляций  нужно вызвать данную функцию
                //она сама произведет обновление данных в нашем списке
                //adapter.notifyDataSetChanged();


                ShowDialog();
            }
        });

    }

    public int iPrice;
    public int iCount;
    public TextView txtViewData;
    public TextView txtViewData1;
    SeekBar sb = null;
    TextView etPassword = null;
    TextView cbShowPassword = null;

    public void ShowDialog()
    {
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.product, null);
        sb = (SeekBar) alertLayout.findViewById(R.id.seekBar);
        etPassword = (TextView) alertLayout.findViewById(R.id.sbtxt);
        cbShowPassword = (TextView) alertLayout.findViewById(R.id.sbtxtsumm);

        //final SeekBar seek = new SeekBar(this);
        txtViewData = new TextView(this);
        txtViewData1 = new TextView(this);
        TextView txt = (TextView) findViewById(R.id.text3);
        TextView txtCount = (TextView) findViewById(R.id.text4);
        txtViewData.setText("0");
        txtViewData1.setText("0");
        String count = txtCount.getText().toString().replace("Количество: ", "");
        String price = txt.getText().toString().replace("Цена: ", "").replace(" руб.", "");

        iPrice = Integer.parseInt(price);
        iCount = Integer.parseInt(count);
        sb.setMax(iCount);
        iCount = 0;
        popDialog.setTitle("Покупка:");
        popDialog.setView(alertLayout);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                //txtView.setText("Value of : " + progress);
                iCount = progress;
                cbShowPassword.setText(String.valueOf(iCount));
                etPassword.setText(String.valueOf(iCount * iPrice));
            }
            public void onStartTrackingTouch(SeekBar arg0) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        popDialog.setPositiveButton("Oплатить",
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                UserLoginTask task = new UserLoginTask(etPassword.getText().toString() ,cbShowPassword.getText().toString(), "0", String.valueOf(user_id), secret);
                task.execute();
                dialog.dismiss();
            }
        });
        popDialog.create();
        popDialog.show();
    }

    private ArrayList<HashMap<String, Object>> myBooks;
    private static final String BOOKKEY = "bookname";
    private static final String PRICEKEY = "bookprice";
    private static final String PRICE = "price";
    private static final String COUNT = "count";
    private static final String IMGKEY = "image";

    public void SetList(JSONArray data, int count) {
        ListView listView = (ListView) findViewById(R.id.list);
        myBooks = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> hm;
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> deses  = new ArrayList<String>();
        ArrayList<String> counts  = new ArrayList<String>();
        ArrayList<String> prices  = new ArrayList<String>();
        ArrayList<Bitmap> pictures = new ArrayList<Bitmap>();

        for (int i = 0; i < data.length(); i++) {
            hm = new HashMap<String, Object>();
            JSONObject mJsonObjectProperty;
            try {
                mJsonObjectProperty = data.getJSONObject(i);
                //hm.put(BOOKKEY, mJsonObjectProperty.getString("Name"));
                names.add(mJsonObjectProperty.getString("Name"));
                deses.add(mJsonObjectProperty.getString("Des"));
                prices.add("Цена: " + mJsonObjectProperty.getString("Min_prise") + " руб.");
                counts.add("Количество: " + mJsonObjectProperty.getString("Count"));
                //hm.put(PRICEKEY, mJsonObjectProperty.getString("Des"));
                //hm.put(PRICE, "Цена: " + mJsonObjectProperty.getString("Min_prise") + " руб.");
                //hm.put(COUNT, "Количество: " + mJsonObjectProperty.getString("Count"));
                if (mJsonObjectProperty.getString("Scr").equals("none")) {
                    //URL newurl = new URL("http://neskuchnij.net/nophoto.jpg");
                    //mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
                    //myImageView.setImageBitmap(mIcon_val);
                    //new DownloadImageTask((ImageView) findViewById(R.id.img)).execute(mJsonObjectProperty.getString("Scr"))
                    DownloadImageTask task = new DownloadImageTask("http://neskuchnij.net/nophoto.jpg");
                    task.execute();
                    while (!task.IsEnd()){}

                    //while (!task.IsEnd()){}
                    pictures.add(task.GetBitmap());
                    //while (!task.IsEnd()){}
                } else {
                    //URL newurl = new URL(mJsonObjectProperty.getString("Scr"));
                    //mIcon_val = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
                    DownloadImageTask task = new DownloadImageTask(mJsonObjectProperty.getString("Scr"));
                    task.execute();
                    while (!task.IsEnd()){}

                    //while (!task.IsEnd()){}
                    pictures.add(task.GetBitmap());
                }
            } catch (Exception e) {
                e.getMessage();
            }
        }
        CustomListAdapter adapter= new CustomListAdapter(this, names, pictures, deses, prices, counts);
        listView=(ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        openQuitDialog();
    }

    private void openQuitDialog() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle("Выход: Вы уверены?");

        quitDialog.setPositiveButton("Да!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                System.exit(0);
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();
    }

    public class UserRegTask extends AsyncTask<Void, Void, Boolean> {
        private String response;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                HttpURLConnection urlConnection = null;
                Thread.sleep(2000);
                URL myURL = new URL(getString(R.string.server_ip) + "?key=getproduct&secret=" + secret);
                //HttpURLConnection urlConnection = (HttpURLConnection) myURL.openConnection();
                HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                //conn.setDoInput(true);
                //conn.setDoOutput(true);

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
                // Simulate network access
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
            mAuthTask = null;
            showProgress(false);

            if (success) {
                String answer = "";
                JSONArray data;
                int count;
                //TODO: append check response
                try {
                    JSONObject dataJsonObj = new JSONObject(response);
                    int code = dataJsonObj.getInt("Code");
                    if (code == 200) {
                        //answer = dataJsonObj.getString("UserName");
                        data = dataJsonObj.getJSONArray("ProductI");
                        count = dataJsonObj.getInt("Count");
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                dataJsonObj.getString("Error"),
                                Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                } catch (JSONException e) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Ошибка на сервере ",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                SetList(data, count);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private class DownloadImageTask extends AsyncTask<Void, Void, Void> {
        Bitmap bmImage;
        String url;
        boolean isEnd = false;

        public Bitmap GetBitmap() {
            return bmImage;
        }

        public boolean IsEnd() {
            return isEnd;
        }

        public DownloadImageTask(String url_) {
            //this.bmImage = bmImage;
            url = url_;
        }

        protected Void doInBackground(Void... params) {
            //String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bmImage = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                //Log.e(e.getMessage());
                e.printStackTrace();
            }
            isEnd = true;
            return null;
        }

        protected void onPostExecute(Bitmap result) {
            isEnd = true;
        }
    }

    class MySimpleAdapter extends SimpleAdapter {

        public MySimpleAdapter(Context context,
                               List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }



    }


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String response;

        private final String mPrice;
        private final String mCount;
        private final String mIdProd;
        private final String mIdUser;
        private final String mSecret;


        UserLoginTask(String price, String count, String idP, String idL, String secret) {
            mPrice = price;
            mCount = count;
            mIdProd = "1";
            mIdUser = idL;
            mSecret = secret;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                Thread.sleep(2000);
                URL myURL = new URL(getString(R.string.server_ip));
                //HttpURLConnection urlConnection = (HttpURLConnection) myURL.openConnection();
                HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("key", "sendorder")
                        .appendQueryParameter("price", mPrice)
                        .appendQueryParameter("count", mCount)
                        .appendQueryParameter("secret", mSecret)
                        .appendQueryParameter("iduser", mIdUser)
                        .appendQueryParameter("idproduct", mIdProd);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer resp = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    resp.append(inputLine);
                }
                in.close();

                response = resp.toString();
                // Simulate network access
            } catch (InterruptedException e) {
                return false;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                response = e.getMessage();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                response = e.getMessage();
            } catch (ProtocolException e) {
                e.printStackTrace();
                response = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                response = e.getMessage();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                try {
                    JSONObject dataJsonObj = new JSONObject(response);
                    int code = dataJsonObj.getInt("Code");
                    if (code == 200)
                    {

                    }
                    else
                    {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                dataJsonObj.getString("Error"),
                                Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                } catch (JSONException e) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Ошибка на стороне сервера",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                new UserRegTask().execute();

                //OpenShop(answer, secret);
                //finish();
        }
    }
}
}
