# Sprint 2 — Entités JPA et repositories

## But de la semaine

Lire les diagrammes de classes et l'ERD, compléter les entités JPA et les repositories laissés vides, puis faire passer les tests fournis. Les quatre missions sont des tâches de développement d'un sprint d'une semaine.

Le périmètre retient Medicament, Pharmacie, Lot et Partenaire. Les champs ci-dessous constituent le contrat de l'exercice, adapté des diagrammes transmis. Les entités complémentaires, la publication complète des stocks, les utilisateurs et les échanges de dons feront l'objet de tickets suivants.

## Consignes communes

1. Cloner https://github.com/profacile/pharmashare et importer le pom.xml avec Java 25.
2. Partir de develop et créer une branche feature/<prenom>-jpa.
3. Lire sa classe de test dans domain/src/test/java/be/profacile/pharmashare/domain/repository.
4. Compléter l'entité avec @Entity et @Table(name = "..."). Ajouter les champs privés et leurs annotations sur les champs.
5. Chaque entité possède un id Long, annoté @Id et @GeneratedValue(strategy = GenerationType.IDENTITY), ainsi que getId().
6. Prévoir un constructeur sans argument public ou protégé pour JPA. Les autres champs disposent de getters et setters publics. Pour Boolean habilite, utiliser getHabilite() et setHabilite(Boolean).
7. Compléter le repository avec extends JpaRepository<Entite, Long>, puis sa méthode de recherche.
8. Exécuter le test par le bouton Play ou Maven, corriger son code et ouvrir une pull request vers develop. Expliquer un test qui échouait et sa correction.

Les annotations appartiennent à jakarta.persistence. Les noms Java et SQL du tableau sont les références communes pour éviter les différences entre diagrammes et implémentations.

## Cynthia — Medicament et MedicamentRepository

**Situation.** Un médicament est une référence de produit. Le stock et la péremption sont portés par les lots qui lui sont associés.

Fichiers à compléter

- domain/src/main/java/be/profacile/pharmashare/domain/model/Medicament.java
- domain/src/main/java/be/profacile/pharmashare/domain/repository/MedicamentRepository.java

Table SQL `medicament`.

| Champ Java | Type | Colonne SQL | Obligatoire |
| --- | --- | --- | --- |
| id | Long | id | Identifiant généré |
| codeCnk | String | code_cnk | Oui, unique, longueur 7 |
| nom | String | nom | Oui |
| principeActif | String | principe_actif | Oui |

Mettre @Column(name = "...", nullable = false) sur les trois champs de contenu. codeCnk possède aussi unique = true et length = 7.

Le repository gère Medicament avec un identifiant Long et déclare `Optional<Medicament> findByCodeCnk(String codeCnk)`. Le CNK absent retourne Optional.empty().

**Validation.** MedicamentRepositoryTest vérifie les champs et annotations, la génération de l'identifiant, la sauvegarde/relecture, la recherche CNK, la suppression et le refus d'un CNK dupliqué. Les valeurs sont fictives.

**Livrable.** Entité et repository complétés, test vert et pull request. Expliquer pourquoi le prix et la péremption appartiennent au lot.

## Jordan — Pharmacie et PharmacieRepository

**Situation.** La pharmacie publiera ses disponibilités. Sa date de dernière publication peut être absente tant qu'elle n'a jamais publié.

Fichiers à compléter

- domain/src/main/java/be/profacile/pharmashare/domain/model/Pharmacie.java
- domain/src/main/java/be/profacile/pharmashare/domain/repository/PharmacieRepository.java

Table SQL `pharmacie`.

| Champ Java | Type | Colonne SQL | Obligatoire |
| --- | --- | --- | --- |
| id | Long | id | Identifiant généré |
| nom | String | nom | Oui |
| adresse | String | adresse | Oui |
| dateDernierePublication | LocalDateTime | date_derniere_publication | Non |

Mettre @Column sur chaque champ avec le nom SQL et la nullabilité indiqués. Le repository gère Pharmacie avec un identifiant Long et déclare `List<Pharmacie> findByNomContainingIgnoreCase(String nom)`.

**Validation.** PharmacieRepositoryTest sauvegarde l'officine, vide le contexte JPA, relit son adresse et son horodatage, puis recherche une partie de son nom en majuscules. Une recherche sans résultat retourne une liste vide.

**Livrable.** Entité, repository, test vert et pull request. Expliquer la différence entre date de publication du stock et péremption d'un lot.

## Idriss — Lot, EtatLot et LotRepository

**Situation.** Une pharmacie détient plusieurs lots d'un médicament. Chaque lot possède son numéro, sa péremption, sa quantité et ses prix.

Fichiers à compléter

- domain/src/main/java/be/profacile/pharmashare/domain/model/Lot.java
- domain/src/main/java/be/profacile/pharmashare/domain/model/EtatLot.java
- domain/src/main/java/be/profacile/pharmashare/domain/repository/LotRepository.java

Table SQL `lot`.

| Champ Java | Type | Colonne SQL | Obligatoire |
| --- | --- | --- | --- |
| id | Long | id | Identifiant généré |
| numeroLot | String | numero_lot | Oui |
| datePeremption | LocalDate | date_peremption | Oui |
| quantite | Integer | quantite | Oui |
| prixNormal | BigDecimal | prix_normal | Oui |
| prixReduit | BigDecimal | prix_reduit | Non |
| statut | EtatLot | statut | Oui |
| medicament | Medicament | medicament_id | Relation obligatoire |
| pharmacie | Pharmacie | pharmacie_id | Relation obligatoire |

Utiliser @Column pour les champs simples. Le statut est stocké avec @Enumerated(EnumType.STRING). Les deux relations utilisent @ManyToOne(optional = false) et @JoinColumn(name = "...", nullable = false).

Compléter EtatLot, dans cet ordre, avec A_COMPLETER, PROPOSE, DEMANDE, RESERVE, REMIS, EN_ANOMALIE, RECEPTIONNE et RETIRE.

Le repository gère Lot avec un identifiant Long et déclare `List<Lot> findByPharmacieId(Long pharmacieId)`.

**Validation.** LotRepositoryTest contrôle les mappings et l'énumération, persiste les références puis le lot, vide le contexte JPA et vérifie les données rechargées ainsi que la recherche par pharmacie.

**Dépendance.** Le premier test de structure peut être exécuté sur le code d'Idriss. Le test de persistance nécessite les entités Medicament et Pharmacie livrées par Cynthia et Jordan. Prévoir leur intégration ou une séance commune avant la revue finale.

**Livrable.** Entité, énumération, repository, test vert après intégration et pull request. Expliquer les deux clés étrangères et l'intérêt de BigDecimal pour les prix. Ces prix sont des données de simulation.

## Florent — Partenaire et PartenaireRepository

**Situation.** Un établissement partenaire peut être habilité ou non pour le pilote. La recherche devra retrouver uniquement les partenaires habilités.

Fichiers à compléter

- domain/src/main/java/be/profacile/pharmashare/domain/model/Partenaire.java
- domain/src/main/java/be/profacile/pharmashare/domain/repository/PartenaireRepository.java

Table SQL `partenaire`.

| Champ Java | Type | Colonne SQL | Obligatoire |
| --- | --- | --- | --- |
| id | Long | id | Identifiant généré |
| nom | String | nom | Oui |
| type | String | type | Oui |
| pays | String | pays | Oui |
| adresse | String | adresse | Oui |
| habilite | Boolean | habilite | Oui |

Chaque champ simple reçoit @Column(name = "...", nullable = false). Le repository gère Partenaire avec un identifiant Long et déclare `List<Partenaire> findByHabiliteTrue()`.

**Validation.** PartenaireRepositoryTest persiste deux partenaires fictifs, l'un habilité et l'autre non, relit les données depuis la base et vérifie que la recherche retourne uniquement le partenaire habilité.

**Livrable.** Entité, repository, test vert et pull request. Expliquer la différence entre une entité et le repository qui permet de l'enregistrer.

## Exécuter et livrer

```powershell
.\mvnw.cmd clean test-compile
.\mvnw.cmd -pl domain test -Dtest=MedicamentRepositoryTest
.\mvnw.cmd verify
```

Remplacer MedicamentRepositoryTest par le nom de sa suite. Les tests démarrent une base H2 en mémoire. Aucun serveur de base externe n'est nécessaire pour ce sprint.

Les classes de services restent préparées pour le prochain exercice. Les DTO request et response sont placés dans le module rest, qui sera complété avec les contrôleurs. Le package dto ne contient pas les entités.

La revue finale vérifie les quatre suites vertes, la compréhension du mapping, la cohérence avec le diagramme et la pull request de chaque étudiant. Tout écart entre le contrat ci-dessus et un diagramme plus récent doit être signalé dans le ticket avant de changer le contrat de test.
