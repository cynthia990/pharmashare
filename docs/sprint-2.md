# Sprint 2 — Coder le domaine PharmaShare

## Objectif et déroulement

Transformer les diagrammes du sprint d'analyse en règles métier exécutables et vérifiables. Durée prévue de cinq jours de travail, à ajuster à la disponibilité des étudiants. Les quatre étudiants travaillent sur le même dépôt.

Le dirigeant fournit le projet Maven, les versions, les contrats, les tests et la revue. Chaque étudiant lit ses tests, implémente son exercice, explique ses choix et ouvre une pull request. Les dates et les clés des futurs tickets restent à fixer dans Jira. Ce document prépare les tickets et ne prétend pas qu'ils ont été créés.

| Étudiant | Mission | Fichiers principaux | Suite à lancer |
| --- | --- | --- | --- |
| Cynthia | Construire les références médicament et pharmacie | `model/Medicament.java`, `model/Pharmacie.java` | `CatalogueTest` |
| Idriss | Implémenter le cycle de vie d'un lot | `model/Lot.java` | `LotTest` |
| Jordan | Publier un stock et calculer sa fraîcheur | `model/PublicationStock.java`, `service/PublicationStockService.java` | `PublicationStockServiceTest` |
| Florent | Contrôler et enregistrer une demande de don | `service/DonService.java` | `DonServiceTest` |

Commande depuis la racine, en remplaçant le nom de la suite

```powershell
.\mvnw.cmd -pl domain test -Dtest=CatalogueTest
```

## Cynthia — Références du catalogue

**Situation.** Une officine publiera des lots. Nous devons distinguer le médicament, qui décrit un produit, de la pharmacie, qui décrit son détenteur.

**À faire.** Ajouter les champs privés et immuables, les validations des constructeurs et les accesseurs prévus. Conserver les UUID reçus. Nettoyer les chaînes avec `strip()`. Les identifiants et libellés sont obligatoires. Le code CNK comporte exactement sept chiffres ASCII après nettoyage. Le code postal belge comporte exactement quatre chiffres et commence par un chiffre de 1 à 9. Cette validation de format ne vérifie pas l'existence dans un référentiel officiel.

**Exemple.** `" 1234567 "` devient `"1234567"`. Un nom composé uniquement d'espaces est refusé. Chaque entrée invalide déclenche `IllegalArgumentException`.

**Livrable.** Les deux modèles, la suite `CatalogueTest` verte et une explication de la différence entre un produit et un lot. Ajouter au moins un exemple personnel de donnée invalide.

## Idriss — Cycle de vie du lot

**Situation.** Une pharmacie déclare dix boîtes d'un lot fictif. Ce lot doit rester identifiable et suivre les étapes du don.

**À faire.** Conserver l'identité du lot, ses références médicament et pharmacie, son numéro nettoyé, sa quantité, sa péremption, son état et le bénéficiaire. Les UUID, le numéro, les dates sont obligatoires. La quantité est strictement positive. À la déclaration, la péremption doit être strictement postérieure à la date fournie. Le lot commence en `A_COMPLETER`, sans bénéficiaire.

| Méthode | État de départ autorisé | Résultat |
| --- | --- | --- |
| `proposer(justificatif, date)` | `A_COMPLETER` | `PROPOSE`, référence de justificatif obligatoire |
| `demander(partenaireId, date)` | `PROPOSE` | `DEMANDE`, bénéficiaire conservé |
| `reserver(date)` | `DEMANDE` | `RESERVE` |
| `remettre(date)` | `RESERVE` | `REMIS` |
| `signalerAnomalie()` | `REMIS` | `EN_ANOMALIE` |
| `confirmerReception(date)` | `REMIS` ou `EN_ANOMALIE` | `RECEPTIONNE` |
| `retirer()` | `A_COMPLETER`, `PROPOSE`, `DEMANDE`, `RESERVE` ou `EN_ANOMALIE` | `RETIRE` |

Les méthodes recevant une date vérifient que le lot est encore utilisable selon la convention du sprint. Un état incompatible ou une péremption atteinte déclenche `IllegalStateException`. Une donnée obligatoire absente déclenche `IllegalArgumentException`. Une opération refusée conserve l'état et le bénéficiaire précédents. `RECEPTIONNE` et `RETIRE` sont terminaux. Éviter un setter permettant de contourner les transitions.

**Livrable.** `Lot`, la suite `LotTest` verte et une table reliant chaque méthode au diagramme d'états. Les contrôles d'habilitation du partenaire sont dans le service de Florent.

## Jordan — Publication et fraîcheur du stock

**Situation.** Une officine publie son stock chaque soir. Une publication échouée doit laisser la dernière publication réussie disponible avec son horodatage.

**À faire.** Implémenter un `PublicationStock` immuable avec copie défensive de la liste. Ses paramètres sont obligatoires et la liste ne contient pas de valeur nulle. Conserver `pharmacieId`, `publieLe` et les offres accessibles par leurs accesseurs.

Dans `PublicationStockService`, conserver le repository et l'horloge injectés. Refuser les dépendances nulles. `publier` valide toute la liste avant sauvegarde, refuse les identifiants de lot dupliqués et les entrées nulles, écarte les offres dont la péremption est atteinte à `LocalDate.now(clock)`, puis crée une publication datée avec `clock.instant()`. Appeler `save` une seule fois après validation. Une liste vide est valide et remplace les anciennes disponibilités. Une erreur de validation déclenche `IllegalArgumentException` et ne déclenche aucune sauvegarde.

Pour `fraicheur`, calculer l'âge avec l'horloge fournie. Refuser une date nulle ou future. Une publication de moins de 24 h est `A_JOUR`. De 24 h à 72 h incluses, elle est `A_CONFIRMER`. Au-delà de 72 h, elle est `MASQUE`. Comparer des durées précises et éviter l'arrondi en heures.

**Livrable.** Le modèle et le service, la suite `PublicationStockServiceTest` verte et une explication des cas exactement à 24 h et à 72 h. Les prix, imports CSV et tâches planifiées seront traités ultérieurement.

## Florent — Demander un don via les ports

**Situation.** Un partenaire veut demander un lot. Le service doit vérifier son existence et son habilitation avant de modifier le lot.

**À faire.** Conserver les deux repositories et l'horloge reçus dans le constructeur. Les dépendances et identifiants sont obligatoires, sinon lever `IllegalArgumentException`. Rechercher le partenaire et le lot. Une référence inconnue déclenche `NoSuchElementException`. Un partenaire non habilité déclenche `IllegalStateException`.

Une fois les contrôles réalisés, appeler `lot.demander(partenaireId, LocalDate.now(clock))`, puis sauvegarder le lot une seule fois. Un refus de contrôle ou de transition ne sauvegarde et ne modifie rien. Les règles de transition sont portées par `Lot` et sont réutilisées par le service.

**Dépendance.** Les tests de succès de Florent nécessitent le modèle de lot d'Idriss. Le contrat permet de compiler et de travailler sur le service avant cette livraison. Idriss prépare une petite pull request avec construction, proposition et demande dès le début du sprint. Florent et Idriss réalisent ensuite une revue croisée.

**Livrable.** Le service, la suite `DonServiceTest` verte après intégration de `Lot`, et une explication du rôle de chaque port. Les classes de repository sont des interfaces fournies. Les doubles en mémoire sont déjà écrits dans les tests.

## Conventions pédagogiques à confirmer

Les captures montrent deux parcours complémentaires, publication publique du stock et pilote de don entre professionnels. Ce sprint implémente une partie de chacun. Il n'implémente pas encore les utilisateurs, rôles, documents téléversés, réservations séparées, prix ou mouvements d'historique de l'ERD.

La péremption est ici une date complète et le lot devient indisponible dès que `date >= peremption`. C'est une convention de prototype à faire valider avant utilisation réelle, notamment pour les étiquettes exprimées en mois et année. La référence de justificatif est une chaîne fictive.

Le diagramme d'états mentionne des délais automatiques de 48 h et 72 h et un retour à `PROPOSE`. Leur point de départ et les acteurs doivent être précisés. Ces automatismes feront l'objet d'un ticket après décision. La confirmation après anomalie représente la décision humaine de conformité du pilote.

Les tests de ce sprint travaillent en mémoire et sur un seul appel à la fois. La persistance ajoutera transactions, gestion d'échec de sauvegarde, verrouillage concurrent et idempotence des requêtes avant exposition d'une API. Une suite verte ne prouve pas l'autorisation d'une opération pharmaceutique réelle.

## Définition de terminé

Le code respecte le contrat et ses règles, les tests ciblés passent, puis la suite complète après intégration. L'étudiant explique un parcours de succès et un refus. Le domaine conserve son indépendance envers Spring et JPA. La pull request comprend la référence du ticket Jira une fois créé. Le dirigeant relit avant fusion.
