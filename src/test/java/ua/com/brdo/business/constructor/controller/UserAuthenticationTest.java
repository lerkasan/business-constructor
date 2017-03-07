package ua.com.brdo.business.constructor.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserAuthenticationTest {

    private final String QUESTIONS_URL = "/api/questions/";

    @Autowired
    private MockMvc mvc;

    @Test
    @WithAnonymousUser
    public void shouldReturnUnauthorizedStatus() throws Exception {
        mvc.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturnUnauthorizedStatusWithUsernamePassword() throws Exception {
        RequestBuilder request = post("/login")
                .param("username", "user")
                .param("password", "invalid");
        mvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnOkStatusWithValidUsernamePassword() throws Exception {
        RequestBuilder request = post("/login")
                .param("username", "admin")
                .param("password", "admin");
        mvc.perform(request)
                .andExpect(status().isOk());
        request = post("/login")
                .param("username", "expert")
                .param("password", "expert");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void expertCanAccessAdminPanel() throws Exception {
        RequestBuilder request = post("/login")
                .param("username", "expert")
                .param("password", "expert");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void adminCanAccessAdminPanel() throws Exception {
        RequestBuilder request = post("/login")
                .param("username", "admin")
                .param("password", "admin");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldSuccessfullyLogout() throws Exception {
        mvc.perform(post("/api/logout"))
                .andExpect(status().isOk());
        mvc.perform(post(QUESTIONS_URL))
                .andExpect(status().isForbidden());
    }
}
