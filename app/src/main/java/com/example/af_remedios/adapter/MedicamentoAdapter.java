package com.example.af_remedios.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.af_remedios.R;
import com.example.af_remedios.Medicamento;

import java.util.ArrayList;

public class MedicamentoAdapter extends RecyclerView.Adapter<MedicamentoAdapter.MedicamentoViewHolder> {

    private ArrayList<Medicamento> listaMedicamentos;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(Medicamento medicamento, int position);

        void onDeleteClick(Medicamento medicamento, int position);

        void onStatusClick(Medicamento medicamento, int position);
    }

    public MedicamentoAdapter(ArrayList<Medicamento> listaMedicamentos, OnItemClickListener listener) {
        this.listaMedicamentos = listaMedicamentos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MedicamentoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medicamento_item, parent, false);
        return new MedicamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicamentoViewHolder holder, int position) {
        Medicamento medicamento = listaMedicamentos.get(position);

        holder.txtNome.setText(medicamento.getNome());
        holder.txtDescricao.setText(medicamento.getDescricao());
        holder.txtHorario.setText(medicamento.getHorario());

        if (medicamento.isTomado()) {
            holder.imgStatus.setImageResource(android.R.drawable.checkbox_on_background);
        } else {
            holder.imgStatus.setImageResource(android.R.drawable.checkbox_off_background);
        }

        holder.imgStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStatusClick(medicamento, position);
            }
        });

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(medicamento, position);
            }
        });

        holder.btnExcluir.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(medicamento, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaMedicamentos.size();
    }

    public static class MedicamentoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgStatus;
        TextView txtNome, txtDescricao, txtHorario;
        ImageButton btnEditar, btnExcluir;

        public MedicamentoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStatus = itemView.findViewById(R.id.imgStatus);
            txtNome = itemView.findViewById(R.id.txtNomeMedicamento);
            txtDescricao = itemView.findViewById(R.id.txtDescricaoMedicamento);
            txtHorario = itemView.findViewById(R.id.txtHorarioMedicamento);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}
