package utp.edu.pe.GrupoUnion.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;
import utp.edu.pe.GrupoUnion.entity.catalogs.EstadoSolicitud;
import utp.edu.pe.GrupoUnion.entity.catalogs.TipoSolicitud;
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

    // Helper para obtener nombre de usuario logueado (Admin)
    private String getUsernameActual() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // ==========================================
    //              BOLETAS
    // ==========================================

    @GetMapping("/boletas/mis-boletas")
    public List<BoletaPago> getMisBoletas() {
        return boletaRepo.findByEmpleadoIdEmpleadoOrderByAnioDescMesDesc(getEmpleadoActual().getIdEmpleado());
    }

    // NUEVO: Historial Admin
    @GetMapping("/admin/boletas/historial")
    public List<BoletaPago> getHistorialBoletas() {
        return boletaRepo.findAllByOrderByFechaSubidaDesc();
    }

    // ACTUALIZADO: Subida con registro de Admin
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
            boleta.setSubidoPor(getUsernameActual()); // Guardamos quién subió

            LocalDate fechaBoleta = LocalDate.of(anio, mes, 1);
            if (fechaBoleta.isBefore(LocalDate.now().minusYears(2))) {
                boleta.setEstado("RESTRINGIDA");
            } else {
                boleta.setEstado("DISPONIBLE");
            }

            return ResponseEntity.ok(boletaRepo.save(boleta));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    // ==========================================
    //            DOCUMENTOS
    // ==========================================

    @GetMapping("/documentos/mis-documentos")
    public List<DocumentoEmpleado> getMisDocumentos() {
        return docRepo.findByEmpleadoIdEmpleado(getEmpleadoActual().getIdEmpleado());
    }

    // NUEVO: Historial Admin
    @GetMapping("/admin/documentos/historial")
    public List<DocumentoEmpleado> getHistorialDocumentos() {
        return docRepo.findAllByOrderByFechaSubidaDesc();
    }

    // ACTUALIZADO: Subida con registro de Admin
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
            doc.setSubidoPor(getUsernameActual()); // Guardamos quién subió

            return ResponseEntity.ok(docRepo.save(doc));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    // ==========================================
    //            SOLICITUDES
    // ==========================================

    @GetMapping("/solicitudes/mis-solicitudes")
    public List<Solicitud> getMisSolicitudes() {
        return solicitudRepo.findByEmpleadoIdEmpleadoOrderByCreadoEnDesc(getEmpleadoActual().getIdEmpleado());
    }

    @GetMapping("/admin/solicitudes")
    public List<Solicitud> getAllSolicitudes() {
        return solicitudRepo.findAllByOrderByCreadoEnDesc();
    }

    @PostMapping(value = "/solicitudes/crear", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> crearSolicitud(
            @RequestParam("asunto") String asunto,
            @RequestParam("detalle") String detalle,
            @RequestParam("idTipo") Integer idTipo,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            Solicitud solicitud = new Solicitud();
            solicitud.setEmpleado(getEmpleadoActual());
            solicitud.setAsunto(asunto);
            solicitud.setDetalle(detalle);

            TipoSolicitud tipo = new TipoSolicitud();
            tipo.setIdTipoSolicitud(idTipo);
            solicitud.setTipoSolicitud(tipo);

            EstadoSolicitud estado = new EstadoSolicitud();
            estado.setIdEstado(1);
            solicitud.setEstadoSolicitud(estado);

            if (file != null && !file.isEmpty()) {
                String url = cloudinaryService.uploadFile(file);
                solicitud.setUrlArchivo(url);
            }

            return ResponseEntity.ok(solicitudRepo.save(solicitud));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "Error al crear solicitud: " + e.getMessage()));
        }
    }
}