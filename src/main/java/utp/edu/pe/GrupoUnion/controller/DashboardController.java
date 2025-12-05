package utp.edu.pe.GrupoUnion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {

    //localhost:8081/admin/dashboard-data
    @GetMapping("/admin/dashboard-data")
    public ResponseEntity<?> getAdminData() {
        Map<String, String> data = new HashMap<>();
        data.put("mensaje", "Bienvenido al Panel de Administrador");
        data.put("info", "Datos sensibles solo para admins...");
        return ResponseEntity.ok(data);
    }

    //localhost:8081/empleado/dashboard-data
    @GetMapping("/empleado/dashboard-data")
    public ResponseEntity<?> getEmpleadoData() {
        Map<String, String> data = new HashMap<>();
        data.put("mensaje", "Bienvenido al Panel de Empleado");
        data.put("info", "Tus datos de asistencia, boletas, etc.");
        return ResponseEntity.ok(data);
    }
}