package com.br.mtgcardmanager.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.Helper.UtilsHelper;
import com.br.mtgcardmanager.LongClickListener;
import com.br.mtgcardmanager.Model.Card;
import com.br.mtgcardmanager.R;

/**
 * Created by Bruno on 21/07/2016.
 */
public class HaveViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnCreateContextMenuListener {
    private       Context           context;
    public        TextView          mCardName;
    public        TextView          mCardEdition;
    public        EditText          mCardQty;
    public        TextView          mFoil;
    private final DatabaseHelper    db_helper;
    private       LongClickListener longClickListener;
    private       int               UNIQUE_FRAGMENT_GROUP_ID;

    public HaveViewHolder (Context context, View itemView){
        super(itemView);
        this.context             = context;
        db_helper                = DatabaseHelper.getInstance(context);
        UNIQUE_FRAGMENT_GROUP_ID = 1;

        itemView.setOnLongClickListener(this);
        itemView.setOnCreateContextMenuListener(this);

        mCardName    = itemView.findViewById(R.id.have_card_name);
        mCardEdition = itemView.findViewById(R.id.have_card_edition);
        mFoil        = itemView.findViewById(R.id.have_card_foil);
        mCardQty     = itemView.findViewById(R.id.have_card_qty);
        mCardQty.setOnEditorActionListener((v, actionId, event) -> {
            updateCardQty();
            mCardQty.clearFocus();
            UtilsHelper.closeKeyboardFrom(this.context, mCardQty);
            Toast.makeText(this.context, this.context.getString(R.string.quantity_updated), Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    public void setLongClickListener(LongClickListener listener){
        this.longClickListener = listener;
    }

    @Override
    public boolean onLongClick(View v) {
        this.longClickListener.onItemLongClick(getLayoutPosition());
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(UNIQUE_FRAGMENT_GROUP_ID, R.id.context_menu_search, 0, R.string.search);
            menu.add(UNIQUE_FRAGMENT_GROUP_ID, R.id.context_menu_delete, 0, R.string.delete);
            menu.add(UNIQUE_FRAGMENT_GROUP_ID, R.id.context_menu_add_note, 0, R.string.add_note);
    }

    public void updateCardQty() {
        String edition_name;
        String name;
        int    quantity;
        int    id_edition;
        String foil;
        Card   existingCard;

        edition_name = mCardEdition.getText().toString();
        name         = mCardName.getText().toString();
        foil         = mFoil.getText().toString().trim();
        quantity     = 0;

        if (foil.equals("(Foil)")) {
            foil = "S";
        } else {
            foil = "N";
        }
        id_edition   = db_helper.getSingleEdition(this.context ,edition_name).getId();
        existingCard = db_helper.checkIfHaveCardExists(UtilsHelper.padronizeCardName(name), id_edition, foil);
        if (existingCard.getQuantity() > 0) {
            quantity = Integer.parseInt(mCardQty.getText().toString());
        }

        db_helper.updateCardQuantity("have", existingCard.getId(), quantity);
    }
}