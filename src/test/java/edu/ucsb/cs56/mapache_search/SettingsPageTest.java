import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)

public class SettingsPageTest {

    @Autowired
    private MockMvc mvc;

    /*
    @Test
    public void getSettingsPage_APIKeyInstructionsExist() throws Exception{
        mvc.perform(MockMvcRequestBuilders.get("/user/settings").accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect()
            
    }
    */

}