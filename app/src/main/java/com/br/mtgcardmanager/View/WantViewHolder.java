package com.br.mtgcardmanager.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.Helper.UtilsHelper;
import com.br.mtgcardmanager.Model.Card;
import com.br.mtgcardmanager.R;


public class WantViewHolder extends RecyclerView.ViewHolder {
    private       Context        context;
    public        TextView       mCardName;
    public        TextView       mCardEdition;
    public        EditText       mCardQty;
    public        TextView       mFoil;
    public        TextView       mBtnMore;
    private final DatabaseHelper db_helper;

    public WantViewHolder(final Context context, View itemView){
        super(itemView);
        this.context = context;
        db_helper    = DatabaseHelper.getInstance(context);

        mCardName    = itemView.findViewById(R.id.want_card_name);
        mCardEdition = itemView.findViewById(R.id.want_card_edition);
        mFoil        = itemView.findViewById(R.id.want_card_foil);
        mCardQty     = itemView.findViewById(R.id.want_card_qty);
        mCardQty.setOnEditorActionListener((v, actionId, event) -> {
            if (mCardQty.getText().toString().isEmpty()) {
                Toast.makeText(this.context, this.context.getString(R.string.invalid_quantity), Toast.LENGTH_SHORT).show();
            } else {
                RelativeLayout layout;

                updateCardQty();
                layout = itemView.findViewById(R.id.recycler_view_want_layout);
                layout.clearFocus();

                UtilsHelper.closeKeyboardFrom(this.context, mCardQty);
                Toast.makeText(this.context, this.context.getString(R.string.quantity_updated), Toast.LENGTH_SHORT).show();
            }

            return true;
        });
        mBtnMore     = itemView.findViewById(R.id.want_btn_more);
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
        foil  = mFoil.getText().toString().trim();
        quantity     = 0;

        if (foil.equals("(Foil)")) {
            foil = "S";
        } else {
            foil = "N";
        }
        id_edition   = db_helper.getSingleEdition(this.context, edition_name).getId();
        existingCard = db_helper.checkIfWantCardExists(UtilsHelper.padronizeCardName(name), id_edition, foil);
        if (existingCard.getQuantity() > 0) {
            quantity = Integer.parseInt(mCardQty.getText().toString());
        }

        db_helper.updateCardQuantity("want", existingCard.getId(), quantity);
    }
}