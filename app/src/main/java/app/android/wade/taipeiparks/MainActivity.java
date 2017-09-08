package app.android.wade.taipeiparks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import app.android.wade.taipeiparks.ui.ParksListAdapter;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mParksRecycleView;
    private ParksListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mParksRecycleView = (RecyclerView) findViewById(R.id.parks_list_view);
        //////
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i + "--");
        }
        /////
        mAdapter = new ParksListAdapter(list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mParksRecycleView.setLayoutManager(layoutManager);
        mParksRecycleView.setAdapter(mAdapter);
    }

}
