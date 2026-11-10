package io.github.szambon23.pokedex.shared;

import io.github.szambon23.pokedex.pokemon.PokemonNotFoundException;
import io.github.szambon23.pokedex.team.DuplicateSpeciesException;
import io.github.szambon23.pokedex.team.SpeciesClauseViolationException;
import io.github.szambon23.pokedex.team.TeamFullException;
import io.github.szambon23.pokedex.team.TeamNotFoundException;
import io.github.szambon23.pokedex.team.UnknownPokemonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // --- 404: the requested resource doesn't exist ---

    @ExceptionHandler(PokemonNotFoundException.class)
    public ProblemDetail handlePokemonNotFound(PokemonNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Pokémon not found");
        problem.setProperty("pokedexNumber", ex.getPokedexNumber());
        return problem;
    }

    @ExceptionHandler(TeamNotFoundException.class)
    public ProblemDetail handleTeamNotFound(TeamNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Team not found");
        problem.setProperty("teamId", ex.getTeamId());
        return problem;
    }

    // --- 400: invalid sort ---

    @ExceptionHandler(InvalidSortPropertyException.class)
    public ProblemDetail handleInvalidSortProperty(InvalidSortPropertyException ex) {
        String detail = "Cannot sort by '" + ex.getProperty() + "'. Allowed properties: "
                + String.join(", ", ex.getAllowedProperties());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setTitle("Invalid sort parameter");
        problem.setProperty("property", ex.getProperty());
        problem.setProperty("allowedProperties", ex.getAllowedProperties());
        return problem;
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ProblemDetail handlePropertyReference(PropertyReferenceException ex) {
        String detail = "Cannot sort by '" + ex.getPropertyName() + "': unknown property";
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setTitle("Invalid sort parameter");
        problem.setProperty("property", ex.getPropertyName());
        return problem;
    }

    // --- 422: business rule violations ---

    @ExceptionHandler(UnknownPokemonException.class)
    public ProblemDetail handleUnknownPokemon(UnknownPokemonException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
        problem.setTitle("Unknown Pokémon");
        problem.setProperty("pokedexNumber", ex.getPokedexNumber());
        return problem;
    }

    @ExceptionHandler(DuplicateSpeciesException.class)
    public ProblemDetail handleDuplicateSpecies(DuplicateSpeciesException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
        problem.setTitle("Duplicate species");
        problem.setProperty("pokedexNumber", ex.getPokedexNumber());
        return problem;
    }

    @ExceptionHandler(TeamFullException.class)
    public ProblemDetail handleTeamFull(TeamFullException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
        problem.setTitle("Team is full");
        problem.setProperty("maxMembers", ex.getMaxMembers());
        return problem;
    }

    @ExceptionHandler(SpeciesClauseViolationException.class)
    public ProblemDetail handleSpeciesClauseViolation(SpeciesClauseViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
        problem.setTitle("Species Clause violation");
        problem.setProperty("duplicatedPokedexNumbers", ex.getDuplicatedPokedexNumbers());
        return problem;
    }

    // --- 400: overrides of Spring's default handlers ---

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<String> messages = new ArrayList<>();
        for (ParameterValidationResult result : ex.getParameterValidationResults()) {
            String parameter = result.getMethodParameter().getParameterName();
            for (MessageSourceResolvable error : result.getResolvableErrors()) {
                messages.add(parameter + ": " + error.getDefaultMessage());
            }
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, String.join("; ", messages));
        problem.setTitle("Invalid request parameter");
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<FieldError> fieldErrors = new ArrayList<>(ex.getBindingResult().getFieldErrors());
        fieldErrors.sort(Comparator.comparing(FieldError::getField));

        List<String> messages = new ArrayList<>();
        List<FieldValidationError> errors = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            messages.add(field + ": " + message);
            errors.add(new FieldValidationError(field, message));
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, String.join("; ", messages));
        problem.setTitle("Invalid request body");
        problem.setProperty("errors", errors);
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String parameter = ex instanceof MethodArgumentTypeMismatchException mismatch
                ? mismatch.getName()
                : ex.getPropertyName();
        String expectedType = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "value";

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, parameter + ": must be a valid " + expectedType);
        problem.setTitle("Invalid request parameter");
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    // --- 500: anything unexpected ---

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpectedException(Exception ex) {
        String detail = "Unexpected error occurred.";
        log.error(detail, ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, detail);
        problem.setTitle("Internal server error");
        return problem;
    }
}