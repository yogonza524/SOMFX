package com.core.somfx;

import java.io.PrintStream;

public class SOMManager
{
  public SOMManager() {}
  
  public static boolean validarDimensionArreglo(int dimension, String arreglo)
  {
    String formateado = arreglo.replaceAll("\\s+", "").replaceAll("\\n+", "");
    String[] validar = formateado.split(";");
    boolean result = validar.length > 0;
    System.out.println("Formateado: " + formateado + "\nValido: " + result);
    for (String v : validar) {
      String[] patron = v.split(",");
      if (patron.length != dimension) {
        result = false;
        break;
      }
    }
    return result;
  }
}
