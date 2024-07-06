package modelo

data class listadoPacientes(

    val UUID_paciente: String,
    val nombre: String,
    val apellido: String,
    val edad: String,
    val enfermedad : String,
    val numeroHabitacion: String,
    val numeroCama: String
)
