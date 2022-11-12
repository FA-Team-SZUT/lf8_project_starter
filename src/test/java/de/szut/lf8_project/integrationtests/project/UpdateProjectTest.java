package de.szut.lf8_project.integrationtests.project;

import de.szut.lf8_project.integrationtests.BaseIntegrationTest;
import de.szut.lf8_project.project.ProjectEntity;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UpdateProjectTest extends BaseIntegrationTest {
    @Test
    void updateProject() throws Exception {
        final ProjectEntity project = new ProjectEntity();
        project.setAssigneeId(123);
        project.setClientId(456);
        project.setClientAssigneeId(789);
        project.setComment("updated successfully");
        this.projectRepository.saveAndFlush(project);

        long projectId = project.getId();

        String content = String.format("{\n" +
                "\t\"id\": %d,\n" +
                "\t\"assigneeId\": 123,\n" +
                "\t\"clientId\": 456,\n" +
                "\t\"clientAssigneeId\": 789,\n" +
                "\t\"comment\": \"updated successfully\"\n" +
                "}", projectId);

        final String contentAsString =
                this.mockMvc.perform(put("/project").content(content).contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", generateJwt()))
                        .andExpect(status().is2xxSuccessful())
                        .andExpect(jsonPath("id", is((int) projectId)))
                        .andExpect(jsonPath("assigneeId", is(123)))
                        .andExpect(jsonPath("clientId", is(456)))
                        .andExpect(jsonPath("clientAssigneeId", is(789)))
                        .andExpect(jsonPath("comment", is("updated successfully")))
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        final long id = Long.parseLong(new JSONObject(contentAsString).get("id").toString());

        final Optional<ProjectEntity> loadedEntity = projectRepository.findById(id);
        assertThat(loadedEntity.get().getId()).isEqualTo(projectId);
        assertThat(loadedEntity.get().getAssigneeId()).isEqualTo(123);
        assertThat(loadedEntity.get().getClientId()).isEqualTo(456);
        assertThat(loadedEntity.get().getClientAssigneeId()).isEqualTo(789);
        assertThat(loadedEntity.get().getComment()).isEqualTo("updated successfully");
    }
}
