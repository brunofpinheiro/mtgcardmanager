package com.br.mtgcardmanager.View;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.br.mtgcardmanager.Model.Card;
import com.br.mtgcardmanager.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentWant extends Fragment {
    private static RecyclerView       recyclerView;
    private static FragmentActivity   fragmentActivity;
    private static TextView           mNoCardsMessage;
    public         ArrayList<Card>    wantCardsList;
    public static  int                contextMenuCardId;
    public static  String             contextMenuNameEn;
    public static  String             contextMenuNamePt;
    public static  String             contextMenuFoil;
    private        int                notificationNumber;
    private        SwipeRefreshLayout swipeContainer;


    public FragmentWant() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView      = inflater.inflate(R.layout.fragment_want, container, false);
        recyclerView       = rootView.findViewById(R.id.recycler_view_want);
        swipeContainer     = rootView.findViewById(R.id.want_swipe_container);
        mNoCardsMessage    = rootView.findViewById(R.id.no_cards_message);
        fragmentActivity   = this.getActivity();
        notificationNumber = 0;

        refreshRecyclerView(true);

        swipeContainer.setOnRefreshListener(() -> {
            refreshRecyclerView(false);
            swipeContainer.setRefreshing(false);
        });
        swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return rootView;
    }

    /**
     * Returns a list of all want cards.
     */
    public void getWantCards() {
        DatabaseHelper dbHelper;

        dbHelper      = new DatabaseHelper(fragmentActivity);
        wantCardsList = new ArrayList<>();
        wantCardsList = dbHelper.getAllWantCards();

        dbHelper.close();
    }

    /**
     * Get the properties of a clicked item.
     * @param card
     */
    public void getClickedItem(Card card) {
        contextMenuCardId = card.getId();
        contextMenuNameEn = card.getName_en();
        contextMenuNamePt = card.getName_pt();
        contextMenuFoil   = card.getFoil();
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
                    if (contextMenuNameEn.isEmpty()) {
                        ((MainActivity) getActivity()).searchCard(contextMenuNamePt);
                    } else {
                        ((MainActivity) getActivity()).searchCard(contextMenuNameEn);
                    }
                    return true;
                case R.id.context_menu_delete:
                    this.deleteConfirmDialog();
                    return true;
                case R.id.context_menu_add_note:
                    createNotification();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Creates a notification
     */
    private void createNotification() {
        NotificationCompat.Builder builder;
        NotificationManager        notificationManager;
        String                     foil = "";

        createNotificationChannel();

        if (contextMenuFoil.equals("S")) {
            foil = "(" + getString(R.string.foil) + ")";
        }

        builder = new NotificationCompat.Builder(fragmentActivity, getString(R.string.app_name));

        builder.setSmallIcon(R.mipmap.ic_notification_white);
        builder.setContentText(getString(R.string.want_notification_text) + " " + contextMenuNamePt + foil);

        notificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationNumber++;
        notificationManager.notify(notificationNumber, builder.build());
    }

    /**
     * Creates the notification channel for the notifications (only necessary for Android O or later)
     */
    private void createNotificationChannel() {
        CharSequence        name;
        int                 importance;
        NotificationChannel channel;
        NotificationManager notificationManager;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            name                = getString(R.string.app_name);
            importance          = NotificationManager.IMPORTANCE_DEFAULT;
            channel             = new NotificationChannel(getString(R.string.app_name), name, importance);
            notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Returns a string with the quantity and the name of all cards.
     * @return
     */
    public String getListToShare() {
        String cardsToShare = "";

        getWantCards();

        cardsToShare += "MTG Card Manager" + System.lineSeparator();
        cardsToShare += System.lineSeparator();
        cardsToShare += "Quero";

        for (Card card : wantCardsList) {
            cardsToShare += System.lineSeparator();
            cardsToShare += card.getQuantity() + "x " + card.getName_pt();
        }

        return cardsToShare;
    }

    /**
     * Reloads the data and refreshes the view.
     */
    public void refreshRecyclerView(boolean updateCardsList) {
        RecyclerView.LayoutManager layoutManager;
        RecyclerView.Adapter       wantAdapter;

        // Get the list of want cards from the db
        if (updateCardsList)
            getWantCards();

        // Show or hide the no cards message
        if (wantCardsList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            mNoCardsMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            mNoCardsMessage.setVisibility(View.GONE);
        }

        // Sets up the recycler view with the list of cards
        layoutManager = new LinearLayoutManager(fragmentActivity);
        recyclerView.setLayoutManager(layoutManager);

        wantAdapter = new WantAdapter(fragmentActivity, wantCardsList);
        wantAdapter.notifyDataSetChanged();
        recyclerView.invalidate();
        recyclerView.setAdapter(wantAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * Shows the confirmation dialog
     */
    public void deleteConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(fragmentActivity, R.style.exclusionConfirmationDialog);
        builder
                .setMessage(getString(R.string.delete_confirmation))
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                    DatabaseHelper dbHelper = DatabaseHelper.getInstance(fragmentActivity);
                    dbHelper.deleteWantCard(contextMenuCardId);
                    refreshRecyclerView(true);
                    Toast.makeText(fragmentActivity, R.string.delete_successful, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.cancel())
                .show();
    }
}