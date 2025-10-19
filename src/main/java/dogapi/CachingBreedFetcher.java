package dogapi;

import java.util.*;

/**
 * This BreedFetcher caches fetch request results to improve performance and
 * lessen the load on the underlying data source. An implementation of BreedFetcher
 * must be provided. The number of calls to the underlying fetcher are recorded.
 * <p>
 * If a call to getSubBreeds produces a BreedNotFoundException, then it is NOT cached
 * in this implementation. The provided tests check for this behaviour.
 * <p>
 * The cache maps the name of a breed to its list of sub breed names.
 */
public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher innerFetcher;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        this.innerFetcher = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        if (cache.containsKey(breed)) {
            return cache.get(breed);
        }

        callsMade++;
        try {
            List<String> subBreeds = innerFetcher.getSubBreeds(breed);
            cache.put(breed, subBreeds);
            return subBreeds;
        } catch (BreedNotFoundException e) {
            callsMade--;
            throw e;
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
}