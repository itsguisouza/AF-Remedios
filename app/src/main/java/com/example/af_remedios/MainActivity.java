package com.example.af_remedios;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.af_remedios.adapter.MedicamentoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicamentoAdapter adapter;
    private ArrayList<Medicamento> listaMedicamentos;
    private FloatingActionButton btnAdicionar;
    private LinearLayout emptyLayout;
    private FirebaseFirestore ControleMedicamentos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ControleMedicamentos = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerViewMedicamentos);
        btnAdicionar = findViewById(R.id.btnAdicionarMedicamento);
        emptyLayout = findViewById(R.id.emptyLayout);

        listaMedicamentos = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MedicamentoAdapter(listaMedicamentos, new MedicamentoAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Medicamento medicamento, int position) {
                editarMedicamento(medicamento);
            }

            @Override
            public void onDeleteClick(Medicamento medicamento, int position) {
                confirmarExclusao(medicamento);
            }

            @Override
            public void onStatusClick(Medicamento medicamento, int position) {
                alterarStatus(medicamento);
            }
        });

        recyclerView.setAdapter(adapter);

        btnAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroMedicamentoActivity.class);
            startActivity(intent);
        });

        carregarMedicamentos();

        Intent serviceIntent = new Intent(this, MedicamentoNotificationService.class);
        startService(serviceIntent);

    }
    private void carregarMedicamentos() {
        ControleMedicamentos.collection("medicamentos")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(MainActivity.this,
                                getString(R.string.erro_carregar, error.getMessage()),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    listaMedicamentos.clear();

                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Medicamento medicamento = doc.toObject(Medicamento.class);
                            medicamento.setId(doc.getId());
                            listaMedicamentos.add(medicamento);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    if (listaMedicamentos.isEmpty()) {
                        emptyLayout.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        emptyLayout.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void alterarStatus(Medicamento medicamento) {
        medicamento.setTomado(!medicamento.isTomado());

        ControleMedicamentos.collection("medicamentos")
                .document(medicamento.getId())
                .update("tomado", medicamento.isTomado())
                .addOnSuccessListener(aVoid -> {
                    String mensagem = medicamento.isTomado() ?
                            getString(R.string.sucesso_marcado_tomado) :
                            getString(R.string.sucesso_desmarcado);
                    Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao atualizar status", Toast.LENGTH_SHORT).show();
                });
    }

    private void editarMedicamento(Medicamento medicamento) {
        Intent intent = new Intent(MainActivity.this, CadastroMedicamentoActivity.class);
        intent.putExtra("medicamento_id", medicamento.getId());
        intent.putExtra("medicamento_nome", medicamento.getNome());
        intent.putExtra("medicamento_descricao", medicamento.getDescricao());
        intent.putExtra("medicamento_horario", medicamento.getHorario());
        intent.putExtra("medicamento_tomado", medicamento.isTomado());
        startActivity(intent);
    }

    private void confirmarExclusao(Medicamento medicamento) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar exclusão")
                .setMessage((getString(R.string.dialog_mensagem_exclusao, medicamento.getNome())) + "?")
                .setPositiveButton("Sim", (dialog, which) -> excluirMedicamento(medicamento))
                .setNegativeButton("Não", null)
                .show();
    }

    private void excluirMedicamento(Medicamento medicamento) {
        ControleMedicamentos.collection("medicamentos")
                .document(medicamento.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Medicamento excluído com sucesso", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao excluir medicamento", Toast.LENGTH_SHORT).show();
                });
    }
}