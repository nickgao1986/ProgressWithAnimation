package nickgao.com.viewpagerswitchexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import nickgao.com.viewpagerswitchexample.adapter.FeedsMainAdapter;

public class TestListActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String TAG = TestListActivity.class.getSimpleName();
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feeds_main);
        mListView = (ListView) findViewById(R.id.news_home_listview);
        mListView.setAdapter(new FeedsMainAdapter(this,getList()));
        mListView.setOnItemClickListener(this);

    }



    public ArrayList<String> getList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("带有进度动画的进度条");
        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                CircleActivity.startActivity(TestListActivity.this);
                break;


        }
    }
}

