package com.example.bivanalzackyh.cardapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Semaphore;

import BroCardsNetworks.ReplyRoutine;
import BroCardsNetworks.Server;

public class PlayerHand extends AppCompatActivity {

    // adapter and card area
    private RecyclerView mplayer_card;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Integer> holdingCard;
    private ArrayList<Integer> drawableID;
    private RecyclerView.Adapter adapter;

    // status
    private TextView playerStatus;

    // server stuffs
    private Server listenServer;

    private Semaphore clickable;
    private Semaphore cardSelected;
    private int selectedCard;
    private boolean win;

    // layout
    TextView playerScore;
    TextView playerName;

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

    private void addCard(int cardNo) {
        holdingCard.add(cardNo);
        drawableID.add(assoc_card.get(cardNo));
        adapter.notifyItemInserted(holdingCard.size() - 1);
        adapter.notifyDataSetChanged();
    }

    private void removeCard(int card) {
        int position = holdingCard.indexOf(card);
        holdingCard.remove(position);
        drawableID.remove(position);

//        mplayer_card.removeViewAt(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, holdingCard.size());
        adapter.notifyDataSetChanged();
    }

    private void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    @Override
    //please take a look at the function onCreate and MyAdapter
    //there is a kind of translation between the array holdingCard and array drawableID
    //I have provided the translation in the assoc_card function
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_hand);

        mplayer_card = (RecyclerView) findViewById(R.id.player_card);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mplayer_card.setLayoutManager(mLayoutManager);

        playerScore = (TextView) findViewById(R.id.playerScore);
        playerName = (TextView) findViewById(R.id.playerHandName);
        playerStatus = (TextView) findViewById(R.id.playerStatus);

        // semaphores
        clickable = new Semaphore(0);
        cardSelected = new Semaphore(0);

        holdingCard = new ArrayList<>();
        adapter = new MyAdapter();

        mplayer_card.setAdapter(adapter);

        playerName.setText(getIntent().getExtras().getString("Name"));

        // start server
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                listenServer = new Server(8888);

                if (!listenServer.isRunning()) {
                    // Server not running
                    Log.e("Listen server", "not running");
                }

                while (listenServer.isRunning()) {
                    JSONObject inquiry = listenServer.listen(new playerReply());
                }

                Class dest;

                // game ended
                if (win) {
                    dest = CongratulateWinning.class;
                } else {
                    dest = PoorLosing.class;
                }

                Intent i = new Intent(getApplicationContext(), dest);
                startActivity(i);
            }
        });

        serverThread.start();
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            ImageView card_im;

            ViewHolder(View v) {
                super(v);
                ImageView mcard_im =(ImageView) v.findViewById(R.id.card_im);
                card_im = mcard_im;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        MyAdapter() {
            drawableID = new ArrayList<Integer>();
            for(int i : holdingCard) {
                drawableID.add(assoc_card.get(i));
            }
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
            holder.card_im.setImageResource(drawableID.get(position));
            holder.card_im.setTag(R.id.activity_player_hand, position);
            holder.card_im.setOnClickListener(ClickListener);
        }

        private View.OnClickListener ClickListener = new View.OnClickListener(){
            public void onClick(View v){
                // on click function has to not remove card anymore...
                // try down the semaphore... seems familiar
                if (clickable.tryAcquire()) {
                    //for now it's just getting card disappear when being clicked
                    //should be updated later on
                    int index = (int) v.getTag(R.id.activity_player_hand);

                    selectedCard = holdingCard.get(index);

                    // remove your turn status
                    playerStatus.setText("------");

                    //still something wrong in here
                    //anyway I will be back on this later on
                    // card removed
                    cardSelected.release();
                }
            }
        };

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return holdingCard.size();
        }
    }

    class playerReply implements ReplyRoutine {
        @Override
        public JSONObject processInquiry(final JSONObject json) {
            // check if need reply
            try {
                boolean needReply = json.getBoolean("requestResponse");

                String type = json.getString("type");

                Log.d("Incoming Json", json.toString());

                int card = 0;
                if (json.has("card"))
                    card = json.getInt("card");

                switch (type) {
                    case "insert":
                        // update the deck
                        final int cardNo = card;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addCard(cardNo);
                            }
                        });
                        break;
                    case "remove":
                        final int cardNo2 = card;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                removeCard(cardNo2);
                            }
                        });
                        break;
                    case "request":
                        // do this is no longer to remove card
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                makeToast("Your Turn! Select card to move");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        playerStatus.setText("Now, it is your turn!");
                                    }
                                });
                                clickable.release();
                            }
                        });
                        cardSelected.acquire();

                        // selectedCard is now store INDEX of card which is selected
                        JSONObject reply = new JSONObject();
                        reply.put("card", selectedCard);

                        return reply;
                    case "score":
                        final int score = json.getInt("score");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                playerScore.setText(String.valueOf(score));
                            }
                        });
                        break;
                    case "end":
                        // game end
                        win = json.getBoolean("win");
                        // don't know if it has problem but hey... we are leaving
                        listenServer.exit();
                        break;
                    default:
                        Log.e("Player hand", "invalid type request");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listenServer.exit();
    }
}