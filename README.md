# PharmaShare

Backend pédagogique de PharmaFacile, PROFACILE SRL. Java 25, Spring Boot 4.1.2, Maven Wrapper 3.9.16.

## Organisation

```text
domain/
  src/main/java/be/profacile/pharmashare/domain/
    model/          entités JPA et énumérations à compléter
    repository/     interfaces Spring Data à compléter
    service/        services laissés vides pour l'exercice suivant
  src/test/java/be/profacile/pharmashare/domain/
    repository/     tests complets fournis aux étudiants
    support/        infrastructure de test fournie
rest/
  src/main/java/be/profacile/pharmashare/rest/dto/
    request/        futurs DTO de requête
    response/       futurs DTO de réponse
bootstrap/          démarrage Spring Boot fourni
docs/sprint-2.md    consignes détaillées et contrat des champs
```

Le module bootstrap dépend de rest, qui dépend de domain. Cette organisation suit le choix pédagogique de placer les entités JPA et les repositories Spring Data dans domain. La séparation entre modèle métier pur et entités de persistance pourra être étudiée ensuite lors du travail sur les ports et adaptateurs de l'architecture hexagonale.

## Exécuter les exercices

Installer un JDK 25 et sélectionner ce JDK dans IntelliJ pour le projet, Maven et les tests. Importer le pom.xml racine comme projet Maven.

```powershell
.\mvnw.cmd --version
.\mvnw.cmd clean test-compile
.\mvnw.cmd -pl domain test -Dtest=MedicamentRepositoryTest
.\mvnw.cmd verify
```

Sous Linux et macOS, utiliser `sh mvnw`.

Les classes de production et les repositories sont laissés vides. Les tests compilent grâce à une infrastructure de test qui inspecte les champs au moment de l'exécution. Ainsi, le bouton Play dans IntelliJ fonctionne dès l'import du projet et signale par exemple « Ajouter @Entity sur Medicament » ou « Ajouter le champ Medicament.id de type Long ».

Les tests vérifient ensuite les annotations JPA, les types, les accesseurs, l'héritage des repositories, la sauvegarde et la relecture sur H2 ainsi que les méthodes de recherche. La base de test est créée et détruite pour chaque exercice. H2 ne remplace pas une validation PostgreSQL avant déploiement.

Les étudiants implémentent les fichiers de leur mission et conservent les tests fournis. Une modification des critères de test nécessite la revue du dirigeant.

## Répartition du sprint

| Étudiant | Entité et repository | Test à exécuter |
| --- | --- | --- |
| Cynthia | Medicament, MedicamentRepository | MedicamentRepositoryTest |
| Jordan | Pharmacie, PharmacieRepository | PharmacieRepositoryTest |
| Idriss | Lot, EtatLot, LotRepository | LotRepositoryTest |
| Florent | Partenaire, PartenaireRepository | PartenaireRepositoryTest |

Les [contrats détaillés](docs/sprint-2.md) précisent tous les champs, les annotations attendues, les recherches et les critères de livraison. Le périmètre de ce sprint porte sur quatre entités principales issues des diagrammes. Les autres entités seront ajoutées avec leurs propres exercices.

## Validation et livraison

Chaque étudiant crée une branche `feature/<prenom>-jpa` depuis `develop`, exécute son test puis ouvre une pull request vers `develop`. Les contributions sont intégrées progressivement dans `develop`. La branche `main` contient la base fournie pour ce sprint. Le dirigeant relit avant fusion. Le test de persistance du lot nécessite les entités Medicament et Pharmacie complétées. Les trois autres exercices peuvent être validés indépendamment.

La CI distingue « Squelette technique » et « Exercices métier ». Le premier vérifie la compilation et le démarrage Spring Boot. Le second reste rouge tant que les exercices sont incomplets. Les rapports sont conservés sept jours.

Le fichier CODEOWNERS désigne le compte profacile pour la revue des tests et des fichiers de configuration. Une protection bloquante de main reste à configurer dans GitHub.

Pour vérifier uniquement le squelette technique

```powershell
.\mvnw.cmd -pl bootstrap -am test -Dtest=PharmaShareApplicationTest "-Dsurefire.failIfNoSpecifiedTests=false"
```

Le bootstrap utilise actuellement le mode sans serveur HTTP. Les contrôleurs, DTO, services et la configuration PostgreSQL seront réalisés dans les exercices suivants.

Sources de versions consultées le 19 novembre 2026

- [Spring Boot](https://spring.io/projects/spring-boot/)
- [Compatibilité Java et Maven](https://docs.spring.io/spring-boot/system-requirements.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Java et support](https://www.oracle.com/java/technologies/java-se-support-roadmap.html)
