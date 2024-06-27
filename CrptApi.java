package org.example;

//через Gradle или Maven импортирую данную библиотеку
import com.fasterxml.jackson.databind.ObjectMapper;

//базовые библиотеки
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class CrptApi {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final int requestLimit;
    private final ScheduledExecutorService scheduler;
    private final AtomicInteger requestCount;
    private final Object lock = new Object();

    //конструктор инициализирующий объект CrptApi, для работы с API Честного знака
    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.requestLimit = requestLimit;
        this.requestCount = new AtomicInteger(0);
        this.scheduler = Executors.newScheduledThreadPool(1);
        long interval = timeUnit.toMillis(1);
        // Запланированная задача для сброса счетчика запросов с фиксированными интервалами
        scheduler.scheduleAtFixedRate(() -> requestCount.set(0), interval, interval, TimeUnit.MILLISECONDS);
    }

    /**
     * Creates a document in the CRPT API.
     *
     * @param document  The document object containing details to be sent.
     * @param signature The signature required for authentication.
     * @throws IOException          If there is an issue with JSON serialization.
     * @throws InterruptedException If the thread is interrupted while waiting.
     */
    public void createDocument(Document document, String signature) throws IOException, InterruptedException {
        synchronized (lock) {
            // Ожидание, если достигнут лимит запросов
            while (requestCount.get() >= requestLimit) {
                lock.wait();
            }
            requestCount.incrementAndGet();
        }

        try {
            // Преобразование объекта документа в формат JSON.
            String requestBody = objectMapper.writeValueAsString(document);
            // Создание HTTP запроса
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://ismp.crpt.ru/api/v3/lk/documents/create"))
                    .header("Content-Type", "application/json")
                    .header("Signature", signature)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } finally {
            synchronized (lock) {
                requestCount.decrementAndGet();
                lock.notifyAll();
            }
        }
    }

    // Внутренний класс для Document и Product
    public static class Document {
        public Description description;
        public String doc_id;
        public String doc_status;
        public String doc_type = "LP_INTRODUCE_GOODS";
        public boolean importRequest;
        public String owner_inn;
        public String participant_inn;
        public String producer_inn;
        public String production_date;
        public String production_type;
        public Product[] products;
        public String reg_date;
        public String reg_number;

        public static class Description {
            public String participantInn;
        }

        public static class Product {
            public String certificate_document;
            public String certificate_document_date;
            public String certificate_document_number;
            public String owner_inn;
            public String producer_inn;
            public String production_date;
            public String tnved_code;
            public String uit_code;
            public String uitu_code;
        }
    }

    // Main метод для теста CrptApi
    public static void main(String[] args) throws IOException, InterruptedException {
        CrptApi api = new CrptApi(TimeUnit.SECONDS, 5);
        Document document = new Document();
        document.description = new Document.Description();
        document.description.participantInn = "1234567890";
        document.doc_id = "1";
        document.doc_status = "NEW";
        document.importRequest = true;
        document.owner_inn = "1234567890";
        document.participant_inn = "1234567890";
        document.producer_inn = "1234567890";
        document.production_date = "2020-01-23";
        document.production_type = "TYPE";
        document.products = new Document.Product[1];
        document.products[0] = new Document.Product();
        document.products[0].certificate_document = "doc";
        document.products[0].certificate_document_date = "2020-01-23";
        document.products[0].certificate_document_number = "123";
        document.products[0].owner_inn = "1234567890";
        document.products[0].producer_inn = "1234567890";
        document.products[0].production_date = "2020-01-23";
        document.products[0].tnved_code = "code";
        document.products[0].uit_code = "code";
        document.products[0].uitu_code = "code";
        document.reg_date = "2020-01-23";
        document.reg_number = "123";

        String signature = "signature";
        api.createDocument(document, signature);
    }
}
