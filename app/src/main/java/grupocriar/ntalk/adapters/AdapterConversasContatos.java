package grupocriar.ntalk.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import grupocriar.ntalk.R;
import grupocriar.ntalk.adapters.objetos.ObAdapterContatos;

/**
 * Created by francisco on 21/12/2017.
 */

public class AdapterConversasContatos extends RecyclerView.Adapter<AdapterConversasContatos.ViewHolder> {

    private List<ObAdapterContatos> listaMensagem;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nomeTextView;
        private ImageView imageView;
        private TextView ultimaMensagem;
        private TextView countMensagem;
        private TextView horaMensagem;

        public ViewHolder(View itemView) {
            super(itemView);
            nomeTextView = itemView.findViewById(R.id.nomeUsuario);
            imageView = itemView.findViewById(R.id.idImagem);
            ultimaMensagem = itemView.findViewById(R.id.ultimaMensagem);
            //  countMensagem = itemView.findViewById(R.id.count_mensagem);
            horaMensagem = itemView.findViewById(R.id.hora_mensagem);
        }
    }

    public AdapterConversasContatos(Context context, List<ObAdapterContatos> listMensagem) {
        this.listaMensagem = listMensagem;
        this.context = context;
    }

    @Override
    public AdapterConversasContatos.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itens_mensagens_contatos, parent, false);
        AdapterConversasContatos.ViewHolder vh = new AdapterConversasContatos.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(AdapterConversasContatos.ViewHolder holder, int position) {
        ObAdapterContatos obAdapterContatos = listaMensagem.get(position);
        String conteudo = obAdapterContatos.getMensagem().getConteudo();
        holder.nomeTextView.setText(obAdapterContatos.getNome());
        holder.ultimaMensagem.setText(conteudo);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        holder.horaMensagem.setText(sdf.format(obAdapterContatos.getMensagem().getDataHoraMensagem()));
    }

    @Override
    public int getItemCount() {
        if (listaMensagem != null) {
            return listaMensagem.size();
        } else {
            return new ArrayList<>().size();
        }
    }


}
