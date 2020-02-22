package br.com.transescolar.Adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import br.com.transescolar.model.Escolas;
import br.com.transescolar.R;

public class EscolaSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    List<Escolas> data= Collections.emptyList();
    Escolas current;

    // create constructor to initialize context and data sent from MainActivity
    public EscolaSearchAdapter(Context context, List<Escolas> data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data=data;
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.linha_escola_list, parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in RecyclerView to bind data and assign values from list
        MyHolder myHolder= (MyHolder) holder;
        Escolas current=data.get(position);
        myHolder.nomeText.setText(current.getNome());
        myHolder.endeText.setText(current.getEndereco());
        myHolder.tellText.setText(current.getTell());

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView nomeText;
        TextView endeText;
        TextView tellText;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            nomeText= itemView.findViewById(R.id.nomeText);
            endeText = itemView.findViewById(R.id.endeText);
            tellText = itemView.findViewById(R.id.tellText);
            itemView.setOnClickListener(this);
        }

        // Click event for all items
        @Override
        public void onClick(View v) {
            Toast.makeText(context, "You clicked an item", Toast.LENGTH_SHORT).show();
        }
    }
}
