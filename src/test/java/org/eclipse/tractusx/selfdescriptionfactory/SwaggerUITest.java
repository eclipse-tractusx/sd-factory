package org.eclipse.tractusx.selfdescriptionfactory;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SwaggerUITest {
   @Autowired
   private MockMvc mockMvc;

   @Test
   public void testGetSwaggerUiExpect200() throws Exception {
      this.mockMvc.perform( get( "/swagger-ui/index.html" ) )
                  .andDo( print() )
                  .andExpect( status().isOk() )
                  .andExpect( content().string( containsString( "<div id=\"swagger-ui\"></div>" ) ) );
   }
}
