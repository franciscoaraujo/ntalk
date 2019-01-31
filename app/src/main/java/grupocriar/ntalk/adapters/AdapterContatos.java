package grupocriar.ntalk.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.grupocriar.ntalk.model.Interlocutor;
import grupocriar.ntalk.R;

/**
 * Created by francisco on 11/16/17.
 */

public class AdapterContatos extends RecyclerView.Adapter<AdapterContatos.ViewHolder> {

    private List<Interlocutor> listMensagem;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeTextView;
        TextView ultimaMensagem;

        public ViewHolder(View itemView) {
            super(itemView);
            nomeTextView = itemView.findViewById(R.id.nomeUsuario);
        }
    }

    public AdapterContatos(Context context, List<Interlocutor> listMensagem) {
        this.listMensagem = listMensagem;
        this.context = context;
    }


    @Override
    public AdapterContatos.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.itens_contatos, parent, false);
        AdapterContatos.ViewHolder vh = new AdapterContatos.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(AdapterContatos.ViewHolder holder, int position) {
        List<Interlocutor> listInterlocutor = new ArrayList<>(listMensagem);
        holder.nomeTextView.setText(listInterlocutor.get(position).getNome());
    }

    @Override
    public int getItemCount() {
        return listMensagem.size();
    }
}




