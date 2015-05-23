package com.leonardofischer.eptc

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class EptcScraperApp {
    
    /**
     * Método principal do scraper, utilizado inicializar o scraping
     */
    public static void main(String[] args) {
        (new File('dados/erros')).mkdirs()
        scrape()
    }

    /**
     * Realiza o scraping do site da EPTC, salvando os dados coletados em arquivo.
     */
    static scrape() {
        def scraper
        try {
            def output = new File('dados/dados.json')
            if( output.exists() ) {
                println "O arquivo '${output.absolutePath}' ja existe. Abortando."
                return
            }

            println "Inicializando"
            scraper = new EptcWebsite(driver: new org.openqa.selenium.firefox.FirefoxDriver())

            def dados = scraper.getDadosEptc("http://www.eptc.com.br/EPTC_Itinerarios/linha.asp",
                new File('dados/dados.json.tmp'),
                new File('dados/erros'))
            output.write( JsonOutput.toJson(dados) )

            buildSample(dados, new File('dados/sample.json'))

            println "Finalizado"
        }
        finally {
            scraper?.driver?.quit()
        }
    }

    /**
     * Metodo auxiliar para gerar o sample sem realizar todo o scrape do site
     */
    static load() {
        def jsonSlurper = new JsonSlurper()
        def consorcios = jsonSlurper.parseText(new File('dados/dados.json').text)
        buildSample(consorcios, new File('dados/sample.json'))
    }

    /**
     * Método auxiliar para gerar um "sample", com 3 linhas de dois consorcios, em
     * um JSON bem formatado.
     */
    static buildSample(def consorcios, def output) {
        println "Gerando sample.json"
        consorcios.removeAll{ consorcio -> consorcio.nome != "Carris" && consorcio.nome != "Unibus" }
        def codigos = ["D43", "343", "R31"]
        consorcios.each{ cons ->
            cons.linhas.removeAll{ linha -> !codigos.contains(linha.codigo) }
        }
        output.write( JsonOutput.prettyPrint(JsonOutput.toJson(consorcios)) )
    }
}
