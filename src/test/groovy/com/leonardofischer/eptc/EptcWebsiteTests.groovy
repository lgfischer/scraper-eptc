package com.leonardofischer.eptc

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait

import org.junit.Test
import org.junit.BeforeClass
import org.junit.AfterClass
import static org.junit.Assert.*

class EptcWebsiteTests {

    static EptcWebsite scraper

    @BeforeClass
    static void initialize() {
        scraper = new EptcWebsite(driver: new FirefoxDriver())
    }

    @AfterClass
    static void finalize() {
        scraper.driver.quit()
    }

    @Test
    void testGetConcessionarias() {
        def concs = scraper.getConsorcios( new File("./src/test/resources/consorcio.html").toURI().toURL().toString() )
        assertEquals 4, concs.size()

        assertEquals "Unibus", concs[0].nome
        assertEquals "http://www.eptc.com.br/EPTC_Itinerarios/Linha.asp?cdEmp=23", concs[0].url
    }

    @Test
    void testGetLinhas() {
        def linhas = scraper.getLinhas( new File("./src/test/resources/consorcio.html").toURI().toURL().toString() )
        assertEquals 193, linhas.size()

        //<option value="179-0">179   - SERRARIA                                          </option>
        assertEquals "179-0", linhas[20].id
        assertEquals "179", linhas[20].codigo
        assertEquals "SERRARIA", linhas[20].nome

        //<option value="210-23">R10   - RAPIDA RESTINGA NOVA/CAVALHADA                    </option>
        assertEquals "210-23", linhas[180].id
        assertEquals "R10", linhas[180].codigo
        assertEquals "RAPIDA RESTINGA NOVA/CAVALHADA", linhas[180].nome
    }

    @Test
    void testGetTabelasHorariosJS() {
        scraper.driver.get( new File("./src/test/resources/tabelahoraria-with-js.html").toURI().toURL().toString() )
        def qunit = scraper.driver.findElement( By.id("qunit") )
        assertTrue qunit.getAttribute("passed").toLong() > 0
        assertTrue qunit.getAttribute("failed").toLong() == 0
    }

    @Test
    void testGetTabelasHorarios() {
        def tabHorarios = scraper.getTabelasHorarios( new File("./src/test/resources/tabelahoraria.html").toURI().toURL().toString() )

        assertEquals 6, tabHorarios.size()

        // check just one table. The full test is in the 'functionTests.js'
        assertEquals "BAIRRO/CENTRO", tabHorarios[0].direcao
        assertEquals "Dias Úteis", tabHorarios[0].validade
        assertEquals 70, tabHorarios[0].horarios.size()
        assertEquals 8, tabHorarios[0].horarios.findAll{it.acessibilidade}.size()
        assertEquals "04:00", tabHorarios[0].horarios[0].horario
        assertEquals "00:20", tabHorarios[0].horarios[69].horario


        tabHorarios = scraper.getTabelasHorarios( new File("./src/test/resources/tabelahoraria-b25.html").toURI().toURL().toString() )
        assertEquals 3, tabHorarios.size()
        assertEquals "BAIR/CENT/BAIR", tabHorarios[0].direcao
        assertEquals "Dias Úteis", tabHorarios[0].validade
        assertEquals 50, tabHorarios[0].horarios.size()
        assertEquals 19, tabHorarios[0].horarios.findAll{it.acessibilidade}.size()
        assertEquals "05:40", tabHorarios[0].horarios[0].horario
        assertEquals "23:05", tabHorarios[0].horarios[49].horario


        tabHorarios = scraper.getTabelasHorarios( new File("./src/test/resources/tabelahoraria-3412.html").toURI().toURL().toString() )
        assertEquals 6, tabHorarios.size()
        assertEquals "SUL/LESTE", tabHorarios[0].direcao
        assertEquals "Dias Úteis", tabHorarios[0].validade
        assertEquals 13, tabHorarios[0].horarios.size()
        assertEquals 8, tabHorarios[0].horarios.findAll{it.acessibilidade}.size()
        assertEquals "05:05", tabHorarios[0].horarios[0].horario
        assertEquals "23:40", tabHorarios[0].horarios[tabHorarios[0].horarios.size()-1].horario


        tabHorarios = scraper.getTabelasHorarios( new File("./src/test/resources/tabelahoraria-2721.html").toURI().toURL().toString() )
        assertEquals 2, tabHorarios.size()
        assertEquals "BAIRRO/CENTRO", tabHorarios[0].direcao
        assertEquals "Dias Úteis", tabHorarios[0].validade
        assertEquals 20, tabHorarios[0].horarios.size()
        assertEquals 4, tabHorarios[0].horarios.findAll{it.acessibilidade}.size()
        assertEquals "05:20", tabHorarios[0].horarios[0].horario
        assertEquals "22:55", tabHorarios[0].horarios[tabHorarios[0].horarios.size()-1].horario

        tabHorarios = scraper.getTabelasHorarios( new File("./src/test/resources/tabelahoraria-2551.html").toURI().toURL().toString() )
        assertEquals( [], tabHorarios )

        tabHorarios = scraper.getTabelasHorarios( new File("./src/test/resources/tabelahoraria-vazia.html").toURI().toURL().toString() )
        assertEquals( [], tabHorarios )

        tabHorarios = scraper.getTabelasHorarios( new File("./src/test/resources/timeout.html").toURI().toURL().toString() )
        assertEquals( null, tabHorarios )
    }

    @Test
    void testGetItinerariosJS() {
        scraper.driver.get( new File("./src/test/resources/itinerario-with-js.html").toURI().toURL().toString() )
        def qunit = scraper.driver.findElement( By.id("qunit") )
        assertTrue qunit.getAttribute("passed").toLong() > 0
        assertTrue qunit.getAttribute("failed").toLong() == 0
    }

    @Test
    void testItinerarios() {
        def itinerarios = scraper.getItinerarios( new File("./src/test/resources/itinerario.html").toURI().toURL().toString() )

        assertEquals 2, itinerarios.size()

        // check just one table. The full test is in the 'functionTests.js'
        assertEquals "BAIRRO/CENTRO", itinerarios[0].direcao
        assertEquals( 18, itinerarios[0].logradouros.size() )
        assertEquals( "TERM.NILO WULFF/AC A SQ CINCO", itinerarios[0].logradouros[0] )
        assertEquals( "ESTR JOAO ANTONIO SILVEIRA", itinerarios[1].logradouros[15] )

        itinerarios = scraper.getItinerarios( new File("./src/test/resources/itinerario-6219.html").toURI().toURL().toString() )
        assertEquals( [], itinerarios )
    }

    @Test
    void testGetLinhasRelacionadas() {
        def relacionadas = scraper.getLinhasRelacionadas( new File("./src/test/resources/itinerario.html").toURI().toURL().toString() )

        assertEquals( 18, relacionadas.size() )

        assertEquals( "210-81", relacionadas[0] )
        assertEquals( "210-74", relacionadas[5] )
        assertEquals( "210-97", relacionadas[10] )
        assertEquals( "210-23", relacionadas[15] )
        assertEquals( "210-33", relacionadas[17] )
    }
}
