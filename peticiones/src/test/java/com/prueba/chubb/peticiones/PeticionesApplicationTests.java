package com.prueba.chubb.peticiones;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

@SpringBootTest
class PeticionesApplicationTests extends CamelTestSupport {

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new Application().myRoute(); // Usar la ruta definida en la aplicación
	}

	@Test
	public void testTimerRoute() throws Exception {
		MockEndpoint mockEndpoint = getMockEndpoint("log:sample");
		mockEndpoint.expectedMessageCount(1);
		mockEndpoint.expectedBodiesReceived("Hello, World!");

		// Enviar un mensaje a la ruta
		template.sendBody("timer:foo", null);

		assertMockEndpointsSatisfied();
	}

	@Test
	public void testFileRoute() throws Exception {
		// Simular la llegada de un archivo
		template.sendBodyAndHeader("file:entrada", "Contenido de prueba", "CamelFileName", "testfile.txt");

		MockEndpoint mockEndpoint = getMockEndpoint("log:file");
		mockEndpoint.expectedMessageCount(1);
		mockEndpoint.expectedBodiesReceived("CONTENIDO DE PRUEBA"); // Se espera en mayúsculas

		assertMockEndpointsSatisfied();
	}

	@Test
	public void testAPIRoute() throws Exception {
		MockEndpoint mockEndpoint = getMockEndpoint("log:api");
		mockEndpoint.expectedMessageCount(1);

		// Simulando la llamada a la API
		template.sendBody("timer:api", null);

		assertMockEndpointsSatisfied();
	}

	@Test
	public void testTransformRoute() throws Exception {
		String result = template.requestBody("direct:transform", "Hello World", String.class);
		assertEquals("Hi World", result); // Verifica que "Hello" se ha transformado a "Hi"
	}

	@Test
	public void testJSONAPIRoute() throws Exception {
		MockEndpoint mockEndpoint = getMockEndpoint("log:jsonData");
		mockEndpoint.expectedMessageCount(1);

		// Simulando la llamada a la API que devuelve JSON
		template.sendBody("timer:jsonApi", null);

		assertMockEndpointsSatisfied();
	}

}
