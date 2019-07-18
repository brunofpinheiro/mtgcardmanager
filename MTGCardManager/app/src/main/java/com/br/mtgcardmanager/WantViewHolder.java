package com.br.mtgcardmanager;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.Helper.UtilsHelper;
import com.br.mtgcardmanager.Model.WantCards;

/**
 * Created by Bruno on 21/07/2016.
 */
public class WantViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnCreateContextMenuListener {
    private Context      context;
    public TextView      mCardName;
    public TextView      mCardEdition;
    public EditText      mCardQty;
    public TextView      mFoil;
    final DatabaseHelper db_helper;
    LongClickListener    longClickListener;
    public static int    UNIQUE_FRAGMENT_GROUP_ID;

    public WantViewHolder(Context context, View itemView){
        super(itemView);
        this.context = context;
        db_helper    = DatabaseHelper.getInstance(context);
        itemView.setOnLongClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
        UNIQUE_FRAGMENT_GROUP_ID = 2;

        mCardName    = (TextView) itemView.findViewById(R.id.want_card_name);
        mCardEdition = (TextView) itemView.findViewById(R.id.want_card_edition);
        mFoil        = (TextView) itemView.findViewById(R.id.want_card_foil);
        mCardQty     = (EditText) itemView.findViewById(R.id.want_card_qty);
        mCardQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                updateCardQty();
                mCardQty.clearFocus();
                return false;
            }
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
        UtilsHelper utils = new UtilsHelper();
        String      edition_name;
        String      name;
        int         quantity;
        int         id_edition;
        WantCards   existingCard;

        edition_name = mCardEdition.getText().toString();
        name         = mCardName.getText().toString();
        String foil  = mFoil.getText().toString().trim();
        quantity     = 0;

        if (foil.equals("(Foil)")) {
            foil = "S";
        } else {
            foil = "N";
        }
        id_edition   = db_helper.getSingleEdition(this.context, edition_name).getId();
        existingCard = db_helper.checkIfWantCardExists(utils.padronizeCardName(name), id_edition, foil);
        if (existingCard.getQuantity() > 0) {
            quantity = Integer.parseInt(mCardQty.getText().toString());
        }// end if

        db_helper.updateCardQuantity("want", existingCard.getId(), quantity);
    }
}