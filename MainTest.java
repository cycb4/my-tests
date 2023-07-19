import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MainTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private Main main;

    @Test
    public void testProcessRainExpectations() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("spr_wtr_adf0eec4a18f5f39663ef2bd3575a8abf71106e0_sfx");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<List> response = new ResponseEntity<>(Arrays.asList(getRainExpectationMap()), HttpStatus.OK);

        when(restTemplate.exchange("https://api.example.com/rainExpectations", HttpMethod.GET, entity, List.class))
                .thenReturn(response);

        // Act
        main.processRainExpectations();

        // Assert
        ArgumentCaptor<Object[]> argument = ArgumentCaptor.forClass(Object[].class);
        verify(jdbcTemplate, times(1)).update(eq("INSERT INTO RainExpectations (ID, Area, Expectation) VALUES (?, ?, ?)"), argument.capture());
        assertEquals(1, argument.getValue()[0]);
        assertEquals("Area1", argument.getValue()[1]);
        assertEquals(50.0, argument.getValue()[2]);
    }

    private Map<String, Object> getRainExpectationMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("area", "Area1");
        map.put("expectation", 50.0);
        return map;
    }
}
