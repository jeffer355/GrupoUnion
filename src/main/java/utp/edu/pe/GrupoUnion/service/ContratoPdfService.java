package utp.edu.pe.GrupoUnion.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import utp.edu.pe.GrupoUnion.entity.management.Contrato;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class ContratoPdfService {

    public byte[] generarContratoPdf(Contrato contrato) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // CORRECCIÓN AQUÍ: Usamos 'getInstance' en lugar de 'write'
            PdfWriter.getInstance(document, out);

            document.open();

            // Fuentes
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font fontTexto = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font fontNegrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);

            // Título
            Paragraph titulo = new Paragraph("CONTRATO DE TRABAJO", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            document.add(titulo);

            // Preparar datos (Manejo de nulos para evitar errores)
            String nombreEmpleado = contrato.getEmpleado().getPersona().getNombres();
            String dni = contrato.getEmpleado().getPersona().getNroDocumento();
            String cargo = contrato.getEmpleado().getCargo().getNombre();
            String sueldo = contrato.getSueldoBase().toString();

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String fechaIni = contrato.getFechaInicio().format(fmt);
            String fechaFin = contrato.getFechaFin() != null ? contrato.getFechaFin().format(fmt) : "Indefinido";
            String regimen = contrato.getTipoRegimen();
            String afp = contrato.getNombreAfp() != null ? contrato.getNombreAfp() : "";

            // Cuerpo del contrato
            Paragraph parrafo1 = new Paragraph();
            parrafo1.setAlignment(Element.ALIGN_JUSTIFIED);
            parrafo1.setLeading(20f); // Espaciado entre líneas

            parrafo1.add(new Chunk("Conste por el presente documento, el contrato de trabajo que celebran de una parte ", fontTexto));
            parrafo1.add(new Chunk("GRUPO UNIÓN", fontNegrita));
            parrafo1.add(new Chunk(", y de la otra parte el Sr(a). ", fontTexto));
            parrafo1.add(new Chunk(nombreEmpleado, fontNegrita));
            parrafo1.add(new Chunk(", identificado con DNI N° " + dni + ".\n\n", fontTexto));

            parrafo1.add(new Chunk("PRIMERO: ", fontNegrita));
            parrafo1.add(new Chunk("El trabajador desempeñará el cargo de " + cargo + ".\n\n", fontTexto));

            parrafo1.add(new Chunk("SEGUNDO: ", fontNegrita));
            parrafo1.add(new Chunk("La remuneración básica será de S/ " + sueldo + " mensuales.\n\n", fontTexto));

            parrafo1.add(new Chunk("TERCERO: ", fontNegrita));
            parrafo1.add(new Chunk("El trabajador se encuentra bajo el régimen pensionario " + regimen + " " + afp + ".\n\n", fontTexto));

            parrafo1.add(new Chunk("CUARTO: ", fontNegrita));
            parrafo1.add(new Chunk("El contrato tiene vigencia desde el " + fechaIni + " hasta " + fechaFin + ".\n\n", fontTexto));

            document.add(parrafo1);

            // Firmas
            document.add(new Paragraph("\n\n\n\n\n\n"));

            Paragraph firmas = new Paragraph("__________________________             __________________________\n      Firma del Empleador                           Firma del Trabajador");
            firmas.setAlignment(Element.ALIGN_CENTER);
            document.add(firmas);

            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}