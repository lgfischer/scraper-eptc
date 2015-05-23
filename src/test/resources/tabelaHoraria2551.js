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
    equal( horarios.length, 3 );

    equal( horarios[0].direcao, "BAIR/CENT/BAIR" );
    equal( horarios[1].direcao, "BAIR/CENT/BAIR" );
    equal( horarios[2].direcao, "BAIR/CENT/BAIR" );

    ok( horarios[0].validade.match(/Dias \Steis/), "(0) Dias Uteis == " + horarios[0].validade );
    ok( horarios[1].validade.match(/S\Sbados/), "(1) Sabados == " + horarios[1].validade );
    equal( horarios[2].validade, "Domingos" );

    equal( horarios[0].horarios.length, 50 );
    equal( horarios[1].horarios.length, 45 );
    equal( horarios[2].horarios.length, 16 );

    equal( horarios[0].intervaloMin, 10 );
    equal( horarios[1].intervaloMin, 20 );
    equal( horarios[2].intervaloMin, 40 );

    equal( horarios[0].intervaloMed, 21 );
    equal( horarios[1].intervaloMed, 22 );
    equal( horarios[2].intervaloMed, 40 );

    equal( horarios[0].intervaloMax, 52 );
    equal( horarios[1].intervaloMax, 27 );
    equal( horarios[2].intervaloMax, 40 );

    // first
    equal( horarios[0].horarios[0].horario, "05:40" );
    equal( horarios[1].horarios[0].horario, "06:20" );
    equal( horarios[2].horarios[0].horario, "10:20" );

    // last
    equal( horarios[0].horarios[horarios[0].horarios.length-1].horario, "23:05" );
    equal( horarios[1].horarios[horarios[1].horarios.length-1].horario, "22:35" );
    equal( horarios[2].horarios[horarios[2].horarios.length-1].horario, "20:20" );

    // 12
    equal( horarios[0].horarios[12].horario, "09:30" );
    equal( horarios[1].horarios[12].horario, "10:34" );
    equal( horarios[2].horarios[12].horario, "18:20" );

    equal( countAcessiveis(horarios[0]), 19 );
    equal( countAcessiveis(horarios[1]), 0 );
    equal( countAcessiveis(horarios[2]), 0 );
});
