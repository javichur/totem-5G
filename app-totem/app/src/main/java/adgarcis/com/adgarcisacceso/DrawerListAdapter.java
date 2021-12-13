package adgarcis.com.adgarcisacceso;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.List;

public class DrawerListAdapter extends ArrayAdapter {
    Context context;
    public static ImageView imageViewHeader;
    public static TextView textViewNombreEventoMenu;

    public DrawerListAdapter(Context context, List objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            if (position == 0) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.drawer_menu_header, null);
                imageViewHeader = convertView.findViewById(R.id.imageview_header);

                Glide.with(AccesosApplication.getContext())
                        .load(R.drawable.accesos_casfid)
                        .fitCenter()
                        .into(imageViewHeader);



                textViewNombreEventoMenu = convertView.findViewById(R.id.textView_nombre_evento);
                textViewNombreEventoMenu.setText("");


            } else if (position == 6) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.drawer_menu_bottom, null);
            } else {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.drawer_menu_items, null);
            }
        }

        if (position > 0 && position < 6) {
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            TextView name = (TextView) convertView.findViewById(R.id.name);

            DrawerItem item = (DrawerItem) getItem(position);
            icon.setBackgroundResource(item.getIconId());
            name.setText(item.getName());
        } else if (position == 6) {

            TextView mTextViewVersion = (TextView) convertView.findViewById(R.id.textview_version);
            LinearLayout mLinearLayoutContainerBottom = (LinearLayout) convertView.findViewById(R.id.container_bottom);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mLinearLayoutContainerBottom.setPadding(0,450,0,0);
            } else {
                mLinearLayoutContainerBottom.setPadding(0,120,0,0);
            }
            PackageInfo pInfo = null;
            try {
                pInfo = mTextViewVersion.getContext().getPackageManager().getPackageInfo(mTextViewVersion.getContext().getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            mTextViewVersion.setText("V " + pInfo.versionName);

        }

        return convertView;
    }
}