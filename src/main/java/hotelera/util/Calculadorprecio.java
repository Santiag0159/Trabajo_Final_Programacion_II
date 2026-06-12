package hotelera.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Calculadorprecio{
    
    public static long calcularDias(LocalDate ingreso,LocalDate salida){
        return ChronoUnit.DAYS.between(ingreso,salida);
        //Chrono calcula diferencias exactas en dias
    }
    
    public static double calcularTotal(long dias, double precioPorNoche){
        return dias * precioPorNoche;
    }
    
}
