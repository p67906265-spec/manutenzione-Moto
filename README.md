# Manutenzione Moto

App Android per registrare manutenzioni con pezzo/intervento, chilometri, costo e autore (Officina o Io).

La versione 1.1 aggiunge:

- icona personalizzata;
- chilometraggio attuale della moto;
- scadenze impostate a un chilometraggio preciso;
- notifica quando i km attuali raggiungono la scadenza.

La versione 1.2 aggiunge più moto, dati separati per ogni moto, esportazione del backup e ripristino dopo reinstallazione. Ogni eliminazione richiede una conferma esplicita.

La versione 1.3 rinnova la schermata principale con pannello moto professionale, elimina il riepilogo della spesa e raccoglie cambio moto, backup, versione e firma nel menu Impostazioni.

La versione 1.4 coordina tutti i pannelli interni con il tema blu notte dell'app.

La versione 1.5 aggiunge la scelta dello sfondo moto: Africa Twin originale ricavata dalla foto dell'utente, Africa Twin blu, Adventure blu, Sportiva oppure nessuno sfondo.

La versione 1.6 aggiunge una vera animazione di apertura con logo, titolo e dissolvenza verso il pannello della moto.

La versione 1.7 corregge il contrasto delle scritte sui pulsanti principali e nei pannelli di conferma.

La versione 1.8 forza direttamente i colori dei pulsanti dopo l'apertura di ogni pannello, compatibile anche con temi Huawei che ignorano lo stile generale.

La versione 1.9 rende obbligatoria la firma permanente nella compilazione GitHub: se manca uno dei quattro secret, il workflow si ferma senza produrre per errore un APK con firma diversa. Prima della pubblicazione viene inoltre verificata la firma dell'APK release.

La versione 2.0 introduce database con date ordinabili, scadenze ricorrenti a km e mesi, preavvisi, chilometri automatici, foto scontrino, sfondo personale, statistiche annuali, export PDF/CSV, menu visibile sulle card, opzione schermo acceso e interfaccia HUD futuristica con splash al neon.

Tocco su una voce: modifica. Pressione lunga: elimina.

Per gli APK firmati permanenti configurare una sola volta i quattro GitHub Secrets: `SIGNING_KEY` (keystore in Base64), `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`. Conservare anche una copia privata del file keystore e delle password: senza la stessa chiave non è possibile aggiornare l'app già installata.

Da Termux è possibile creare la chiave e inserire automaticamente i quattro secret eseguendo `bash configura-firma-termux.sh`. Il backup privato della chiave viene salvato nella cartella Download/Firma_Manutenzione_Moto del telefono e non deve mai essere caricato nel repository.
