import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.HashMap;

@SpringBootApplication
public class QualifierApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(QualifierApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // TODO: Optimize later if needed
        RestTemplate restTemplate = new RestTemplate();
        String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Sriram Krishnamurthi");
        requestBody.put("regNo", "1RF22IS084");
        requestBody.put("email", "sriramkrishnamurthi12@gmail.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<HashMap<String, String>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<HashMap> response = restTemplate.postForEntity(generateWebhookUrl, request, HashMap.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String webhookUrl = (String) response.getBody().get("webhook");
            String token = (String) response.getBody().get("accessToken");

            String final_sql_query = "SELECT e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, d.DEPARTMENT_NAME, " +
                                     "(SELECT COUNT(*) FROM EMPLOYEE e2 WHERE e2.DEPARTMENT = e.DEPARTMENT " +
                                     "AND e2.DOB > e.DOB) AS YOUNGER_EMPLOYEES_COUNT " +
                                     "FROM EMPLOYEE e JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                                     "ORDER BY e.EMP_ID DESC;";

            HashMap<String, String> answerBody = new HashMap<>();
            answerBody.put("finalQuery", final_sql_query);

            HttpHeaders answerHeaders = new HttpHeaders();
            answerHeaders.setContentType(MediaType.APPLICATION_JSON);
            answerHeaders.set("Authorization", token);

            HttpEntity<HashMap<String, String>> answerRequest = new HttpEntity<>(answerBody, answerHeaders);
            restTemplate.postForEntity(webhookUrl, answerRequest, String.class);
        }
    }
}
