package com.dwdm.textselection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dwdm.textselection.selection.SelectableLayoutManager;
import com.dwdm.textselection.selection.SelectableRecyclerView;
import com.dwdm.textselection.selection.SelectableTextView;
import com.dwdm.textselection.selection.SelectionCallback;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = ">>>>>";
    private SelectableRecyclerView recyclerView;
    private String sampleText1;
    private String sampleText2;
    private String sampleCation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (SelectableRecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layout = new SelectableLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layout);
        sampleText1 = getString(R.string.sample_text1);
        sampleText2 = getString(R.string.sample_text2);
        sampleCation = getString(R.string.caption_text);
        recyclerView.setAdapter(adapter);

        recyclerView.setSelectionCallback(new SelectionCallback() {
            @Override
            public void startSelection() {
                Log.d(TAG, "startSelection() called with " + "");
            }

            @Override
            public void stopSelection() {
                Log.d(TAG, "stopSelection() called with " + "");
            }
        });

    }

    final RecyclerView.Adapter adapter = new RecyclerView.Adapter<VHolder>() {
        @Override
        public VHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new VHolder(LayoutInflater.from(getBaseContext()).inflate(R.layout.item_layout, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(VHolder viewHolder, int position) {
            viewHolder.text_view1.setText(sampleText1);
            viewHolder.text_view1.setKey(" pos: " + position + sampleText1);
            viewHolder.text_view2.setText(sampleText2);
            viewHolder.text_view2.setKey(" pos: " + position + sampleText2);
            viewHolder.caption_text_view.setText(sampleCation);
            viewHolder.caption_text_view.setKey(" pos: " + position + sampleCation);
        }

        @Override
        public int getItemCount() {
            return 100;
        }

       
    };
    class VHolder extends RecyclerView.ViewHolder {

        SelectableTextView text_view2;
        SelectableTextView text_view1;
        SelectableTextView caption_text_view;

        public VHolder(View itemView) {
            super(itemView);
            text_view2 = (SelectableTextView) itemView.findViewById(R.id.text_view2);
            text_view1 = (SelectableTextView) itemView.findViewById(R.id.text_view1);
            caption_text_view = (SelectableTextView) itemView.findViewById(R.id.caption_text_view);

        }
    }
}
