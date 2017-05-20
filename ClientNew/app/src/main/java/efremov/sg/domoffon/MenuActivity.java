package efremov.sg.domoffon;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    protected static final String LOG_TAG = "my_tag";
    TabHost.TabSpec tabSpec;
    public String secret;
    public int user_id = 0;
    public String name;
    public String surname;
    public String patronymic;
    public String cdek;
    public String addr;
    public String phone;

    private ListView lvP;
    private ListView lvA;
    private ProductListAdapter adapterP;
    private ProductListAdapter adapterA;
    private List<Product> mProductListA;
    private List<Product> mProductList;
    private SwipeRefreshLayout mSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        intent.getIntExtra("response", user_id);
        secret = intent.getStringExtra("secret");
        name = intent.getStringExtra("name");
        surname = intent.getStringExtra("surname");
        patronymic = intent.getStringExtra("patronymic");
        cdek = intent.getStringExtra("cdek");
        addr = intent.getStringExtra("addr");
        phone = intent.getStringExtra("phone");

        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView  = (View) navigationView.inflateHeaderView(R.layout.nav_header_menu);
        TextView nameTxt = (TextView) headerView.findViewById(R.id.textViewNameUser);
        TextView surTxt = (TextView) headerView.findViewById(R.id.textViewSurUser);
        TextView patTxt = (TextView) headerView.findViewById(R.id.textViewPatUser);
        nameTxt.setText(name);
        surTxt.setText(surname);
        patTxt.setText(patronymic);
        /*TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        // инициализация
        tabHost.setup();

        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator("Спец предложение");
        tabSpec.setContent(R.id.tab1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator("Товары");
        tabSpec.setContent(R.id.tab2);
        tabHost.addTab(tabSpec);

        // логгируем переключение вкладок
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                Log.d(LOG_TAG, "tabId = " + tabId);

            }
        });*/
        //Товары
        ProductTask task = new ProductTask(secret);
        task.execute();
        //Предложения
        //lvA = (ListView)findViewById(R.id.listview_a);
        //mProductListA = new ArrayList<>();

        //mProductListA.add(new Product(0,"text", "des", 12, 1));
        //mProductListA.add(new Product(1,"text1", "des12", 10, 1));
        //mProductListA.add(new Product(2,"text2", "ds", 1, 1));

        //adapterA = new ProductListAdapter(getApplicationContext(), mProductListA);
        //lvA.setAdapter(adapterA);

        //mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        //Настраиваем выполнение OnRefreshListener для данной activity:
        lvP = (ListView)findViewById(R.id.listview_product);
        lvP.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d(LOG_TAG, "itemClick: position = " + position + ", id = "
                        + id);
            }
        });
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefresh.setOnRefreshListener(this);


        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefresh.setOnRefreshListener(this);
        //Настраиваем цветовую тему значка обновления, используя наши цвета:
        //mSwipeRefresh.setColorSchemeResources
        //        (R.color.primary_material_light_1, R.color.colorAccent,R.color.colorPrimaryDark);
    }

    public void SetAdapter(List<Product> product){
        adapterP = new ProductListAdapter(getApplicationContext(), product);
        lvP.setAdapter(adapterP);
    }

    @Override
    public void onRefresh() {
        adapterP.clear();
        ProductTask task = new ProductTask(secret);
        task.execute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Отменяем анимацию обновления
                mSwipeRefresh.setRefreshing(false);
                Random random = new Random();
            }
        }, 4000);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //findViewById(R.id.include_edit).setVisibility(View.INVISIBLE);
        //findViewById(R.id.include_main).setVisibility(View.INVISIBLE);
        //findViewById(R.id.include_my).setVisibility(View.INVISIBLE);
        //findViewById(R.id.include_send).setVisibility(View.INVISIBLE);

        if (id == R.id.nav_spec) {
            //findViewById(R.id.include_main).setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_my) {
            Intent intent = new Intent(this, MyOrdersActivity.class);
            intent.putExtra("response", id);
            intent.putExtra("secret", secret);
            intent.putExtra("name", name);
            intent.putExtra("surname", surname);
            intent.putExtra("patronymic", patronymic);
            //intent.putExtra("cdek", c);
            intent.putExtra("addr", addr);
            intent.putExtra("phone", phone);

            startActivity(intent);
        } else if (id == R.id.nav_edit) {
            Intent intent = new Intent(this, EditUserActivity.class);
            intent.putExtra("response", id);
            intent.putExtra("secret", secret);
            intent.putExtra("name", name);
            intent.putExtra("surname", surname);
            intent.putExtra("patronymic", patronymic);
            //intent.putExtra("cdek", c);
            intent.putExtra("addr", addr);
            intent.putExtra("phone", phone);

            startActivity(intent);
            //findViewById(R.id.include_edit).setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_send) {
            //findViewById(R.id.include_send).setVisibility(View.VISIBLE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class ProductTask extends AsyncTask<Void, Void, Boolean> {

        private String response;

        private final String mSecret;

        ProductTask(String secret) {
            mSecret = secret;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                Thread.sleep(2000);
                URL myURL = new URL(getString(R.string.server_ip) + "getproduct");
                //HttpURLConnection urlConnection = (HttpURLConnection) myURL.openConnection();
                HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("secret", mSecret);
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
            } catch (ProtocolException e) {
                e.printStackTrace();
                response = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                response = e.getMessage();
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                mProductList = new ArrayList<>();
                //TODO: append check response
                try {
                    JSONObject dataJsonObj = new JSONObject(response);
                    int code = dataJsonObj.getInt("Code");
                    int count = dataJsonObj.getInt("Count");
                    JSONArray array = dataJsonObj.getJSONArray("ProductI");
                    if (code == 200)
                    {
                        for (int i = 0; i < array.length(); i++){
                            JSONObject obj = array.getJSONObject(i);
                            mProductList.add(new Product(i, obj.getString("Name"), obj.getString("Des"), obj.getInt("Count"), obj.getInt("Min_prise"), obj.getString("Scr")));
                        }
                        SetAdapter(mProductList);
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
                            e.getMessage(),
                            Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                //finish();
            } else {
            }
        }
    }
}
