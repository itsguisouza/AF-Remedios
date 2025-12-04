package com.example.af_remedios;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CadastroMedicamentoActivity extends AppCompatActivity {

    private TextInputEditText edtNome, edtDescricao, edtHorario;
    private Button btnSalvar, btnCancelar;
    private TextView txtTitulo;
    private FirebaseFirestore ControleMedicamentos;
    private boolean isEdicao = false;
    private String medicamentoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_medicamento);

        ControleMedicamentos = FirebaseFirestore.getInstance();

        edtNome = findViewById(R.id.edtNome);
        edtDescricao = findViewById(R.id.edtDescricao);
        edtHorario = findViewById(R.id.edtHorario);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnCancelar = findViewById(R.id.btnCancelar);
        txtTitulo = findViewById(R.id.txtTitulo);

        if (getIntent().hasExtra("medicamento_id")) {
            isEdicao = true;
            medicamentoId = getIntent().getStringExtra("medicamento_id");
            txtTitulo.setText(R.string.titulo_editar);
            carregarDadosMedicamento();
        }

        btnSalvar.setOnClickListener(v -> salvarMedicamento());

        btnCancelar.setOnClickListener(v -> finish());

    }

    private void carregarDadosMedicamento() {
        String nome = getIntent().getStringExtra("medicamento_nome");
        String descricao = getIntent().getStringExtra("medicamento_descricao");
        String horario = getIntent().getStringExtra("medicamento_horario");

        edtNome.setText(nome);
        edtDescricao.setText(descricao);
        edtHorario.setText(horario);
    }

    private void salvarMedicamento() {
        String nome = edtNome.getText().toString().trim();
        String descricao = edtDescricao.getText().toString().trim();
        String horario = edtHorario.getText().toString().trim();

        if (nome.isEmpty()) {
            edtNome.setError(getString(R.string.erro_nome_vazio));
            edtNome.requestFocus();
            return;
        }

        if (descricao.isEmpty()) {
            edtDescricao.setError(getString(R.string.erro_descricao_vazia));
            edtDescricao.requestFocus();
            return;
        }

        if (horario.isEmpty()) {
            edtHorario.setError(getString(R.string.erro_horario_vazio));
            edtHorario.requestFocus();
            return;
        }

        if (isEdicao) {
            atualizarMedicamento(nome, descricao, horario);
        } else {
            criarNovoMedicamento(nome, descricao, horario);
        }
    }

    private void criarNovoMedicamento(String nome, String descricao, String horario) {
        Map<String, Object> medicamento = new HashMap<>();
        medicamento.put("nome", nome);
        medicamento.put("descricao", descricao);
        medicamento.put("horario", horario);
        medicamento.put("tomado", false);

        ControleMedicamentos.collection("medicamentos")
                .add(medicamento)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Medicamento cadastrado com sucesso!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, getString(R.string.erro_cadastrar, e.getMessage()),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void atualizarMedicamento(String nome, String descricao, String horario) {
        boolean tomado = getIntent().getBooleanExtra("medicamento_tomado", false);

        Map<String, Object> medicamento = new HashMap<>();
        medicamento.put("nome", nome);
        medicamento.put("descricao", descricao);
        medicamento.put("horario", horario);
        medicamento.put("tomado", tomado);

        ControleMedicamentos.collection("medicamentos")
                .document(medicamentoId)
                .set(medicamento)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Medicamento atualizado com sucesso!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, getString(R.string.erro_atualizar, e.getMessage()),
                            Toast.LENGTH_SHORT).show();
                });
    }
}