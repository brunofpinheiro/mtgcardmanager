package com.br.mtgcardmanager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.br.mtgcardmanager.Model.Edition;
import com.br.mtgcardmanager.R;

import java.util.List;


public class EditionsDialogAdapter extends ArrayAdapter<Edition> {

    private int layoutResource;

    public EditionsDialogAdapter(Context context, int layoutResource, List<Edition> editions) {
        super(context, layoutResource, editions);
        this.layoutResource = layoutResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View           view;
        LayoutInflater layoutInflater;
        Edition        edition;
        TextView       mDialogEditionName;

        view = convertView;

        if (view == null) {
            layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }

        edition = getItem(position);

        if (edition != null) {
            mDialogEditionName = view.findViewById(R.id.dialog_edition_name);
            mDialogEditionName.setText(edition.getEdition());
        }

        return view;
    }
}