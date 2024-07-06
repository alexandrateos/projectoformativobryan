package RecyclerViewHelpers

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import jennifer.teos.proyectoformativojenni.MainActivity
import jennifer.teos.proyectoformativojenni.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.listadoMedicamentos
import modelo.listadoPacienteMedicamento
import modelo.listadoPacientes
import java.sql.Connection
import java.sql.SQLException

class Adaptador(private var listaPacientes: List<listadoPacientes>, private val context: Context) : RecyclerView.Adapter<Adaptador.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.txtPacienteDato)
        val imgBorrar: ImageView = view.findViewById(R.id.imgBorrar)
        val imgEditar: ImageView = view.findViewById(R.id.imgEditar)

        fun bind(paciente: listadoPacientes) {
            textView.text = "${paciente.nombre} ${paciente.apellido}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paciente = listaPacientes[position]
        holder.bind(paciente)

        holder.itemView.setOnClickListener {
            mostrarInformacionPacienteDialog(paciente)
        }

        holder.imgBorrar.setOnClickListener {
            eliminarRegistro(paciente.UUID_paciente, position)
        }

        holder.imgEditar.setOnClickListener {
            mostrarEditarPacienteDialog(paciente)
        }
    }

    override fun getItemCount(): Int {
        return listaPacientes.size
    }

    fun actualizarRecyclerView(nuevaLista: List<listadoPacientes>) {
        listaPacientes = nuevaLista
        notifyDataSetChanged()
    }

    fun eliminarRegistro(uuid: String, position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val conexion = ClaseConexion().cadenaConexion()

                val statementPacienteMedicamento = conexion?.prepareStatement("DELETE FROM PacienteMedicamento WHERE UUID_paciente = ?")
                statementPacienteMedicamento?.setString(1, uuid)
                statementPacienteMedicamento?.executeUpdate()

                val statementPacientes = conexion?.prepareStatement("DELETE FROM Pacientes WHERE UUID_paciente = ?")
                statementPacientes?.setString(1, uuid)
                val rowsAffected = statementPacientes?.executeUpdate()

                if (rowsAffected != null && rowsAffected > 0) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Registro eliminado correctamente", Toast.LENGTH_SHORT).show()
                        listaPacientes = listaPacientes.toMutableList().apply { removeAt(position) }
                        notifyItemRemoved(position)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al eliminar el registro: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun mostrarInformacionPacienteDialog(paciente: listadoPacientes) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_informacion_paciente, null)

        val mdNombre = dialogView.findViewById<EditText>(R.id.mdNombre)
        val mdApellido = dialogView.findViewById<EditText>(R.id.mdApellido)
        val mdEdad = dialogView.findViewById<EditText>(R.id.mdEdad)
        val mdEnfermedad = dialogView.findViewById<EditText>(R.id.mdEnfermedad)
        val mdNumeroHabitacion = dialogView.findViewById<EditText>(R.id.mdNumeroHabitacion)
        val mdNumeroCama = dialogView.findViewById<EditText>(R.id.mdNumeroCama)
        val mdMedicamentosAsignados = dialogView.findViewById<EditText>(R.id.mdMedicamentos)
        val mdHoraAplicacion = dialogView.findViewById<EditText>(R.id.mdHora)
        val mdBtnVolver = dialogView.findViewById<Button>(R.id.mdBtnVolver)

        mdNombre.setText(paciente.nombre)
        mdApellido.setText(paciente.apellido)
        mdEdad.setText(paciente.edad)
        mdEnfermedad.setText(paciente.enfermedad)
        mdNumeroHabitacion.setText(paciente.numeroHabitacion)
        mdNumeroCama.setText(paciente.numeroCama)

        CoroutineScope(Dispatchers.IO).launch {
            val medicamentos = obtenerDatosMedicamentos(paciente.UUID_paciente)
            val horasAplicacion = obtenerHorasAplicacion(paciente.UUID_paciente)

            withContext(Dispatchers.Main) {
                mdMedicamentosAsignados.setText(medicamentos.joinToString(", ") { it.medicamentosAsignados })
                mdHoraAplicacion.setText(horasAplicacion.joinToString(", "))
            }
        }

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        mdBtnVolver.setOnClickListener {
            dialog.dismiss()
        }
    }

    fun mostrarEditarPacienteDialog(paciente: listadoPacientes) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.activity_editar_paciente, null)

        val editNombre = dialogView.findViewById<EditText>(R.id.editNombre)
        val editApellido = dialogView.findViewById<EditText>(R.id.editApellido)
        val editEdad = dialogView.findViewById<EditText>(R.id.editEdad)
        val editEnfermedad = dialogView.findViewById<EditText>(R.id.editEnfermedad)
        val editNumeroHabitacion = dialogView.findViewById<EditText>(R.id.editNumeroHabitacion)
        val editNumeroCama = dialogView.findViewById<EditText>(R.id.editNumeroCama)
        val editMedicamentosAsignados = dialogView.findViewById<EditText>(R.id.editMedicamentos)
        val editHoraAplicacion = dialogView.findViewById<EditText>(R.id.editHora)
        val btnGuardar = dialogView.findViewById<Button>(R.id.btnGuardar)
        val btnVolver = dialogView.findViewById<Button>(R.id.btnVolver)

        editNombre.setText(paciente.nombre)
        editApellido.setText(paciente.apellido)
        editEdad.setText(paciente.edad)
        editEnfermedad.setText(paciente.enfermedad)
        editNumeroHabitacion.setText(paciente.numeroHabitacion)
        editNumeroCama.setText(paciente.numeroCama)

        // Obtener y mostrar los datos de medicamentos y paciente-medicamento
        CoroutineScope(Dispatchers.IO).launch {
            val medicamentos = obtenerDatosMedicamentos(paciente.UUID_paciente)
            val horasAplicacion = obtenerHorasAplicacion(paciente.UUID_paciente)

            withContext(Dispatchers.Main) {
                editMedicamentosAsignados.setText(medicamentos.joinToString(", ") { it.medicamentosAsignados })
                editHoraAplicacion.setText(horasAplicacion.joinToString(", "))
            }
        }

        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        btnGuardar.setOnClickListener {
            val nuevoNombre = editNombre.text.toString()
            val nuevoApellido = editApellido.text.toString()
            val nuevaEdad = editEdad.text.toString()
            val nuevaEnfermedad = editEnfermedad.text.toString()
            val nuevoNumeroHabitacion = editNumeroHabitacion.text.toString()
            val nuevoNumeroCama = editNumeroCama.text.toString()
            val nuevosMedicamentos = editMedicamentosAsignados.text.toString().split(", ").map { it.trim() }
            val nuevasHoras = editHoraAplicacion.text.toString().split(", ").map { it.trim() }

            CoroutineScope(Dispatchers.IO).launch {
                val conexion = ClaseConexion().cadenaConexion()


                    val actualizarPaciente = conexion?.prepareStatement(
                        "UPDATE Pacientes SET nombre = ?, apellido = ?, edad = ?, enfermedad = ?, numeroHabitacion = ?, numeroCama = ? WHERE UUID_paciente = ?"
                    )
                    actualizarPaciente?.setString(1, nuevoNombre)
                    actualizarPaciente?.setString(2, nuevoApellido)
                    actualizarPaciente?.setString(3, nuevaEdad)
                    actualizarPaciente?.setString(4, nuevaEnfermedad)
                    actualizarPaciente?.setString(5, nuevoNumeroHabitacion)
                    actualizarPaciente?.setString(6, nuevoNumeroCama)
                    actualizarPaciente?.setString(7, paciente.UUID_paciente)
                    actualizarPaciente?.executeUpdate()

                    val eliminarPacienteMedicamento = conexion?.prepareStatement(
                        "DELETE FROM PacienteMedicamento WHERE UUID_paciente = ?"
                    )
                    eliminarPacienteMedicamento?.setString(1, paciente.UUID_paciente)
                    eliminarPacienteMedicamento?.executeUpdate()

                    for (i in nuevosMedicamentos.indices) {
                        val medicamentoUUID = obtenerUUIDMedicamento(nuevosMedicamentos[i], conexion)
                        if (medicamentoUUID != null) {
                            val insertarPacienteMedicamento = conexion?.prepareStatement(
                                "INSERT INTO PacienteMedicamento (UUID_paciente, UUID_medicamentos, horaAplicacion) VALUES (?, ?, ?)"
                            )
                            insertarPacienteMedicamento?.setString(1, paciente.UUID_paciente)
                            insertarPacienteMedicamento?.setString(2, medicamentoUUID)
                            insertarPacienteMedicamento?.setString(3, nuevasHoras.getOrElse(i) { "" })
                            insertarPacienteMedicamento?.executeUpdate()
                        }
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }

            }
        }

        btnVolver.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private fun obtenerDatosMedicamentos(uuidPaciente: String): List<listadoMedicamentos> {
        val objConexion = ClaseConexion().cadenaConexion()
        val medicamentos = mutableListOf<listadoMedicamentos>()


            val statement = objConexion?.prepareStatement("SELECT * FROM Medicamentos WHERE UUID_medicamentos IN (SELECT UUID_medicamentos FROM PacienteMedicamento WHERE UUID_paciente = ?)")
            statement?.setString(1, uuidPaciente)
            val resultSet = statement?.executeQuery()

            while (resultSet?.next() == true) {
                val uuidMedicamento = resultSet.getString("UUID_medicamentos")
                val medicamento = resultSet.getString("medicamentosAsignados")

                medicamentos.add(listadoMedicamentos(uuidMedicamento, medicamento))
            }

            resultSet?.close()
            statement?.close()


        return medicamentos
    }

    private fun obtenerHorasAplicacion(uuidPaciente: String): List<String> {
        val objConexion = ClaseConexion().cadenaConexion()
        val horas = mutableListOf<String>()


            val statement = objConexion?.prepareStatement("SELECT horaAplicacion FROM PacienteMedicamento WHERE UUID_paciente = ?")
            statement?.setString(1, uuidPaciente)
            val resultSet = statement?.executeQuery()

            while (resultSet?.next() == true) {
                val horaAplicacion = resultSet.getString("horaAplicacion")
                horas.add(horaAplicacion)
            }

            resultSet?.close()
            statement?.close()
        return horas
    }

    private fun obtenerUUIDMedicamento(nombreMedicamento: String, conexion: Connection?): String? {

            val statement = conexion?.prepareStatement("SELECT UUID_medicamentos FROM Medicamentos WHERE medicamentosAsignados = ?")
            statement?.setString(1, nombreMedicamento)
            val resultSet = statement?.executeQuery()

            if (resultSet?.next() == true) {
                return resultSet.getString("UUID_medicamentos")
            }

            resultSet?.close()
            statement?.close()

        return null
    }
}





