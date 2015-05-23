/**
 * Obtem a lista de linhas de uma página de seleção de linhas. Retorna uma lista
 * de objetos. Cada objeto possui:
 * - id: o ID interno da linha no site da EPTC
 * - codigo: o código de identificação da linha (visivel nos letreiros de ônibus)
 * - nome: o nome da linha, conforme o site da EPTC
 * - urlItinerarios: a URL para obter os itinerários daquela linha
 * - urlHorarios: a URL para obter as tabelas de horários daquela linha
 */
function getLinhas() {
    var i;
    var linhas = [];
    var select = document.getElementsByName("Linha")[0];
    var options = select.options;
    for (i=0; i<options.length; ++i) {
        var option = options[i];
        var id = option.value;
        var index = option.text.indexOf(' - ');
        var codigo = option.text.substring(0, index).trim();
        var nome = option.text.substring(index+3, option.text.length).trim();
        var urlItinerarios = "http://www.eptc.com.br/EPTC_Itinerarios/Cadastro.asp?Tipo=I&Linha="+ id+"&Veiculo=1&Sentido=0&Logradouro=0&Action=Nada";
        var urlHorarios =    "http://www.eptc.com.br/EPTC_Itinerarios/Cadastro.asp?Tipo=TH&Linha="+id+"&Veiculo=1&Sentido=0&Logradouro=0&Action=Nada";
        linhas.push( {id:id, codigo:codigo, nome:nome, urlItinerarios:urlItinerarios, urlHorarios:urlHorarios} );
    }
    return linhas;
}

function getDirecoes() {
    return ["BAIRRO/CENTRO", "CENTRO/BAIRRO", "BAIRRO/TERMINAL", "TERMINAL/BAIRRO",
        "SUL/LESTE", "LESTE/SUL", "NORTE/SUL", "SUL/NORTE", "NORTE/LESTE", "LESTE/NORTE", 
        "BAIR/CENT/BAIR", "CENT/BAIR/CENT", "TERMINAL/BAIRRO/TERMINAL"];
}

/**
 * Processa uma página com as tabelas de horários, e retorna uma lista dessas tabelas.
 * A lista retornada possui uma lista de objetos com as seguintes entradas
 *
 * - direcao: a direção em que a tabela de horários é válida (em geral, centro/bairro ou bairro/centro)
 * - validade: o texto indicativo da validade da tabela (dias úteis, sábados, domingos)
 * - horarios: uma lista de horários (conforme abaixo)
 * - intervaloMin: o intervalo mínimo entre os horários (conforme EPTC)
 * - intervaloMed: o intervalo médio entre os horários (conforme EPTC)
 * - intervaloMax: o intervalo máximo entre os horários (conforme EPTC)
 *
 * Um objeto horário possui os seguintes campos
 * - horario: string com o horário (formato HH:MM)
 * - acessibilidade: booleano indicando se aquele horario possui veículo adaptado a portadores de deficiencia
 */
function getTabelasHorarios() {
    var tabelashorarios = [];
    var direcao;
    var validade;
    var intervaloMin;
    var intervaloMed;
    var intervaloMax;
    var horarios = [];

    var masterContainer = document.getElementById("caixa_perfil").parentElement;
    var children = masterContainer.childNodes;

    for (var i=0; i<children.length; ++i) {
        var child = children[i];

        var childTextContent = child.textContent;
        if( typeof childTextContent==='string') {
            var direcoes = getDirecoes();
            for(var k=0; k<direcoes.length; ++k) {
                if( childTextContent.indexOf( direcoes[k] )>=0 ) {
                    direcao = childTextContent.trim();
                }
            }

            if( childTextContent.indexOf("Nenhum registro encontrado!")>=0 ) {
                return [];
            }

            if( childTextContent.indexOf("Script timed out")>=0 ) {
                return {serverTimeOut:true};
            }
        }
        
        if(child.nodeName.toLowerCase()=='table' && child.getAttribute('bordercolor')=='#0B309B' ) {
            var texts = child.getElementsByTagName('b');
            for ( var j=0; j<texts.length; ++j ) {
                var t = texts[j].textContent;
                if( t.match(/\d+:\d+/) ) {
                    var acessibilidade = texts[j].getElementsByTagName('img').length > 0;
                    horarios.push( {horario:t.trim(), acessibilidade:acessibilidade} );
                }
                else if( t.match(/\S+/) ) { // ignore empty cells
                    validade = t.trim();
                }
            }
        }
        
        if(child.nodeName.toLowerCase()=='table' && child.getAttribute('bordercolor')==null ) {
            var texts = child.getElementsByTagName('td');
            for ( var j=0; j<texts.length; ++j ) {
                var t = texts[j].textContent;
                if( t.match(/m\Snimo/) ) { // matches 'mínimo'
                    intervaloMin = parseInt( t.replace(/\D*/, '') );
                }
                else if( t.match(/m\Sdio/) ) { // matches 'médio'
                    intervaloMed = parseInt( t.replace(/\D*/, '') );
                }
                else if( t.match(/m\Sximo/) ) { // matches 'máximo'
                    intervaloMax = parseInt( t.replace(/\D*/, '') );
                }
            }
        }

        if( direcao!=null && validade!=null && horarios.length>0 && 
                intervaloMin!=null && intervaloMed!=null && intervaloMax!=null ) {
            tabelashorarios.push({
                direcao:direcao,
                validade:validade,
                horarios:horarios,
                intervaloMin:intervaloMin,
                intervaloMed:intervaloMed,
                intervaloMax:intervaloMax
            });
            // direcao does not reset between tables, all other does
            validade = null;
            intervaloMin = null;
            intervaloMed = null;
            intervaloMax = null;
            horarios = [];
        }
    }

    if( tabelashorarios.length==0 ) {
        return {invalidHtmlFormat:true}
    }

    return tabelashorarios;
}

/**
 * Processa uma página de itinerários, retornando todos os itinerários para uma linha.
 * Retorna uma lista de objetos, onde cada objeto possui as seguintes entradas:
 *
 * - direcao: string com a direção do itinerário (em geral, bairro/centro ou centro/bairro)
 * - logradouros: lista de strings com os nomes dos logradouros percorridos pela linha, em ordem
 */
function getItinerarios() {
    var itinerarios = [];
    var logradouros = [];
    var direcao = null;

    var masterContainer = document.getElementById("caixa_perfil").parentElement;
    var children = masterContainer.childNodes;

    for (var i=0; i<children.length; ++i) {
        var child = children[i];

        var childTextContent = child.textContent;
        if( typeof childTextContent==='string') {
            var direcoes = getDirecoes();
            for(var k=0; k<direcoes.length; ++k) {
                if( childTextContent.indexOf( direcoes[k] )>=0 ) {
                    direcao = childTextContent.trim();
                }
            }

            if( childTextContent.indexOf("Nenhum registro encontrado!")>=0 ) {
                return [];
            }

            if( childTextContent.indexOf("Script timed out")>=0 ) {
                return {serverTimeOut:true};
            }
        }

        if(child.nodeName.toLowerCase()=='table' && child.getAttribute('bordercolor')=='#0B309B' ) {
            var texts = child.getElementsByTagName('b');
            for ( var j=0; j<texts.length; ++j ) {
                var t = texts[j].textContent;
                logradouros.push( t.trim().replace(/\s+/, ' ') );
            }
        }

        if( direcao!=null && logradouros.length>0 ) {
            itinerarios.push({direcao:direcao, logradouros:logradouros});
            direcao = null;
            logradouros = [];
        }
    }

    if( itinerarios.length==0 ) {
        return {invalidHtmlFormat:true}
    }

    return itinerarios;
}

/**
 * Processa uma página de itinerarios ou de tabelas de horarios, retornando a lista
 * de linhas relacionadas à linha em exibição.
 *
 * Retorna uma lista de strings, onde cada string é o ID da linha no site da EPTC.
 */
function getLinhasRelacionadas() {
    var relacionadas = [];

    var masterContainer = document.getElementById("caixa_perfil").parentElement;
    var children = masterContainer.childNodes;

    for (var i=0; i<children.length; ++i) {
        var child = children[i];

        if( (typeof child.textContent==='string') && child.textContent.indexOf("Linhas relacionadas")>=0 ) {
            var links = child.getElementsByTagName('a');
            for ( var j=0; j<links.length; ++j ) {
                var l = links[j];
                var href = l.getAttribute('href');
                var start = href.indexOf("Linha=") + "Linha=".length;
                var end = href.indexOf("&", start);
                relacionadas.push(href.substring(start, end));
            }
        }
    }
    return relacionadas;
}
