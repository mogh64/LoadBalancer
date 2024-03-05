package front;

import com.sun.net.httpserver.HttpExchange;
import org.example.front.request.HttpRequestParser;
import org.example.front.exception.InvalidRequestException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class HttpRequestParserTest {
    private static final String URI = "http://localhost:8000/myapp/myservice?word=ant";
    private static final String WRONG_URI = "http://localhost:8000/";
    private static final String ROUTE_KEY = "myapp";
    private HttpRequestParser httpRequestParser;
    @Mock
    private HttpExchange httpExchange;
    private AutoCloseable autoCloseable;

    @Before
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        httpRequestParser = new HttpRequestParser();
    }

    @After
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testParseSuccessfully() throws URISyntaxException {

        when(httpExchange.getRequestURI()).thenReturn(new URI(URI));
        var routeKy =  httpRequestParser.getRouteKey(httpExchange);
        assertThat(routeKy).isEqualTo(ROUTE_KEY);
    }

    @Test
    public void testParseFailWhenUriNotHaveContext() throws URISyntaxException {
        when(httpExchange.getRequestURI()).thenReturn(new URI(WRONG_URI));
        assertThatThrownBy(()-> httpRequestParser.getRouteKey(httpExchange)).isInstanceOf(InvalidRequestException.class).hasMessageContaining("context not exist");

    }
}
