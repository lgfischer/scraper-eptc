package com.leonardofischer.eptc

/**
 * Uma tabela de horários de uma linha.
 */
class TabelaHorarios {

    /**
     * A direção em que uma tabela de horários é valida.
     */
    String direcao

    /**
     * A validade dessa tabela de horários (dias úteis, sábados, domingos)
     */
    String validade

    /**
     * A lista de horários desta tabela.
     *
     * Note que os horários aqui mencionados referen-se ao horário de saída do
     * onibus do fim da linha.
     */
    List<Horario> horarios

    int intervaloMin
    int intervaloMed
    int intervaloMax

    String toString() {
        return "TabelaHorarios(direcao:$direcao, validade:$validade)"
    }
}
