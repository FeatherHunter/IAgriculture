package com.ifuture.iagriculture.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifuture.carcontrl_client.R;

import java.util.List;
import java.util.Map;

/**
 * Created by feather on 2016/3/19.
 */
public class OptionAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public OptionAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * 组件集合，对应list.xml中的控件
     *
     * @author Administrator
     */
    public final class Component {
        public ImageView picture;
        public TextView name;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Component component = null;
        if (convertView == null) {
            component = new Component();
            //获得组件，实例化组件
            convertView = layoutInflater.inflate(R.layout.option_items, null); //这里是items的布局文件！
            component.picture = (ImageView) convertView.findViewById(R.id.option_picture);
            component.name = (TextView) convertView.findViewById(R.id.option_name);
            convertView.setTag(component);
        } else {
            component = (Component) convertView.getTag();
        }
        //绑定数据

        component.picture.setBackgroundResource((Integer) data.get(position).get("option_picture"));
        component.name.setText((String) data.get(position).get("option_name"));
        return convertView;
    }

}

