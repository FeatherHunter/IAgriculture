package com.ifuture.iagriculture.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ifuture.iagriculture.listview.ListViewItem;
import com.ifuture.iagriculture.R;

import java.util.ArrayList;

import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;
import com.ifuture.iagriculture.listview.ViewHolder;

/**
 * @CopyRight: 王辰浩 2016~2026
 * @Author Feather Hunter(猎羽)
 * @qq: 975559549
 * @Version: 1.0
 * @Date: 2016/4/12
 * @Description: 主菜单HomeFragment中显示地区和大棚号的Listview的监听器
 *
 * @FunctionList:
 *      1. getView 					        //设置Listview中Item的样式
 *      2. isItemViewTypePinned             //判断是标题还是内容
 **/
public class HomeListViewAdapter extends BaseAdapter implements PinnedSectionListAdapter{

    private ArrayList<ListViewItem> list;
    private Context context;

    public HomeListViewAdapter(Context context,ArrayList<ListViewItem> list){
        this.setList(list);
        this.context=context;
    }

    @Override
    public View getView(int position, View converView, ViewGroup viewGrop) {
        // TODO Auto-generated method stub
        /*---------------------------------------------------------------------
		 *    获取到文本和图像的组件，在converView为空时是第一次创建
		 *    非null表示最近被释放的item的视图用来容纳新的item
		 *---------------------------------------------------------------------*/
        ViewHolder viewHolder = null;
        if(converView==null){
            viewHolder = new ViewHolder();
            converView=LayoutInflater.from(context).inflate(R.layout.home_listview, null);
            viewHolder.textView=(TextView)converView.findViewById(R.id.title);
            viewHolder.imageView=(ImageView)converView.findViewById(R.id.imageView1);
            converView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) converView.getTag();
        }
        /*---------------------------------------------------------------------
		 *    获取item，并且根据是标题还是内容进行配置
		 *---------------------------------------------------------------------*/
        ListViewItem item = getItem(position);
        viewHolder.textView.setText(getItem(position).text);
        if (item.type == ListViewItem.SECTION) { //为农场号
            viewHolder.textView.setBackgroundResource(R.color.mygreen4); //设置背景色为绿色
            viewHolder.textView.setTextColor(Color.WHITE);               //字为白色
            viewHolder.imageView.setVisibility(View.GONE);               //不显示图片

        }else{                                  //为大棚号
            viewHolder.textView.setBackgroundResource(R.color.white);
            viewHolder.imageView.setVisibility(View.VISIBLE);
        }
        return converView;
    }
    @Override
    public boolean isItemViewTypePinned(int viewType) {
        // TODO Auto-generated method stub
        return viewType == ListViewItem.SECTION;//0是标题，1是内容
    }

    public ArrayList<ListViewItem> getList() {
        return list;
    }

    public void setList(ArrayList<ListViewItem> list) {
        if(list!=null){
            this.list = list;
        }else{
            list=new ArrayList<ListViewItem>();
        }
    }
    @Override
    public int getViewTypeCount() {
        return 2;//2种view的类型 baseAdapter中得方法
    }
    @Override
    public int getItemViewType(int position) {
        return ((ListViewItem)getItem(position)).type;
    }
    public void refresh(ArrayList<ListViewItem> arr){
        setList(arr);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public ListViewItem getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}

