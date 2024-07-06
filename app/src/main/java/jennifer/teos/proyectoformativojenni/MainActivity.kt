package jennifer.teos.proyectoformativojenni

import RecyclerViewHelpers.Adaptador
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.listadoMedicamentos
import modelo.listadoPacienteMedicamento
import modelo.listadoPacientes
import java.sql.SQLException

class MainActivity : AppCompatActivity() {

    private lateinit var rcvDatos: RecyclerView
    private lateinit var miAdaptador: Adaptador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rcvDatos = findViewById(R.id.rcvDatos)
        rcvDatos.layoutManager = LinearLayoutManager(this)

        miAdaptador = Adaptador(emptyList(), this)
        rcvDatos.adapter = miAdaptador

        val btnCrear = findViewById<ImageButton>(R.id.btnCrear)
        btnCrear.setOnClickListener {
            val pantallaCrear = Intent(this, crear_paciente_medicamento::class.java)
            startActivity(pantallaCrear)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarDatosIniciales()
    }

    private fun cargarDatosIniciales() {
        CoroutineScope(Dispatchers.IO).launch {
            val nuevosDatos = obtenerDatosPacientes()

            withContext(Dispatchers.Main) {
                miAdaptador.actualizarRecyclerView(nuevosDatos)
            }
        }
    }

    private fun obtenerDatosPacientes(): List<listadoPacientes> {
        val objConexion = ClaseConexion().cadenaConexion()
        val listadoPacientes = mutableListOf<listadoPacientes>()

        try {
            val statement = objConexion?.createStatement()
            val resultSet = statement?.executeQuery("SELECT * FROM Pacientes")

            while (resultSet?.next() == true) {
                val uuid = resultSet.getString("UUID_paciente")
                val nombre = resultSet.getString("nombre")
                val apellido = resultSet.getString("apellido")
                val edad = resultSet.getString("edad")
                val enfermedad = resultSet.getString("enfermedad")
                val numeroHabitacion = resultSet.getString("numeroHabitacion")
                val numeroCama = resultSet.getString("numeroCama")

                val paciente = listadoPacientes(
                    uuid,
                    nombre,
                    apellido,
                    edad,
                    enfermedad,
                    numeroHabitacion,
                    numeroCama
                )

                listadoPacientes.add(paciente)
            }

            resultSet?.close()
            statement?.close()
        } catch (e: SQLException) {
            Log.e("MainActivity", "Error al obtener datos de pacientes: ${e.message}")
        } finally {
            objConexion?.close()
        }

        return listadoPacientes
    }
}






