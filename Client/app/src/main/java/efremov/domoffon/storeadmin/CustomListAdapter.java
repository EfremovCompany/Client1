package efremov.domoffon.storeadmin;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Сергей on 18.04.2017.
 */
public class CustomListAdapter extends ArrayAdapter<String> {

        private final Activity context;
        private final ArrayList<String> itemname;
        private final ArrayList<String> des;
    private final ArrayList<String> price;
    private final ArrayList<String> count;
        private final ArrayList<Bitmap> imgid;

        public CustomListAdapter(Activity context, ArrayList<String> itemname, ArrayList<Bitmap> imgid, ArrayList<String> des_, ArrayList<String> price_, ArrayList<String> count_) {
            super(context, R.layout.listview, itemname);
            // TODO Auto-generated constructor stub

            this.context=context;
            this.itemname=itemname;
            this.des = des_;
            this.price = price_;
            this.count = count_;
            this.imgid=imgid;
        }

        public View getView(int position,View view,ViewGroup parent) {
            LayoutInflater inflater=context.getLayoutInflater();
            View rowView=inflater.inflate(R.layout.listview, null,true);

            //TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
            TextView extratxt = (TextView) rowView.findViewById(R.id.text1);
            TextView extratxt2 = (TextView) rowView.findViewById(R.id.text2);
            TextView extratxt3 = (TextView) rowView.findViewById(R.id.text3);
            TextView extratxt4 = (TextView) rowView.findViewById(R.id.text4);

            //txtTitle.setText(itemname[position]);
            try {
                imageView.setImageBitmap(imgid.get(position));
                extratxt.setText(itemname.get(position));
                extratxt2.setText(des.get(position));
                extratxt3.setText(price.get(position));
                extratxt4.setText(count.get(position));
            }
            catch (Exception e){
                e.getMessage();
            }
            return rowView;

        };
    }

