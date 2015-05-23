Scraper EPTC
============

Scraper dos dados do site de Itinerários e Horários de Ônibus da EPTC Porto Alegre (http://www.eptc.com.br/EPTC_Itinerarios/linha.asp). Ele é capaz de extrair em formato estruturado todos os dados (tabelas de horários e itinerários) de todas as linhas de ônibus disponíveis no site da EPTC.

Para mais informações sobre scraping, veja http://en.wikipedia.org/wiki/Web_scraping

**English docs**: This project was developed for use by Brazilian developers, who speak Portuguese. If you need an English version of the documentation, please open a new Issue and I will include it =)

Dados
-----

Caso você esteja interessado apenas no download dos dados, eles estão disponíveis no diretório `dados` deste projeto. 

A execução do scraper é necessária apenas se você deseja garantir que possui a última versão dos dados publicada no site da EPTC. Os dados atualmente disponíveis com o projeto foram atualizados em 23 de maio de 2015.


Formato de Dados
----------------

Os dados são salvos em formato JSON. Utilize um parser JSON da sua linguagem de preferência para realizar a leitura e processamento dos horários e itinerários.

Ao realizar a abertura do arquivo `dados.json`, terá uma lista de objetos do tipo `Consorcio`, conforme referência à seguir. Como exemplo, verifique o arquivo `sample.json`, possui uma pequena fração do arquivo `dados.json`, com 2 consórcios e 3 linhas, formatado para melhor leitura.

Referência dos Tipos de objeto no arquivo `dados.json`

Objeto `Consorcio`: representa uma empresa ou consórcio de empresas. Usado para agrupar as linhas de um mesmo consórcio.
- `nome`: Nome do Consorcio (exemplos: Carris, Conorte)
- `url`: Url da página do consorcio no site da EPTC (exemplo: para o consorcio Carris, a URL é http://www.eptc.com.br/EPTC_Itinerarios/Linha.asp?cdEmp=3)
- `linhas`: uma lista de objetos do tipo `Linha`

Objeto `Linha`: objeto utilizado para descrever uma linha, incluindo seu nome, código, itinerário e horários de partida do final de linha.
- `id`: string identificador de uma linha no site da EPTC. O `id` deve ser utilizado dentro do site da EPTC, e pode ser utilizado como identificador único da linha.
- `codigo`: string com o código da linha, geralmente visível nos letreiros dos ônibus. Note que em diversas linhas, o `codigo` e o `id` da linha são distintos. O `codigo` deve ser utilizado para exibição da linha para pessoas.
- `nome`: string com o nome da linha.
- `itinerarios`: uma lista de objetos `Itinerario`, com os diversos itinerários que a linha pode percorrer. Em geral, uma linha possui ao menos dois itinerarios: um para a direção bairro/centro, e outro para a direção centro/bairro.
- `urlItinerarios`: string com a URL a ser utilizada para acessar a página com itinerários da linha no site da EPTC.
- `tabelasHorarios`: lista de objetos `TabelaHorarios`, com os horários de partida desta linha. Em geral, uma linha possui uma tabela de horários para cada direção, e pode ter diferentes horários em diferentes dias da semana.
- `urlHorarios`: string com a URL utilizada para obter as tabelas de horários da linha.
- `linhasRelacionadas`: uma lista de strings, onde cada um é o ID de outra linhas relacionadas à esta, segundo o site da EPTC.

Objeto `Itinerario`: descreve um itinerário de uma linha. Inclui a direção e lista de ruas que irá percorrer.
- `direcao`: string com a direção em que o itinerário da linha é valido.
- `logradouros`: uma lista de strings com os logradouros deste itinerário, na ordem que é percorrido pela linha na direçao acima.

Objeto `TabelaHorarios`: descreve a tabela de horários de uma linha para um dia da semana e direção.
- `direcao`: string a direção em que uma tabela de horários é válida. Em geral, possui valores como `BAIRRO/CENTRO` e `CENTRO/BAIRRO`, mas para algumas linhas específicas pode ter valores como `NORTE/SUL` ou `BAIRRO/CENTRO/BAIRRO`, além de outros.
- `validade`: string descritivo dos dias da semana em que a tabela de horários é valida. Em geral, possui valores como `Dias úteis`, `Sábados` ou `Domingos`.
- `horarios`: uma lista de objetos `Horario` para esta tabela de horário. Cada `Horario` representa o horário em que a linha parte do final de linha da direção indicada, nos dias indicados pelo campo `Validade`.
- `intervaloMin`: número inteiro com o intervalo mínimo entre cada partida de fim de linha, para esta tabela de horários.
- `intervaloMed`: número inteiro com o intervalo médio entre os horários de partida de fim de linha, para esta tabela de horários.
- `intervaloMax`: número inteiro com o intervalo maximo entre cada partida de fim de linha, para esta tabela de horários.

Objeto `Horario`: descreve os horários de partida de uma linha, incluindo informações sobre o veículo que irá partir naquele horário.
- `horario`: string com um horario de partida da linha, no formato `HH:MM`.
- `acessibilidade`: booleano que indica se o veículo que parte do fim de linha neste horário é adaptado para pessoas com deficiência.


Execução do Scraper
-------------------

Para executar o scraper, você precisa ter instalado:

- Gradle 2.4 (https://gradle.org/). Versões posteriores devem ser suportadas sem problemas.
- Mozilla Firefox 38.0.1. Versões posteriores do Firefox devem ser suportadas, mas precisar com a atualização do Selenium (ver item "Problemas conhecidos")

Para executar os testes, execute

    gradle test

Para executar o scraper e fazer o download dos dados do site da EPTC, execute

    gradle run



Problemas conhecidos
------------

- `org.openqa.selenium.firefox.NotConnectedException`: O Firefox possui um sistema de update automático. As versões mais recentes do Firefox podem não ser compatíveis com o Selenium (http://www.seleniumhq.org/) atualmente configurado no projeto. Por serem projetos estáveis, é provavel que a atualização do Selenium seja suficiente para corrigir eventuais erros. Visite http://www.seleniumhq.org/download/ para conhecer a versão mais recente do Selenium para Java, e atualize o número da versão no arquivo `build.gradle`.
- Algumas linhas geram erro interno no site da EPTC, e impedem o processo de scraping para estas linhas. No momento, não há correção que possa ser feita no scraper. Quando estes erros ocorrem, o HTML gerado pelo site da EPTC é salvo no diretório `dados/erros`, com um nome `<id da linha>-<horario|itinerario>.html`, indicando onde ocorreu o erro.

