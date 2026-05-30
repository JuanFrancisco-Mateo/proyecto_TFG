package es.cifpcarlos3.proyecto.dao.impl;

import es.cifpcarlos3.proyecto.dao.InmersionesDAO;
import es.cifpcarlos3.proyecto.model.*;
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
        List<Inmersion> inmersiones = new ArrayList<>();
        String consulta = "SELECT i.*, ib.idBarco, ic.lugar FROM inmersion i " +
                          "LEFT JOIN inmersion_barco ib ON i.idInmersion = ib.idInmersion " +
                          "LEFT JOIN inmersion_costa ic ON i.idInmersion = ic.idInmersion";
        try (var conexion = db.getConnection();
             Statement sentencia = conexion.createStatement();
             ResultSet rdo = sentencia.executeQuery(consulta)) {

            while (rdo.next()) {
                String tipo = rdo.getString("tipo");
                Inmersion inmersion;
                if ("BARCO".equals(tipo)) {
                    InmersionBarco ib = new InmersionBarco();
                    ib.setIdInmersion(rdo.getInt("idInmersion"));
                    ib.setTipo("tipo");
                    ib.setNombre(rdo.getString("nombre"));
                    String certMin = rdo.getString("certificacionMinima");
                    if (certMin != null && !certMin.isEmpty()) {
                        ib.setCertificacionMinima(Certificacion.valueOf(certMin));
                    }
                    ib.setPlazasMax(rdo.getInt("plazasMax"));
                    ib.setPrecio(rdo.getDouble("precio"));
                    ib.setDuracionMin(rdo.getInt("duracionMin"));
                    // Barco se podría cargar por separado
                    inmersion = ib;
                } else {
                    InmersionCosta ic = new InmersionCosta();
                    ic.setIdInmersion(rdo.getInt("idInmersion"));
                    ic.setTipo("tipo");
                    ic.setNombre(rdo.getString("nombre"));
                    String certMin = rdo.getString("certificacionMinima");
                    if (certMin != null && !certMin.isEmpty()) {
                        ic.setCertificacionMinima(Certificacion.valueOf(certMin));
                    }
                    ic.setPlazasMax(rdo.getInt("plazasMax"));
                    ic.setPrecio(rdo.getDouble("precio"));
                    ic.setDuracionMin(rdo.getInt("duracionMin"));
                    ic.setLugar(rdo.getString("lugar") != null ? rdo.getString("lugar") : "");
                    inmersion = ic;
                }
                inmersiones.add(inmersion);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener la lista de inmersiones: " + e.getMessage());
        }

        return inmersiones;
    }
}

