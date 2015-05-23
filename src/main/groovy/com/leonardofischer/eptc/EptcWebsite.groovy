package com.leonardofischer.eptc

import com.leonardofischer.util.ClasspathUtils

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

/**
 * Classe responsável por realizar o scraping do site da EPTC
 */
class EptcWebsite {

    /**
     * Driver selenium utilizado para comunicar com o navegador.
     */
    WebDriver driver

    /**
     * Código JavaScript que será injetado nas páginas da EPTC durante o 
     * scraping. O código é carregado no seu primeiro uso do arquivo
     * com/leonardofischer/eptc/functions.js
     */
    private static String functionsJs = null;


    /**
     * Coordena todo o processo de scraping, percorrendo cada uma das páginas
     * e retornando todos os dados escrapeados.
     * @param urlEptc url principal do site da eptc com os horários
     * @param temporaryOutput um arquivo onde será salvo os dados temporariamente
     *                        durante o processo
     * @return lista de consorcios, onde cada consorcio possui as linhas 
     *         escrapeadas do site da EPTC.
     */
    List<Consorcio> getDadosEptc(String urlEptc, File temporaryOutput = null, File errorsOutput = null) {
        def consorcios = getConsorcios(urlEptc)

        int totalLinhas = 0
        consorcios.each { cons ->
            cons.linhas = getLinhas(cons.url)
            totalLinhas += cons.linhas.size()
        }

        int linhaAtual = 0
        consorcios.each { cons ->
            cons.linhas.each { linha ->

                // para após 10 linhas. Debug only
                //if( linhaAtual>=10 ) {
                //    return
                //}

                runWithRetry(8) {
                    linha.tabelasHorarios = getTabelasHorarios(linha.urlHorarios)
                    if( linha.tabelasHorarios==null && errorsOutput!=null ) {
                        (new File(linha.codigo+"-horario.html", errorsOutput)).text = driver.pageSource
                    }
                    linha.itinerarios = getItinerarios(linha.urlItinerarios)
                    if( linha.itinerarios==null && errorsOutput!=null ) {
                        (new File(linha.codigo+"-itinerario.html", errorsOutput)).text = driver.pageSource
                    }
                    linha.linhasRelacionadas = getLinhasRelacionadas()

                    temporaryOutput?.write( JsonOutput.toJson(consorcios) )
                    linhaAtual++
                    if( linha.tabelasHorarios==null || linha.itinerarios==null ) {
                        println "> Possivel problema em ${linha.id} - ${linha.codigo} - ${linha.nome}"
                    }
                    println "Progresso: ${linhaAtual}/${totalLinhas}"
                }
            }
        }

        temporaryOutput?.delete()

        return consorcios
    }

    /**
     * Executa a closure por até maxRetries vezes, até que ela finalize
     * sem lançar excessões. Se a closure lançar uma excessão, aguarda um
     * delay de "2^num_tentativas" segundos e tenta novamente. Se o número
     * de tentativas não for suficiente, relança a excessão.
     *
     * Método necessário para tratar eventuais erros de rede e travamentos
     * no site da EPTC (que parece travar por vários segundos a cada ~50
     * chamadas seguidas).
     * @param maxRetries número máximo de tentativas
     * @param c          closure a ser executada
     */
    private void runWithRetry(int maxRetries, Closure c) {
        int retry = 0
        boolean done = false
        while( retry<maxRetries && !done ) {
            retry++
            try {
                c()
                done = true
            }
            catch(Exception e) {
                if( retry<maxRetries ) {
                    def t = (2 << retry)
                    println "Erro! Aguardando ${t}s"
                    sleep( t*1000 )
                }
                else {
                    throw e
                }
            }                    
        }
    }

    /**
     * Retorna a lista de consorcios exibida na página principal do site
     * da EPTC.
     */
    List<Consorcio> getConsorcios(String urlEptc) {
        driver.get(urlEptc)
        def links = driver.findElements(By.xpath("//a[contains(@href, 'Linha.asp')]"))

        return links.collect { link ->
            new Consorcio(nome: link.text, url:link.getAttribute('href') )
        }
    }

    /**
     * Retorna a lista de linhas de um consorcio de empresas.
     *
     * Note que, por questões de performance, processa a função 'getLinhas()'
     * do arquivo functions.js no browser, e converte o JSON retornado para
     * as classes do scraper. Ver callJsonFunction para detalhes.
     */
    List<Linha> getLinhas(String urlConsorcio) {
        driver.get(urlConsorcio)
        def jsonObj = callJsonFunction("getLinhas")

        return jsonObj.collect { linha ->
            new Linha(id:linha.id, codigo:linha.codigo, nome:linha.nome, urlItinerarios:linha.urlItinerarios, urlHorarios:linha.urlHorarios)
        }
    }

    /**
     * Retorna a lista de tabelas de horários de uma linha.
     *
     * Note que, por questões de performance, processa a função 'getTabelasHorarios()'
     * do arquivo functions.js no browser, e converte o JSON retornado para
     * as classes do scraper. Ver callJsonFunction para detalhes.
     */
    List<TabelaHorarios> getTabelasHorarios(String urlTabelasHorarios) {
        driver.get(urlTabelasHorarios)
        def jsonObj = callJsonFunction("getTabelasHorarios")

        if( jsonObj instanceof Map ) {
            if( jsonObj.invalidHtmlFormat ) {
                println "Erro: HTML em formato nao esperado na URL '${urlTabelasHorarios}'. "+
                    "Nao é possivel extrair a tabela de horarios desta URL."
                return null
            }
            if( jsonObj.serverTimeOut ) {
                println "Erro: ocorreu timeout ao processar a tabela de horarios na URL '${urlTabelasHorarios}'"
                return null
            }
        }

        // Converte o objeto JSON para o formato da estrutura interna
        return jsonObj.collect { tb ->
            def horarios = tb.horarios.collect{ new Horario(horario:it.horario, acessibilidade:it.acessibilidade) }
            new TabelaHorarios( direcao:tb.direcao, validade:tb.validade, horarios:tb.horarios, 
                intervaloMin:tb.intervaloMin, intervaloMed:tb.intervaloMed, intervaloMax:tb.intervaloMax )
        }
    }

    /**
     * Retorna a lista de itinerários de uma linha.
     *
     * Note que, por questões de performance, processa a função 'getItinerarios()'
     * do arquivo functions.js no browser, e converte o JSON retornado para
     * as classes do scraper. Ver callJsonFunction para detalhes.
     */
    List<Itinerario> getItinerarios(String urlItinerarios) {
        driver.get(urlItinerarios)
        def jsonObj = callJsonFunction("getItinerarios")

        if( jsonObj instanceof Map ) {
            if( jsonObj.invalidHtmlFormat ) {
                println "Erro: HTML em formato nao esperado na URL '${urlTabelasHorarios}'. "+
                    "Nao é possivel extrair a tabela de itinerarios desta URL."
                return null
            }
            if( jsonObj.serverTimeOut ) {
                println "Erro: ocorreu timeout ao processar a tabela de horarios na URL '${urlTabelasHorarios}'"
                return null
            }
        }

        // Converte o objeto JSON para o formato da estrutura interna
        return jsonObj.collect {
            new Itinerario( direcao:it.direcao, logradouros:it.logradouros )
        }
    }

    /**
     * Retorna a lista de linhas relacionadas à uma linha.
     *
     * Note que, por questões de performance, processa a função 'getLinhasRelacionadas()'
     * do arquivo functions.js no browser, e converte o JSON retornado para
     * as classes do scraper. Ver callJsonFunction para detalhes.
     */
    List<String> getLinhasRelacionadas(String urlLinhasRelacionadas = null) {
        if( urlLinhasRelacionadas ) {
            driver.get(urlLinhasRelacionadas)
        }
        return callJsonFunction("getLinhasRelacionadas")
    }
 
    /**
     * Executa a função JavaScript indicada na página HTML, e retorna seu resultado.
     * 
     * Internamente, injeta o código javascript do arquivo 'com/leonardofischer/
     * eptc/functions.js' na página em exibição pelo driver, executa a função 
     * indicada, e retorna o resultado dela. JSON é utilizado para comunicar entre 
     * o navegador e o scraper.
     *
     * Optei por executar o processamento das páginas usando JS dentro do navegador
     * porque, apesar de funcionar corretamente, o Selenium é lento na comunicação
     * com o navegador. Se for pequeno o número de chamadas do Selenium à pagina 
     * web, a performance é pouco relevante. Mas quando processa uma centena de 
     * chamadas, o scraping de uma página pode levar minutos usando selenium. Por
     * exemplo, algumas páginas de horários de onibus possuem mais de 300 horários
     * (6 tabelas com 50 horários cada). Essas páginas levam minutos processando
     * via Selenium, mas pouco menos de 0,5s usando JavaScript (incluindo o tempo
     * de comunicação com o scraper).
     */
    private Object callJsonFunction(String functionName) {
        loadFunctionsJs()
        def jsonString = ((JavascriptExecutor)driver).executeScript("${functionsJs}; return JSON.stringify( ${functionName}() );")
        def jsonSlurper = new JsonSlurper()
        return jsonSlurper.parseText(jsonString)
    }

    /**
     * Carrega o arquivo 'com/leonardofischer/eptc/functions.js' em memória se
     * ainda não tiver sido carregado antes.
     */
    private loadFunctionsJs() {
        if( functionsJs==null ) {
            def path = "com/leonardofischer/eptc/functions.js"
            functionsJs = ClasspathUtils.getResourceAsString(path)
            if( !functionsJs ) {
                throw new IllegalStateException("Nao foi possivel carregar '${path}'")
            }
        }
    }
}
