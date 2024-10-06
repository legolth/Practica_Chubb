package com.prueba.chubb.peticiones.application;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

public class Application {

    @Bean
    public RouteBuilder myRoute() {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // Ruta del temporizador
                from("timer:foo?period=5000") // Cada 5 segundos
                        .setBody().constant("Hello, World!")
                        .to("log:sample");

                // Ruta para leer archivos
                from("file:entrada?noop=true") // 'noop=true' para no mover el archivo
                        .log("Archivo recibido: ${header.CamelFileName}")
                        .to("direct:processFile"); // Envía el archivo a procesar

                // Ruta de procesamiento de archivos
                from("direct:processFile")
                        .process(exchange -> {
                            String body = exchange.getIn().getBody(String.class);
                            // Transformar el contenido a mayúsculas
                            String upperCaseBody = body.toUpperCase();
                            exchange.getIn().setBody(upperCaseBody);
                        })
                        .to("file:salida?fileName=processed-${header.CamelFileName}");  // Guardar en el directorio de salida

                // Llamada a una API
                from("timer:api?period=10000") // Llamada cada 10 segundos
                        .to("https://localhost:8081/api/personas")
                        .log("Datos de la API: ${body}"); // Imprimir la respuesta de la API

                // Ejemplo de procesamiento de datos
                from("direct:transform")
                        .process(exchange -> {
                            String originalBody = exchange.getIn().getBody(String.class);
                            String transformedBody = originalBody.replace("Hello", "Hi");
                            exchange.getIn().setBody(transformedBody);
                        })
                        .to("log:transformedData"); // Log del cuerpo transformado

                // Ejemplo de llamada a una API y procesamiento de JSON
                from("timer:jsonApi?period=20000") // Cada 20 segundos
                        .to("https://localhost:8081/api/productos")
                        .unmarshal().json() // Deserializa la respuesta JSON
                        .process(exchange -> {
                            // Procesa los datos JSON
                            List<Map<String, Object>> posts = exchange.getIn().getBody(List.class);
                            // Aquí puedes realizar operaciones adicionales sobre los datos
                            exchange.getIn().setBody(posts);
                        })
                        .to("log:jsonData"); // Imprimir los datos JSON procesados
            }
        };
    }
}
