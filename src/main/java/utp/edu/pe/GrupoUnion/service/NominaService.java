package utp.edu.pe.GrupoUnion.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.management.BoletaPago;
import utp.edu.pe.GrupoUnion.entity.management.Contrato;
import utp.edu.pe.GrupoUnion.repository.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NominaService {

    private final ContratoRepository contratoRepo;
    private final AsistenciaRepository asistenciaRepo;
    private final BoletaPagoRepository boletaRepo;
    private final EmpleadoRepository empleadoRepo;
    private final CloudinaryService cloudinaryService;

    // Tasas Referenciales
    private final BigDecimal TASA_ONP = new BigDecimal("0.13");
    private final BigDecimal TASA_ESSALUD = new BigDecimal("0.09");
    private final BigDecimal TASA_AFP_PROM = new BigDecimal("0.129");

    public NominaService(ContratoRepository contratoRepo, AsistenciaRepository asistenciaRepo,
                         BoletaPagoRepository boletaRepo, EmpleadoRepository empleadoRepo,
                         CloudinaryService cloudinaryService) {
        this.contratoRepo = contratoRepo;
        this.asistenciaRepo = asistenciaRepo;
        this.boletaRepo = boletaRepo;
        this.empleadoRepo = empleadoRepo;
        this.cloudinaryService = cloudinaryService;
    }

    // 1. CÁLCULO INDIVIDUAL (EN MEMORIA)
    public BoletaPago calcularBoletaIndividual(Integer idEmpleado, Integer mes, Integer anio) {
        Empleado emp = empleadoRepo.findById(idEmpleado)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        Contrato contrato = contratoRepo.findByEmpleadoIdEmpleadoAndVigenteTrue(idEmpleado)
                .orElseThrow(() -> new RuntimeException("El empleado no tiene contrato vigente para generar boleta."));

        // Cálculos financieros
        int diasDelMes = 30;
        long diasFaltas = asistenciaRepo.contarFaltasPorMes(idEmpleado, mes, anio);

        BigDecimal sueldoBase = contrato.getSueldoBase();
        BigDecimal valorDia = sueldoBase.divide(new BigDecimal(diasDelMes), 2, RoundingMode.HALF_UP);

        BigDecimal descuentoFaltas = valorDia.multiply(new BigDecimal(diasFaltas)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal sueldoBruto = sueldoBase.subtract(descuentoFaltas);

        BigDecimal tasaPension = "ONP".equals(contrato.getTipoRegimen()) ? TASA_ONP : TASA_AFP_PROM;
        BigDecimal descuentoPension = sueldoBruto.multiply(tasaPension).setScale(2, RoundingMode.HALF_UP);

        BigDecimal aporteEsSalud = sueldoBruto.multiply(TASA_ESSALUD).setScale(2, RoundingMode.HALF_UP);
        BigDecimal neto = sueldoBruto.subtract(descuentoPension).setScale(2, RoundingMode.HALF_UP);

        // Llenar Objeto
        BoletaPago boleta = new BoletaPago();
        boleta.setEmpleado(emp);
        boleta.setMes(mes);
        boleta.setAnio(anio);
        boleta.setSueldoBasicoCalculado(sueldoBase);
        boleta.setDiasFaltados((int) diasFaltas);
        boleta.setDescuentoFaltas(descuentoFaltas);
        boleta.setDescuentoPension(descuentoPension);
        boleta.setAporteEsSalud(aporteEsSalud);
        boleta.setNetoPagar(neto);

        return boleta;
    }

    // 2. GENERAR PDF (BYTES)
    public byte[] generarPdfBoletaBytes(BoletaPago boleta) throws DocumentException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Estilos
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new BaseColor(0, 48, 87));
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
        BaseColor colorPrimario = new BaseColor(0, 48, 87);

        // Encabezado
        Paragraph titulo = new Paragraph("BOLETA DE PAGO", titleFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph periodo = new Paragraph("Periodo: " + getMesNombre(boleta.getMes()) + " " + boleta.getAnio(), boldFont);
        periodo.setAlignment(Element.ALIGN_CENTER);
        periodo.setSpacingAfter(20);
        document.add(periodo);

        // Datos del Trabajador
        PdfPTable tablaDatos = new PdfPTable(2);
        tablaDatos.setWidthPercentage(100);
        addCeldaDatos(tablaDatos, "Trabajador:", boleta.getEmpleado().getPersona().getNombres(), boldFont, textFont);
        addCeldaDatos(tablaDatos, "Documento:", boleta.getEmpleado().getPersona().getNroDocumento(), boldFont, textFont);
        addCeldaDatos(tablaDatos, "Cargo:", boleta.getEmpleado().getCargo().getNombre(), boldFont, textFont);
        addCeldaDatos(tablaDatos, "Fecha Ingreso:", boleta.getEmpleado().getFechaIngreso().toString(), boldFont, textFont);
        document.add(tablaDatos);
        document.add(new Paragraph("\n"));

        // Tabla Conceptos
        PdfPTable tablaConceptos = new PdfPTable(3);
        tablaConceptos.setWidthPercentage(100);
        tablaConceptos.setWidths(new float[]{4, 1.5f, 1.5f});

        addCeldaHeader(tablaConceptos, "CONCEPTO", colorPrimario);
        addCeldaHeader(tablaConceptos, "INGRESOS", colorPrimario);
        addCeldaHeader(tablaConceptos, "DESCUENTOS", colorPrimario);

        addFilaConcepto(tablaConceptos, "Sueldo Básico", boleta.getSueldoBasicoCalculado(), null, textFont);

        if (boleta.getDiasFaltados() > 0) {
            addFilaConcepto(tablaConceptos, "Faltas (" + boleta.getDiasFaltados() + " días)", null, boleta.getDescuentoFaltas(), textFont);
        }

        addFilaConcepto(tablaConceptos, "Fondo Pensión", null, boleta.getDescuentoPension(), textFont);

        // Relleno
        addFilaConcepto(tablaConceptos, " ", null, null, textFont);
        addFilaConcepto(tablaConceptos, " ", null, null, textFont);

        // Totales
        BigDecimal totalIngresos = boleta.getSueldoBasicoCalculado();
        BigDecimal totalDescuentos = boleta.getDescuentoFaltas().add(boleta.getDescuentoPension());

        addFilaTotal(tablaConceptos, "TOTALES", totalIngresos, totalDescuentos, boldFont);
        document.add(tablaConceptos);
        document.add(new Paragraph("\n"));

        // Neto
        Paragraph neto = new Paragraph("NETO A PAGAR: S/ " + boleta.getNetoPagar(), titleFont);
        neto.setAlignment(Element.ALIGN_RIGHT);
        document.add(neto);

        document.close();
        return out.toByteArray();
    }

    // 3. GUARDAR DEFINITIVO (INDIVIDUAL)
    @Transactional
    public BoletaPago guardarBoletaDefinitiva(Integer idEmpleado, Integer mes, Integer anio, String username) throws Exception {
        BoletaPago boleta = calcularBoletaIndividual(idEmpleado, mes, anio);
        byte[] pdfBytes = generarPdfBoletaBytes(boleta);

        // --- CORRECCIÓN AQUÍ ---
        // Usamos el método uploadBytes que creamos en CloudinaryService
        String nombreArchivo = "Boleta_" + idEmpleado + "_" + mes + "_" + anio + "_" + System.currentTimeMillis();
        String url = cloudinaryService.uploadBytes(pdfBytes, nombreArchivo);

        boleta.setUrlArchivo(url);
        boleta.setEstado("DISPONIBLE");
        boleta.setSubidoPor(username);

        return boletaRepo.save(boleta);
    }

    // 4. GENERACIÓN MASIVA (Opcional, pero necesario para evitar errores de compilación si el controller lo llama)
    @Transactional
    public void generarPlanillaMasiva(Integer mes, Integer anio, String usuarioGenerador) {
        List<Empleado> empleados = empleadoRepo.findAll();
        for (Empleado emp : empleados) {
            try {
                if (contratoRepo.findByEmpleadoIdEmpleadoAndVigenteTrue(emp.getIdEmpleado()).isPresent()) {
                    guardarBoletaDefinitiva(emp.getIdEmpleado(), mes, anio, usuarioGenerador);
                }
            } catch (Exception e) {
                System.out.println("Error generando boleta masiva para ID " + emp.getIdEmpleado() + ": " + e.getMessage());
            }
        }
    }

    // --- MÉTODOS AUXILIARES ---
    private void addCeldaHeader(PdfPTable table, String text, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE)));
        cell.setBackgroundColor(bg);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addFilaConcepto(PdfPTable table, String desc, BigDecimal ing, BigDecimal descMonto, Font font) {
        table.addCell(new PdfPCell(new Phrase(desc, font)));

        PdfPCell cIng = new PdfPCell(new Phrase(ing != null ? ing.toString() : "", font));
        cIng.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cIng);

        PdfPCell cDesc = new PdfPCell(new Phrase(descMonto != null ? descMonto.toString() : "", font));
        cDesc.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cDesc);
    }

    private void addFilaTotal(PdfPTable table, String desc, BigDecimal ing, BigDecimal descMonto, Font font) {
        table.addCell(new PdfPCell(new Phrase(desc, font)));
        PdfPCell cIng = new PdfPCell(new Phrase(ing.toString(), font)); cIng.setHorizontalAlignment(Element.ALIGN_RIGHT); table.addCell(cIng);
        PdfPCell cDesc = new PdfPCell(new Phrase(descMonto.toString(), font)); cDesc.setHorizontalAlignment(Element.ALIGN_RIGHT); table.addCell(cDesc);
    }

    private void addCeldaDatos(PdfPTable table, String label, String val, Font bold, Font norm) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " ", bold));
        p.add(new Chunk(val, norm));
        PdfPCell c = new PdfPCell(p);
        c.setBorder(0);
        table.addCell(c);
    }

    private String getMesNombre(int m) {
        String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        return (m > 0 && m <= 12) ? meses[m-1] : "";
    }
}