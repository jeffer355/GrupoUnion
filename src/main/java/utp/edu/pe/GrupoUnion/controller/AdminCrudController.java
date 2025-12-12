package utp.edu.pe.GrupoUnion.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utp.edu.pe.GrupoUnion.entity.auth.Usuario;
import utp.edu.pe.GrupoUnion.entity.catalogs.Cargo;
import utp.edu.pe.GrupoUnion.entity.catalogs.Departamento;
import utp.edu.pe.GrupoUnion.entity.catalogs.RegimenPensionario;
import utp.edu.pe.GrupoUnion.entity.catalogs.TipoContrato;
import utp.edu.pe.GrupoUnion.entity.catalogs.TipoDocumento;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.core.Persona;
import utp.edu.pe.GrupoUnion.entity.management.BoletaPago;
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
    private final CargoRepository cargoRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    // --- MÓDULO CONTRATOS ---
    private final ContratoRepository contratoRepository;
    private final NominaService nominaService;
    private final ContratoPdfService pdfService;

    public AdminCrudController(UsuarioRepository usuarioRepository,
                               DepartamentoRepository departamentoRepository,
                               EmpleadoRepository empleadoRepository,
                               PersonaRepository personaRepository,
                               TipoDocumentoRepository tipoDocumentoRepository,
                               CargoRepository cargoRepository,
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
        this.cargoRepository = cargoRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
        this.contratoRepository = contratoRepository;
        this.nominaService = nominaService;
        this.pdfService = pdfService;
    }

    // ================= ENDPOINT CARGOS =================
    @GetMapping("/cargos")
    public List<Cargo> getAllCargos() {
        return cargoRepository.findAll();
    }

    // ================= MÓDULO CONTRATOS =================

    @GetMapping("/contratos")
    public List<Contrato> getHistorialContratos() {
        return contratoRepository.findAllByOrderByFechaGeneracionDesc();
    }

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

            String sueldoStr = payload.get("sueldoBase").toString();
            BigDecimal sueldo = new BigDecimal(sueldoStr);

            String fechaIniStr = (String) payload.get("fechaInicio");
            String fechaFinStr = (String) payload.get("fechaFin");
            String username = (String) payload.get("usuario");

            if (sueldo.compareTo(new BigDecimal("1130")) < 0) {
                return ResponseEntity.badRequest().body(crearMensaje("El sueldo no puede ser menor al mínimo vital (S/ 1130)."));
            }

            contratoRepository.desactivarContratosPrevios(idEmpleado);

            Empleado emp = empleadoRepository.findById(idEmpleado)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado con ID: " + idEmpleado));

            Contrato c = new Contrato();
            c.setEmpleado(emp);

            TipoContrato tc = new TipoContrato(); tc.setIdTipoContrato(1);
            c.setTipoContrato(tc);

            RegimenPensionario rp = new RegimenPensionario(); rp.setIdRegimen(1);
            c.setRegimenPensionario(rp);

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

    // ================= MÓDULO BOLETAS =================

    @PostMapping("/boletas/previsualizar")
    public ResponseEntity<byte[]> previsualizarBoleta(@RequestBody Map<String, Object> payload) {
        try {
            Integer idEmpleado = (Integer) payload.get("idEmpleado");
            Integer mes = (Integer) payload.get("mes");
            Integer anio = (Integer) payload.get("anio");

            BoletaPago boleta = nominaService.calcularBoletaIndividual(idEmpleado, mes, anio);
            byte[] pdfBytes = nominaService.generarPdfBoletaBytes(boleta);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/boletas/guardar-generada")
    public ResponseEntity<?> guardarBoletaGenerada(@RequestBody Map<String, Object> payload) {
        try {
            Integer idEmpleado = (Integer) payload.get("idEmpleado");
            Integer mes = (Integer) payload.get("mes");
            Integer anio = (Integer) payload.get("anio");
            String username = (String) payload.get("usuario");

            BoletaPago boleta = nominaService.guardarBoletaDefinitiva(idEmpleado, mes, anio, username);
            return ResponseEntity.ok(crearMensaje("Boleta generada y guardada correctamente. ID: " + boleta.getIdBoleta()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(crearMensaje("Error: " + e.getMessage()));
        }
    }

    // ================= ENDPOINTS EXISTENTES =================
    @GetMapping("/tipos-documento") public List<TipoDocumento> getAllTiposDoc() { return tipoDocumentoRepository.findAll(); }
    @GetMapping("/usuarios") public List<Usuario> getAllUsuarios() { return usuarioRepository.findAll(); }

    @PostMapping("/usuarios")
    @Transactional
    public ResponseEntity<?> createUsuario(@RequestBody Usuario u) {
        try {
            // 1. Validaciones básicas
            if (u.getUsername() == null || u.getHashPass() == null) {
                return ResponseEntity.badRequest().body(crearMensaje("Error: Faltan datos de usuario (email o contraseña)."));
            }

            // 2. Verificar si el USUARIO (Login) ya existe
            if (usuarioRepository.findByUsername(u.getUsername()).isPresent()) {
                return ResponseEntity.badRequest().body(crearMensaje("Error: Ya existe un usuario registrado con el email " + u.getUsername()));
            }

            // 3. Lógica INTELIGENTE para la Persona (El núcleo del arreglo)
            Persona personaFinal;
            String emailIngresado = u.getUsername(); // Usamos el username como email

            // Buscamos si ya existe alguien con ese correo en la tabla PERSONA
            Optional<Persona> personaExistente = personaRepository.findByEmail(emailIngresado);

            if (personaExistente.isPresent()) {
                // CASO A: La persona YA EXISTE (es tu empleado que ya registraste antes)
                personaFinal = personaExistente.get();

                // Validación extra: ¿Esa persona ya tiene usuario?
                if (usuarioRepository.findByPersona(personaFinal).isPresent()) {
                    return ResponseEntity.badRequest().body(crearMensaje("Error: El empleado " + personaFinal.getNombres() + " ya tiene un usuario activo."));
                }

                // Si llegamos aquí, la persona existe pero no tiene usuario. ¡Perfecto!
                // Usaremos esta 'personaFinal' para el nuevo usuario.
                System.out.println(">>> Vinculando usuario a persona existente ID: " + personaFinal.getIdPersona());

            } else {
                // CASO B: La persona NO EXISTE. Hay que crearla desde cero.
                if (u.getPersona() == null) {
                    return ResponseEntity.badRequest().body(crearMensaje("Error: Datos de persona requeridos para nuevos registros."));
                }

                personaFinal = u.getPersona();
                personaFinal.setEmail(emailIngresado); // Aseguramos que el email coincida

                // Valores por defecto para evitar errores de nulos
                if (personaFinal.getTipoDocumento() == null || personaFinal.getTipoDocumento().getIdTipoDoc() == null) {
                    TipoDocumento td = new TipoDocumento(); td.setIdTipoDoc(1); // DNI por defecto
                    personaFinal.setTipoDocumento(td);
                }
                if (personaFinal.getNroDocumento() == null || personaFinal.getNroDocumento().isEmpty()) {
                    personaFinal.setNroDocumento("TMP" + System.currentTimeMillis());
                }

                // Guardamos la nueva persona
                personaFinal = personaRepository.save(personaFinal);
                System.out.println(">>> Creando nueva persona ID: " + personaFinal.getIdPersona());
            }

            // 4. Configurar y Guardar el Usuario
            u.setPersona(personaFinal); // Aquí vinculamos la persona (nueva o existente)
            u.setHashPass(passwordEncoder.encode(u.getHashPass()));

            if (u.getActivo() == null) u.setActivo(true);
            if (u.getRequiereCambioPass() == null) u.setRequiereCambioPass(true);

            Usuario usuarioGuardado = usuarioRepository.save(u);
            return ResponseEntity.ok(usuarioGuardado);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearMensaje("Error interno: " + e.getMessage()));
        }
    }

    @PutMapping("/usuarios")
    public ResponseEntity<?> updateUsuario(@RequestBody Usuario u) {

        // 1. Verificar si el usuario existe y obtener sus datos actuales (incluyendo el hash de la contraseña)
        Usuario existingUser = usuarioRepository.findById(u.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para actualizar."));

        // 2. Actualizar datos de la Persona (nombre, email, foto, etc.) si se enviaron
        if (u.getPersona() != null) {
            personaRepository.save(u.getPersona());
            existingUser.setPersona(u.getPersona()); // Actualizar la referencia por si es nueva
        }

        // 3. Actualizar campos de control (Rol y Estado)
        if (u.getActivo() != null) existingUser.setActivo(u.getActivo());
        if (u.getRol() != null) existingUser.setRol(u.getRol());
        if (u.getRequiereCambioPass() != null) existingUser.setRequiereCambioPass(u.getRequiereCambioPass());

        // 4. LÓGICA DE SEGURIDAD: Solo actualizar la contraseña si se envió un hash nuevo
        // Esto previene que una simple actualización de datos personales (sin contraseña) borre el hash existente.
        if (u.getHashPass() != null && !u.getHashPass().isEmpty()) {
            existingUser.setHashPass(passwordEncoder.encode(u.getHashPass()));
        }

        // 5. Guardar el usuario actualizado
        return ResponseEntity.ok(usuarioRepository.save(existingUser));
    }


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

    @PostMapping("/personas/{id}/foto")
    public ResponseEntity<?> subirFoto(@PathVariable Integer id, @RequestParam("file") MultipartFile f) {
        try {
            String url = cloudinaryService.uploadFile(f);
            Persona p = personaRepository.findById(id).orElseThrow(() -> new RuntimeException("Persona no encontrada"));
            p.setFotoUrl(url);
            personaRepository.save(p);
            return ResponseEntity.ok(crearMensaje(url));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(crearMensaje("Error subiendo foto: " + e.getMessage()));
        }
    }

    private Map<String, String> crearMensaje(String mensaje) {
        Map<String, String> response = new HashMap<>();
        response.put("message", mensaje);
        return response;
    }
}