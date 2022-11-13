package de.szut.lf8_project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.szut.lf8_project.common.JWT;
import de.szut.lf8_project.domain.customer.Customer;
import de.szut.lf8_project.domain.customer.CustomerId;
import de.szut.lf8_project.domain.employee.EmployeeId;
import de.szut.lf8_project.domain.employee.ProjectRole;
import de.szut.lf8_project.domain.project.*;
import de.szut.lf8_project.repository.EmployeeData;
import de.szut.lf8_project.repository.RepositoryException;
import de.szut.lf8_project.repository.projectRepository.ProjectDataRepository;
import de.szut.lf8_project.repository.projectRepository.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;

@AutoConfigureMockMvc
@SpringBootTest
public abstract class FullIntegrationTest extends WithAppContextContainerTest {

    protected static JWT jwt;
    private static boolean setUpIsDone = false;
    private final List<Object> objectsToBeClearedAfterTest = new ArrayList<>();
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    private Environment env;
    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    public void setUpJwt() throws JsonProcessingException {
        if (setUpIsDone) return;
        jwt = getFreshJwt();
        setUpIsDone = true;
    }

    @AfterEach
    public void clearObjects() {
        objectsToBeClearedAfterTest.forEach(thing -> {
            if (thing instanceof EmployeeId) {
                deleteEmployeeInRemoteRepository((EmployeeId) thing);
            }
        });
    }

    private void deleteEmployeeInRemoteRepository(final EmployeeId employeeId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", jwt.jwt());
        RequestEntity<String> requestEntity = new RequestEntity<>(
                headers,
                HttpMethod.DELETE,
                URI.create(env.getProperty("employeeapi.baseUrl") + employeeId.toString())
        );

        new RestTemplate().exchange(requestEntity, String.class);
    }

    private JWT getFreshJwt() throws JsonProcessingException {
        String jsonString = new RestTemplate()
                .postForEntity(
                        Objects.requireNonNull(env.getProperty("authProvider.url")),
                        getLoginBody(),
                        String.class)
                .getBody();
        Map<String, String> map = new ObjectMapper().readValue(jsonString, Map.class);
        return new JWT("Bearer " + map.get("access_token"));
    }

    private HttpEntity<MultiValueMap<String, String>> getLoginBody() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> bodyParamMap = new LinkedMultiValueMap<>();
        bodyParamMap.add("grant_type", "password");
        bodyParamMap.add("client_id", env.getProperty("authProvider.client_id"));
        bodyParamMap.add("username", env.getProperty("authProvider.user"));
        bodyParamMap.add("password", env.getProperty("authProvider.password"));
        return new HttpEntity<>(bodyParamMap, headers);
    }

    protected EmployeeId createEmployeeInRemoteRepository() {
        String jsonBody = String.format("""
                {
                  "firstName": "Testnutzer für Integrationstest",
                  "lastName": "%s",
                  "street": "Teststr",
                  "postcode": "28282",
                  "city": "Bremen",
                  "phone": "0111778899",
                  "skillSet": []
                }
                """, UUID.randomUUID());
        EmployeeData rawEmployee = new RestTemplate()
                .postForEntity(
                        Objects.requireNonNull(env.getProperty("employeeapi.baseUrl")),
                        buildPostRequest(jsonBody),
                        EmployeeData.class)
                .getBody();

        EmployeeId employeeId = new EmployeeId(rawEmployee.getId());
        objectsToBeClearedAfterTest.add(employeeId);


        return employeeId;
    }

    protected Project createProjectInDatabase() throws RepositoryException {
        ProjectLead projectLead = new ProjectLead( new ProjectLeadId(createEmployeeInRemoteRepository().unbox()));
        Project project = Project.builder()
                .projectId( Optional.empty())
                .projectName( new ProjectName("Name"))
                .projectDescription( Optional.of(new ProjectDescription("Beschreibung")))
                .projectLead( projectLead)
                .customer(new Customer(new CustomerId(16L)))
                .customerContact( new CustomerContact("Franz-Ferdinand Falke"))
                .startDate( Optional.of(new StartDate( LocalDate.of(2022, 1, 20))))
                .plannedEndDate( Optional.of(new PlannedEndDate( LocalDate.of(2022, 4, 24))))
                .actualEndDate( Optional.of(new ActualEndDate( LocalDate.of(2022, 6, 26))))
                .teamMembers(Set.of(new TeamMember(new EmployeeId(456L), new ProjectRole("Developer"))))
                .build();

        return projectRepository.saveProject(project);
    }

    private HttpEntity<String> buildPostRequest(String jsonBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("Authorization", jwt.jwt());

        return new HttpEntity<>(jsonBody, headers);
    }
}