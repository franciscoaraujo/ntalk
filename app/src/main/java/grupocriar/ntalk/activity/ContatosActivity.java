package grupocriar.ntalk.activity;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import br.com.grupocriar.ntalk.model.Interlocutor;
import br.com.grupocriar.ntalk.model.Retorno;
import br.com.grupocriar.ntalk.model.Token;
import br.com.grupocriar.ntalk.model.Usuario;
import grupocriar.ntalk.activity.listeners.TouchListenerMensagens;
import grupocriar.ntalk.utils.LocalApplication;
import grupocriar.ntalk.R;
import grupocriar.ntalk.adapters.AdapterContatos;
import grupocriar.ntalk.controllers.ConnectLogin;
import grupocriar.ntalk.controllers.ControllerContatos;


/**
 * Created by francisco on 11/13/17.
 */

public class ContatosActivity extends AppCompatActivity implements TouchListenerMensagens.ClickListener,
        SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {


    public static final String DESTINATARIO_CONTATO = "destinatario.contato";

    private RecyclerView.Adapter<AdapterContatos.ViewHolder> mAdapter;
    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarbHandler = new Handler();
    private long fileSize = 0;
    private boolean status;

    private volatile Set<Interlocutor> myDatasetLocalInterlocutores;
    private List<Interlocutor> listInterlocutores;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contatos_activity);

        Toolbar toolbar = findViewById(R.id.toolbar_contato);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        LocalApplication localApplication = LocalApplication.getInstance();
        if (!localApplication.checkNetworkConnection()) {
            Toast.makeText(getApplicationContext(), "Sem conexao para realizar operacao", Toast.LENGTH_SHORT).show();
            return;
        }
        ConversasActivity.telaAtivaMensagensActivity = true;

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage("Carregando contatos...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;
        fileSize = 0;
        status = true;
        try {
            Usuario usuario = LocalApplication.getInstance().getUsuario();

            new Thread(() -> {
                ControllerContatos contatos = new ControllerContatos(getApplicationContext());
                if (contatos.getContatosBancoLocal() != null) {
                    listInterlocutores = contatos.getContatosBancoLocal();
                } else {
                    ConnectLogin connectLogin = new ConnectLogin(usuario);
                    Retorno<Token> token;
                    Retorno<List<Interlocutor>> contatosServidor;
                    try {
                        token = connectLogin.getToken();
                        contatosServidor = contatos.getContatosServidor(token.getObjeto());
                        listInterlocutores = contatosServidor.getObjeto();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(listInterlocutores, (that, other) ->
                        String.CASE_INSENSITIVE_ORDER.compare(that.getNome(), other.getNome()));
                myDatasetLocalInterlocutores = new LinkedHashSet<>(listInterlocutores);
                runOnUiThread(this::atualizaContatos);
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_contatos, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    private void atualizaContatos() {
        mRecyclerView = findViewById(R.id.recycler_contents_contatos);
        mRecyclerView.setHasFixedSize(true);

        try {
            Interlocutor interlocutorLocalRemetente = getInterlocutorLocal(myDatasetLocalInterlocutores);
            LocalApplication.getInstance().setUsuarioCelular(interlocutorLocalRemetente);
            myDatasetLocalInterlocutores.remove(interlocutorLocalRemetente);//retiro da lista o usuario local

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new AdapterContatos(getBaseContext(), new ArrayList<>(myDatasetLocalInterlocutores));
            mRecyclerView.setAdapter(mAdapter);
            progressBar.dismiss();
            mRecyclerView.addOnItemTouchListener(new TouchListenerMensagens(getApplicationContext(), mRecyclerView, this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<Interlocutor> dataSetBuscaItnerlocutor;


    private void buscaContatos(String nomeContato) {
        if (null == nomeContato || nomeContato.trim().equals("")) {
            limparBuscaContato();
            return;
        }

        dataSetBuscaItnerlocutor = new ArrayList<>(myDatasetLocalInterlocutores);
        for (int i = dataSetBuscaItnerlocutor.size() - 1; i >= 0; i--) {
            Interlocutor interlocutor = dataSetBuscaItnerlocutor.get(i);
            if (!interlocutor.getNome().toUpperCase().contains(nomeContato.toUpperCase())) {
                dataSetBuscaItnerlocutor.remove(interlocutor);
            }
        }
        mAdapter = new AdapterContatos(getBaseContext(), dataSetBuscaItnerlocutor);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void limparBuscaContato() {
        mAdapter = new AdapterContatos(getBaseContext(), new ArrayList<>(myDatasetLocalInterlocutores));
        mRecyclerView.setAdapter(mAdapter);
    }


    @Nullable
    private Interlocutor getInterlocutorLocal(Set<Interlocutor> myDatasetInterlocutores) throws IOException {
        final Usuario usuario = LocalApplication.getInstance().getUsuario();
        Iterator<Interlocutor> it = myDatasetInterlocutores.iterator();
        while (it.hasNext()) {
            Interlocutor interlocutor = it.next();
            if (interlocutor.getUsuarioNucleo().contains(usuario.getUsuario()))
                return interlocutor;
        }
        return null;
    }

    @Override
    public void onClick(View view, int position) {
        ArrayList<Interlocutor> listMensagem;
        if (dataSetBuscaItnerlocutor != null) {
            listMensagem = new ArrayList<>(dataSetBuscaItnerlocutor);
        } else {
            listMensagem = new ArrayList<>(myDatasetLocalInterlocutores);
        }
        Interlocutor destinatario = listMensagem.get(position);
        Intent intetMensagemActivity = new Intent();
        intetMensagemActivity.putExtra(DESTINATARIO_CONTATO, destinatario);
        setResult(RESULT_OK, intetMensagemActivity);
        finish();
    }

    @Override
    public void onLongClick(View view, int position) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        //ConversasActivity.active = false;
        ConversasActivity.telaAtivaMensagensActivity = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConversasActivity.telaAtivaMensagensActivity = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConversasActivity.telaAtivaMensagensActivity = false;
    }

    private void progressBar() {
        while (status) {
            progressBarStatus = 100;
            progressBarbHandler.post(() -> progressBar.setProgress(progressBarStatus));
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String nomeContato) {
        //TODO CRIAR UM METODO DE BUSCA NA LISTA DE CONTATOS
        buscaContatos(nomeContato);
        return false;

    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        //TODO METODO PARA LIMPAR A BUSCA
        limparBuscaContato();
        return false;
    }
}
