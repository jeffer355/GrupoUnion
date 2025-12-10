package utp.edu.pe.GrupoUnion.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import utp.edu.pe.GrupoUnion.entity.core.Empleado;
import utp.edu.pe.GrupoUnion.entity.management.BoletaPago;
import utp.edu.pe.GrupoUnion.entity.management.Contrato;
import utp.edu.pe.GrupoUnion.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class NominaService {

    private final ContratoRepository contratoRepo;
    private final AsistenciaRepository asistenciaRepo;
    private final BoletaPagoRepository boletaRepo;
    private final EmpleadoRepository empleadoRepo;

    // Tasas Referenciales (Estas tasas son aproximadas para el ejemplo)
    private final BigDecimal TASA_ONP = new BigDecimal("0.13"); // 13%
    private final BigDecimal TASA_ESSALUD = new BigDecimal("0.09"); // 9%
    // Promedio AFP (10% fondo + 1.7% seguro + 1.2% comisión = ~12.9%)
    private final BigDecimal TASA_AFP_PROM = new BigDecimal("0.129");
    private final BigDecimal SUELDO_MINIMO_VITAL = new BigDecimal("1130");

    public NominaService(ContratoRepository contratoRepo, AsistenciaRepository asistenciaRepo, BoletaPagoRepository boletaRepo, EmpleadoRepository empleadoRepo) {
        this.contratoRepo = contratoRepo;
        this.asistenciaRepo = asistenciaRepo;
        this.boletaRepo = boletaRepo;
        this.empleadoRepo = empleadoRepo;
    }

    @Transactional
    public void generarPlanillaMasiva(Integer mes, Integer anio, String usuarioGenerador) {
        List<Empleado> empleados = empleadoRepo.findAll();
        // Contar el número de días del mes para cálculos precisos (no 30 fijos)
        int diasDelMes = java.time.YearMonth.of(anio, mes).lengthOfMonth();

        for (Empleado emp : empleados) {
            // 1. Obtener Contrato Vigente
            Contrato contrato = contratoRepo.findByEmpleadoIdEmpleadoAndVigenteTrue(emp.getIdEmpleado()).orElse(null);
            if (contrato == null) continue;

            // 2. Calcular Faltas
            long diasFaltas = asistenciaRepo.contarFaltasPorMes(emp.getIdEmpleado(), mes, anio);

            // 3. Cálculos Financieros
            BigDecimal sueldoBase = contrato.getSueldoBase();

            // a) Descuento por Faltas: Fórmula: (Sueldo / Días del Mes) x Días Faltados
            BigDecimal valorDia = sueldoBase.divide(new BigDecimal(diasDelMes), 2, RoundingMode.HALF_UP);
            BigDecimal descuentoFaltas = valorDia.multiply(new BigDecimal(diasFaltas)).setScale(2, RoundingMode.HALF_UP);

            BigDecimal sueldoBrutoRestante = sueldoBase.subtract(descuentoFaltas);

            // b) Descuento por Pensión
            BigDecimal tasaPension = "ONP".equals(contrato.getTipoRegimen()) ? TASA_ONP : TASA_AFP_PROM;
            BigDecimal descuentoPension = sueldoBrutoRestante.multiply(tasaPension).setScale(2, RoundingMode.HALF_UP);

            // c) EsSalud (9% del empleador, no es descuento, es Aporte de la empresa)
            BigDecimal aporteEsSalud = sueldoBrutoRestante.multiply(TASA_ESSALUD).setScale(2, RoundingMode.HALF_UP);

            // d) Neto a Pagar = Sueldo Bruto Restante - Descuento Pensión
            BigDecimal neto = sueldoBrutoRestante.subtract(descuentoPension).setScale(2, RoundingMode.HALF_UP);

            // 4. Guardar Boleta (Simulada)
            BoletaPago boleta = new BoletaPago();
            boleta.setEmpleado(emp);
            boleta.setMes(mes);
            boleta.setAnio(anio);
            boleta.setEstado("GENERADA");
            boleta.setSubidoPor(usuarioGenerador + " (Auto)");

            boleta.setSueldoBasicoCalculado(sueldoBase);
            boleta.setDiasFaltados((int) diasFaltas);
            boleta.setDescuentoFaltas(descuentoFaltas);
            boleta.setDescuentoPension(descuentoPension);
            boleta.setAporteEsSalud(aporteEsSalud);
            boleta.setNetoPagar(neto);

            boletaRepo.save(boleta);
        }
    }
}