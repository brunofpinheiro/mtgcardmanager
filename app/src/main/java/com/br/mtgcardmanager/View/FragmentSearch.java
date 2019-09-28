package com.br.mtgcardmanager.View;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.br.mtgcardmanager.Adapter.EditionsDialogAdapter;
import com.br.mtgcardmanager.Helper.DatabaseHelper;
import com.br.mtgcardmanager.Helper.UtilsHelper;
import com.br.mtgcardmanager.Model.Card;
import com.br.mtgcardmanager.Model.Edition;
import com.br.mtgcardmanager.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSearch extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static ArrayList<Edition> cardEditions;
    private ArrayList<Edition>        availableEditions;
    private static Elements           cardNamePT;
    private static Elements           cardNameEN;
    private Edition                   edition;
    private String                    foil;
    public  Boolean                   secondRequest;
    public  Boolean                   btn_have_pressed;
    public  Boolean                   btn_have_4_pressed;
    public  Boolean                   btn_want_pressed;
    public  Boolean                   btn_want_4_pressed;
    public  Button                    mButtonHave;
    public  Button                    mButtonHave4;
    public  Button                    mButtonWant;
    public  Button                    mButtonWant4;
    public  DatabaseHelper            dbHelper;
    public  Dialog                    dialog;
    public  Document                  doc;
    public  Elements                  pageTitle;
    public  EditionsDialogAdapter     editionsAdapter;
    public  ListView                  editionsListView;
    public  ProgressDialog            progressDialog;
    public  RequestQueue              queue;
    public  String                    searchedCard;
    public  String                    url;
    public  String                    title = "";
    public  String                    selectedEdition;
    public  StringRequest             stringRequest;
    public  View                      fragSearchView;
    public  UtilsHelper               utils;

    public FragmentSearch() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragSearchView = inflater.inflate(R.layout.fragment_search, container, false);
        mButtonHave    = fragSearchView.findViewById(R.id.btn_tenho);
        mButtonHave4   = fragSearchView.findViewById(R.id.btn_tenho_4);
        mButtonWant    = fragSearchView.findViewById(R.id.btn_quero);
        mButtonWant4   = fragSearchView.findViewById(R.id.btn_quero_4);

        btn_have_pressed   = false;
        btn_have_4_pressed = false;
        btn_want_pressed   = false;
        btn_want_4_pressed = false;
        foil               = "N";

        // set listeners for the buttons
        mButtonHave.setOnClickListener(this);
        mButtonHave4.setOnClickListener(this);
        mButtonWant.setOnClickListener(this);
        mButtonWant4.setOnClickListener(this);

        return fragSearchView;
    }

    /**
     * recebe a query digitada no campo de busca do menu (searchView), pesquisa a carta e
     * cria o layout na tela.
     *
     * @param activity
     * @param query
     */
    public void searchLigaMagic(final Activity activity, final String query) {
        final ImageView mCardImage  = activity.findViewById(R.id.ivCardImageID);
        final TextView  mCardNamePT = activity.findViewById(R.id.cardNameID);
        final TextView  mCardNameEN = activity.findViewById(R.id.cardNameEN);

        progressDialog = new ProgressDialog(activity, R.style.customProgressDialog);
        progressDialog.setMessage(activity.getString(R.string.loading));
        progressDialog.show();

        queue        = Volley.newRequestQueue(activity.getApplicationContext());
        searchedCard = query.replace(" ", "+");
        url          = "https://www.ligamagic.com.br/?view=cards%2Fsearch&card=" + searchedCard;
        cardEditions = new ArrayList<>();

        if (dbHelper == null)
            dbHelper  = DatabaseHelper.getInstance(this.getContext());

        if (availableEditions == null || availableEditions.size() == 0)
            availableEditions = dbHelper.getAllEditions();

        // Request a string response from the provided URL.
        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        doc = Jsoup.parse(response);
                        title = doc.select("title").toString();
                        secondRequest = false;
                        if (title.contains("Busca:")) {
                            secondRequest = true;
                            url = "https://www.ligamagic.com.br/?view=cards/card&card=" + searchedCard;

                            stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            doc = Jsoup.parse(response);
                                            pageTitle = doc.select("title");
                                            // li[id=paginacao-1] indicates that the card was not found
                                            if (doc.select("li[id=paginacao-1]").size() > 0) {
                                                Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.card_not_found), Toast.LENGTH_LONG).show();
                                            } else {
                                                if (secondRequest) {
                                                    montarLayout(activity, mCardImage, mCardNamePT, mCardNameEN);
                                                }
                                            }
                                            //end if
                                        }//end onResponse
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.search_failed), Toast.LENGTH_LONG).show();
                                }//end onErrorResponse
                            });
                            queue.add(stringRequest);
                        }//end if
                        if (!secondRequest) {
                            montarLayout(activity, mCardImage, mCardNamePT, mCardNameEN);
                        }//end if
                    }//end onResponse
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.search_failed), Toast.LENGTH_LONG).show();
            }//end onErrorResponse
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }//end pesquisarLigaMagic

    /**
     * Gets the screen components and builds the layout.
     * @param activity
     * @param mCardImage
     * @param mCardNamePT
     * @param mCardNameEN
     */
    public void montarLayout(Activity activity, ImageView mCardImage, TextView mCardNamePT, TextView mCardNameEN){
        Button   mBtnHaveAdd;
        Button   mBtnHaveAdd4;
        Button   mBtnWantAdd;
        Button   mBtnWantAdd4;
        TextView mTvPrices;
        TextView mTvMinPrice;
        TextView mTvMaxPrice;
        Elements imgSpan;
        Elements htmlEditionsNames;
        Elements htmlDivPrecos;
        String   cardImgURL;
        String   menorPreco;
        String   maiorPreco;

        mBtnHaveAdd  = activity.findViewById(R.id.btn_tenho);
        mBtnHaveAdd4 = activity.findViewById(R.id.btn_tenho_4);
        mBtnWantAdd  = activity.findViewById(R.id.btn_quero);
        mBtnWantAdd4 = activity.findViewById(R.id.btn_quero_4);
        mTvPrices    = activity.findViewById(R.id.tvPrices);
        mTvMinPrice  = activity.findViewById(R.id.tvMinPrice);
        mTvMaxPrice  = activity.findViewById(R.id.tvMaxPrice);

        mBtnHaveAdd.setVisibility(View.VISIBLE);
        mBtnHaveAdd4.setVisibility(View.VISIBLE);
        mBtnWantAdd.setVisibility(View.VISIBLE);
        mBtnWantAdd4.setVisibility(View.VISIBLE);
        mTvPrices.setVisibility(View.VISIBLE);
        mTvMinPrice.setVisibility(View.VISIBLE);
        mTvMaxPrice.setVisibility(View.VISIBLE);

        imgSpan    = doc.select("div[id=card-image-src]");
        cardImgURL = "https:" + imgSpan.select("img").attr("src");
        if (!cardImgURL.isEmpty()) {
            Picasso.with(activity.getBaseContext()).load(cardImgURL).into(mCardImage);
        } else {
            Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.card_not_found), Toast.LENGTH_LONG).show();
        }

        cardNamePT = doc.select("div[id=card-sm-name]").select("p[class=nome-principal]");
        cardNameEN = doc.select("div[id=card-sm-name]").select("p[class=nome-auxiliar]");

        mCardNamePT.setText(cardNamePT.get(0).text());
        mCardNameEN.setText(cardNameEN.get(0).text());

        htmlEditionsNames = doc.select("div[id=card-filtros]")
                .select("div:last-child[class=filtro]").select("div[class=filtro-opcao]");

        for (int i = 0; i < htmlEditionsNames.size(); i++) {
            ImageView mImageView;
            String    aux;

            mImageView = new ImageView(activity.getApplicationContext());
            mImageView.setAdjustViewBounds(true);

            // create the list of editions the card appeared in
            edition = new Edition();

            aux = htmlEditionsNames.get(i).text();
            if (aux.contains(")"))
                aux = aux.substring(aux.indexOf(")") +1).trim();

            for (Edition ed : availableEditions) {
                if (ed.getEdition().equalsIgnoreCase(aux) || ed.getEdition_pt().equalsIgnoreCase(aux)) {
                    edition.setEdition(aux);
                    cardEditions.add(edition);
                }
            }
        }

        htmlDivPrecos = doc.select("div[id=alerta-preco]");
        menorPreco    = htmlDivPrecos.select("div[class=col-xl-6 col-6 b preco-menor]").select("font[class=bigger]").html();
        maiorPreco    = htmlDivPrecos.select("div[class=col-xl-6 col-6 b preco-maior]").select("font[class=bigger]").html();

        mTvMinPrice.setText(activity.getResources().getString(R.string.min) + " R$ " + menorPreco);
        mTvMinPrice.setTextSize(16);
        mTvMaxPrice.setText(activity.getResources().getString(R.string.max) + " R$ " + maiorPreco);
        mTvMaxPrice.setTextSize(16);

        progressDialog.dismiss();
    }

    // Listeners for buttons HAVE and WANT
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_tenho: {
                btn_have_pressed = true;
                if (cardEditions.size() > 1) {
                    createEditionsDialog();
                } else {
                    selectedEdition = cardEditions.get(0).getEdition();
                    insertCard("have");
                }
                break;
            }
            case R.id.btn_tenho_4: {
                btn_have_4_pressed = true;
                if (cardEditions.size() > 1) {
                    createEditionsDialog();
                } else {
                    selectedEdition = cardEditions.get(0).getEdition();
                    insertCard("have");
                }
                break;
            }
            case R.id.btn_quero: {
                btn_want_pressed = true;
                if (cardEditions.size() > 1) {
                    createEditionsDialog();
                } else {
                    selectedEdition = cardEditions.get(0).getEdition();
                    insertCard("want");
                }
                break;
            }
            case R.id.btn_quero_4: {
                btn_want_4_pressed = true;
                if (cardEditions.size() > 1) {
                    createEditionsDialog();
                } else {
                    selectedEdition = cardEditions.get(0).getEdition();
                    insertCard("want");
                }
                break;
            }
        }
    }

    public void createEditionsDialog() {
        dialog = new Dialog(this.getContext(), R.style.customDialogTheme);
        dialog.setTitle(R.string.editions_dialog_title);
        dialog.setContentView(R.layout.editions_dialog);
        dialog.setCancelable(true);

        editionsListView = dialog.findViewById(R.id.editions_dialog_listview);
        editionsAdapter  = new EditionsDialogAdapter(dialog.getContext(), R.layout.editions_dialog_item, cardEditions);
        editionsListView.setAdapter(editionsAdapter);

        dialog.show();

        editionsListView.setClickable(true);
        editionsListView.setOnItemClickListener(this);

        // Checkbox Foil Listener
        CheckBox mFoil = dialog.findViewById(R.id.editions_dialog_foil_checkbox);
        mFoil.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    foil = "S";
                } else {
                    foil = "N";
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        edition         = (Edition) editionsListView.getItemAtPosition(position);
        selectedEdition = edition.getEdition();

        dialog.cancel();
        if (btn_have_pressed || btn_have_4_pressed) {
            insertCard("have");
        } else if (btn_want_pressed || btn_want_4_pressed) {
            insertCard("want");
        }

    }

    public void insertCard(String tableName) {
        utils     = new UtilsHelper();
        dbHelper  = DatabaseHelper.getInstance(this.getContext());
        int       id_edition;
        int       quantity;
        Card      existingCard = new Card();

        // Get edition info
        selectedEdition = utils.padronizeEdition(selectedEdition);
        edition         = dbHelper.getSingleEdition(this.getContext(), selectedEdition);
        id_edition      = edition.getId();

        if (id_edition > 0) {
            // If the card already exists update its quantity, else insert a new record
            if (tableName.equalsIgnoreCase("have")) {
                existingCard = dbHelper.checkIfHaveCardExists(utils.padronizeForSQL(cardNameEN.text()), id_edition, foil);
            } else if (tableName.equalsIgnoreCase("want")){
                existingCard = dbHelper.checkIfWantCardExists(utils.padronizeForSQL(cardNameEN.text()), id_edition, foil);
            }

            if (existingCard.getQuantity() > 0) {
                if (btn_have_4_pressed || btn_want_4_pressed) {
                    quantity = existingCard.getQuantity() + 4;
                } else {
                    quantity = existingCard.getQuantity() + 1;
                }

                if (tableName.equalsIgnoreCase("have")) {
                    dbHelper.updateCardQuantity("have", existingCard.getId(), quantity);
                } else if (tableName.equalsIgnoreCase("want")) {
                    dbHelper.updateCardQuantity("want", existingCard.getId(), quantity);
                }

            } else {
                Card newCard = new Card();
                newCard.setName_pt(cardNamePT.text());
                newCard.setName_en(cardNameEN.text());
                newCard.setId_edition(edition.getId());
                newCard.setFoil(foil);

                if (btn_have_4_pressed || btn_want_4_pressed) {
                    quantity = 4;
                    newCard.setQuantity(quantity);
                } else {
                    quantity = 1;
                    newCard.setQuantity(quantity);
                }

                if (tableName.equalsIgnoreCase("have")) {
                    dbHelper.insertHaveCard(newCard);
                } else if (tableName.equalsIgnoreCase("want")) {
                    dbHelper.insertWantCard(newCard);
                }
            }

            if (dbHelper.card_id != 0) {
                if (btn_have_4_pressed || btn_want_4_pressed) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.four_cards_inserted, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.one_card_inserted, Toast.LENGTH_SHORT).show();
                }

                // Refresh the contents of FragmentHave
                if (tableName.equalsIgnoreCase("have")) {
                    FragmentHave fragmentHave = new FragmentHave();
                    fragmentHave.refreshRecyclerView(true);
                } else if (tableName.equalsIgnoreCase("want")) {
                    FragmentWant fragmentWant = new FragmentWant();
                    fragmentWant.refreshRecyclerView(true);
                }

            } else {
                Toast.makeText(getActivity().getApplicationContext(), R.string.insert_failed, Toast.LENGTH_LONG).show();
            }

            btn_have_pressed   = false;
            btn_have_4_pressed = false;
            btn_want_pressed   = false;
            btn_want_4_pressed = false;
            foil               = "N";
        }
    }
}
