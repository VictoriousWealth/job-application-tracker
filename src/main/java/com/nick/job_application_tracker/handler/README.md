Great idea! A `README.md` under `handler/` explaining the purpose and usage of the `GlobalExceptionHandler` will be valuable for documentation and onboarding others to your project.

Here's a ready-to-use example you can paste directly into:

```
src/main/java/com/nick/job_application_tracker/handler/README.md
```

---

## 📄 `README.md` for `handler/GlobalExceptionHandler.java`

````markdown
# GlobalExceptionHandler

The `GlobalExceptionHandler` is a centralized error-handling component that intercepts exceptions thrown by controller methods across the application and returns consistent, structured error responses in JSON format.

This promotes cleaner controller logic and ensures that clients receive standardized error messages.

---

## 🔧 Features

- Centralized handling of exceptions
- Returns structured error response using `ErrorResponseDTO`
- Logs warnings or errors for traceability
- HTTP status codes matched to exception types

---

## 💡 What Exceptions Are Handled?

| Exception Type                             | HTTP Status | Description                               |
|-------------------------------------------|-------------|-------------------------------------------|
| `EntityNotFoundException`                 | 404         | Resource not found                        |
| `RuntimeException`                        | 500         | Fallback for unhandled exceptions         |
| `MethodArgumentNotValidException`         | 400         | Request validation failure (e.g. DTOs)    |
| `HttpMessageNotReadableException`         | 400         | Malformed JSON input                      |
| `MethodArgumentTypeMismatchException`     | 400         | Path/query param type mismatch            |
| `IllegalArgumentException`               | 400         | Invalid arguments passed to controller    |
| `ConstraintViolationException`           | 400         | Validation failure on path/query params   |

---

## 🧾 Error Response Format

All errors are returned in the following format (from `ErrorResponseDTO`):

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed: email must not be blank",
  "path": "/api/users/signup",
  "timestamp": "2025-06-15T12:34:56.123Z"
}
````

---

## 🧪 Swagger Integration

Error responses are integrated into Swagger UI using:

* `@ApiResponse` annotations in controllers, or
* Global `OpenApiCustomizer` in `OpenApiConfig` for 400 and 500 status codes.

This allows `ErrorResponseDTO` to appear in the API docs.

---

## 🛠 Modifying the Handler

To add support for more exception types, simply create a new `@ExceptionHandler` method and return a `ResponseEntity<ErrorResponseDTO>`.

Example:

```java
@ExceptionHandler(CustomAppException.class)
public ResponseEntity<ErrorResponseDTO> handleCustomError(CustomAppException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
}
```

---

## 📁 File Location

```
src/main/java/com/nick/job_application_tracker/handler/GlobalExceptionHandler.java
```

---

```

Let me know if you'd like:
- A similar README template for `controller/` or `config/`
- To include unit test documentation in this file as well

This keeps your project self-documenting and very professional 👌
```
