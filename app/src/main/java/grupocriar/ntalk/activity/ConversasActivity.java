package grupocriar.ntalk.activity;


import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.grupocriar.ntalk.model.Interlocutor;
import br.com.grupocriar.ntalk.model.Mensagem;
import grupocriar.ntalk.utils.LocalApplication;
import grupocriar.ntalk.R;

import grupocriar.ntalk.adapters.AdapteConversas;
import grupocriar.ntalk.persistence.DataBase;
import grupocriar.ntalk.persistence.UserDataBase;
import grupocriar.ntalk.persistence.dao.DAOMensagem;


public class ConversasActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "tag";
    public static String PENDING_INTENT_MENSAGEM = "pending.intent.mensagem";
    private FloatingActionButton btnEnviaMensagem;
    private ArrayAdapter<Mensagem> adapter;
    private ListView listViewMensagens;
    private ArrayList<Mensagem> listMensagens;
    private Interlocutor usuarioCelular, destinoContato;
    private EditText txtMensagem;

    public static volatile boolean telaAtivaMensagensActivity = true;//flag para sinalizar se a tela esta tiva

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_mensagem);
        loadViewComponents();
    }


    private void loadViewComponents() {
        Toolbar toolbar = findViewById(R.id.toolbar_mensagens);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        txtMensagem = findViewById(R.id.txtMensagem);
        btnEnviaMensagem = findViewById(R.id.btnEnviar);
        btnEnviaMensagem.setOnClickListener(this);
        listViewMensagens = findViewById(R.id.lv_conversas);
        listMensagens = new ArrayList<>();
        listViewMensagens.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        adapter = new AdapteConversas(ConversasActivity.this, listMensagens);
        adapter.notifyDataSetChanged();
        listViewMensagens.setAdapter(adapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        telaAtivaMensagensActivity = true;
        carregarAdapter();
        autoUpdate();
    }

    private static volatile boolean threadRunning = false;

    private void autoUpdate() {//faz atualizacao das conversas na tela
        if (threadRunning) return;
        threadRunning = true;
        new Thread(() -> {
            while (threadRunning) {
                if (ConversasContatosActivity.novaMensagem) {
                    ConversasContatosActivity.novaMensagem = false;
                    runOnUiThread(this::carregarAdapter);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void carregarAdapter() {
        try {
            usuarioCelular = LocalApplication.getInstance().getUsuarioCelular();

            TextView nomeContato = findViewById(R.id.id_nome_contato_toolbar);
            destinoContato = (Interlocutor) getIntent().getSerializableExtra(ContatosActivity.DESTINATARIO_CONTATO);
            Mensagem pendingMensagemItent = (Mensagem) getIntent().getSerializableExtra(PENDING_INTENT_MENSAGEM);
            if (destinoContato == null) {//seleciono a origem do contato se vem da activityconatatos ou do pending_intent.
                destinoContato = LocalApplication.getInstance().getDestinoContato();
                if (destinoContato.getNome() == null) {
                    destinoContato = pendingMensagemItent.getRemetente();
                }
            }
            nomeContato.setText(destinoContato.getNome());
            List<Mensagem> mensagems = new DAOMensagem(new DataBase(
                    new UserDataBase(getApplicationContext()))).select(destinoContato.getIdInterlocutor());//**
            listMensagens.addAll(mensagems);
            listViewMensagens.setSelection(listViewMensagens.getAdapter().getCount() - 1);
            if (adapter != null)
                adapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        telaAtivaMensagensActivity = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        telaAtivaMensagensActivity = false;
    }

    private Mensagem mensagens;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEnviar:
                String mensagemTexto = txtMensagem.getText().toString();
                if (mensagemTexto.length() != 0) {
                    Mensagem allMsgOf = new Mensagem(usuarioCelular, destinoContato)
                            .setConteudo(mensagemTexto)
                            .setRemetente(usuarioCelular)
                            .setDestinatario(destinoContato)
                            .setDataHoraMensagem(Calendar.getInstance().getTime());//pegando a hora atual da mensagem
                    listMensagens.add(allMsgOf);
                    adapter.notifyDataSetChanged();
                    listViewMensagens.setSelection(listViewMensagens.getAdapter().getCount() - 1);
                    new DAOMensagem(new DataBase(new UserDataBase(getApplicationContext()))).insert(allMsgOf);//**
                    try {
                        LocalApplication.getInstance().setDestinoContato(destinoContato);
                        LocalApplication.getInstance().setUsuarioCelular(usuarioCelular);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        txtMensagem.setText("");
    }


    @Override
    protected void onDestroy() {
        threadRunning = false;
        super.onDestroy();
    }
}
