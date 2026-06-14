package com.mycompany.carrental.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.mycompany.carrental.domain.DrivingLicense;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

/**
 * Spring Data Elasticsearch repository for the {@link DrivingLicense} entity.
 */
public interface DrivingLicenseSearchRepository
    extends ReactiveElasticsearchRepository<DrivingLicense, Long>, DrivingLicenseSearchRepositoryInternal {}

interface DrivingLicenseSearchRepositoryInternal {
    Flux<DrivingLicense> search(String query, Pageable pageable);

    Flux<DrivingLicense> search(Query query);
}

class DrivingLicenseSearchRepositoryInternalImpl implements DrivingLicenseSearchRepositoryInternal {

    private final ReactiveElasticsearchTemplate reactiveElasticsearchTemplate;

    DrivingLicenseSearchRepositoryInternalImpl(ReactiveElasticsearchTemplate reactiveElasticsearchTemplate) {
        this.reactiveElasticsearchTemplate = reactiveElasticsearchTemplate;
    }

    @Override
    public Flux<DrivingLicense> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        nativeQuery.setPageable(pageable);
        return search(nativeQuery);
    }

    @Override
    public Flux<DrivingLicense> search(Query query) {
        return reactiveElasticsearchTemplate.search(query, DrivingLicense.class).map(SearchHit::getContent);
    }
}
