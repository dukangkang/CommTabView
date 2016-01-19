package com.docking.commtab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommTabActivity extends AppCompatActivity {

    private String items[] = {
            "电影", "电视剧", "影院热映"
    };
    private List<String> list = new ArrayList<String>();
    private CommTabView mCommTabView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_tab);
        init();
    }

    private void init() {
        for (int i = 0; i < 15; i++) {
            Random random = new Random();
            int pos = random.nextInt(items.length);
            list.add(items[pos]);
        }
        mCommTabView = (CommTabView) this.findViewById(R.id.commtab);
        mCommTabView.setOnChangedListener(mOnChangedListener);
//        mCommTab.setDefaultPosition(2);
//        mCommTab.setFixTab(true, 5);
//        mCommTabView.setFixTab(true);
        mCommTabView.replaceTabs(list);
    }


    private CommTabView.OnChangedListener mOnChangedListener = new CommTabView.OnChangedListener() {

        @Override
        public void onChangedItem(int checkedId) {
            Toast.makeText(CommTabActivity.this, "点击第 " + checkedId + " 项", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDefaultItem(int position) {
            Toast.makeText(CommTabActivity.this, "默认选中第 " + position + " 项", Toast.LENGTH_SHORT).show();
        }
    };
}
