package br.com.transescolar.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;


import java.util.ArrayList;
import java.util.List;

import br.com.transescolar.Activies.EditRotaActivity;
import br.com.transescolar.Activies.InfRotaActivity;
import br.com.transescolar.Activies.PerfilActivity;
import br.com.transescolar.controler.RotaControler;
import br.com.transescolar.model.Rota;
import br.com.transescolar.Conexao.SessionManager;
import br.com.transescolar.R;


public class RotaAdapter extends RecyclerSwipeAdapter<RotaAdapter.MyViewHolder> {

    private List<Rota> contacts;
    private Context context;
    String getId;
    SessionManager sessionManager;
    RotaControler rotaControler;
    Rota objRota;

    public RotaAdapter(Context context, List<Rota> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    @Override
    public RotaAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.linha_rotas_list, parent, false);

        RotaAdapter.MyViewHolder rAdapter = new RotaAdapter.MyViewHolder(view);

        sessionManager = new SessionManager(context);
        rotaControler = new RotaControler();
        objRota = new Rota();

        return rAdapter;
    }

    @Override
    public void onBindViewHolder(final RotaAdapter.MyViewHolder holder, final int position) {
        final Rota rota = this.contacts.get(position);
        holder.txtNomeRota.setText(rota.getNm_rota());
        holder.txtDias.setText(rota.getDias());
        holder.txtHora.setText(rota.getHora());

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        //from the right
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wraper));

        holder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(RotaAdapter.this.context, InfRotaActivity.class);
                it.putExtra("rota", rota);
                RotaAdapter.this.context.startActivity(it);
            }
        });

        holder.Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(RotaAdapter.this.context, EditRotaActivity.class);
                it.putExtra("rota", rota);
                RotaAdapter.this.context.startActivity(it);
                }
            }
        );

        holder.Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mView = inflater.inflate(R.layout.dialog_text, null);
                final TextView nomeE = mView.findViewById(R.id.nomeD);
                nomeE.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                Button mSim = mView.findViewById(R.id.btnSim);
                Button mNao = mView.findViewById(R.id.btnNao);

                alertDialog.setView(mView);
                final AlertDialog dialog = alertDialog.create();

                nomeE.setText("Você deseja realmente deletar?");
                mSim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getId = rota.getId();
                        objRota.setId(getId);
                        rotaControler.deleteRota(objRota, context);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("data_changed"));

//                        mItemManger.removeShownLayouts(holder.swipeLayout);
//                        contacts.remove(position);
//                        notifyItemRemoved(position);
//                        notifyItemRangeChanged(position, contacts.size());
//                        mItemManger.closeAllItems();
                        dialog.dismiss();

                    }
                });
                mNao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        mItemManger.bindView(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        //return contacts == null ? 0 : contacts.size();
        return this.contacts.size();
    }

    public Object getItem(int i) {
        return this.contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setDataObjectList(List<Rota> dataObjectList) {
        this.contacts= dataObjectList;
        notifyDataSetChanged();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private final Context context;
        TextView txtHora, txtNomeRota, txtDias, Delete, Edit;
        static SwipeLayout swipeLayout;


        public MyViewHolder(View itemView) {
            super(itemView);

            context = itemView.getContext();
            txtNomeRota = itemView.findViewById(R.id.txtNomeRota);
            txtDias = itemView.findViewById(R.id.txtDias);
            txtHora = itemView.findViewById(R.id.txtHora);
            Delete = itemView.findViewById(R.id.Delete);
            Edit = itemView.findViewById(R.id.Edit);
            swipeLayout = itemView.findViewById(R.id.swipe);

        }
    }

}
