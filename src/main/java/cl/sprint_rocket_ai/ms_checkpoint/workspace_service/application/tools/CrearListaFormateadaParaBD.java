package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.application.tools;

import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools.dtos.FormatToListRequest;
import cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.in.tools.dtos.FormatToListResponse;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CrearListaFormateadaParaBD {

    public enum TipoDato {
        STRING,
        INT
    }

    public static FormatToListResponse construir(FormatToListRequest request) {
        Set<String> unicos = removeDuplicates(request.valores());
        String contenido = switch (request.tipo()) {
            case STRING -> formatString(unicos);
            case INT    -> formatInt(unicos);
        };
        String statement = request.columna() + " IN (" + contenido + ")";
        return new FormatToListResponse(statement);
    }

    private static Set<String> removeDuplicates(Collection<?> valores) {
        return valores.stream()
                .filter(v -> v != null && !v.toString().isBlank())
                .map(v -> v.toString().trim())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static String formatString(Set<String> valores) {
        return valores.stream()
                .map(v -> "'" + v.replace("'", "''") + "'")
                .collect(Collectors.joining(","));
    }

    private static String formatInt(Set<String> valores) {
        return valores.stream()
                .map(CrearListaFormateadaParaBD::parsearEntero)
                .collect(Collectors.joining(","));
    }

    private static String parsearEntero(String valor) {
        try {
            return Long.valueOf(valor).toString();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor no numérico para tipo INT: " + valor, e);
        }
    }
}
