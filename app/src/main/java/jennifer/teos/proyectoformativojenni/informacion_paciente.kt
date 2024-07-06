package jennifer.teos.proyectoformativojenni

import RecyclerViewHelpers.Adaptador
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.listadoMedicamentos
import modelo.listadoPacientes
import java.sql.SQLException

class informacion_paciente : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_informacion_paciente)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val uuidPaciente = intent.getStringExtra("UUID_PACIENTE")

        if (uuidPaciente != null) {
            mostrarDatosPaciente(uuidPaciente)
        }

        val btnVolver = findViewById<Button>(R.id.mdBtnVolver)
        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun mostrarDatosPaciente(uuidPaciente: String) {
        val objConexion = ClaseConexion().cadenaConexion()
        val statement = objConexion?.prepareStatement("SELECT * FROM Pacientes WHERE UUID_paciente = ?")
        statement?.setString(1, uuidPaciente)
        val resultSet = statement?.executeQuery()

        var paciente = listadoPacientes("", "", "", "", "", "", "")

        while (resultSet?.next() == true) {
            paciente = listadoPacientes(
                resultSet.getString("UUID_paciente"),
                resultSet.getString("nombre"),
                resultSet.getString("apellido"),
                resultSet.getString("edad"),
                resultSet.getString("enfermedad"),
                resultSet.getString("numeroHabitacion"),
                resultSet.getString("numeroCama")
            )
        }

        resultSet?.close()
        statement?.close()
        objConexion?.close()

        // Mostrar datos del paciente en las vistas correspondientes
        findViewById<EditText>(R.id.mdNombre).setText(paciente.nombre)
        findViewById<EditText>(R.id.mdApellido).setText(paciente.apellido)
        findViewById<EditText>(R.id.mdEdad).setText(paciente.edad)
        findViewById<EditText>(R.id.mdEnfermedad).setText(paciente.enfermedad)
        findViewById<EditText>(R.id.mdNumeroHabitacion).setText(paciente.numeroHabitacion)
        findViewById<EditText>(R.id.mdNumeroCama).setText(paciente.numeroCama)

        // Obtener y mostrar los medicamentos asignados y las horas de aplicación
        val medicamentos = obtenerDatosMedicamentos(uuidPaciente)
        val horasAplicacion = obtenerHorasAplicacion(uuidPaciente)

        findViewById<EditText>(R.id.mdMedicamentos).setText(medicamentos.joinToString(", ") { it.medicamentosAsignados })
        findViewById<EditText>(R.id.mdHora).setText(horasAplicacion.joinToString(", "))
    }

    private fun obtenerDatosMedicamentos(uuidPaciente: String): List<listadoMedicamentos> {
        val objConexion = ClaseConexion().cadenaConexion()
        val medicamentos = mutableListOf<listadoMedicamentos>()

        try {
            val statement = objConexion?.prepareStatement("SELECT * FROM Medicamentos WHERE UUID_paciente = ?")
            statement?.setString(1, uuidPaciente)
            val resultSet = statement?.executeQuery()

            while (resultSet?.next() == true) {
                val uuidMedicamento = resultSet.getString("UUID_medicamento")
                val medicamento = resultSet.getString("medicamentosAsignados")

                medicamentos.add(listadoMedicamentos(uuidMedicamento, medicamento))
            }

            resultSet?.close()
            statement?.close()
        } catch (e: SQLException) {
            Log.e("informacion_paciente", "Error al obtener medicamentos: ${e.message}")
        } finally {
            objConexion?.close()
        }

        return medicamentos
    }

    private fun obtenerHorasAplicacion(uuidPaciente: String): List<String> {
        val objConexion = ClaseConexion().cadenaConexion()
        val horas = mutableListOf<String>()

        try {
            val statement = objConexion?.prepareStatement("SELECT horaAplicacion FROM PacienteMedicamento WHERE UUID_paciente = ?")
            statement?.setString(1, uuidPaciente)
            val resultSet = statement?.executeQuery()

            while (resultSet?.next() == true) {
                val horaAplicacion = resultSet.getString("horaAplicacion")
                horas.add(horaAplicacion)
            }

            resultSet?.close()
            statement?.close()
        } catch (e: SQLException) {
            Log.e("informacion_paciente", "Error al obtener horas de aplicación: ${e.message}")
        } finally {
            objConexion?.close()
        }

        return horas
    }
}


