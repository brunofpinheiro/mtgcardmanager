package com.br.mtgcardmanager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.br.mtgcardmanager.Model.Editions;
import com.br.mtgcardmanager.R;

import java.util.List;

/**
 * Created by Bruno on 30/07/2016.
 */
public class EditionsDialogAdapter extends ArrayAdapter<Editions> {

    private int layout_resource;

    public EditionsDialogAdapter(Context context, int layoutResource, List<Editions> editions) {
        super(context, layoutResource, editions);
        this.layout_resource = layoutResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layout_inflater = LayoutInflater.from(getContext());
            view = layout_inflater.inflate(layout_resource, null);
        }

        Editions editions = getItem(position);

        if (editions != null) {
            TextView mDialogEditionName = (TextView) view.findViewById(R.id.dialog_edition_name);
            mDialogEditionName.setText(editions.getEdition());
        }

        return view;
    }
}