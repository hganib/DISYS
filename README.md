Übersicht

Dieses Projekt nutzt eine Microservice-Architektur, um Energie-Daten von verschiedenen Quellen (User, Producer) zu verarbeiten, historische Aggregationen zu speichern und aktuelle Kennzahlen zu berechnen.

⸻

Hauptkomponenten
1.	Energy User Service
  •	Verantwortlich für das Erzeugen und Versenden von Verbrauchsdaten (EnergyMessage mit type="USER") über RabbitMQ (Queue energy).

2.	Energy Producer Service
  •	Erzeugt Produktionsdaten (EnergyMessage mit type="PRODUCER") und sendet sie an dieselbe Queue energy.

3.	Usage Service
  •	Konsument der Queue energy.
  •	Aggregiert USER- und PRODUCER-Nachrichten auf Stundenbasis:
  •	Persistiert in Tabelle energy_historical.
  •	Nach jedem Update sendet es eine HistoricalUpdateMessage an Queue energy_historical_updates.

4.	Current Percentage Service
  •	Konsument der Queue energy_historical_updates.
  •	Berechnet pro Stunde den Anteil von Community- vs. Grid-Verbrauch.
  •	Speichert das Ergebnis in Tabelle energy_current.

5.	Shared Module
  •	DTOs (EnergyMessage, HistoricalUpdateMessage) und gemeinsame Utilities (JPAUtil, round()) werden einmal zentral definiert und von allen Services als Maven-Dependency eingebunden.

⸻

Datenfluss & Messaging

 [User]        [Producer]
    \              /
     \ send USER  / send PRODUCER
      >-----------<  RabbitMQ 
      | Queue: energy           
      v                         
 [Usage Service]               
  - aggregate & persist         
  - publish HIST-UPDATE        
      > Queue: energy_historical_updates
      |
      v
 [Current Percentage Service]
  - calculate percentage
  - persist in energy_current


⸻

Persistenz
	•	PostgreSQL als zentrale Datenbank:
	•	Schema disysdb
	•	Tabelle energy_historical: Stunden-Aggregate von Produktion & Verbrauch
	•	Tabelle energy_current: Aktuelle Prozentwerte je Stunde
	•	JPA/Hibernate (Jakarta Persistence) als ORM:
	•	hibernate.hbm2ddl.auto im Dev-Modus auf update, im Prod auf validate
	•	Entities: EnergyHistorical, EnergyCurrent

⸻

Deployment
	•	Jeder Service als eigenständiges Docker-Container oder JAR:
	•	Implementiert mit Java 21, Maven Multi-Module
	•	Kommunikation über RabbitMQ (Container)
	•	PostgreSQL (Container / Managed Service)
	•	Skalierung:
	•	Energy User/Producer können horizontal skaliert werden (mehr Instanzen senden parallel)
	•	Usage Service kann mehrere Consumer-Instanzen nutzen (Load Balancing)
	•	Current Percentage Service ebenfalls skalierbar

⸻

Vorteile dieser Architektur
	•	Entkopplung: Producer und Consumer über Message Broker asynchron verbunden
	•	Skalierbarkeit: Jeder Service lässt sich unabhängig skalieren
	•	Wartbarkeit: Klare Trennung der Verantwortlichkeiten und zentrale Shared-Bibliothek
	•	Ausfallsicherheit: RabbitMQ puffert Nachrichten; Datenbank-Persistenz sichert Daten