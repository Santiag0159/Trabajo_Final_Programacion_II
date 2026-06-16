package hotelera.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Calculadorprecio {
    public static long calcularDias(LocalDate ingreso, LocalDate salida) {
        return ChronoUnit.DAYS.between(ingreso, salida);
    }
    
    public static double calcularTotal(long dias, double precioPorNoche) {
        return dias * precioPorNoche;
    }
}