package be.profacile.pharmashare.domain.model;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class CatalogueTest {
    private final UUID id = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Test
    void conserveLesInformationsDuMedicamentSansEspacesSuperflus() {
        Medicament medicament = new Medicament(id, " 1234567 ", " Produit fictif ", " 10 mg ", " comprimé ");
        assertAll(
            () -> assertEquals(id, medicament.id()),
            () -> assertEquals("1234567", medicament.codeCnk()),
            () -> assertEquals("Produit fictif", medicament.nom()),
            () -> assertEquals("10 mg", medicament.dosage()),
            () -> assertEquals("comprimé", medicament.forme())
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "123", "12345678", "abcdefg", "123 567"})
    void refuseUnCodeCnkIncorrect(String code) {
        assertThrows(IllegalArgumentException.class,
            () -> new Medicament(id, code, "Produit fictif", "10 mg", "comprimé"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void refuseChaqueLibelleVide(String valeur) {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Medicament(id, "1234567", valeur, "10 mg", "comprimé")),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Medicament(id, "1234567", "Produit", valeur, "comprimé")),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Medicament(id, "1234567", "Produit", "10 mg", valeur)),
            () -> assertThrows(IllegalArgumentException.class,
                () -> new Pharmacie(id, valeur, "1070"))
        );
    }

    @Test
    void conserveLesInformationsDeLaPharmacie() {
        Pharmacie pharmacie = new Pharmacie(id, " Officine fictive ", " 1070 ");
        assertEquals(id, pharmacie.id());
        assertEquals("Officine fictive", pharmacie.nom());
        assertEquals("1070", pharmacie.codePostal());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "107", "10700", "abcd", "0000"})
    void refuseUnCodePostalBelgeInvalide(String codePostal) {
        assertThrows(IllegalArgumentException.class,
            () -> new Pharmacie(id, "Officine fictive", codePostal));
    }

    @Test
    void exigeLesIdentifiants() {
        assertThrows(IllegalArgumentException.class,
            () -> new Medicament(null, "1234567", "Produit", "10 mg", "comprimé"));
        assertThrows(IllegalArgumentException.class,
            () -> new Pharmacie(null, "Officine fictive", "1070"));
    }
}
