# PharmaShare

Squelette pédagogique du backend **PharmaFacile**, développé par PROFACILE SRL avec Cynthia, Jordan, Florent et Idriss. Sprint 2 de développement du domaine, préparé le 19 novembre 2026. Les dates effectives du sprint seront fixées dans Jira.

## Démarrer

Installer un JDK 25 et définir `JAVA_HOME` vers ce JDK. Importer le `pom.xml` racine comme projet Maven dans IntelliJ. Utiliser le Maven Wrapper du dépôt.

```powershell
.\mvnw.cmd --version
.\mvnw.cmd test-compile
.\mvnw.cmd verify
```

Sur Linux et macOS, utiliser `sh mvnw` à la place de `.\mvnw.cmd`.

`test-compile` doit réussir. `verify` doit initialement échouer sur les exercices contenant `TODO`. Lire le nom du test, son assertion et le contrat dans [la fiche de sprint](docs/sprint-2.md), puis implémenter la règle métier. Un échec `UnsupportedOperationException` est attendu au départ. Une erreur de compilation ou de téléchargement Maven indique un autre problème.

Les contrats de méthode sont fournis pour que chacun puisse coder et compiler sans attendre les autres. Garder leurs signatures. Implémenter les corps et les champs privés nécessaires. Les tests fournis font partie des critères de validation. Toute modification de ces tests doit être relue par le dirigeant. Ajouter des tests personnels est encouragé.

## Architecture

```text
pharmashare/
  pom.xml                         agrégateur et versions communes
  domain/
    src/main/java/be/profacile/pharmashare/domain/
      model/                      objets et règles métier
      repository/                 interfaces des ports sortants
      service/                    orchestration métier
    src/test/java/                tests et doubles en mémoire
  bootstrap/                      démarrage Spring Boot
  docs/sprint-2.md                 consignes et contrats
```

Le module `domain` utilise Java et JUnit pour ses tests. Ses modèles ne dépendent ni de Spring, ni de JPA, ni d'une base. Le module `bootstrap` dépend du domaine. Les adaptateurs REST et PostgreSQL seront ajoutés après ce sprint. Les repositories du domaine sont des interfaces dont les signatures sont fournies. Leur implémentation de persistance constituera une tâche d'un prochain sprint.

Pour vérifier uniquement le démarrage technique avec le domaine compilé et les exercices encore rouges

```powershell
.\mvnw.cmd -pl bootstrap -am test -Dtest=PharmaShareApplicationTest "-Dsurefire.failIfNoSpecifiedTests=false"
```

Pour créer le JAR de démarrage

```powershell
.\mvnw.cmd package -DskipTests
java -jar bootstrap/target/pharmashare-bootstrap-0.1.0-SNAPSHOT.jar
```

Le bootstrap démarre, puis termine normalement car aucun serveur HTTP ni traitement permanent n'est encore configuré.

## Travail en équipe

Chaque étudiant crée une branche `feature/<prenom>-<sujet>` depuis `main`, puis ouvre une pull request vers `main`. Une revue par le dirigeant est nécessaire avant fusion. Les tests métier seront rouges sur `main` tant que tous les exercices ne sont pas livrés. Vérifier d'abord sa suite ciblée, puis la suite complète à l'intégration. La CI transmet les vrais résultats et conserve les rapports JUnit même en cas d'échec.

Une fois le dépôt créé, configurer sur GitHub une règle de protection de `main` et la revue par le dirigeant, selon les fonctions disponibles pour l'organisation. Le fichier de workflow seul n'empêche pas les modifications des tests ou les fusions directes.

## Versions retenues

- Java 25 LTS, sans fonctionnalités preview.
- Spring Boot 4.1.2, version stable affichée par le projet officiel lors de la préparation.
- Maven Wrapper 3.9.16, dans la branche 3.9 demandée.

Le support longue durée Spring dépend des offres et calendriers de l'éditeur. La version retenue n'est pas présentée comme un abonnement LTS acquis.

Sources de versions consultées le 19 novembre 2026

- [Spring Boot et version stable](https://spring.io/projects/spring-boot/)
- [Compatibilité Java et Maven](https://docs.spring.io/spring-boot/system-requirements.html)
- [Distributions Maven](https://maven.apache.org/download.cgi)
- [Java 25 et support](https://www.oracle.com/java/technologies/java-se-support-roadmap.html)

## Périmètre du prototype

Les données et justificatifs sont fictifs. Les règles de date sont des conventions d'exercice documentées. Les fonctions de vente, de prix, d'alternative médicamenteuse, de transfert réel et de validation professionnelle restent à spécifier avant une mise en service.
