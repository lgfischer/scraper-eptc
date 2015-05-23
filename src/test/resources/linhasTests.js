QUnit.done(function( details ) {
    var qunit = document.getElementById("qunit")
    qunit.setAttribute('total', details.total);
    qunit.setAttribute('passed', details.passed);
    qunit.setAttribute('failed', details.failed);
    qunit.setAttribute('runtime', details.runtime);
});

test("getLinhas", function() {
    var linhas = getLinhas();
    console.log(linhas);
    equal( linhas.length, 193 );

    //<option value="179-0">179   - SERRARIA                                          </option>
    equal( linhas[20].id, "179-0" );
    equal( linhas[20].codigo, "179" );
    equal( linhas[20].nome, "SERRARIA" );

    //<option value="210-23">R10   - RAPIDA RESTINGA NOVA/CAVALHADA                    </option>
    equal( linhas[180].id, "210-23" );
    equal( linhas[180].codigo, "R10" );
    equal( linhas[180].nome, "RAPIDA RESTINGA NOVA/CAVALHADA" );
});
