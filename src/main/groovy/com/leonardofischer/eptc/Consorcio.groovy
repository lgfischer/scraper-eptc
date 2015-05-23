package com.leonardofischer.eptc

/**
 * Container para as linhas de um consorcio de empresas de ônibus
 * de porto alegre (Unibus, Conorte, Carris, STS)
 */
class Consorcio {

    /**
     * Nome do Consorcio
     */
    String nome

    /**
     * Url da página do consorcio no site da EPTC
     */
    String url

    /**
     * Lista de linhas deste consorcio
     */
    List<Linha> linhas

    String toString() {
        return "Consorcio(nome:$nome)"
    }
}
