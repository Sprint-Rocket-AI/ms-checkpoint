package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.tools;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools.dtos.FormatToListRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools.dtos.FormatToListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CrearListaFormateadaParaBDTest {

    @Test
    @DisplayName("Construye lista STRING con columna e IN eliminando duplicados")
    void shouldWhenConstruir_formatString_con_columna_y_IN_eliminando_duplicados_y_preservando_orden() {
        // Given: entrada sencilla con duplicados
        String valores = "0-134095\n0-134104\n0-134095";
        FormatToListRequest request = new FormatToListRequest("CUENTA_ID", CrearListaFormateadaParaBD.TipoDato.STRING, valores, true);

        // When
        FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

        // Then: resultado esperado con comillas simples y sin duplicados
        String esperado = "CUENTA_ID IN ('0-134095','0-134104')";
        assertEquals(esperado, response.statement());
    }

    @Test
    @DisplayName("Construye lista INT sin columna ni IN y elimina duplicados")
    void shouldWhenConstruir_formatInt_sin_columna_y_sin_IN_convertir_y_eliminar_duplicados() {
        // Given: valores numéricos con duplicados
        String valores = "1\n2\n2";
        FormatToListRequest request = new FormatToListRequest(null, CrearListaFormateadaParaBD.TipoDato.INT, valores, false);

        // When
        FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

        // Then: sin columna y sin IN, contenido entre paréntesis y números sin comillas
        String esperado = "(1,2)";
        assertEquals(esperado, response.statement());
    }

    @Test
    @DisplayName("Lanza excepción si INT contiene token no numérico")
    void shouldWhenConstruir_throwIllegalArgumentException_when_tipo_INT_y_valor_no_numerico() {
        // Given: valores que contienen un token no numérico
        String valores = "10\nnot-a-number\n30";
        FormatToListRequest request = new FormatToListRequest("COL", CrearListaFormateadaParaBD.TipoDato.INT, valores, true);

        // When / Then: se espera IllegalArgumentException por parseo
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> CrearListaFormateadaParaBD.construir(request));

        assertTrue(ex.getMessage().contains("Valor no numérico"));
    }

    @Test
    @DisplayName("Maneja líneas vacías y devuelve paréntesis vacíos")
    void shouldWhenConstruir_handle_empty_and_blank_lines_generando_clausula_con_parentesis_vacio() {
        // Given: solo líneas vacías o con espacios
        String valores = "\n  \n";
        FormatToListRequest request = new FormatToListRequest("MI_COL", CrearListaFormateadaParaBD.TipoDato.STRING, valores, false);

        // When
        FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

        // Then: al no haber valores, el contenido queda vacío dentro de los paréntesis
        String esperado = "MI_COL ()";
        assertEquals(esperado, response.statement());
    }

    @Test
    @DisplayName("Escapa comillas simples en valores STRING")
    void shouldWhenConstruir_escape_single_quotes_en_valores_STRING() {
        // Given: valor que contiene comilla simple
        String valores = "O'Neil\nAlice";
        FormatToListRequest request = new FormatToListRequest("NOMBRE", CrearListaFormateadaParaBD.TipoDato.STRING, valores, true);

        // When
        FormatToListResponse response = CrearListaFormateadaParaBD.construir(request);

        // Then: la comilla simple interna debe duplicarse para escapar en SQL
        String esperado = "NOMBRE IN ('O''Neil','Alice')";
        assertEquals(esperado, response.statement());
    }
}

