Zarys dzia³ania:

1. Klasy z JSoupem pobieraj¹ dane ze stron, wykonuj¹ ca³y parsing
(g³ównie chodzi tu o datê (string->int[3]), co by³o wczeœniej domen¹
Concertu). Po uporz¹dkowaniu danych ze strony wrzucaj¹ wszystko
do bazy*. 

2. Zadanie ConcertManagera polega tylko na wyci¹gniêciu danych z bazy*,
przetworzenie ich na obiekty Concertu i wrzucenie do siebie do listy.
Jest to tymczasowe i IMO nieefektywne (lepiej pomijac kontenery),
ale nie musia³em grzebaæ w MainActivity (penwie bym zjeba³).

3. MainActivity leci po staremu, ze wzglêdu na zachowanie modelu 
funkcji ConMgr'a (gettery).

*Baza obs³ugiwana jest przez dbManagera, który jest wykorzystywany 
przez ConcertManagera (konstruktor, przekazanie dma z poziomu MainAct)
i Jsoupy (one same generuj¹ nowego dbma). 
---------------------------------------------------------
ZMIANY
- Wywalony parser z Concertu
- Tymczasowo nieu¿ywany AlterArt
- ConcertManager:
	* konstruktor (dbManager)
	* gettery u¿ywaj¹ Cursora ("iterator" po bazie)
	* collect() - pobiera dane z bazy, tworzy Concerty
	  i wrzuca je do swojej listy
- JSoupy:
	* JSoupDownloader uruchamia wszystkie strony
	* Wszystko leci na dbMgr utworzonym w MainAcitvity
	* wywalone gettery, s³u¿y tylko do komunikacji strona<->baza
	* dodany boolean() i obs³uga kontroli wersji strony
- MainActivity:
	* tworzymy dbManagera, który obs³u¿y ConcertManager
------------------------------------------------------------------
Struktura bazy i dbManager:
BAZA:
2 tabele: Concerts [artist|city|spot|day|month|year|agency|url]
	  Hashcodes[agency|hash]

dbManager:
	* dodawanie koncertu
	* metody ogarniaj¹ce hashcode
-------------------------------------------------------------------
Nie wiem czy patrzyliœcie wczeœniej na mojego brancha, ale
JSoupy wygl¹daj¹ tak, ¿e ka¿da strona ma w³asn¹ klasê, których getData()
[g³ówna metoda] jest wywo³ywana w JSoupDownloader (g³ówna klasa do pobierania)
-------------------------------------------------------------------
KONTROLA WERSJI!!!
W bazie mamy tabelê Hashcodes [AGENCY | HASH ]
Agency jest polem unikatowym (key value),
odpowiada za agencjê (stronê), z któej pobieramy
Hash to hashcode pierwszego (najnowszego) eventu. Za ka¿dym razem
jak chcemy robiæ update najpierw pobieramy tylko jeden koncert, 
ustalamy jego hashcode, porównujemy z baz¹, jeœli dodali jakieœ nowe
ecenty to hase siê ró¿ni¹ i lecimy z ca³ym dodawaniem do bazy oraz oczywiœcie
podmieniamy wartoœæ pola HASH w Hashcodes. 
--------------------------------------------------------------------
TODO:
W razie update'u poza nowymi koncertami dodadza siê równie¿ te co by³y
Mo¿na rozwi¹zaæ sprawdzaniem hashu ka¿dego koncertu a¿ do momentu
trafienia na ten w bazie.
	