package com.leonardofischer.eptc

/**
 * Um horário de partida de onibus do fim da linha.
 */
class Horario {

    /**
     * O horário, no formato HH:MM
     */
    String horario

    /**
     * Indica se a linha possui veículo adaptado para pessoas com deficiência
     */
    boolean acessibilidade

    String toString() {
        return "Horario(horario:$horario, acessibilidade:$acessibilidade)"
    }
}
