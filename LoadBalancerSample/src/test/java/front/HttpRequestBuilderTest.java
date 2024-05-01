package front;

import com.sun.net.httpserver.HttpExchange;
import org.example.back.Server;
import org.example.common.UriSchema;
import org.example.front.request.HttpRequestBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;
import java.net.http.HttpRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class HttpRequestBuilderTest {
    private static final URI CLIENT_REQUEST_URI = URI.create("http://localhost:8080/myapp/myservice/1");
    private HttpRequestBuilder requestBuilder;
    @Mock
    private HttpExchange httpExchange;
    private AutoCloseable autoCloseable;


    @Before
    public void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        requestBuilder = new HttpRequestBuilder();
    }

    @After
    public void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    public void testSuccessfullHttpRequestBuild() {

        when(httpExchange.getRequestURI()).thenReturn(CLIENT_REQUEST_URI);
        when(httpExchange.getRequestMethod()).thenReturn("GET");

        Server server = Server.builder().schema(UriSchema.http.name()).host("localhost").port(6060).context("myapp").build();
        HttpRequest destinationRequest = requestBuilder.createHttpRequest(httpExchange,server);

        assertThat(destinationRequest.uri().getPath()).isEqualTo(CLIENT_REQUEST_URI.getPath());
        assertThat(CLIENT_REQUEST_URI.getHost()+CLIENT_REQUEST_URI.getPort()).isNotEqualTo(destinationRequest.uri().getHost()+destinationRequest.uri().getPort());
    }
}
