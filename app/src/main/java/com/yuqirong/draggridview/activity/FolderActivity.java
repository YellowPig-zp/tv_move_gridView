package com.yuqirong.draggridview.activity;

import android.app.Activity;
import android.os.Bundle;

import com.yuqirong.draggridview.R;
import com.yuqirong.draggridview.adapter.DragGridAdapter;
import com.yuqirong.draggridview.view.DragGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qin on 7/14/2017.
 */

//Folder
public class FolderActivity extends Activity {
    private DragGridView mGridView;

    private List<String> list = new ArrayList();

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folder);
        mGridView = (DragGridView) findViewById(R.id.folderGridView);
        list = getIntent().getStringArrayListExtra("items");
        DragGridAdapter adapter = new DragGridAdapter(list, null, this, mGridView);
        mGridView.setAdapter(adapter);
    }
}
