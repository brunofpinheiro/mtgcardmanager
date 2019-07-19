package com.br.mtgcardmanager.View;


import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.br.mtgcardmanager.Adapter.WantAdapter;
import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.Model.WantCards;
import com.br.mtgcardmanager.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentWant extends Fragment {
    private static RecyclerView               recyclerView;
    private static FragmentActivity           fragmentActivity;
    private static TextView                   no_cards_message;
    private static RecyclerView.LayoutManager layoutManager;
    private        ArrayList<WantCards>       want_cards_list;
    private        RecyclerView.Adapter       wantAdapter;
    public static  int                        context_menu_card_id;
    public static  String                     context_menu_name_en;
    public static  String                     context_menu_name_pt;
    public static  String                     context_menu_foil;
    private        int                        notification_number;


    public FragmentWant() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView    = inflater.inflate(R.layout.fragment_want, container, false);
        recyclerView     = rootView.findViewById(R.id.recycler_view_want);
        no_cards_message = rootView.findViewById(R.id.no_cards_message);
        fragmentActivity = this.getActivity();
        registerForContextMenu(recyclerView);
        notification_number = 0;

        refreshRecyclerView();

        return rootView;
    }

    /**
     * Returns a list of all want cards.
     */
    private void getWantCards() {
        DatabaseHelper dbHelper = new DatabaseHelper(fragmentActivity);
        want_cards_list = new ArrayList<>();
        want_cards_list = dbHelper.getAllWantCards();
        dbHelper.close();
    }

    /**
     * Get the properties of a long pressed item.
     * @param card
     */
    public void getLongPressedItem(WantCards card){
        context_menu_card_id = card.getId();
        context_menu_name_en = card.getName_en();
        context_menu_name_pt = card.getName_pt();
        context_menu_foil    = card.getFoil();
    }

    /**
     * Creates a context menu for the selected item.
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == 2) {
            switch (item.getItemId()) {
                case R.id.context_menu_search:
                    if (context_menu_name_en.isEmpty()) {
                        ((MainActivity) getActivity()).searchCard(context_menu_name_pt);
                    } else {
                        ((MainActivity) getActivity()).searchCard(context_menu_name_en);
                    }
                    return true;
                case R.id.context_menu_delete:
                    this.deleteConfirmDialog();
                    return true;
                case R.id.context_menu_add_note:
                    String foil = "";

                    if (context_menu_foil.equals("S")) {
                        foil = "(" + getString(R.string.foil) + ")";
                    }

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(fragmentActivity);

                    mBuilder.setSmallIcon(R.mipmap.ic_notification_white);
                    mBuilder.setContentTitle(getString(R.string.notification_title));
                    mBuilder.setContentText(getString(R.string.want_notification_text) + " " + context_menu_name_pt + foil);

                    NotificationManager mNotificationManager = (NotificationManager)
                            getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                    notification_number = notification_number + 1;
                    mNotificationManager.notify(notification_number, mBuilder.build());
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Reloads the data and refreshes the view.
     */
    public void refreshRecyclerView() {
        // Get the list of want cards from the db
        getWantCards();

        // Show or hide the no cards message
        if (want_cards_list.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            no_cards_message.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            no_cards_message.setVisibility(View.GONE);
        }

        // Sets up the recycler view with the list of cards
        layoutManager = new LinearLayoutManager(fragmentActivity);
        recyclerView.setLayoutManager(layoutManager);

        wantAdapter = new WantAdapter(fragmentActivity, want_cards_list);
        wantAdapter.notifyDataSetChanged();
        recyclerView.invalidate();
        recyclerView.setAdapter(wantAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * Shows the confirmation dialog
     */
    public void deleteConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext(), R.style.exclusionConfirmationDialog);
        builder
                .setMessage(getString(R.string.delete_confirmation))
                .setPositiveButton(getString(R.string.yes),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DatabaseHelper dbHelper = DatabaseHelper.getInstance(fragmentActivity);
                        dbHelper.deleteWantCard(context_menu_card_id);
                        refreshRecyclerView();
                        Toast.makeText(fragmentActivity, R.string.delete_successful, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}