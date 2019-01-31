package grupocriar.ntalk.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.com.grupocriar.ntalk.model.Interlocutor;
import br.com.grupocriar.ntalk.model.Mensagem;
import br.com.grupocriar.ntalk.model.Usuario;
import grupocriar.ntalk.activity.listeners.TouchListenerMensagens;
import grupocriar.ntalk.controllers.NtalkService;
import grupocriar.ntalk.utils.LocalApplication;
import grupocriar.ntalk.R;
import grupocriar.ntalk.adapters.AdapterConversasContatos;
import grupocriar.ntalk.adapters.objetos.ObAdapterContatos;

import grupocriar.ntalk.persistence.DataBase;
import grupocriar.ntalk.persistence.UserDataBase;
import grupocriar.ntalk.persistence.dao.DAOInterlocutor;
import grupocriar.ntalk.persistence.dao.DAOMensagem;

public class ConversasContatosActivity extends AppCompatActivity
        implements View.OnClickListener, TouchListenerMensagens.ClickListener,
        SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {

    public static final String TAG_CONVERSAS_CONTATOS = "conversas.contatos";
    public static final int REQUEST_CODE = 20;

    private RecyclerView mRecyclerView;
    private volatile Interlocutor usuarioCelular;
    private Interlocutor destinatarioContato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversasActivity.telaAtivaMensagensActivity = false; // aqui esta indicando que a tela ConversasActivity nao esta visival


        Intent it = new Intent(getApplicationContext(), NtalkService.class);
        startService(it);

        if (savedInstanceState == null) {
            buscaMensagensServidor();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_conversas_contatos, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem menuItem = menu.findItem(R.id.action_search_contatos_conversas);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarRecicleView();
        refreshRecycler();
        buscaMensagensServidor();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConversasActivity.telaAtivaMensagensActivity = false;//indicando que a tela ConversasContatosActivity nao esta visivel
    }

    private void carregarRecicleView() {
        setContentView(R.layout.activity_conversas_contatos);

        Toolbar toolbar = findViewById(R.id.toolbar_contato_mensagem);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        mRecyclerView = findViewById(R.id.recycler_contents_contatos_mensagem);
        mRecyclerView.addOnItemTouchListener(new TouchListenerMensagens(getApplicationContext(), mRecyclerView, this));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setHasFixedSize(true);

        FloatingActionButton floatingActionButton = findViewById(R.id.btnEnviar);
        floatingActionButton.setOnClickListener(this);
        try {
            //Recuperando o interlocutor local do banco de definindo como usuario no LocalApplication
            Set<Interlocutor> myDatasetLocalInterlocutores = new DAOInterlocutor(
                    new DataBase(new UserDataBase(getApplicationContext()))).selectAll();//**
            usuarioCelular = getInterlocutorLocal(myDatasetLocalInterlocutores);
            LocalApplication.getInstance().setUsuarioCelular(usuarioCelular);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static volatile boolean novaMensagem;
    private static volatile boolean threadRunning = false;

    private void buscaMensagensServidor() {
        if (threadRunning) return;
        threadRunning = true;
        new Thread(() -> {
            while (threadRunning) {
                try {
                    runOnUiThread(this::refreshRecycler);
                    Thread.sleep(500);
                } catch (Throwable e) {
                }
            }
        }).start();
    }

    private List<ObAdapterContatos> listContatosMensagens;
    private RecyclerView.Adapter<AdapterConversasContatos.ViewHolder> mAdapter;

    private void refreshRecycler() {// esse metodo faz a atualizando quando chega uma mensagem nova na tela ConversasContatosActivity
        Log.i("TAG_CONVERSA_CONTATOS", "loading...");
        try {
            if (usuarioCelular != null) {
                listContatosMensagens = new ArrayList<>();
                destinatarioContato = LocalApplication.getInstance().getDestinoContato();

                List<Mensagem> mensagens = new ArrayList<>(new DAOMensagem(new DataBase(
                        new UserDataBase(getApplicationContext()))).selectAll());//**

                for (Mensagem m : mensagens) {
                    Interlocutor interlocutor;
                    if (m.getRemetente().getIdInterlocutor() == LocalApplication.getInstance().getUsuarioCelular().getIdInterlocutor()) {
                        interlocutor = m.getDestinatario();
                    } else {
                        interlocutor = m.getRemetente();
                    }
                    boolean adicionar = true;
                    for (ObAdapterContatos o : listContatosMensagens) {
                        if (o.getIdInterlocutor() == interlocutor.getIdInterlocutor()) {
                            adicionar = false;
                            break;
                        }
                    }
                    if (adicionar)
                        listContatosMensagens.add(
                                new ObAdapterContatos(
                                        interlocutor.getIdInterlocutor(),
                                        m,
                                        interlocutor.getNome(), interlocutor));
                }
                mAdapter = new AdapterConversasContatos(getApplicationContext(), listContatosMensagens);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.setAdapter(mAdapter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pesquisaContatos(String nomeContato) {
        if (null == nomeContato || nomeContato.trim().equals("")) {
            limpaPesquisaContatos();
            return;
        }
        List<ObAdapterContatos> interlocutors = new ArrayList<>(listContatosMensagens);

        for (int i = interlocutors.size() - 1; i >= 0; i--) {
            ObAdapterContatos interlocutor = interlocutors.get(i);
            if (!interlocutor.getNome().toUpperCase().contains(nomeContato.toUpperCase())) {
                interlocutors.remove(interlocutor);
                Log.i("TAG_INTERLOCUTOR", interlocutors.toString());
            }
        }

        mAdapter = new AdapterConversasContatos(getApplicationContext(), interlocutors);
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void limpaPesquisaContatos() {
        mAdapter = new AdapterConversasContatos(getApplicationContext(), listContatosMensagens);
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
    public void onClick(View v) {
        Intent intent = new Intent(this, ContatosActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null && resultCode == RESULT_OK) {
            destinatarioContato = (Interlocutor) data.getSerializableExtra(ContatosActivity.DESTINATARIO_CONTATO);
            Intent intetMensagemActivity = new Intent(getApplicationContext(), ConversasActivity.class);
            intetMensagemActivity.putExtra(ContatosActivity.DESTINATARIO_CONTATO, destinatarioContato);
            startActivity(intetMensagemActivity);
        }
    }

    @Override
    public void onClick(View view, int position) {
        Intent intetMensagemActivity = new Intent(getApplicationContext(), ConversasActivity.class);
        intetMensagemActivity.putExtra(ContatosActivity.DESTINATARIO_CONTATO, listContatosMensagens.get(position).getInterlocutor());
        startActivity(intetMensagemActivity);
    }

    @Override
    public void onLongClick(View view, int position) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String nomeContato) {//faz pesquisa do contato usando o menu toolbar search
        pesquisaContatos(nomeContato);
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        limpaPesquisaContatos();
        return false;
    }
}
