package es.cifpcarlos3.proyecto.dao.impl;

import es.cifpcarlos3.proyecto.dao.InmersionesDAO;
import es.cifpcarlos3.proyecto.model.Certificacion;
import es.cifpcarlos3.proyecto.model.Inmersion;
import es.cifpcarlos3.proyecto.util.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InmersionesDAOImpl implements InmersionesDAO {
    private final DatabaseConnection db;
    public InmersionesDAOImpl(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public List<Inmersion> listarInmersiones() {
        String consulta = "SELECT * FROM inmersiones";
        List<Inmersion> inmersiones=new ArrayList<>();
        try (var conexion  = db.getConnection();
             Statement sentencia = conexion.createStatement();
             ResultSet rdo = sentencia.executeQuery(consulta)){

            while (rdo.next()) {

            }

        }catch (SQLException e){
            System.err.println("Error al obtener la lista de inmersiones: " + e.getMessage());
        }

        return inmersiones;
    }


}

