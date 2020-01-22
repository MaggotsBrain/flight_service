package de.btu.flight_service.service;

import de.btu.flight_service.interfaces.IFlightService;
import de.btu.flight_service.models.FlightAddRequest;
import de.btu.flight_service.models.FlightGetRequest;
import de.btu.flight_service.models.FlightResponse;
import de.btu.flight_service.models.IcaoListResponse;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FlightService implements IFlightService {

    @Value("${spring.elasticsearch.rest.uris}")
    private String host = "localhost";

    @Value("${elasticsearch.index")
    private String index;

    private static final Logger logger = LoggerFactory.getLogger(FlightService.class);

    private RestHighLevelClient elasticClient;

    @PostConstruct
    public void init() {
        this.elasticClient = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, 9200, "http")));
    }

    @PreDestroy
    private void destroy() {
        try {
            if (elasticClient != null)
                elasticClient.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    public boolean add(FlightAddRequest messsage) {
        try {
            final XContentBuilder builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.field("timestamp", LocalDateTime.now());
                builder.field("ICAO", messsage.getIcao());
                builder.field("Callsign", messsage.getCallsign());
                builder.field("Speed", messsage.getSpeed());
                builder.field("Heading", messsage.getHeading());
                builder.field("Position", messsage.getPosition());
                builder.field("eo", messsage.getEo());
                builder.field("Parity", messsage.getParity());
                builder.field("Time", messsage.getTime());
            }
            builder.endObject();
            elasticClient.index(new IndexRequest("flights").source(builder), RequestOptions.DEFAULT);
            return true;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public List<FlightResponse> get(FlightGetRequest message) {
        List<FlightResponse> response = new ArrayList<>();
        try {
            SearchRequest searchRequest = new SearchRequest("flights");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(new MatchQueryBuilder("ICAO", message.getIcao()));
            searchRequest.source(searchSourceBuilder);

            SearchResponse flightsResponse = elasticClient.search(searchRequest, RequestOptions.DEFAULT);

            SearchHit[] searchHits = flightsResponse.getHits().getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                response.add(FlightResponse.builder().
                        icao(sourceAsMap.get("ICAO").toString())
                        .callsign(sourceAsMap.get("Callsign").toString())
                        .speed(sourceAsMap.get("Speed").toString())
                        .heading(sourceAsMap.get("Heading").toString())
                        .position(sourceAsMap.get("Position").toString())
                        .eo(sourceAsMap.get("eo").toString())
                        .parity(sourceAsMap.get("Parity").toString())
                        .time(sourceAsMap.get("Time").toString())
                        .build());
            }
            return response;

        } catch (IOException e) {
            logger.error(e.getMessage());
            return response;
        }
    }

    @Override
    public FlightResponse getLast(FlightGetRequest message) {
        List<FlightResponse> response = new ArrayList<>();
        try {
            SearchRequest searchRequest = new SearchRequest("flights");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(new MatchQueryBuilder("ICAO", message.getIcao()));
            searchSourceBuilder.sort(new FieldSortBuilder("timestamp").order(SortOrder.DESC));
            searchRequest.source(searchSourceBuilder);
            SearchResponse flightsResponse = elasticClient.search(searchRequest, RequestOptions.DEFAULT);

            SearchHit[] searchHits = flightsResponse.getHits().getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                response.add(FlightResponse.builder().
                        icao(sourceAsMap.get("ICAO").toString())
                        .callsign(sourceAsMap.get("Callsign").toString())
                        .speed(sourceAsMap.get("Speed").toString())
                        .heading(sourceAsMap.get("Heading").toString())
                        .position(sourceAsMap.get("Position").toString())
                        .eo(sourceAsMap.get("eo").toString())
                        .parity(sourceAsMap.get("Parity").toString())
                        .time(sourceAsMap.get("Time").toString())
                        .build());
            }
            return response.get(0);

        } catch (IOException e) {
            logger.error(e.getMessage());
            return response.get(response.size() - 1);
        }
    }

    public List<FlightResponse> getAll() {
        List<FlightResponse> response = new ArrayList<>();
        try {
            SearchRequest searchRequest = new SearchRequest("flights");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
            searchRequest.source(searchSourceBuilder);

            SearchResponse flightsResponse = elasticClient.search(searchRequest, RequestOptions.DEFAULT);

            SearchHit[] searchHits = flightsResponse.getHits().getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                response.add(FlightResponse.builder().
                        icao(sourceAsMap.get("ICAO").toString())
                        .callsign(sourceAsMap.get("Callsign").toString())
                        .speed(sourceAsMap.get("Speed").toString())
                        .heading(sourceAsMap.get("Heading").toString())
                        .position(sourceAsMap.get("Position").toString())
                        .eo(sourceAsMap.get("eo").toString())
                        .parity(sourceAsMap.get("Parity").toString())
                        .time(sourceAsMap.get("Time").toString())
                        .build());
            }
            return response;

        } catch (IOException e) {
            logger.error(e.getMessage());
            return response;
        }
    }

    public IcaoListResponse listIcao() {
        Set<String> icao = new HashSet<>();
        try {
            String[] includeFields = new String[]{"ICAO"};
            String[] excludeFields = new String[]{};
            SearchRequest searchRequest = new SearchRequest("flights");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.fetchSource(includeFields, excludeFields);
            searchSourceBuilder.size(10000);
            searchRequest.source(searchSourceBuilder);

            SearchResponse flightsResponse = elasticClient.search(searchRequest, RequestOptions.DEFAULT);

            SearchHit[] searchHits = flightsResponse.getHits().getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                icao.add(sourceAsMap.get("ICAO").toString());
            }
            return IcaoListResponse.builder()
                    .icao(icao)
                    .build();

        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
