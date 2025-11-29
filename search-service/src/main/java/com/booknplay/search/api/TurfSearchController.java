package com.booknplay.search.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api/search/turfs")
public class TurfSearchController {

    private final ExecutorService searchExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @GetMapping(produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> search(@RequestParam("lat") double lat, @RequestParam("lng") double lng) {
        // Simulated shards
        List<Integer> shards = List.of(1,2,3,4,5);
    return Flux.fromIterable(shards)
        .flatMap(shard -> reactor.core.publisher.Mono.fromFuture(
            CompletableFuture.supplyAsync(() -> queryShard(shard, lat, lng), searchExecutor)
        ))
        .delayElements(Duration.ofMillis(50));
    }

    private String queryShard(int shard, double lat, double lng) {
        try { Thread.sleep(30); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return "shard=" + shard + ":turf@" + (lat + shard*0.001) + "," + (lng + shard*0.001);
    }
}
