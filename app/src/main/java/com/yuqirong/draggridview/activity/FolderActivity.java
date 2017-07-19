package com.yuqirong.draggridview.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

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

    boolean isOpen = false;

    private ArrayList<String> removedItems = new ArrayList<>();

    private boolean removing = false;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folder);
        mGridView = (DragGridView) findViewById(R.id.folderGridView);
        list = getIntent().getStringArrayListExtra("items");
        final DragGridAdapter adapter = new DragGridAdapter(list, null, this, mGridView);
        mGridView.setAdapter(adapter);

        Button removeBtn = (Button) findViewById(R.id.removeBtn);
        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removing = !removing;
            }
        });

        Button deleteFolder = (Button) findViewById(R.id.deleteFolderBtn);
        deleteFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getIntent().putStringArrayListExtra("allItems", (ArrayList<String>) list);
                setResult(0x717, getIntent());
                finish();
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (removing) {
                    removedItems.add(list.remove(i));
                    adapter.notifyDataSetChanged();
                    return;
                }
                if(!isOpen) {
                    isOpen = true;
                    view.findViewById(R.id.tv_text).setBackgroundResource(R.color.pressedColor);
                    mGridView.mode = DragGridView.MODE_NORMAL;
                }else{
                    isOpen = false;
                    view.findViewById(R.id.tv_text).setBackgroundResource(R.color.colorAccent);
                    mGridView.mode = DragGridView.MODE_FORBID;
                }
            }
        });
        mGridView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.e(">>>>>focus",b+"");
                if(b){
                    if(mGridView.getChildAt(0)!=null) {
                        mGridView.getChildAt(0).findViewById(R.id.tv_text).setBackgroundResource(R.color.colorAccent);
                    }
                }else {
                    for (int m = 0; m < mGridView.getChildCount(); m++) {
                        mGridView.getChildAt(m).findViewById(R.id.tv_text).setBackgroundResource(R.color.normalColor);
                    }
                }
            }
        });
        mGridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("dd","selectedPosition"+i);
                Log.e("dd", "getLastVisiblePosition:"+mGridView.getLastVisiblePosition());
                Log.e("dd", "getFirstVisiblePosition:"+mGridView.getFirstVisiblePosition());
                if(mGridView.mode==DragGridView.MODE_FORBID){
                    if(mGridView.getChildAt(mGridView.position-mGridView.getFirstVisiblePosition())!=null) {
                        mGridView.getChildAt(mGridView.position-mGridView.getFirstVisiblePosition()).findViewById(R.id.tv_text).setBackgroundResource(R.color.normalColor);
                    }
                    if(mGridView.getChildAt(i-mGridView.getFirstVisiblePosition())!=null) {
                        mGridView.getChildAt(i-mGridView.getFirstVisiblePosition()).findViewById(R.id.tv_text).setBackgroundResource(R.color.colorAccent);
                    }
                    mGridView.position = i;
                    if(mGridView.getLastVisiblePosition()-mGridView.position<5) {
                        mGridView.smoothScrollBy(mGridView.getChildAt(mGridView.position-mGridView.getFirstVisiblePosition()).getHeight()+mGridView.getVerticalSpacing(),300);

                    }else if(mGridView.position-mGridView.getFirstVisiblePosition()<5){
                        mGridView.smoothScrollBy(-(mGridView.getChildAt(mGridView.position-mGridView.getFirstVisiblePosition()).getHeight()+mGridView.getVerticalSpacing()),300);
                    }
                }else {
                    Log.e("position:",mGridView.position+" i:"+i);
                    mGridView.itemMove(mGridView.position, i);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        getIntent().putStringArrayListExtra("removedItems", removedItems);
        setResult(0x717, getIntent());
        super.onBackPressed();
        finish();
    }
}
