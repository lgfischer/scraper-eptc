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
    equal( horarios.length, 6 );

    equal( horarios[0].direcao, "BAIRRO/CENTRO" );
    equal( horarios[1].direcao, "BAIRRO/CENTRO" );
    equal( horarios[2].direcao, "BAIRRO/CENTRO" );
    equal( horarios[3].direcao, "CENTRO/BAIRRO" );
    equal( horarios[4].direcao, "CENTRO/BAIRRO" );
    equal( horarios[5].direcao, "CENTRO/BAIRRO" );

    ok( horarios[0].validade.match(/Dias \Steis/), "(0) Dias Uteis == " + horarios[0].validade );
    ok( horarios[3].validade.match(/Dias \Steis/), "(3) Dias Uteis == " + horarios[3].validade  );
    ok( horarios[1].validade.match(/S\Sbados/), "(1) Sabados == " + horarios[1].validade );
    ok( horarios[4].validade.match(/S\Sbados/), "(4) Sabados == " + horarios[4].validade );
    equal( horarios[2].validade, "Domingos" );
    equal( horarios[5].validade, "Domingos" );

    equal( horarios[0].horarios.length, 70 );
    equal( horarios[1].horarios.length, 68 );
    equal( horarios[2].horarios.length, 56 );
    equal( horarios[3].horarios.length, 69 );
    equal( horarios[4].horarios.length, 68 );
    equal( horarios[5].horarios.length, 56 );

    equal( horarios[0].intervaloMin, 10 );
    equal( horarios[1].intervaloMin, 10 );
    equal( horarios[2].intervaloMin, 12 );
    equal( horarios[3].intervaloMin, 10 );
    equal( horarios[4].intervaloMin, 7 );
    equal( horarios[5].intervaloMin, 12 );

    equal( horarios[0].intervaloMed, 17 );
    equal( horarios[1].intervaloMed, 18 );
    equal( horarios[2].intervaloMed, 22 );
    equal( horarios[3].intervaloMed, 17 );
    equal( horarios[4].intervaloMed, 18 );
    equal( horarios[5].intervaloMed, 22 );

    equal( horarios[0].intervaloMax, 66 );
    equal( horarios[1].intervaloMax, 55 );
    equal( horarios[2].intervaloMax, 55 );
    equal( horarios[3].intervaloMax, 52 );
    equal( horarios[4].intervaloMax, 50 );
    equal( horarios[5].intervaloMax, 55 );

    // first
    equal( horarios[0].horarios[0].horario, "04:00" );
    equal( horarios[1].horarios[0].horario, "04:05" );
    equal( horarios[2].horarios[0].horario, "04:00" );
    equal( horarios[3].horarios[0].horario, "05:00" );
    equal( horarios[4].horarios[0].horario, "04:55" );
    equal( horarios[5].horarios[0].horario, "04:43" );

    // last
    equal( horarios[0].horarios[horarios[0].horarios.length-1].horario, "00:20" );
    equal( horarios[1].horarios[horarios[1].horarios.length-1].horario, "00:30" );
    equal( horarios[2].horarios[horarios[2].horarios.length-1].horario, "00:30" );
    equal( horarios[3].horarios[horarios[3].horarios.length-1].horario, "01:05" );
    equal( horarios[4].horarios[horarios[4].horarios.length-1].horario, "01:13" );
    equal( horarios[5].horarios[horarios[5].horarios.length-1].horario, "01:13" );

    // 25
    equal( horarios[0].horarios[25].horario, "11:45" );
    equal( horarios[1].horarios[25].horario, "12:18" );
    equal( horarios[2].horarios[25].horario, "14:10" );
    equal( horarios[3].horarios[25].horario, "12:15" );
    equal( horarios[4].horarios[25].horario, "13:06" );
    equal( horarios[5].horarios[25].horario, "14:58" );

    equal( countAcessiveis(horarios[0]), 8 );
    equal( countAcessiveis(horarios[1]), 3 );
    equal( countAcessiveis(horarios[2]), 2 );
    equal( countAcessiveis(horarios[3]), 8 );
    equal( countAcessiveis(horarios[4]), 1 );
    equal( countAcessiveis(horarios[5]), 2 );
});
