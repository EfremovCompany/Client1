package efremov.sg.domoffon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Aleksey on 20.05.2017.
 */

public class OrderListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Order> mOrderList;

    public OrderListAdapter(Context mContext, List<Order> mOrderList) {
        this.mContext = mContext;
        this.mOrderList = mOrderList;
    }

    public void clear() {
        mOrderList.clear();
        return;
    }

    @Override
    public int getCount() {
        return mOrderList.size();
    }

    @Override
    public Object getItem(int position) {
        return mOrderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_order, null);
        TextView txName = (TextView)v.findViewById(R.id.olistview_item_name);
        TextView txAddr = (TextView)v.findViewById(R.id.olistview_item_addr);
        TextView txCount = (TextView)v.findViewById(R.id.olistview_item_count);
        TextView txPrice = (TextView)v.findViewById(R.id.olistview_item_price);
        TextView txStatus = (TextView)v.findViewById(R.id.olistview_item_status);

        txName.setText(mOrderList.get(position).getName());
        txAddr.setText("    " + mOrderList.get(position).getAddr());
        txCount.setText("Количество: " + String.valueOf(mOrderList.get(position).getCount()));
        txPrice.setText(String.valueOf(mOrderList.get(position).getPrice()) + " руб.");
        txStatus.setText("    " + mOrderList.get(position).getStatus());
        v.setTag(mOrderList.get(position).getId());

        return v;
    }
}
