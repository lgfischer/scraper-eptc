QUnit.done(function( details ) {
    var qunit = document.getElementById("qunit")
    qunit.setAttribute('total', details.total);
    qunit.setAttribute('passed', details.passed);
    qunit.setAttribute('failed', details.failed);
    qunit.setAttribute('runtime', details.runtime);
});

test("getItinerarios", function() {
    var itinerarios = getItinerarios();

    equal( itinerarios.length, 2 );

    equal( itinerarios[0].direcao, "BAIRRO/CENTRO" );
    equal( itinerarios[1].direcao, "CENTRO/BAIRRO" );

    equal( itinerarios[0].logradouros.length, 18);
    equal( itinerarios[0].logradouros[0], "TERM. NILO WULFF/AC A SQ CINCO" );
    equal( itinerarios[0].logradouros[5], "R COMENDADOR CASTRO" );
    equal( itinerarios[0].logradouros[10], "AV CHUI" );
    equal( itinerarios[0].logradouros[15], "AC HAMMOND" );
    equal( itinerarios[0].logradouros[17], "TERM. LOUREIRO DA SILVA" );

    equal( itinerarios[1].logradouros.length, 18);
    equal( itinerarios[1].logradouros[0], "TERM. LOUREIRO DA SILVA" );
    equal( itinerarios[1].logradouros[5], "AV BORGES DE MEDEIROS" );
    equal( itinerarios[1].logradouros[10], "AV CEL MARCOS" );
    equal( itinerarios[1].logradouros[15], "ESTR JOAO ANTONIO SILVEIRA" );
    equal( itinerarios[1].logradouros[17], "TERM. NILO WULFF/AC A SQ CINCO" );
});

test("getLinhasRelacionadas", function() {
    var relacionadas = getLinhasRelacionadas();

    equal( relacionadas.length, 18 );

    equal( relacionadas[0], "210-81" );
    equal( relacionadas[5], "210-74" );
    equal( relacionadas[10], "210-97" );
    equal( relacionadas[15], "210-23" );
    equal( relacionadas[17], "210-33" );
});
