package efremov.sg.domoffon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class ShopActivity extends AppCompatActivity {

    protected static final String LOG_TAG = "my_tag";
    TabHost.TabSpec tabSpec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
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

        // вторая вкладка по умолчанию активна
        tabHost.setCurrentTabByTag("tag2");

        // логгируем переключение вкладок
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                Log.d(LOG_TAG, "tabId = " + tabId);

            }
        });

    }
}
