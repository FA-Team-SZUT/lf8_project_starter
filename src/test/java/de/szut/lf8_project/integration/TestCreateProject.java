package de.szut.lf8_project.integration;

import de.szut.lf8_project.domain.employee.EmployeeId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Der Create Project Rest-Endpunkt")
public class TestCreateProject extends IntegrationTestSetup {


    @Test
    @DisplayName("sollte erfolgreich ein Projekt erstellen")
    void createProject() throws Exception {
        EmployeeId newEmployee = createEmployeeInRemoteRepository();
        String jsonBody = String.format("""
                 {
                        "projectName": "foobar",
                        "projectLeadId": %d,
                        "customerId": 789,
                        "customerContact": "Testkontakt",
                        "projectDescription": "foobar at the beach",
                        "startDate": "2022-09-23"
                        }
                """, newEmployee.unbox());
        String expectedJsonContent = String.format("""
                    {
                    "projectId": 1,
                    "projectName": "foobar",
                    "projectDescription": "foobar at the beach",
                    "startDate": "2022-09-23",
                     "projectLead": {
                        "projectLeadId": %d
                    },
                    "customer" : {
                        "customerId": 789
                        }
                    }
                """, newEmployee.unbox());

        ResultActions result = mockMvc.perform(post("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
                .content(jsonBody)
        );

        result
                .andExpect(content().json(expectedJsonContent))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("einen 404 Fehler werfen wenn der angegebene Mitarbeiter nicht gefunden werden kann")
    void missingEmployee() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", jwt.jwt())
                .content("""
                 {
                        "projectName": "foobar",
                        "projectLeadId": 9999999999,
                        "customerId": 789,
                        "customerContact": "Testkontakt",
                        "projectDescription": "foobar at the beach",
                        "startDate": "2022-09-23"
                        }
                """)
        );

        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("einen 401 Fehler werfen wenn die Authorisierung fehlschlägt")
    void noAuth() throws Exception {
        ResultActions result = mockMvc.perform(post("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "invalidjwt")
                .content("""
                 {
                        "projectName": "foobar",
                        "projectLeadId": 2,
                        "customerId": 789,
                        "customerContact": "Testkontakt",
                        "projectDescription": "foobar at the beach",
                        "startDate": "2022-09-23"
                        }
                """)
        );

        result.andExpect(status().is(401));
    }


}
