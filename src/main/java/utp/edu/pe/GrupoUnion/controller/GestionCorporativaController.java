package utp.edu.pe.GrupoUnion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.management.*;
import utp.edu.pe.GrupoUnion.repository.*;
import utp.edu.pe.GrupoUnion.service.CloudinaryService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gestion")
public class GestionCorporativaController {

    private final BoletaPagoRepository boletaRepo;
    private final DocumentoEmpleadoRepository docRepo;
    private final SolicitudRepository solicitudRepo;
    private final EmpleadoRepository empleadoRepo;
    private final UsuarioRepository usuarioRepo;
    private final CloudinaryService cloudinaryService;

    public GestionCorporativaController(BoletaPagoRepository boletaRepo, DocumentoEmpleadoRepository docRepo, SolicitudRepository solicitudRepo, EmpleadoRepository empleadoRepo, UsuarioRepository usuarioRepo, CloudinaryService cloudinaryService) {
        this.boletaRepo = boletaRepo;
        this.docRepo = docRepo;
        this.solicitudRepo = solicitudRepo;
        this.empleadoRepo = empleadoRepo;
        this.usuarioRepo = usuarioRepo;
        this.cloudinaryService = cloudinaryService;
    }

    private Empleado getEmpleadoActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioRepo.findByUsername(auth.getName()).orElseThrow();
        return empleadoRepo.findByPersona(usuario.getPersona()).orElseThrow(() -> new RuntimeException("No es empleado"));
    }

    // ================= BOLETAS =================

    @GetMapping("/boletas/mis-boletas")
    public List<BoletaPago> getMisBoletas() {
        return boletaRepo.findByEmpleadoIdEmpleadoOrderByAnioDescMesDesc(getEmpleadoActual().getIdEmpleado());
    }

    @PostMapping("/admin/boletas/upload")
    public ResponseEntity<?> subirBoleta(@RequestParam("idEmpleado") Integer idEmpleado,
                                         @RequestParam("mes") Integer mes,
                                         @RequestParam("anio") Integer anio,
                                         @RequestParam("file") MultipartFile file) {
        try {
            String url = cloudinaryService.uploadFile(file);
            Empleado emp = empleadoRepo.findById(idEmpleado).orElseThrow();

            BoletaPago boleta = new BoletaPago();
            boleta.setEmpleado(emp);
            boleta.setMes(mes);
            boleta.setAnio(anio);
            boleta.setUrlArchivo(url);

            // Regla de antigüedad: si es > 2 años, nace RESTRINGIDA, sino DISPONIBLE
            LocalDate fechaBoleta = LocalDate.of(anio, mes, 1);
            if (fechaBoleta.isBefore(LocalDate.now().minusYears(2))) {
                boleta.setEstado("RESTRINGIDA");
            } else {
                boleta.setEstado("DISPONIBLE");
            }

            return ResponseEntity.ok(boletaRepo.save(boleta));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error subiendo boleta: " + e.getMessage()));
        }
    }

    // ================= DOCUMENTOS =================

    @GetMapping("/documentos/mis-documentos")
    public List<DocumentoEmpleado> getMisDocumentos() {
        return docRepo.findByEmpleadoIdEmpleado(getEmpleadoActual().getIdEmpleado());
    }

    @PostMapping("/admin/documentos/upload")
    public ResponseEntity<?> subirDocumento(@RequestParam("idEmpleado") Integer idEmpleado,
                                            @RequestParam("nombre") String nombre,
                                            @RequestParam("tipo") String tipo,
                                            @RequestParam("file") MultipartFile file) {
        try {
            String url = cloudinaryService.uploadFile(file);
            Empleado emp = empleadoRepo.findById(idEmpleado).orElseThrow();

            DocumentoEmpleado doc = new DocumentoEmpleado();
            doc.setEmpleado(emp);
            doc.setNombre(nombre);
            doc.setTipo(tipo);
            doc.setUrlArchivo(url);

            return ResponseEntity.ok(docRepo.save(doc));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error subiendo documento"));
        }
    }

    // ================= SOLICITUDES =================

    @GetMapping("/solicitudes/mis-solicitudes")
    public List<Solicitud> getMisSolicitudes() {
        return solicitudRepo.findByEmpleadoIdEmpleadoOrderByCreadoEnDesc(getEmpleadoActual().getIdEmpleado());
    }

    @GetMapping("/admin/solicitudes")
    public List<Solicitud> getAllSolicitudes() {
        return solicitudRepo.findAllByOrderByCreadoEnDesc();
    }

    @PostMapping("/solicitudes/crear")
    public ResponseEntity<?> crearSolicitud(@RequestBody Solicitud solicitud) {
        solicitud.setEmpleado(getEmpleadoActual());
        // Asumiendo que tienes un EstadoSolicitudRepository o lo manejas por ID
        // Aquí simplificado: asegúrate de setear el estado inicial "PENDIENTE"
        return ResponseEntity.ok(solicitudRepo.save(solicitud));
    }

    @PutMapping("/admin/solicitudes/{id}/estado")
    public ResponseEntity<?> cambiarEstadoSolicitud(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        Solicitud sol = solicitudRepo.findById(id).orElseThrow();
        // Lógica para cambiar estado usando tu entidad EstadoSolicitud
        // sol.setEstadoSolicitud(...);
        return ResponseEntity.ok(solicitudRepo.save(sol));
    }
}