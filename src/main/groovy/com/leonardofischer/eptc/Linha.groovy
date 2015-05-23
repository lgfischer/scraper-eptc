package com.leonardofischer.eptc

/**
 * Uma linha de ônibus
 */
class Linha {

    /**
     * Identificador no site da EPTC da linha.
     */
    String id

    /**
     * O código da linha, visível nos letreiros dos ônibus.
     *
     * Note que em diversas linhas, o código e o ID da linha são distintos.
     * O ID deve ser utilizado dentro do site da EPTC, e o código deve ser
     * utilizado para exibição da linha para pessoas.
     */
    String codigo

    /**
     * O nome da linha
     */
    String nome

    /**
     * A URL utilizada para obter os itinerários da linha
     */
    String urlItinerarios

    /**
     * A lista de itinerários da linha.
     *
     * Lembrando, uma linha pode ter itinerários distintos de acordo com a direção
     * (bairro/centro vs centro/bairro)
     */
    List<Itinerario> itinerarios = []

    /**
     * A URL utilizada para obter as tabelas de horários da linha
     */
    String urlHorarios

    /**
     * As tabelas de horários desta linha.
     *
     * Lembrando, uma linha possui uma tabela de horários para cada direção,
     * e pode ter diferentes horários em diferentes dias da semana.
     */
    List<TabelaHorarios> tabelasHorarios = []

    /**
     * A lista de IDs de linhas relacionadas à essa, segundo o site da EPTC.
     */
    List<String> linhasRelacionadas = []

    String toString() {
        return "Linha(id:$id, codigo:$codigo, nome:$nome)"
    }
}
