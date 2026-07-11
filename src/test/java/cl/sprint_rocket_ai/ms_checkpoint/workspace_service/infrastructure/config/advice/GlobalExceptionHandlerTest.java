package cl.sprint_rocket_ai.ms_checkpoint.workspace_service.infrastructure.config.advice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    // ─── MethodArgumentNotValidException ──────────────────────────────────────

    @Nested
    @DisplayName("handleMethodArgumentNotValid")
    class HandleMethodArgumentNotValid {

        private MethodArgumentNotValidException buildException(List<FieldError> fieldErrors) {
            BindingResult bindingResult = mock(BindingResult.class);
            when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            when(ex.getBindingResult()).thenReturn(bindingResult);
            return ex;
        }

        @Test
        @DisplayName("Debe retornar status 400 BAD_REQUEST")
        void shouldReturn400BadRequest() {
            // Given
            FieldError fieldError = new FieldError("dto", "nombre", "El nombre es obligatorio");
            MethodArgumentNotValidException ex = buildException(List.of(fieldError));

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentNotValid(ex);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("Debe incluir el mensaje 'Errores de validación' en el cuerpo")
        void shouldIncludeValidationErrorMessage() {
            // Given
            FieldError fieldError = new FieldError("dto", "titulo", "El titulo es obligatorio");
            MethodArgumentNotValidException ex = buildException(List.of(fieldError));

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentNotValid(ex);

            // Then
            assertEquals("Errores de validación", response.getBody().get("message"));
        }

        @Test
        @DisplayName("Debe mapear cada campo inválido a su mensaje de error en 'errors'")
        void shouldMapEachInvalidFieldToItsMessage() {
            // Given
            FieldError error1 = new FieldError("dto", "titulo", "Requerido");
            FieldError error2 = new FieldError("dto", "userId", "No puede ser vacío");
            MethodArgumentNotValidException ex = buildException(List.of(error1, error2));

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentNotValid(ex);

            // Then
            @SuppressWarnings("unchecked")
            Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
            assertNotNull(errors);
            assertEquals("Requerido", errors.get("titulo"));
            assertEquals("No puede ser vacío", errors.get("userId"));
        }

        @Test
        @DisplayName("Debe usar 'invalido' como mensaje cuando el FieldError no tiene mensaje")
        void shouldUseDefaultMessageWhenFieldErrorHasNoMessage() {
            // Given
            FieldError fieldError = new FieldError("dto", "campo", null, false, null, null, null);
            MethodArgumentNotValidException ex = buildException(List.of(fieldError));

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentNotValid(ex);

            // Then
            @SuppressWarnings("unchecked")
            Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
            assertEquals("invalido", errors.get("campo"));
        }

        @Test
        @DisplayName("Debe incluir timestamp y status 400 en el cuerpo de la respuesta")
        void shouldIncludeTimestampAndStatusInBody() {
            // Given
            FieldError fieldError = new FieldError("dto", "nombre", "Requerido");
            MethodArgumentNotValidException ex = buildException(List.of(fieldError));
            LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleMethodArgumentNotValid(ex);

            // Then
            LocalDateTime timestamp = (LocalDateTime) response.getBody().get("timestamp");
            assertNotNull(timestamp);
            assertFalse(timestamp.isBefore(antes));
            assertEquals(400, response.getBody().get("status"));
        }
    }

    // ─── ConstraintViolationException ─────────────────────────────────────────

    @Nested
    @DisplayName("handleConstraintViolation")
    class HandleConstraintViolation {

        @SuppressWarnings("unchecked")
        private ConstraintViolation<Object> buildViolation(String path, String message) {
            ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
            Path mockPath = mock(Path.class);
            when(mockPath.toString()).thenReturn(path);
            when(violation.getPropertyPath()).thenReturn(mockPath);
            when(violation.getMessage()).thenReturn(message);
            return violation;
        }

        @Test
        @DisplayName("Debe retornar status 400 BAD_REQUEST")
        void shouldReturn400BadRequest() {
            // Given
            ConstraintViolationException ex = new ConstraintViolationException(
                    Set.of(buildViolation("execute.id", "no debe estar vacío")));

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleConstraintViolation(ex);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("Debe mapear la ruta de cada violación a su mensaje en 'errors'")
        void shouldMapEachViolationPathToItsMessage() {
            // Given
            ConstraintViolation<Object> violation = buildViolation("execute.userId", "no debe estar vacío");
            ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleConstraintViolation(ex);

            // Then
            @SuppressWarnings("unchecked")
            Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
            assertNotNull(errors);
            assertEquals("no debe estar vacío", errors.get("execute.userId"));
        }

        @Test
        @DisplayName("Debe incluir el mensaje 'Errores de validación' en el cuerpo")
        void shouldIncludeValidationErrorMessage() {
            // Given
            ConstraintViolationException ex = new ConstraintViolationException(
                    Set.of(buildViolation("campo", "inválido")));

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleConstraintViolation(ex);

            // Then
            assertEquals("Errores de validación", response.getBody().get("message"));
        }

        @Test
        @DisplayName("Debe incluir timestamp y status 400 en el cuerpo de la respuesta")
        void shouldIncludeTimestampAndStatusInBody() {
            // Given
            ConstraintViolationException ex = new ConstraintViolationException(
                    Set.of(buildViolation("campo", "requerido")));
            LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleConstraintViolation(ex);

            // Then
            LocalDateTime timestamp = (LocalDateTime) response.getBody().get("timestamp");
            assertNotNull(timestamp);
            assertFalse(timestamp.isBefore(antes));
            assertEquals(400, response.getBody().get("status"));
        }
    }

    // ─── IllegalArgumentException ─────────────────────────────────────────────

    @Nested
    @DisplayName("handleIllegalArgument")
    class HandleIllegalArgument {

        @Test
        @DisplayName("Debe retornar status 400 BAD_REQUEST")
        void shouldReturn400BadRequest() {
            // Given
            IllegalArgumentException ex = new IllegalArgumentException("Argumento inválido");

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        @DisplayName("Debe incluir el mensaje de la excepción en el cuerpo")
        void shouldIncludeExceptionMessageInBody() {
            // Given
            IllegalArgumentException ex = new IllegalArgumentException("El valor no puede ser negativo");

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

            // Then
            assertEquals("El valor no puede ser negativo", response.getBody().get("message"));
        }

        @Test
        @DisplayName("No debe incluir 'errors' en el cuerpo cuando no hay detalles")
        void shouldNotIncludeErrorsFieldWhenNoDetails() {
            // Given
            IllegalArgumentException ex = new IllegalArgumentException("Error simple");

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

            // Then
            assertFalse(response.getBody().containsKey("errors"));
        }

        @Test
        @DisplayName("Debe incluir timestamp y status 400 en el cuerpo de la respuesta")
        void shouldIncludeTimestampAndStatusInBody() {
            // Given
            IllegalArgumentException ex = new IllegalArgumentException("Error");
            LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

            // Then
            LocalDateTime timestamp = (LocalDateTime) response.getBody().get("timestamp");
            assertNotNull(timestamp);
            assertFalse(timestamp.isBefore(antes));
            assertEquals(400, response.getBody().get("status"));
        }

        @Test
        @DisplayName("Debe incluir el error 'Bad Request' en el cuerpo")
        void shouldIncludeBadRequestErrorInBody() {
            // Given
            IllegalArgumentException ex = new IllegalArgumentException("Error");

            // When
            ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(ex);

            // Then
            assertEquals("Bad Request", response.getBody().get("error"));
        }
    }
}
