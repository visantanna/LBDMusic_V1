package com.rogacheski.lbd.lbdmusic.SearchActivities;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.rogacheski.lbd.lbdmusic.R;
import com.rogacheski.lbd.lbdmusic.Views.LoginActivity;
import com.rogacheski.lbd.lbdmusic.Views.ProfileContractorActivity;
import com.rogacheski.lbd.lbdmusic.adapter.HintAdapter;
import com.rogacheski.lbd.lbdmusic.base.baseActivity;
import com.rogacheski.lbd.lbdmusic.controllers.ControllerLocations;
import com.rogacheski.lbd.lbdmusic.controllers.RecyclerItemClickListener;
import com.rogacheski.lbd.lbdmusic.session.Session;
import com.rogacheski.lbd.lbdmusic.model.user;
import com.rogacheski.lbd.lbdmusic.singleton.PicassoSingleton;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;

public class SearchByLocationActivity extends baseActivity
        implements NavigationView.OnNavigationItemSelectedListener , PicassoSingleton.PicassoCallbacksInterface {

    private TextView mUsernameEdit;
    private ImageView mUserPicture;

    // Informações de usuário atual;
    private user userLogado;

    // Corpo principal da tela
    private RelativeLayout mainBodyRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_location);
        mainBodyRL = (RelativeLayout) findViewById(R.id.search_by_location);

        mContext = SearchByLocationActivity.this;
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

        jbInit();
    }

    private void jbInit() {

        Spinner spinnerPais = (Spinner)mainBodyRL.findViewById(R.id.spinnerCountry);
        Spinner spinnerEstado = (Spinner)mainBodyRL.findViewById(R.id.spinnerState);
        Spinner spinnerCidade = (Spinner)mainBodyRL.findViewById(R.id.spinnerCity);

        carregaSpinnerVazio(spinnerPais, getBaseContext().getString(R.string.Pais));
        carregaSpinnerVazio(spinnerEstado, getBaseContext().getString(R.string.Estado));
        carregaSpinnerVazio(spinnerCidade, getBaseContext().getString(R.string.Cidade));

        //cria Controllers
        final ControllerLocations controllerPaises = new ControllerLocations(getBaseContext() ,spinnerPais );
        final ControllerLocations controllerEstados = new ControllerLocations(getBaseContext(), spinnerEstado);
        final ControllerLocations controllerCidades = new ControllerLocations(getBaseContext(), spinnerCidade);

        Button Procurar = (Button) mainBodyRL.findViewById(R.id.ButtonSearch);
        Procurar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Spinner spinnerPais = (Spinner)mainBodyRL.findViewById(R.id.spinnerCountry);
                Spinner spinnerEstado = (Spinner)mainBodyRL.findViewById(R.id.spinnerState);
                Spinner spinnerCidade = (Spinner)mainBodyRL.findViewById(R.id.spinnerCity);
                String pais = spinnerPais.getSelectedItem().toString();
                if(!pais.equals(getBaseContext().getString(R.string.vazioSpinner)) && !pais.equals(getBaseContext().getString(R.string.Pais)) ) {
                    controllerCidades.buscarUsuarios((String) spinnerPais.getSelectedItem(), (String) spinnerEstado.getSelectedItem(), (String) spinnerCidade.getSelectedItem());
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext , R.style.AppTheme_Dark_Dialog);
                    try {
                        builder.setMessage("Selecione um pais!").
                                setNeutralButton(getBaseContext().getString(R.string.Ok)
                                        , new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }}).show();

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });


        spinnerPais.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                controllerEstados.carregaEstados((String)parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                controllerCidades.carregaCidades((String)parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        controllerPaises.carregaPaises();
    }

    private void carregaSpinnerVazio(Spinner spinner, String hint) {
        List listaVazia = Arrays.asList(getBaseContext().getString(R.string.vazioSpinner) , hint );
        HintAdapter<String> hintAdapter = new HintAdapter<String>(getBaseContext() , R.layout.simple_spinner_layout, listaVazia );
        spinner.setAdapter(hintAdapter);
        spinner.setTextAlignment(Spinner.TEXT_ALIGNMENT_CENTER);
        spinner.setSelection(hintAdapter.getCount());
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
        TextView contractorName = (TextView) findViewById(R.id.textViewSearchByLocation);
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

        ImageView contractorLogo = (ImageView) findViewById(R.id.imageViewSearchByLocation);

        String image = userLogado.getPicture();
        if(!(image.equals("") || image.equals("null"))){
            PicassoSingleton.getInstance(new WeakReference<>(mContext), new WeakReference<PicassoSingleton.PicassoCallbacksInterface>(SearchByLocationActivity.this))
                    .setProfilePictureAsync(contractorLogo, image , getDrawable(R.drawable.ic_account_circle_white));
        }
    }
}
