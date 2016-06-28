package qs.spring.controller.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceView;

import qs.spring.config.WebConfig;
import qs.spring.controller.HomeController;


@RunWith(SpringJUnit4ClassRunner.class)  
@ContextConfiguration(classes = WebConfig.class)  
@WebAppConfiguration
public class HomeControllerTest {
	
	@Test
    public void testIndex() throws Exception{
		HomeController controller = new HomeController();
		MockMvc mockMvc = standaloneSetup(controller)
				.setSingleView(new InternalResourceView("/WEB-INF/view/index.jsp"))
				.build();
		
		mockMvc.perform(get("/spring"))
			.andExpect(view().name("index"))
			.andExpect(model().attributeExists("name"));
    }
}
