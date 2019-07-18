package com.br.mtgcardmanager.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.br.mtgcardmanager.FragmentHave;
import com.br.mtgcardmanager.HaveViewHolder;
import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.LongClickListener;
import com.br.mtgcardmanager.Model.HaveCards;
import com.br.mtgcardmanager.R;

import java.util.ArrayList;

/**
 * Created by Bruno on 21/07/2016.
 */
public class HaveAdapter extends RecyclerView.Adapter<HaveViewHolder> {
    private Context              context;
    private ArrayList<HaveCards> have_cards;

    public HaveAdapter (Context context, ArrayList<HaveCards> have_cards){
        this.context    = context;
        this.have_cards = have_cards;
    }

    //Create new views (invoked by the layout manager)
    @Override
    public HaveViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_have, parent, false);
        HaveViewHolder viewHolder = new HaveViewHolder(context, view);
        return viewHolder;
    }

    //Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder (final HaveViewHolder viewHolder, int position) {
        DatabaseHelper  dbHelper = new DatabaseHelper(context);
        final HaveCards card = have_cards.get(position);

        viewHolder.mCardName.setText(card.getName_pt() + " (" + card.getName_en() + ")");
        viewHolder.mCardQty.setText(card.getQuantity() + "");
        viewHolder.mCardEdition.setText(dbHelper.getEditionById(this.context, card.getId_edition()));
        if (card.getFoil().equals("S")) {
            viewHolder.mFoil.setText(" (" + context.getString(R.string.foil) + ")");
        }

        viewHolder.setLongClickListener(new LongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                FragmentHave fragment_have = new FragmentHave();
                fragment_have.getLongPressedItem(card);
            }
        });
    }

    @Override
    public int getItemCount(){
        return have_cards.size();
    }
}