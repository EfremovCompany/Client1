package efremov.sg.domoffon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Aleksey on 01.05.2017.
 */

public class ProductListAdapter  extends BaseAdapter{
    private Context mContext;
    private List<Product> mProductList;

    public ProductListAdapter(Context mContext, List<Product> mProductList) {
        this.mContext = mContext;
        this.mProductList = mProductList;
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
        View v = View.inflate(mContext, R.layout.item_product, null);
        TextView txName = (TextView)v.findViewById(R.id.listview_item_name);
        TextView txDes = (TextView)v.findViewById(R.id.listview_item_des);
        TextView txCount = (TextView)v.findViewById(R.id.listview_item_count);
        TextView txPrice = (TextView)v.findViewById(R.id.listview_item_price);

        txName.setText(mProductList.get(position).getName());
        txDes.setText(mProductList.get(position).getDes());
        txCount.setText("Количество: " + String.valueOf(mProductList.get(position).getCount()));
        txPrice.setText(String.valueOf(mProductList.get(position).getPrice()) + " руб.");

        v.setTag(mProductList.get(position).getId());

        return v;
    }
}
