package efremov.sg.domoffon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

/**
 * Created by Aleksey on 01.05.2017.
 */

public class ProductListAdapter  extends BaseAdapter{
    private Context mContext;
    private List<Product> mProductList;

    // статический ViewHolder класс внутри адаптера
    public static final class ViewHolder {
        View v;
        TextView txName;
        TextView txDes;
        TextView txCount;
        TextView txPrice;
    }

    public ProductListAdapter(Context mContext, List<Product> mProductList) {
        this.mContext = mContext;
        this.mProductList = mProductList;
    }

    public void clear() {
        mProductList.clear();
        return;
    }

    @Override
    public int getCount() {
        return mProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();;
        holder.v = View.inflate(mContext, R.layout.item_product, null);
        holder.txName = (TextView)holder.v.findViewById(R.id.listview_item_name);
        holder.txDes = (TextView)holder.v.findViewById(R.id.listview_item_des);
        holder.txCount = (TextView)holder.v.findViewById(R.id.listview_item_count);
        holder.txPrice = (TextView)holder.v.findViewById(R.id.listview_item_price);
        //ImageView imView = (ImageView)v.findViewById(R.id.listview_item_img);

        holder.txName.setText(mProductList.get(position).getName());
        holder.txDes.setText("    " + mProductList.get(position).getDes());
        holder.txCount.setText("Количество: " + String.valueOf(mProductList.get(position).getCount()));
        holder.txPrice.setText(String.valueOf(mProductList.get(position).getPrice()) + " руб.");

        //if (!"none".contains(mProductList.get(position).getSrc())) {
            //IMGTask task = new IMGTask(mProductList.get(position).getSrc());
            //task.execute();
            //while (!task.isEnd)
            //{}
            //imView.setImageBitmap(task.GetIMG());
            /*try {
                final Bitmap bitmap = BitmapFactory.decodeStream(new URL(mProductList.get(position).getSrc()).openStream());
                imView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.getMessage();
            }*/
        //}

        holder.v.setTag(mProductList.get(position).getId());

        return holder.v;
    }

    public class IMGTask extends AsyncTask<Void, Void, Boolean> {

        private boolean isEnd = false;
        Bitmap img;

        private final String murl;

        IMGTask(String url) {
            murl = url;
        }

        public Bitmap GetIMG(){
            return img;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                img = BitmapFactory.decodeStream(new URL(murl).openStream());
                isEnd = true;
            } catch (Exception e) {
                e.getMessage();
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            isEnd = true;
        }
    }
}
