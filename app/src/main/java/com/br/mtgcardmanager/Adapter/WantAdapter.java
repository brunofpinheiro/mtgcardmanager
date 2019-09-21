package com.br.mtgcardmanager.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.br.mtgcardmanager.View.FragmentWant;
import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.LongClickListener;
import com.br.mtgcardmanager.Model.WantCard;
import com.br.mtgcardmanager.R;
import com.br.mtgcardmanager.View.WantViewHolder;

import java.util.ArrayList;

/**
 * Created by Bruno on 21/07/2016.
 */
public class WantAdapter extends RecyclerView.Adapter<WantViewHolder> {
    private Context              context;
    private ArrayList<WantCard> want_cards;

    public WantAdapter(Context context, ArrayList<WantCard> want_cards){
        this.context    = context;
        this.want_cards = want_cards;
    }

    //Create new views (invoked by the layout manager)
    @Override
    public WantViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_want, parent, false);
        WantViewHolder viewHolder = new WantViewHolder(context, view);
        return viewHolder;
    }

    //Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder (final WantViewHolder viewHolder, int position) {
        DatabaseHelper  dbHelper = new DatabaseHelper(context);
        final WantCard card = want_cards.get(position);

        if (card.getName_en().isEmpty()) {
            viewHolder.mCardName.setText(card.getName_pt());
        } else {
            viewHolder.mCardName.setText(card.getName_pt() + " (" + card.getName_en() + ")");
        }
        viewHolder.mCardQty.setText(card.getQuantity() + "");
        viewHolder.mCardEdition.setText(dbHelper.getEditionById(this.context, card.getId_edition()));
        if (card.getFoil().equals("S")) {
            viewHolder.mFoil.setText(" (" + context.getString(R.string.foil) + ")");
        }

        viewHolder.setLongClickListener(new LongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                FragmentWant fragmentWant = new FragmentWant();
                fragmentWant.getLongPressedItem(card);
            }
        });
    }

    @Override
    public int getItemCount(){
        return want_cards.size();
    }
}