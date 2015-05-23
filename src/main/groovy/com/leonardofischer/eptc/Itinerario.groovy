package com.leonardofischer.eptc

/**
 * Um itinerário de uma linha
 */
class Itinerario {

    /**
     * Direção em que o itinerário é valido
     */
    String direcao

    /**
     * Lista de logradouros deste itinerário, em ordem que é percorrido pela 
     * linha na direçao acima, segundo o site da EPTC.
     */
    List<String> logradouros
}
