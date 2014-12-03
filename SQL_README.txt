Zarys dzia�ania:

1. Klasy z JSoupem pobieraj� dane ze stron, wykonuj� ca�y parsing
(g��wnie chodzi tu o dat� (string->int[3]), co by�o wcze�niej domen�
Concertu). Po uporz�dkowaniu danych ze strony wrzucaj� wszystko
do bazy*. 

2. Zadanie ConcertManagera polega tylko na wyci�gni�ciu danych z bazy*,
przetworzenie ich na obiekty Concertu i wrzucenie do siebie do listy.
Jest to tymczasowe i IMO nieefektywne (lepiej pomijac kontenery),
ale nie musia�em grzeba� w MainActivity (penwie bym zjeba�).

3. MainActivity leci po staremu, ze wzgl�du na zachowanie modelu 
funkcji ConMgr'a (gettery).

*Baza obs�ugiwana jest przez dbManagera, kt�ry jest wykorzystywany 
przez ConcertManagera (konstruktor, przekazanie dma z poziomu MainAct)
i Jsoupy (one same generuj� nowego dbma). Mo�e mo�naby
da� dbm w konstruktor jsoup�w / tworzy� nowego w ConMgr - na razie
pomiesza�em konwencje, �eby zobaczy� czy obie dzia�aj� (dzia�aj� :)).
---------------------------------------------------------
ZMIANY
- Wywalony parser z Concertu
- Tymczasowo nieu�ywany AlterArt
- ConcertManager:
	* konstruktor (dbManager)
	* gettery u�ywaj� Cursora ("iterator" po bazie)
	* collect() - pobiera dane z bazy, tworzy Concerty
	  i wrzuca je do swojej listy
- JSoupy:
	* wywalone gettery, s�u�y tylko do komunikacji strona<->baza
- MainActivity:
	* tworzymy dbManagera, kt�ry obs�u�y ConcertManagera
     !!!* zmiana tekstu Toastu (onPostExec) - bez tego nia dzia�a 
	  nie mam zielonego poj�cia czemu
-------------------------------------------------------------------
Dodatkowo: nie wiem czy patrzyli�cie wcze�niej na mojego brancha, ale
JSoupy wygl�daj� tak, �e ka�da strona ma w�asn� klas�, kt�ra importuje
(teraz ju� bezesensownid) interfejs JSoupDownloader.
	