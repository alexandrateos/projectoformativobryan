package jennifer.teos.proyectoformativojenni

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import java.util.UUID

class crear_paciente_medicamento : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_paciente_medicamento)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnListadoDos = findViewById<ImageButton>(R.id.btnListadoDos)
        val btnAgregarPaciente = findViewById<Button>(R.id.btnAgregarPaciente)
        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtApellido = findViewById<EditText>(R.id.txtApellido)
        val txtEdad = findViewById<EditText>(R.id.txtEdad)
        val txtEnfermedad = findViewById<EditText>(R.id.txtEnfermedad)
        val txtNumeroHabitacion = findViewById<EditText>(R.id.txtNumeroHabitacion)
        val txtNumeroCama = findViewById<EditText>(R.id.txtNumeroCama)
        val txtMedicamentoAsignado = findViewById<EditText>(R.id.txtMedicamentoAsignado)
        val txtHoraAplicacion = findViewById<EditText>(R.id.txtHoraAplicacion)

        btnListadoDos.setOnClickListener {
            val pantallaMain = Intent(this, MainActivity::class.java)
            startActivity(pantallaMain)
        }

        btnAgregarPaciente.setOnClickListener {
            val nombre = txtNombre.text.toString()
            val apellido = txtApellido.text.toString()
            val edad = txtEdad.text.toString()
            val enfermedad = txtEnfermedad.text.toString()
            val numeroHabitacion = txtNumeroHabitacion.text.toString()
            val numeroCama = txtNumeroCama.text.toString()
            val medicamentoAsignado = txtMedicamentoAsignado.text.toString()
            val horaAplicacion = txtHoraAplicacion.text.toString()

            GlobalScope.launch(Dispatchers.IO) {
                val objConexion = ClaseConexion().cadenaConexion()

                val uuidPaciente = UUID.randomUUID().toString()

                val insertarPaciente = objConexion?.prepareStatement(
                    "INSERT INTO Pacientes (UUID_paciente, nombre, apellido, edad, enfermedad, numeroHabitacion, numeroCama) VALUES (?,?,?,?,?,?,?)"
                )
                insertarPaciente?.setString(1, uuidPaciente)
                insertarPaciente?.setString(2, nombre)
                insertarPaciente?.setString(3, apellido)
                insertarPaciente?.setString(4, edad)
                insertarPaciente?.setString(5, enfermedad)
                insertarPaciente?.setString(6, numeroHabitacion)
                insertarPaciente?.setString(7, numeroCama)
                insertarPaciente?.executeUpdate()

                val uuidMedicamento = UUID.randomUUID().toString()

                val insertarMedicamento = objConexion?.prepareStatement(
                    "INSERT INTO Medicamentos (UUID_medicamentos, medicamentosAsignados) VALUES (?,?)"
                )
                insertarMedicamento?.setString(1, uuidMedicamento)
                insertarMedicamento?.setString(2, medicamentoAsignado)
                insertarMedicamento?.executeUpdate()

                val insertarPacienteMedicamento = objConexion?.prepareStatement(
                    "INSERT INTO PacienteMedicamento (UUID_paciente, UUID_medicamentos, horaAplicacion) VALUES (?,?,?)"
                )
                insertarPacienteMedicamento?.setString(1, uuidPaciente)
                insertarPacienteMedicamento?.setString(2, uuidMedicamento)
                insertarPacienteMedicamento?.setString(3, horaAplicacion)
                insertarPacienteMedicamento?.executeUpdate()

                withContext(Dispatchers.Main) {
                    val builder = AlertDialog.Builder(this@crear_paciente_medicamento)
                    builder.setTitle("Éxito")
                    builder.setMessage("Los datos del paciente se agregaron correctamente.")
                    builder.setPositiveButton("Aceptar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val dialog = builder.create()
                    dialog.show()

                    txtNombre.text.clear()
                    txtApellido.text.clear()
                    txtEdad.text.clear()
                    txtEnfermedad.text.clear()
                    txtNumeroHabitacion.text.clear()
                    txtNumeroCama.text.clear()
                    txtMedicamentoAsignado.text.clear()
                    txtHoraAplicacion.text.clear()
                }
            }
        }
    }
}

