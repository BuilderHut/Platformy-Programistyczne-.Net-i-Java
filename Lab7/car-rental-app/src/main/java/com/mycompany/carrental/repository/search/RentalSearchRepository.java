package com.mycompany.carrental.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.carrental.domain.Rental;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link Rental} entity.
 */
public interface RentalSearchRepository extends ReactiveElasticsearchRepository<Rental, Long>, RentalSearchRepositoryInternal {}

interface RentalSearchRepositoryInternal {
    Flux<Rental> search(String query, Pageable pageable);

    Flux<Rental> search(Query query);
}

class RentalSearchRepositoryInternalImpl implements RentalSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    RentalSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<Rental> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<Rental> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, Rental.class).map(SearchHit::getContent);
    }
}
