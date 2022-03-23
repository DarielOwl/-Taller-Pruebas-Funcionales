package com.example.RESTfulTest;

import java.util.Optional;

import com.example.RESTfulTest.model.Widget;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ResTfulTestApplicationTests {

	@Test
	void contextLoads() {
	}

	//PUT para modificar un elemento que se encuentra en la base de datos
	@Test
    @DisplayName("PUT /rest/widget/1 - Conflict")
    void testUpdateWidgetConflict() throws Exception {
        // Setup our mocked service
        Widget widgetToPut = new Widget("New Widget", "This is my widget", 1);
        Widget widgetToReturn = new Widget(1L, "New Widget", "This is my widget", 2);
        doReturn(Optional.of(widgetToReturn)).when(service).findById(1L);
        doReturn(widgetToReturn).when(service).save(any());

        // Execute the POST request
        mockMvc.perform(put("/rest/widget/{id}", 1l)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 3)
                .content(asJsonString(widgetToPut)))

                // Validate the response code and content type
                .andExpect(status().isConflict());
    }



	//PUT para modificar un elemento que no se encuentra en la base de datos (not found)
	@Test
    @DisplayName("PUT /rest/widget/1 - Not Found")
    void testUpdateWidgetNotFound() throws Exception {
        // Setup our mocked service
        Widget widgetToPut = new Widget("New Widget", "This is my widget");
        doReturn(Optional.empty()).when(service).findById(1L);

        // Execute the POST request
        mockMvc.perform(put("/rest/widget/{id}", 1l)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 3)
                .content(asJsonString(widgetToPut)))

                // Validate the response code and content type
                .andExpect(status().isNotFound());
    }



	//GET para obtener un elemento por ID que se encuentra en la base de datos
	@Test
    @DisplayName("GET /rest/widget/1")
    void testGetWidgetById() throws Exception {
        // Setup our mocked service
        Widget widget = new Widget(1l, "Widget Name", "Description", 1);
        doReturn(Optional.of(widget)).when(service).findById(1l);

        // Execute the GET request
        mockMvc.perform(get("/rest/widget/{id}", 1L))
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/rest/widget/1"))
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Widget Name")))
                .andExpect(jsonPath("$.description", is("Description")))
                .andExpect(jsonPath("$.version", is(1)));
    }
}
