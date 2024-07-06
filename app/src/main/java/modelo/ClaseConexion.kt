package modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {
    fun cadenaConexion(): Connection? {
        return try {
            Class.forName("oracle.jdbc.driver.OracleDriver")
            val ip = "jdbc:oracle:thin:@192.168.0.24:1521:xe"
            val usuario = "JENNIFER_TEOS"
            val contrasena = "123456"

            DriverManager.getConnection(ip, usuario, contrasena)
        } catch (e: Exception) {
            println("Este es el error: $e")
            null
        }
    }
}