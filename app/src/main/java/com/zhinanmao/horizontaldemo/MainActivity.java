package com.zhinanmao.horizontaldemo;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity  {

    private ListView listView;
    private int lastPosition = -1;
    private float lastXOffset = 0;
    private float downX =  0;
    private boolean isRight = false;
    private List<DataBean> data = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        for (int i = 0; i < 10; i++) {
            data.add(new DataBean(R.mipmap.ic_launcher, "Freeman", "Horizontal实现QQ侧滑删除", "2016-04-25"));
        }

        listView.setAdapter(new MyAdapter());
    }

    public int dpToPx(int dp) {
        return (int) (getResources().getDisplayMetrics().density * ((float) dp)+0.5);
    }

    /**
     * 获取ListView指定位置的ItemView
     * @param listView
     * @param position
     * @return
     */
    private View getViewByPosition(ListView listView, int position) {
        int firstItemPos = listView.getFirstVisiblePosition();
        int lastItemPos = firstItemPos + listView.getChildCount() - 1;
        if (position < firstItemPos || position > lastItemPos) {
            return listView.getAdapter().getView(position, null, listView);
        } else {
            int childIndex = position - firstItemPos;
            return listView.getChildAt(childIndex);
        }
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public android.view.View getView(final int position, android.view.View convertView, final ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_layout, parent, false);
                holder.icon = (ImageView) convertView.findViewById(R.id.icon);
                holder.nameText = (TextView) convertView.findViewById(R.id.name_text);
                holder.contentText = (TextView) convertView.findViewById(R.id.content_text);
                holder.timeText = (TextView) convertView.findViewById(R.id.time_text);
                holder.contentLayout = (LinearLayout) convertView.findViewById(R.id.content_layout);
                holder.horizontalScrollView = (HorizontalScrollView) convertView.findViewById(R.id.horizontal_scrollview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.contentLayout.getLayoutParams();
            params.width = getResources().getDisplayMetrics().widthPixels;
            holder.contentLayout.setLayoutParams(params);
            holder.icon.setImageResource(data.get(position).icon);
            holder.nameText.setText(data.get(position).name);
            holder.contentText.setText(data.get(position).content);
            holder.timeText.setText(data.get(position).time);

            holder.horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final View view = v;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downX = event.getX();
                            if (lastPosition != -1 && lastPosition != position) {
                                View openedItemView = getViewByPosition(listView, lastPosition);
                                if (openedItemView != null) {
                                    final HorizontalScrollView horizontalScrollView = ((HorizontalScrollView)openedItemView.findViewById(R.id.horizontal_scrollview));
                                    horizontalScrollView.smoothScrollTo(0, 0);
                                    lastPosition = -1;//关闭展开item后均要此步骤                              
                                }
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (event.getX() > lastXOffset) {
                                isRight = true;
                            } else {
                                isRight = false;
                            }
                            lastXOffset = event.getX();
                            break;
                        case MotionEvent.ACTION_UP:
                            float distance = Math.abs(event.getX() - downX);
                            if (distance == 0.0) {
                                if (lastPosition == position) {
                                    v.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((HorizontalScrollView)view).fullScroll(View.FOCUS_LEFT);
                                            lastPosition = -1;
                                        }
                                    });
                                } else if (lastPosition == -1) {                                                                     
                                        Toast.makeText(getContext(), "触发了点击事件",Toast.LENGTH_SHORT).show();                                                                     
                                } else {
                                    lastPosition = -1;
                                }
                            } else if (distance > 0 && distance < dpToPx(70)) {
                                v.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isRight) {
                                            ((HorizontalScrollView) view).fullScroll(View.FOCUS_RIGHT);
                                        } else {
                                            ((HorizontalScrollView)view).fullScroll(View.FOCUS_LEFT);
                                        }
                                    }
                                });
                            } else {
                                v.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isRight) {
                                            ((HorizontalScrollView) view).fullScroll(View.FOCUS_LEFT);
                                        } else {
                                            lastPosition = position;
                                            ((HorizontalScrollView)view).fullScroll(View.FOCUS_RIGHT);
                                        }
                                    }
                                });
                            }
                            break;
                        default:
                            break;
                    }

                    return false;
                }
            });

            return convertView;
        }
    }

    private class ViewHolder {
        public ImageView icon;
        public TextView nameText;
        public TextView contentText;
        public TextView timeText;
        public LinearLayout contentLayout;
        public HorizontalScrollView horizontalScrollView;
    }

    private class DataBean {
        public int icon;
        public String name;
        public String content;
        public String time;

        public DataBean(int icon, String name, String content, String time) {
            this.icon = icon;
            this.name = name;
            this.content = content;
            this.time = time;
        }
    }
}
