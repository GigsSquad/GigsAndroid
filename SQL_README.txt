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
i Jsoupy (one same generuj¹ nowego dbma). Mo¿e mo¿naby
daæ dbm w konstruktor jsoupów / tworzyæ nowego w ConMgr - na razie
pomiesza³em konwencje, ¿eby zobaczyæ czy obie dzia³aj¹ (dzia³aj¹ :)).
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
	* wywalone gettery, s³u¿y tylko do komunikacji strona<->baza
- MainActivity:
	* tworzymy dbManagera, który obs³u¿y ConcertManagera
     !!!* zmiana tekstu Toastu (onPostExec) - bez tego nia dzia³a 
	  nie mam zielonego pojêcia czemu
-------------------------------------------------------------------
Dodatkowo: nie wiem czy patrzyliœcie wczeœniej na mojego brancha, ale
JSoupy wygl¹daj¹ tak, ¿e ka¿da strona ma w³asn¹ klasê, która importuje
(teraz ju¿ bezesensownid) interfejs JSoupDownloader.
	