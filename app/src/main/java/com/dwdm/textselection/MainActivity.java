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
    private String sampleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (SelectableRecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layout = new SelectableLayoutManager(this);
        layout.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layout);
        sampleText = getString(R.string.sample_text);
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
        public void onBindViewHolder(VHolder viewHolder, int i) {
            viewHolder.textView.setText(sampleText);
            viewHolder.textView.setKey(" pos: "+i+sampleText);
        }

        @Override
        public int getItemCount() {
            return 100;
        }

       
    };
    class VHolder extends RecyclerView.ViewHolder {

        SelectableTextView textView;

        public VHolder(View itemView) {
            super(itemView);
            textView = (SelectableTextView) itemView.findViewById(R.id.text_view);

        }
    }
}
