package com.br.mtgcardmanager.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.br.mtgcardmanager.View.FragmentHave;
import com.br.mtgcardmanager.View.HaveViewHolder;
import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.LongClickListener;
import com.br.mtgcardmanager.Model.HaveCard;
import com.br.mtgcardmanager.R;

import java.util.ArrayList;

/**
 * Created by Bruno on 21/07/2016.
 */
public class HaveAdapter extends RecyclerView.Adapter<HaveViewHolder> {
    private Context             context;
    private ArrayList<HaveCard> haveCards;

    public HaveAdapter (Context context, ArrayList<HaveCard> haveCards){
        this.context   = context;
        this.haveCards = haveCards;
    }

    //Create new views (invoked by the layout manager)
    @Override
    public HaveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View           view;
        HaveViewHolder viewHolder;

        view       = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_have, parent, false);
        viewHolder = new HaveViewHolder(context, view);
        return viewHolder;
    }

    //Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder (final HaveViewHolder viewHolder, int position) {
        DatabaseHelper  dbHelper;
        final HaveCard  card;

        dbHelper = new DatabaseHelper(context);
        card     = haveCards.get(position);

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
                FragmentHave fragmentHave;

                fragmentHave = new FragmentHave();
                fragmentHave.getLongPressedItem(card);
            }
        });
    }

    @Override
    public int getItemCount(){
        return haveCards.size();
    }
}