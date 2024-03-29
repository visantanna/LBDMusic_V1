package com.rogacheski.lbd.lbdmusic.SearchActivities;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rogacheski.lbd.lbdmusic.R;
import com.rogacheski.lbd.lbdmusic.Views.LoginActivity;
import com.rogacheski.lbd.lbdmusic.Views.ProfileContractorActivity;
import com.rogacheski.lbd.lbdmusic.Views.ProfileMusicianActivity;
import com.rogacheski.lbd.lbdmusic.adapter.SearchRecyclerAdapter;
import com.rogacheski.lbd.lbdmusic.base.baseActivity;
import com.rogacheski.lbd.lbdmusic.controllers.RecyclerItemClickListener;
import com.rogacheski.lbd.lbdmusic.entity.BandEntity;
import com.rogacheski.lbd.lbdmusic.session.Session;
import com.rogacheski.lbd.lbdmusic.model.user;
import com.rogacheski.lbd.lbdmusic.singleton.PicassoSingleton;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchByNameActivity extends baseActivity
        implements NavigationView.OnNavigationItemSelectedListener , PicassoSingleton.PicassoCallbacksInterface {

    @Bind(R.id.input_search_by_name) EditText searchBar;

    private TextView mUsernameEdit;
    private ImageView mUserPicture;

    // Informações de usuário atual;
    private user userLogado;

    // Corpo principal da tela
    private RelativeLayout mainBodyRL;


    /** Variaveis relacionadas ao RecyclerView*/

    private View view;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private SearchRecyclerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_name);

        mContext = SearchByNameActivity.this;
        session = new Session(mContext);

        Intent intent = getIntent();
        userLogado = (user) intent.getSerializableExtra("com.rogacheski.lbd.lbdmusic.USER");

        /** Set contractor's name and image*/
        loadProfileBar();

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);
        /*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        mUsernameEdit = (TextView)header.findViewById(R.id.username);
        mUserPicture = (ImageView)header.findViewById(R.id.drawer_profilePicture);

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);
        ActivityManager.TaskDescription tDesk = new ActivityManager.TaskDescription(getString(R.string.app_name),bm,getResources().getColor(R.color.colorPrimaryDark));
        this.setTaskDescription(tDesk);
        getWindow().setBackgroundDrawableResource(R.color.windowColor);


        //BandEntity banda = (BandEntity) Coisa;

        TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after){

            }

            public void onTextChanged(CharSequence s, int start, int before, int count){

            }

            public void afterTextChanged(Editable s)
            {
                atualizarLista(searchBar.getText().toString().trim());
            }
        };

        searchBar.addTextChangedListener(mTextEditorWatcher);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            //TransitionLeft(MainActivity.class);
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            callProfile();
        } else if (id == R.id.nav_logout) {
            session.setusename("");
            TransitionLeft(LoginActivity.class);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void callProfile(){
        if(userLogado.getType() == "Musician") {

        } else {
            TransitionRight(ProfileContractorActivity.class);
        }
    }

    public void chamarProfile(View view){
        callProfile();
    }

    @Override
    public void onPicassoSuccessCallback() {
    }

    @Override
    public void onPicassoErrorCallback() {
        Log.e("TAG", "error");
    }

    private void loadProfileBar(){

        /** Name */
        TextView contractorName = (TextView) findViewById(R.id.textViewSearchByName);
        String name = userLogado.getFantasyName();
        int maxSize = 15;

        // Se o nome for muito grande, corte o nome
        // WayToLongOfAName --> WayToLongO...
        if(name.length() > maxSize){
            name = name.substring(0, maxSize-3);
            name = name + "...";
        }

        contractorName.setText(name);

        /** Logo */

        ImageView contractorLogo = (ImageView) findViewById(R.id.imageViewSearchByName);

        String image = userLogado.getPicture();
        if(!(image.equals("") || image.equals("null"))){
            PicassoSingleton.getInstance(new WeakReference<>(mContext), new WeakReference<PicassoSingleton.PicassoCallbacksInterface>(SearchByNameActivity.this))
                    .setProfilePictureAsync(contractorLogo, image , getDrawable(R.drawable.ic_account_circle_white));
        }
    }

    private void updateCardView(List<BandEntity> resultados) {

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewSearchByname);
        mLinearLayoutManager = new LinearLayoutManager(SearchByNameActivity.this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new SearchRecyclerAdapter(resultados);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        int idUsuario = mAdapter.getSearchResult().get(position).getIdUsuario();
                        String idUser = Integer.toString(idUsuario);
                        TransitionRightExtraId(ProfileMusicianActivity.class , "id" , idUser);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }


    private void atualizarLista(String input){
        final String termoDePesquisa = input;

        //ShowCustomDialog("Searching");
        AsyncHttpClient searchRequest = new AsyncHttpClient();
        searchRequest.get("http://www.lbd.bravioseguros.com.br/musicianrest/search/" + input, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                try {
                    // Ler do conteúdo "data" da resposta
                    JSONArray dataJson = (JSONArray) response.get("data");

                    if(dataJson.length() == 0) {
                        BandEntity resultados[] = new BandEntity[0];
                        updateCardView(Arrays.asList(resultados));
                        return;
                        //DismissDialog();
                        //WriteMessage("Não encontramos nenhum músico ou banda com o nome \"" + termoDePesquisa + "\"", "long");
                    }

                    int numberOfResults = dataJson.length();

                    BandEntity resultados[] = new BandEntity[numberOfResults];
                    //band resultados[] = new band [numberOfResults];

                    // Para cada valor encontrado
                    for(int i=0; i<numberOfResults; i++){
                        resultados[i] = new BandEntity();
                        //resultados[i] = new band();
                        JSONObject atual = dataJson.getJSONObject(i);

                        String idUsuarioString = atual.get("idUsuario").toString();
                        if(!idUsuarioString.equals("") || idUsuarioString.equals("null")){
                            resultados[i].setIdUsuario(Integer.parseInt(idUsuarioString));
                        }
                        String idAddressString = atual.get("idAddress").toString();
                        if(!(idAddressString.equals("") || idAddressString.equals("null"))){
                            resultados[i].setIdAddress(Integer.parseInt(idAddressString));
                        }
                        resultados[i].setsNomeBanda(atual.get("fantasyName").toString());
                        resultados[i].setFname(atual.get("firstName").toString());
                        resultados[i].setLname(atual.get("lastName").toString());
                        resultados[i].setdImagemBanda(atual.get("profileImage").toString());
                        resultados[i].setdImagemDescBanda(atual.get("backpicture").toString());
                        //resultados[i].setAverageRating(atual.get("").);

                    }
                    //DismissDialog();

                    updateCardView(Arrays.asList(resultados));

                } catch (JSONException e) {
                    //DismissDialog();
                    e.printStackTrace();
                    // DismissDialog();
                    //TransitionLeft(LoginActivity.class);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                DismissDialog();
                //WriteMessage("Não encontramos nenhum músico ou banda com o nome \"" + termoDePesquisa + "\"", "long");
                //TransitionLeft(MainActivity.class);
            }

            @Override
            public void onFinish() {

            }
        });
    }


    public static class SearchByNameActivity$$ViewBinder<T extends SearchByNameActivity> implements ButterKnife.ViewBinder<T> {
      @Override public void bind(final ButterKnife.Finder finder, final T target, Object source) {
        View view;
        view = finder.findRequiredView(source, 2131558630, "field 'searchBar'");
        target.searchBar = finder.castView(view, 2131558630, "field 'searchBar'");
      }

      @Override public void unbind(T target) {
        target.searchBar = null;
      }
    }
}
