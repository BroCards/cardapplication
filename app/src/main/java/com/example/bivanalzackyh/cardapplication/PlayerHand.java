package com.example.bivanalzackyh.cardapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class PlayerHand extends AppCompatActivity {

    private RecyclerView mplayer_card;
    private RecyclerView.LayoutManager mLayoutManager;

    private static final Map<Integer,Integer> assoc_card = new Hashtable<Integer, Integer>() {{
        put(0, R.drawable.clubs_2); put(1, R.drawable.clubs_3); put(2, R.drawable.clubs_4); put(3, R.drawable.clubs_5);
        put(4, R.drawable.clubs_6); put(5, R.drawable.clubs_7); put(6, R.drawable.clubs_8); put(7, R.drawable.clubs_9);
        put(8, R.drawable.clubs_10); put(9, R.drawable.clubs_j); put(10, R.drawable.clubs_q); put(11, R.drawable.clubs_k);
        put(12, R.drawable.clubs_a);

        put(13, R.drawable.diamonds_2); put(14, R.drawable.diamonds_3); put(15, R.drawable.diamonds_4); put(16, R.drawable.diamonds_5);
        put(17, R.drawable.diamonds_6); put(18, R.drawable.diamonds_7); put(19, R.drawable.diamonds_8); put(20, R.drawable.diamonds_9);
        put(21, R.drawable.diamonds_10); put(22, R.drawable.diamonds_j); put(23, R.drawable.diamonds_q); put(24, R.drawable.diamonds_k);
        put(25, R.drawable.diamonds_a);

        put(26, R.drawable.hearts_2); put(27, R.drawable.hearts_3); put(28, R.drawable.hearts_4); put(29, R.drawable.hearts_5);
        put(30, R.drawable.hearts_6); put(31, R.drawable.hearts_7); put(32, R.drawable.hearts_8); put(33, R.drawable.hearts_9);
        put(34, R.drawable.hearts_10); put(35, R.drawable.hearts_j); put(36, R.drawable.hearts_q); put(37, R.drawable.hearts_k);
        put(38, R.drawable.hearts_a);

        put(39, R.drawable.spades_2); put(40, R.drawable.spades_3); put(41, R.drawable.spades_4); put(42, R.drawable.spades_5);
        put(43, R.drawable.spades_6); put(44, R.drawable.spades_7); put(45, R.drawable.spades_8); put(46, R.drawable.spades_9);
        put(47, R.drawable.spades_10); put(48, R.drawable.spades_j); put(49, R.drawable.spades_q); put(50, R.drawable.spades_k);
        put(51, R.drawable.spades_a);
    }};

    @Override
    //please take a look at the function onCreate and MyAdapter
    //there is a kind of translation between the array games and array cards_id
    //I have provided the translation in the assoc_card function
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_hand);

        mplayer_card = (RecyclerView) findViewById(R.id.player_card);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mplayer_card.setLayoutManager(mLayoutManager);

        int[] games = {0,1,2,3};
        RecyclerView.Adapter adapter = new MyAdapter(games);
        mplayer_card.setAdapter(adapter);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private int[] mDataset;
        private int[] cards_id = {assoc_card.get(0), assoc_card.get(1), assoc_card.get(2), assoc_card.get(3)};

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public ImageView card_im;

            public ViewHolder(View v) {
                super(v);
                ImageView mcard_im =(ImageView) v.findViewById(R.id.card_im);
                card_im = mcard_im;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(int[] myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_game_example, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.card_im.setImageResource(cards_id[position]);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }
    }
