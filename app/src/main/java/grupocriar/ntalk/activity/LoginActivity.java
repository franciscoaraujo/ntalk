package grupocriar.ntalk.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import br.com.grupocriar.ntalk.model.Retorno;
import br.com.grupocriar.ntalk.model.Token;
import br.com.grupocriar.ntalk.model.Usuario;
import grupocriar.ntalk.controllers.NtalkBroadcastService;
import grupocriar.ntalk.controllers.NtalkService;
import grupocriar.ntalk.utils.LocalApplication;
import grupocriar.ntalk.R;
import grupocriar.ntalk.controllers.ConnectLogin;
import grupocriar.ntalk.utils.SecurityUtils;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PESSOA = 0;
    private static final int PRODUTO = 0;
    private static final int PROJETO = 0;

    private EditText emailTextEdit;
    private EditText senhaTextEdit;


    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarbHandler = new Handler();
    private long fileSize = 0;


    public boolean insert = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean primeiroAcesso = LocalApplication.getInstance().getPrimeiroAcesso();

        if (savedInstanceState == null) {
            if (!primeiroAcesso) {
                Intent intentMensagensContatos = new Intent(getApplicationContext(), ConversasContatosActivity.class);
                intentMensagensContatos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentMensagensContatos);
                return;
            }
            setContentView(R.layout.login_activity);
            loadViews();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadViews() {
        emailTextEdit = findViewById(R.id.idEmail);
        senhaTextEdit = findViewById(R.id.idSenha);
        Button btnEnviar = findViewById(R.id.btnLogin);
        btnEnviar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (emailTextEdit.getText().toString().equals("")
                || senhaTextEdit.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Preencha todos os campo", Toast.LENGTH_SHORT).show();
        } else {
            LocalApplication localApplication = LocalApplication.getInstance();
            if (!localApplication.checkNetworkConnection()) {
                Toast.makeText(getApplicationContext(), "Não Existe conexao disponivel", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar = new ProgressDialog(this);
            progressBar.setCancelable(true);
            progressBar.setMessage("Fazendo login..");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.show();
            progressBarStatus = 0;
            fileSize = 0;

            String userEmail = emailTextEdit.getText().toString();
            String passWord = SecurityUtils.geraMDFIVE(senhaTextEdit.getText().toString());

            Usuario usuario = new Usuario();
            usuario.setIdPessoa(PESSOA).setIdProduto(PRODUTO).setIdProjeto(PROJETO)
                    .setUsuario(userEmail + "@grupocriar")
                    .setSenha(passWord);
            new Thread(() -> {
                try {
                    ConnectLogin login = new ConnectLogin(usuario);
                    Retorno<Token> token = login.getToken();
                    if (token.getCodigo().equals("000")) {
                        //vou recuperar esse objeto na ActivityContatos ou onde for necessario*****
                        LocalApplication.getInstance().setUsuarioApplication(usuario);
                        Intent intentMensagensContatos = new Intent(getApplicationContext(), ConversasContatosActivity.class);
                        intentMensagensContatos.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        LocalApplication.getInstance().setPrimeiroAcesso(false);
                        startActivity(intentMensagensContatos);

                    } else {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                                "Usuário e Senha não confere", Toast.LENGTH_SHORT).show());
                    }
                    insert = false;
                    progressBar.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void loadProgressBar() {
        while (insert) {
            progressBarStatus = 100;
            progressBarbHandler.post(() -> progressBar.setProgress(progressBarStatus));
        }
    }

    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }


}


