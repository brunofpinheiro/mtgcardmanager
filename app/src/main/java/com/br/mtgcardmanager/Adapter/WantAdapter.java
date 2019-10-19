package com.br.mtgcardmanager.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.br.mtgcardmanager.Model.Card;
import com.br.mtgcardmanager.View.FragmentWant;
import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.R;
import com.br.mtgcardmanager.View.WantViewHolder;

import java.util.ArrayList;


public class WantAdapter extends RecyclerView.Adapter<WantViewHolder> {
    private Context         context;
    private ArrayList<Card> wantCards;

    public WantAdapter(Context context, ArrayList<Card> wantCards){
        this.context   = context;
        this.wantCards = wantCards;
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
        DatabaseHelper dbHelper;
        final          Card card;
        FragmentWant   fragmentWant;

        dbHelper = new DatabaseHelper(context);
        card     = wantCards.get(position);

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

        fragmentWant = new FragmentWant();
        fragmentWant.registerForContextMenu(viewHolder.mBtnMore);

        viewHolder.mBtnMore.setOnClickListener(view -> viewHolder.mBtnMore.showContextMenu());
        viewHolder.mBtnMore.setOnCreateContextMenuListener((contextMenu, view1, contextMenuInfo) -> {
            int UNIQUE_FRAGMENT_GROUP_ID = 2;

            fragmentWant.getClickedItem(card);

            contextMenu.add(UNIQUE_FRAGMENT_GROUP_ID, R.id.context_menu_search, 0, R.string.search);
            contextMenu.add(UNIQUE_FRAGMENT_GROUP_ID, R.id.context_menu_delete, 0, R.string.delete);
            contextMenu.add(UNIQUE_FRAGMENT_GROUP_ID, R.id.context_menu_add_note, 0, R.string.add_note);
        });
    }

    @Override
    public int getItemCount(){
        return wantCards.size();
    }
}