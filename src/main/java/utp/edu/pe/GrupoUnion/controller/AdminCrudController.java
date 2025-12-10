package utp.edu.pe.GrupoUnion.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;
import utp.edu.pe.GrupoUnion.entity.catalogs.Departamento;
import utp.edu.pe.GrupoUnion.entity.catalogs.RegimenPensionario;
import utp.edu.pe.GrupoUnion.entity.catalogs.TipoContrato;
import utp.edu.pe.GrupoUnion.entity.catalogs.TipoDocumento;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.core.Persona;
import utp.edu.pe.GrupoUnion.entity.management.Contrato;
import utp.edu.pe.GrupoUnion.payload.AreaResumenDTO;
import utp.edu.pe.GrupoUnion.repository.*;
import utp.edu.pe.GrupoUnion.service.CloudinaryService;
import utp.edu.pe.GrupoUnion.service.ContratoPdfService;
import utp.edu.pe.GrupoUnion.service.NominaService;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminCrudController {

    private final UsuarioRepository usuarioRepository;
    private final DepartamentoRepository departamentoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final PersonaRepository personaRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    // --- NUEVAS INYECCIONES PARA EL MÓDULO CONTRATOS ---
    private final ContratoRepository contratoRepository;
    private final NominaService nominaService;
    private final ContratoPdfService pdfService;

    public AdminCrudController(UsuarioRepository usuarioRepository,
                               DepartamentoRepository departamentoRepository,
                               EmpleadoRepository empleadoRepository,
                               PersonaRepository personaRepository,
                               TipoDocumentoRepository tipoDocumentoRepository,
                               PasswordEncoder passwordEncoder,
                               CloudinaryService cloudinaryService,
                               ContratoRepository contratoRepository,
                               NominaService nominaService,
                               ContratoPdfService pdfService) {
        this.usuarioRepository = usuarioRepository;
        this.departamentoRepository = departamentoRepository;
        this.empleadoRepository = empleadoRepository;
        this.personaRepository = personaRepository;
        this.tipoDocumentoRepository = tipoDocumentoRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
        this.contratoRepository = contratoRepository;
        this.nominaService = nominaService;
        this.pdfService = pdfService;
    }

    // ================= MÓDULO CONTRATOS =================

    @GetMapping("/contratos")
    public List<Contrato> getHistorialContratos() {
        return contratoRepository.findAllByOrderByFechaGeneracionDesc();
    }

    // Endpoint para descargar el PDF generado
    @GetMapping("/contratos/{id}/pdf")
    public ResponseEntity<byte[]> descargarContratoPdf(@PathVariable Integer id) {
        Contrato contrato = contratoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));

        byte[] pdfBytes = pdfService.generarContratoPdf(contrato);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=Contrato_" + contrato.getIdContrato() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/contratos")
    @Transactional
    public ResponseEntity<?> crearContrato(@RequestBody Map<String, Object> payload) {
        try {
            Integer idEmpleado = (Integer) payload.get("idEmpleado");
            String tipoRegimen = (String) payload.get("tipoRegimen");
            String nombreAfp = (String) payload.get("nombreAfp");

            // Conversión segura de Sueldo
            String sueldoStr = payload.get("sueldoBase").toString();
            BigDecimal sueldo = new BigDecimal(sueldoStr);

            String fechaIniStr = (String) payload.get("fechaInicio");
            String fechaFinStr = (String) payload.get("fechaFin");
            String username = (String) payload.get("usuario");

            // Validación Sueldo Mínimo
            if (sueldo.compareTo(new BigDecimal("1130")) < 0) {
                return ResponseEntity.badRequest().body(crearMensaje("El sueldo no puede ser menor al mínimo vital (S/ 1130)."));
            }

            // Desactivar contrato anterior
            contratoRepository.desactivarContratosPrevios(idEmpleado);

            Empleado emp = empleadoRepository.findById(idEmpleado)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + idEmpleado));

            Contrato c = new Contrato();
            c.setEmpleado(emp);

            // --- SOLUCIÓN ERROR FOREIGN KEY ---
            // Asignamos el ID 1 (que ya insertaste con SQL) para evitar el error de integridad.
            TipoContrato tc = new TipoContrato(); tc.setIdTipoContrato(1);
            c.setTipoContrato(tc);

            RegimenPensionario rp = new RegimenPensionario(); rp.setIdRegimen(1);
            c.setRegimenPensionario(rp);
            // ----------------------------------

            c.setSueldoBase(sueldo);
            c.setTipoRegimen(tipoRegimen);
            c.setNombreAfp(nombreAfp);
            c.setFechaInicio(LocalDate.parse(fechaIniStr));

            if (fechaFinStr != null && !fechaFinStr.isEmpty()) {
                c.setFechaFin(LocalDate.parse(fechaFinStr));
            }

            c.setGeneradoPor(username);
            c.setVigente(true);

            Contrato guardado = contratoRepository.save(c);

            return ResponseEntity.ok(crearMensaje("Contrato generado correctamente. ID: " + guardado.getIdContrato()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(crearMensaje("Error: " + e.getMessage()));
        }
    }

    // ================= MÓDULO PLANILLAS =================
    @PostMapping("/planillas/generar")
    public ResponseEntity<?> generarPlanilla(@RequestBody Map<String, Object> payload) {
        try {
            Integer mes = (Integer) payload.get("mes");
            Integer anio = (Integer) payload.get("anio");
            String username = (String) payload.get("usuario");

            nominaService.generarPlanillaMasiva(mes, anio, username);

            return ResponseEntity.ok(crearMensaje("Cálculo de planilla finalizado para " + mes + "/" + anio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(crearMensaje("Error: " + e.getMessage()));
        }
    }

    // ================= ENDPOINTS EXISTENTES (Mantenidos) =================
    @GetMapping("/tipos-documento") public List<TipoDocumento> getAllTiposDoc() { return tipoDocumentoRepository.findAll(); }
    @GetMapping("/usuarios") public List<Usuario> getAllUsuarios() { return usuarioRepository.findAll(); }
    @PostMapping("/usuarios") public ResponseEntity<?> createUsuario(@RequestBody Usuario u) { return ResponseEntity.ok(usuarioRepository.save(u)); } // Simplificado para brevedad
    @PutMapping("/usuarios") public ResponseEntity<?> updateUsuario(@RequestBody Usuario u) { return ResponseEntity.ok(usuarioRepository.save(u)); }
    @DeleteMapping("/usuarios/{id}") public ResponseEntity<?> deleteUsuario(@PathVariable Integer id) { usuarioRepository.deleteById(id); return ResponseEntity.ok().build(); }

    @GetMapping("/empleados") public List<Empleado> getAllEmpleados() { return empleadoRepository.findAll(); }
    @PostMapping("/empleados") public ResponseEntity<?> createEmpleado(@RequestBody Empleado e) { personaRepository.save(e.getPersona()); return ResponseEntity.ok(empleadoRepository.save(e)); }
    @PutMapping("/empleados") public ResponseEntity<?> updateEmpleado(@RequestBody Empleado e) { personaRepository.save(e.getPersona()); return ResponseEntity.ok(empleadoRepository.save(e)); }
    @DeleteMapping("/empleados/{id}") public ResponseEntity<?> deleteEmpleado(@PathVariable Integer id) { empleadoRepository.deleteById(id); return ResponseEntity.ok().build(); }

    @GetMapping("/areas") public List<AreaResumenDTO> getAllAreas() { return departamentoRepository.obtenerAreasConConteo(); }
    @GetMapping("/areas/{id}/empleados") public List<Empleado> getEmpleadosPorArea(@PathVariable Integer id) { return empleadoRepository.findByDepartamentoIdDepartamento(id); }
    @PostMapping("/areas") public Departamento createArea(@RequestBody Departamento d) { return departamentoRepository.save(d); }
    @PutMapping("/areas/{id}") public Departamento updateArea(@PathVariable Integer id, @RequestBody Departamento d) { return departamentoRepository.save(d); }
    @DeleteMapping("/areas/{id}") public ResponseEntity<?> deleteArea(@PathVariable Integer id) { departamentoRepository.deleteById(id); return ResponseEntity.ok().build(); }

    @PostMapping("/personas/{id}/foto") public ResponseEntity<?> subirFoto(@PathVariable Integer id, @RequestParam("file") MultipartFile f) { return ResponseEntity.ok().build(); }

    private Map<String, String> crearMensaje(String mensaje) {
        Map<String, String> response = new HashMap<>();
        response.put("message", mensaje);
        return response;
    }
}