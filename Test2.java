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
    public void testProcessSnowExpectations() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("your_token_here");
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<List> response = new ResponseEntity<>(Arrays.asList(getSnowExpectationMap()), HttpStatus.OK);

        when(restTemplate.exchange("https://api.example.com/snowExpectations", HttpMethod.GET, entity, List.class))
                .thenReturn(response);

        // Act
        main.processSnowExpectations();

        // Assert
        ArgumentCaptor<Object[]> argument = ArgumentCaptor.forClass(Object[].class);
        verify(jdbcTemplate, times(1)).update(eq("INSERT INTO SnowExpectations (ID, Area, Expectation) VALUES (?, ?, ?)"), argument.capture());
        assertEquals(1, argument.getValue()[0]);
        assertEquals("Area1", argument.getValue()[1]);
        assertEquals(20.0, argument.getValue()[2]);
    }

    private Map<String, Object> getSnowExpectationMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("area", "Area1");
        map.put("expectation", 20.0);
        return map;
    }
}
