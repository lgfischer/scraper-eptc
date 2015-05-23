QUnit.done(function( details ) {
    var qunit = document.getElementById("qunit")
    qunit.setAttribute('total', details.total);
    qunit.setAttribute('passed', details.passed);
    qunit.setAttribute('failed', details.failed);
    qunit.setAttribute('runtime', details.runtime);
});

function countAcessiveis(tabelaHorarios) {
    var count = 0;
    for(var i=0; i<tabelaHorarios.horarios.length; ++i) {
        count += tabelaHorarios.horarios[i].acessibilidade ? 1 : 0;
    }
    return count;
}

test("getTabelasHorarios", function() {
    var horarios = getTabelasHorarios();
    console.log(horarios);
    equal( horarios.length, 2 );

    equal( horarios[0].direcao, "BAIRRO/CENTRO" );
    equal( horarios[1].direcao, "CENTRO/BAIRRO" );

    ok( horarios[0].validade.match(/Dias \Steis/), "(0) Dias Uteis == " + horarios[0].validade );
    ok( horarios[1].validade.match(/Dias \Steis/), "(1) Dias Uteis == " + horarios[1].validade );

    equal( horarios[0].horarios.length, 20 );
    equal( horarios[1].horarios.length, 17 );

    equal( horarios[0].intervaloMin, 30 );
    equal( horarios[1].intervaloMin, 30 );

    equal( horarios[0].intervaloMed, 55 );
    equal( horarios[1].intervaloMed, 56 );

    equal( horarios[0].intervaloMax, 92 );
    equal( horarios[1].intervaloMax, 115 );

    // first
    equal( horarios[0].horarios[0].horario, "05:20" );
    equal( horarios[1].horarios[0].horario, "06:35" );

    // last
    equal( horarios[0].horarios[horarios[0].horarios.length-1].horario, "22:55" );
    equal( horarios[1].horarios[horarios[1].horarios.length-1].horario, "21:45" );

    // 12
    equal( horarios[0].horarios[12].horario, "16:05" );
    equal( horarios[1].horarios[12].horario, "16:50" );

    equal( countAcessiveis(horarios[0]), 4 );
    equal( countAcessiveis(horarios[1]), 4 );
});
