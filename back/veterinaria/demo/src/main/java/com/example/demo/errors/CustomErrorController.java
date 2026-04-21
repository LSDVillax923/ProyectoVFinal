package com.example.demo.errors;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object originalUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        int statusCode = status != null
            ? Integer.parseInt(status.toString())
            : HttpStatus.INTERNAL_SERVER_ERROR.value();

        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            String requestedUrl = originalUri != null ? originalUri.toString() : "desconocida";
            model.addAttribute("errorTitle", "Error 404");
            model.addAttribute("errorSubtitle", "URL equivocada");
            model.addAttribute(
                "errorMessage",
                "La ruta " + requestedUrl + " no existe. Verifica la URL e intenta nuevamente."
            );
            return "error404";
        }

        model.addAttribute("error505", "Ocurrió un error inesperado en la aplicación.");
        return "error505";
    }
}