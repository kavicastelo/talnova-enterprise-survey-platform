package com.talnova.tesp.aiservice.adapter;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AiAdapterFactoryServiceImpl implements AiAdapterFactoryService {

    private final Map<String, AiProviderAdapter> adapterMap;

    public AiAdapterFactoryServiceImpl(List<AiProviderAdapter> adapters) {
        this.adapterMap = adapters.stream()
                .collect(Collectors.toMap(
                        adapter -> adapter.getProviderName().toUpperCase(),
                        Function.identity()
                ));
    }

    @Override
    public AiProviderAdapter getAdapter(String providerName) {
        if (providerName == null || providerName.isBlank()) {
            return adapterMap.getOrDefault("OPENAI", adapterMap.values().iterator().next());
        }
        AiProviderAdapter adapter = adapterMap.get(providerName.toUpperCase());
        if (adapter == null) {
            throw new IllegalArgumentException("Unsupported AI Provider Adapter: " + providerName);
        }
        return adapter;
    }
}
