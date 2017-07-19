package com.yuqirong.draggridview.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yuqirong.draggridview.R;
import com.yuqirong.draggridview.adapter.DragGridAdapter;
import com.yuqirong.draggridview.view.DragGridView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {

    private DragGridView mGridView;

    private List<String> list = new ArrayList();

    // Map that maps from id to lists corresponds to folders
    private static Map<String, List<String>> folders = new HashMap<>();

    // Total file and folder count
    private int totalFile = 0;

    boolean isOpen = false;

    final int CODE = 0x717;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGridView = (DragGridView) findViewById(R.id.mGridView);
        for(int i = 0 ;i<51;i++){
            totalFile++;
            list.add(i+"");
        }
        DragGridAdapter mAdapter = new DragGridAdapter(list, folders, this, mGridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Add to folder
                if (mGridView.addToFolder) {
                    if (folders.containsKey(list.get(i))) {
                        Toast.makeText(MainActivity.this, "不能把文件夹加入另一文件夹", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    folders.get(mGridView.currFolder).add(list.get(i));
                    list.remove(list.get(i));
                    ((DragGridAdapter)MainActivity.this.mGridView.getAdapter()).notifyDataSetChanged();
                    return;
                }
                // Click on a folder. Choose whether to move, to add or to open.
                final View v = view;
                final int index = i;
                if (folders.containsKey(list.get(i))) {
                    if (isOpen) {
                        isOpen = false;
                        v.findViewById(R.id.tv_text).setBackgroundResource(R.color.colorAccent);
                        mGridView.mode = DragGridView.MODE_FORBID;
                        return;
                    }
                    AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                    alert.setTitle("文件夹");
                    alert.setMessage("请选择移动文件夹、添加文件或打开文件夹");
                    alert.setButton(DialogInterface.BUTTON_NEUTRAL, "移动", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(!isOpen) {
                                isOpen = true;
                                v.findViewById(R.id.tv_text).setBackgroundResource(R.color.pressedColor);
                                mGridView.mode = DragGridView.MODE_NORMAL;
                            }else{}
                        }
                    });
                    alert.setButton(DialogInterface.BUTTON_NEGATIVE, "添加", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mGridView.addToFolder = true;
                            mGridView.currFolder = list.get(index);
                            return;
                        }
                    });
                    alert.setButton(DialogInterface.BUTTON_POSITIVE, "打开", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(MainActivity.this, FolderActivity.class);
                            intent.putStringArrayListExtra("items", (ArrayList<String>) folders.get(list.get(index)));
                            intent.putExtra("folderID", list.get(index));
                            startActivityForResult(intent, CODE);
                            return;
                        }
                    });
                    alert.show();
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

        // Use the button to create a placeholder, which will be used as a folder
        Button btnCreateFolder = (Button) findViewById(R.id.create_folder);
        btnCreateFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> myList = MainActivity.this.list;
                int newItemId = totalFile;
                myList.add("" + newItemId);
                folders.put("" + newItemId, new ArrayList<String>());
                totalFile++;
                ((DragGridAdapter)MainActivity.this.mGridView.getAdapter()).notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mGridView.addToFolder) {
            mGridView.addToFolder = false;
            mGridView.currFolder = null;
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE && resultCode == CODE) {
            if (data.getStringArrayListExtra("allItems") != null) {
                folders.remove(data.getStringExtra("folderID"));
                list.addAll(data.getStringArrayListExtra("allItems"));
                list.remove(data.getStringExtra("folderID"));
                return;
            }
            ArrayList<String> removedItems = data.getStringArrayListExtra("removedItems");
            String folderID = data.getStringExtra("folderID");
            folders.get(folderID).removeAll(removedItems);
            list.addAll(removedItems);
            ((BaseAdapter) mGridView.getAdapter()).notifyDataSetChanged();
        }
    }
}
